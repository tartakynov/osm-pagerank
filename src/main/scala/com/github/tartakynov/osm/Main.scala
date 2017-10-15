package com.github.tartakynov.osm

import com.github.tartakynov.osm.algorithms.PageRank
import com.github.tartakynov.osm.graph.Graph
import com.github.tartakynov.osm.graph.Weights._
import com.typesafe.scalalogging.StrictLogging

object Main extends StrictLogging {
  def main(args: Array[String]): Unit = {
    //    val graph = Normalizer.normalize(Graph.fromCSV("data/vertices.csv", "data/edges.csv"))
    //    graph.save("data/output_normalized_vertices.csv", "data/output_normalized_edges.csv")
    val graph = Graph.fromCSV("data/output_normalized_vertices.csv", "data/output_normalized_edges.csv")
    val weights = PageRank(d = 0.85, e = 0.001).calculate(graph)
    weights.save("data/page_rank.csv")
  }
}
