package com.github.tartakynov.osm.graph

import java.io.{BufferedWriter, FileWriter}

import com.github.tartakynov.osm.algorithms.UpstreamSegmentsCounter.Weights
import com.typesafe.scalalogging.StrictLogging
import com.vividsolutions.jts.geom.LineString
import com.vividsolutions.jts.io.WKTReader

import scala.annotation.tailrec

/**
  * Graph of linear segments, e.g. a road graph or river net
  */
object Graph extends StrictLogging {
  type Graph = Map[Segment, Set[Segment]]

  /**
    * Reads the segments graph from csv 2 files.
    * Segments file contains segments identifiers and geometries.
    * Edges file contains undirected edges that show which segments are intersecting at any point.
    *
    * @param segmentsFile CSV file with the following format: id,linestring_wkt
    * @param edgesFile    CSV file with the following format: id1,id2
    * @return
    */
  def fromCSV(segmentsFile: String, edgesFile: String): Graph = {
    logger.info(s"Reading graph from $segmentsFile and $edgesFile")
    val segmentsSource = io.Source.fromFile("data/vertices.csv")
    val edgesSource = io.Source.fromFile("data/edges.csv")
    val wktReader = new WKTReader()
    val segments = segmentsSource.getLines.map { line =>
      val cols = line.split(",", 2)
      val id = cols(0).toLong
      val geom = wktReader.read(cols(1).stripPrefix("\"").stripSuffix("\""))
      id -> Segment(geom.asInstanceOf[LineString])
    }.toMap

    edgesSource.getLines.flatMap { line =>
      val cols = line.split(",", 2)
      val id1 = cols(0).trim.toLong
      val id2 = cols(1).trim.toLong
      segments.get(id1) -> segments.get(id2) match {
        case (Some(first), Some(second)) => Seq(first -> second, second -> first)
        case _ => Nil
      }
    }.toList.groupBy(_._1).mapValues(_.map(_._2).toSet)
  }

  implicit class GraphEx(graph: Graph) {
    def save(segmentsFile: String, edgesFile: String, weights: Option[Weights] = None): Unit = {
      def id(segment: Segment): String = java.lang.Integer.toUnsignedLong(segment.hashCode()).toString

      val segmentsWriter = new BufferedWriter(new FileWriter(segmentsFile))
      val edgesWriter = new BufferedWriter(new FileWriter(edgesFile))
      try {
        logger.info(s"Saving graph to $segmentsFile and $edgesFile")
        graph.foreach {
          case (segment, edges) =>
            segmentsWriter.write(s"""${id(segment)},"${segment.geometry.toText}"""")
            weights.foreach(w => segmentsWriter.write(s",${w(segment)}"))
            segmentsWriter.write("\n")
            edges.map(link => s"${id(segment)},${id(link)}\n").foreach(edgesWriter.write)
        }
      } finally {
        segmentsWriter.close()
        edgesWriter.close()
      }
    }

    /**
      * Applies a binary operator starting from the start segment and as far as possible against
      * the flow of the segments
      */
    def foldBFS[T](start: Segment, init: T)(op: (T, Segment) => T): T = {
      @tailrec
      def loop(acc: T, next: List[Segment], visited: Set[Segment]): T = next match {
        case Nil => acc
        case head :: tail if visited.contains(head) => loop(acc, tail, visited)
        case head :: tail =>
          val parents = graph(head).diff(visited).filter(_.flowsInto(head))
          loop(op(acc, head), tail ++ parents, visited + head)
      }

      loop(init, List(start), Set())
    }

    /**
      * Checks if the two segments can be merged into one segment
      */
    def areContractible(first: Segment, second: Segment): Boolean = if (first.continuedBy(second)) {
      val x = graph(first).count(s => first.geometry.getEndPoint.intersects(s.geometry))
      val y = graph(second).count(s => second.geometry.getStartPoint.intersects(s.geometry))
      x == 1 && y == 1
    } else {
      false
    }

    /**
      * Adds a segment to the graph
      */
    def add(segment: Segment, links: Set[Segment]): Graph = {
      var g = graph
      g += segment -> links
      links.foreach { q =>
        g = g.updated(q, g.getOrElse(q, Set()) + segment)
      }

      g
    }

    /**
      * Remove given segments from the graph
      */
    def remove(segments: Segment*): Graph = {
      var g = graph
      segments.flatMap(g.getOrElse(_, Nil)).foreach { neighbour =>
        g = g.updated(neighbour, graph(neighbour).filterNot(segments.contains))
      }

      g -- segments
    }
  }

}
