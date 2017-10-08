package org.f100ded.osm.pagerank.graph

import org.f100ded.osm.pagerank.graph.Graph._

import scala.annotation.tailrec

/**
  * Utilities for data preparation
  */
object GraphUtils {
  /**
    * Split segments by intersection points
    */
  def split(graph: Graph): Graph = {
    @tailrec
    def loop(g: Graph, next: List[Segment], visited: Set[Segment]): Graph = next match {
      case Nil => g
      case head :: tail if visited.contains(head) => loop(g, tail, visited)
      case head :: tail =>
        val neighbours = g(head)
        neighbours.find(head.splittableBy).flatMap(head.splitBy) match {
          case Some((s1, s2)) =>
            var gg = g
            gg = gg.add(s1, gg(head).filter(s1.isTouching) + s2)
            gg = gg.add(s2, gg(head).filter(s2.isTouching) + s1)
            gg = gg.remove(head)
            loop(gg, List(s1, s2) ++ tail, visited + head)

          case _ => loop(g, tail ++ neighbours.diff(visited), visited + head)
        }
    }

    loop(graph, graph.keySet.toList, Set())
  }

  /**
    * Merge segments continuing each other into bigger segments
    */
  def merge(graph: Graph): Graph = {
    @tailrec
    def loop(g: Graph, next: List[Segment], visited: Set[Segment]): Graph = next match {
      case Nil => g
      case head :: tail if visited.contains(head) || !g.contains(head) => loop(g, tail, visited)
      case head :: tail =>
        val neighbours = g(head)
        neighbours.filter(g.canMerge(head, _)).toList match {
          case segment :: Nil =>
            var gg = g
            val merged = head.concat(segment)
            val links = (gg(segment) ++ gg(head)) - segment - head
            gg += merged.get -> links
            gg = gg.remove(segment, head)
            loop(gg, tail ++ links.diff(visited), visited + head)
          case _ => loop(g, tail ++ neighbours.diff(visited), visited + head)
        }
    }

    loop(graph, graph.keySet.toList, Set())
  }

}