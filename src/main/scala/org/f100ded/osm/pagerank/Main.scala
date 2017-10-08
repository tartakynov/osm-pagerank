package org.f100ded.osm.pagerank

import org.f100ded.osm.pagerank.graph.Graph

object Main {
  def main(args: Array[String]): Unit = {
    val graph = Graph.fromCSV(
      vertices = io.Source.fromFile("data/vertices.csv"),
      edges = io.Source.fromFile("data/edges.csv")
    )

    println(graph.keySet.size)
  }
}
