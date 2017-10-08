package org.f100ded.osm.pagerank.graph

import scala.annotation.tailrec
import scala.collection.mutable
import scala.io.Source

/**
  * Graph of linear segments, e.g. a road graph or river net
  */
object GraphUtils {
  type Graph = Map[Segment, Set[Segment]]

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
        val x = neighbours.filter(canMerge(g, segment, _))
        if (x.size == 1) {
          val head = x.head
          val merged = segment.concat(head)
          val links = (g(head) ++ g(segment)).filterNot(v => v.equals(head) || v.equals(segment))
          g += merged.get -> links
          g = forget(g, head, segment)
          stack.pushAll(links.diff(visited))
        } else {
          stack.pushAll(neighbours.diff(visited))
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
        val parents = graph(segment).diff(visited).filter(_.flowsInto(segment))
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
          case Some((s1, s2)) =>
            val x = g(segment).filter(n => s1.geometry.intersects(n.geometry))
            g += s1 -> (x + s2)
            x.foreach { q =>
              g = g.updated(q, g(q) + s1)
            }

            val y = g(segment).filter(n => s2.geometry.intersects(n.geometry))
            g += s2 -> (y + s1)
            y.foreach { q =>
              g = g.updated(q, g(q) + s2)
            }
            g = forget(g, segment)
            stack.push(s1)
            stack.push(s2)
          case _ =>
            stack.pushAll(neighbours.diff(visited))
        }
      }
    }

    g
  }

  def split2(graph: Graph): Graph = {
    @tailrec
    def loop(g: Graph, next: List[Segment], visited: Set[Segment]): Graph = next match {
      case Nil => g
      case head :: tail if visited.contains(head) => loop(g, tail, visited)
      case head :: tail =>
        val neighbours = g(head)
        neighbours.find(head.splittableBy).flatMap(head.splitBy) match {
          case Some((s1, s2)) =>
            var gg = g
            gg = addLink(gg, s1, gg(head).filter(s1.linkedWith) + s2)
            gg = addLink(gg, s2, gg(head).filter(s2.linkedWith) + s1)
            gg = forget(gg, head)
            loop(gg, List(s1, s2) ++ tail, visited + head)

          case _ => loop(g, tail ++ neighbours.diff(visited), visited + head)
        }
    }

    loop(graph, graph.keySet.toList, Set())
  }

  def merge2(graph: Graph): Graph = {
    @tailrec
    def loop(g: Graph, next: List[Segment], visited: Set[Segment]): Graph = next match {
      case Nil => g
      case head :: tail if visited.contains(head) => loop(g, tail, visited)
      case head :: tail =>
        val neighbours = g(head)
        neighbours.filter(canMerge(g, head, _)).toList match {
          case segment :: Nil =>
            var gg = g
            val merged = head.concat(segment)
            val links = (gg(segment) ++ gg(head)) - segment - head
            gg += merged.get -> links
            gg = forget(gg, segment, head)
            loop(g, tail ++ links.diff(visited), visited + head)
          case _ => loop(g, tail ++ neighbours.diff(visited), visited + head)
        }
    }

    loop(graph, graph.keySet.toList, Set())
  }

  def addLink(graph: Graph, segment: Segment, links: Set[Segment]): Graph = {
    var g = graph
    g += segment -> links
    links.foreach { q =>
      g = g.updated(q, g.getOrElse(q, Set()) + segment)
    }

    g
  }

}