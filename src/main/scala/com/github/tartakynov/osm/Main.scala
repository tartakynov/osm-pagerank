package com.github.tartakynov.osm

import com.github.tartakynov.osm.algorithms.PageRank
import com.github.tartakynov.osm.graph.Graph
import com.github.tartakynov.osm.graph.Graph._
import com.typesafe.scalalogging.StrictLogging

object Main extends StrictLogging {
  def main(args: Array[String]): Unit = {
    //    val graph = Normalizer.normalize(Graph.fromCSV("data/vertices.csv", "data/edges.csv"))
    //    graph.save("data/output_normalized_vertices.csv", "data/output_normalized_edges.csv")
    val graph = Graph.fromCSV("data/output_normalized_vertices.csv", "data/output_normalized_edges.csv")
    val weights = PageRank.calculate(graph)
    graph.save("data/output_weighted1_vertices.csv", "data/output_weighted1_edges.csv", Some(weights))
  }
}
