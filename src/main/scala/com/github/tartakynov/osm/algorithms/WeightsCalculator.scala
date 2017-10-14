package com.github.tartakynov.osm.algorithms

import com.github.tartakynov.osm.graph.Graph.Graph
import com.github.tartakynov.osm.graph.Segment

trait WeightsCalculator {
  type Weights = Map[Segment, Int]

  def calculate(graph: Graph): Weights
}
