package com.github.tartakynov.osm.algorithms

import com.github.tartakynov.osm.graph.Graph.Graph
import com.github.tartakynov.osm.graph.Weights.Weights

trait WeightsCalculator {
  def calculate(graph: Graph): Weights
}

