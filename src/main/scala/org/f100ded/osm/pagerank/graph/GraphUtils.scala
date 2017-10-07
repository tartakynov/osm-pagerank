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
    val visited = mutable.Set[Segment]()
    val stack = mutable.Stack[Segment]()
    var g = graph
    stack.pushAll(g.keySet)
    while (stack.nonEmpty) {
      val segment = stack.pop()
      if (!visited.contains(segment) && g.contains(segment)) {
        visited.add(segment)
        val neighbours = g(segment)
        neighbours.filter(canMerge(g, segment, _)) match {
          case head :: Nil =>
            val merged = segment.concat(head)
            val links = (g(head) ++ g(segment)).filterNot(v => v.equals(head) || v.equals(segment))
            g += merged.get -> links
            g = forget(g, head, segment)
            stack.pushAll(links.filterNot(visited.contains))
          case _ =>
            stack.pushAll(neighbours.filterNot(visited.contains))
        }
      }
    }

    g
  }

  /**
    * Checks if the two segments can be merged into one segment
    */
  def canMerge(graph: Graph, first: Segment, second: Segment): Boolean = if (first.continuedBy(second)) {
    val x = graph(first).count(s => first.geometry.getEndPoint.intersects(s.geometry))
    val y = graph(second).count(s => second.geometry.getStartPoint.intersects(s.geometry))
    x == 1 && y == 1
  } else {
    false
  }

  /**
    * Remove given segments from the graph
    */
  def forget(graph: Graph, segments: Segment*): Graph = {
    var g = graph
    segments.flatMap(g.getOrElse(_, Nil)).foreach { neighbour =>
      g = g.updated(neighbour, graph(neighbour).filterNot(segments.contains))
    }

    g -- segments
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
    val visited = mutable.Set[Segment]()
    val stack = mutable.Stack[Segment]()
    var g = graph
    stack.pushAll(g.keySet)
    while (stack.nonEmpty) {
      val segment = stack.pop()
      if (!visited.contains(segment)) {
        visited.add(segment)
        val neighbours = g(segment)
        neighbours.flatMap(segment.splitBy).headOption match {
          case None =>
          case Some((s1, s2)) =>
            val x = g(segment).filter(n => s1.geometry.intersects(n.geometry))
            g += s1 -> (x :+ s2)
            x.foreach { q =>
              g = g.updated(q, g(q) :+ s1)
            }

            val y = g(segment).filter(n => s2.geometry.intersects(n.geometry))
            g += s2 -> (y :+ s1)
            y.foreach { q =>
              g = g.updated(q, g(q) :+ s2)
            }
            g = forget(g, segment)
            stack.push(s1)
            stack.push(s2)
        }
        stack.pushAll(neighbours.filterNot(visited.contains))
      }
    }

    g
  }
}