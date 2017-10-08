package org.f100ded.osm.pagerank.graph

import com.vividsolutions.jts.geom.LineString
import com.vividsolutions.jts.io.WKTReader

import scala.annotation.tailrec
import scala.io.Source

/**
  * Graph of linear segments, e.g. a road graph or river net
  */
object Graph {
  type Graph = Map[Segment, Set[Segment]]

  def fromCSV(vertices: Source, edges: Source): Graph = {
    val wktReader = new WKTReader()
    val segments = vertices.getLines.map { line =>
      val cols = line.split(",", 2)
      val id = cols(0).toLong
      val geom = wktReader.read(cols(1))
      id -> Segment(geom.asInstanceOf[LineString])
    }.toMap

    edges.getLines.map { line =>
      val cols = line.split(",", 2)
      val id1 = cols(0).toLong
      val id2 = cols(1).toLong
      segments(id1) -> segments(id2)
    }.toList.groupBy(_._1).mapValues(_.map(_._2).toSet)
  }

  implicit class GraphEx(graph: Graph) {
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
    def canMerge(first: Segment, second: Segment): Boolean = if (first.continuedBy(second)) {
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
