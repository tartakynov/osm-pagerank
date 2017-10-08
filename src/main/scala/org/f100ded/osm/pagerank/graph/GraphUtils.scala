package org.f100ded.osm.pagerank.graph

import com.typesafe.scalalogging.StrictLogging
import org.f100ded.osm.pagerank.graph.Graph._

import scala.annotation.tailrec

/**
  * Utilities for data preparation
  */
object GraphUtils extends StrictLogging {
  /**
    * Normalizes the given graph
    */
  def normalize(graph: Graph): Graph = {
    logger.info(s"Normalizing the graph")
    reduce(split(graph))
  }

  /**
    * Split segments by intersection points
    *
    * Example (direction of C is not relevant):
    *       A             A1     A2
    * >------*-----> = >------*----->
    *        |                |
    *        |                |
    *      C |              C |
    */
  def split(graph: Graph): Graph = {
    @tailrec
    def loop(g: Graph, next: List[Segment], visited: Set[Segment]): Graph = next match {
      case Nil => g
      case segment :: tail if visited.contains(segment) => loop(g, tail, visited)
      case segment :: tail =>
        val neighbours = g(segment)
        neighbours.find(segment.splittableBy).flatMap(segment.splitBy) match {
          case Some((first, second)) =>
            val newGraph = g
              .add(first, g(segment).filter(first.isTouching) + second)
              .add(second, g(segment).filter(second.isTouching) + first)
              .remove(segment)
            loop(newGraph, List(first, second) ++ tail, visited + segment)
          case _ => loop(g, tail ++ neighbours.diff(visited), visited + segment)
        }
    }

    logger.info(s"Splitting segments by intersection points. Found ${graph.size} segments")
    val startTime = System.currentTimeMillis()
    val newGraph = loop(graph, graph.keySet.toList, Set())
    logger.info(s"Done splitting in ${System.currentTimeMillis() - startTime}ms " +
      s"with ${newGraph.size} segments")
    newGraph
  }

  /**
    * Reduce number of vertices in the graph by contracting the edges between segments continuing each other
    *
    * Case when the edge between A and B will be contracted:
    *    A      B            AB
    * >----->------> = >------------>
    *
    * Case when the edge between A and B won't be contracted (direction of C is not relevant):
    *    A      B         A      B
    * >------*-----> = >------*----->
    *        |                |
    *        |                |
    *      C |              C |
    */
  def reduce(graph: Graph): Graph = {
    @tailrec
    def loop(g: Graph, next: List[Segment], visited: Set[Segment]): Graph = next match {
      case Nil => g
      case first :: tail if visited.contains(first) => loop(g, tail, visited)
      case first :: tail =>
        val neighbours = g(first)
        neighbours.find(g.areContractible(first, _)) match {
          case Some(second) =>
            val links = (g(first) ++ g(second)) - first - second
            val newSegment = first.concat(second).get
            val newGraph = g.add(newSegment, links).remove(first, second)
            loop(newGraph, tail :+ newSegment, visited + first + second)
          case None => loop(g, tail ++ neighbours.diff(visited), visited + first)
        }
    }

    logger.info(s"Performing edge contraction on segments continuing each other. Found ${graph.size} segments")
    val startTime = System.currentTimeMillis()
    val newGraph = loop(graph, graph.keySet.toList, Set())
    logger.info(s"Done merging in ${System.currentTimeMillis() - startTime}ms " +
      s"with ${newGraph.size} segments")
    newGraph
  }

}