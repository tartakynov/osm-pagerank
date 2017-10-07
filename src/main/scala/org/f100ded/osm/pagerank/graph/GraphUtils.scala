package org.f100ded.osm.pagerank.graph

import scala.collection.mutable
import scala.io.Source

/**
  * Graph of linear segments, e.g. a road graph or river net
  */
object GraphUtils {
  type Graph = Map[Segment, Seq[Segment]]

  def fromCSV(file: Source): Graph = {
    ???
  }

  /**
    * Merge segments continuing each other into a bigger segment
    */
  def merge(graph: Graph): Graph = {
//    val visited = mutable.Set[Segment]()
//    val stack = mutable.Stack[Segment]()
//    var g = graph
//    stack.pushAll(g.keySet)
//    while (stack.nonEmpty) {
//      val segment = stack.pop()
//      if (!visited.contains(segment)) {
//        visited.add(segment)
//        val next = g(segment).filterNot(visited.contains)
//        next.filter(segment.continuedBy) match {
//          case head :: Nil =>
//            val merged = segment.merge(head)
//            g += merged.get -> (g(head) ++ g(segment))
//            g --= Seq(head, segment)
//            stack.pushAll(next.filterNot(head.equals))
//          case _ =>
//            stack.pushAll(next)
//        }
//      }
//    }
//
//    g
    ???
  }

  /**
    * Traverse the graph by direction of the flow
    */
  def dfs(graph: Graph, start: Segment): Set[Segment] = {
    val visited = mutable.Set[Segment]()
    val stack = mutable.Stack[Segment]()
    stack.push(start)
    while (stack.nonEmpty) {
      val segment = stack.pop()
      if (!visited.contains(segment)) {
        visited.add(segment)
        val parents = graph(segment).filterNot(visited.contains).filter(_.flowsInto(segment))
        stack.pushAll(parents)
      }
    }

    visited.toSet
  }

  /**
    * Split segments by intersection points
    */
  def split(graph: Graph): Graph = {
    // https://stackoverflow.com/questions/5471234/how-to-implement-a-dfs-with-immutable-data-types
    ???
  }
}
