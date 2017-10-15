package com.github.tartakynov.osm.algorithms

import com.github.tartakynov.osm.graph.Graph.Graph
import com.github.tartakynov.osm.graph.Segment
import com.github.tartakynov.osm.graph.Weights.Weights
import com.typesafe.scalalogging.StrictLogging

import scala.collection.mutable

/**
  * Calculates number of upstream segments for each segment
  */
object UpstreamSegmentsCounter extends WeightsCalculator with StrictLogging {

  def calculate(graph: Graph): Weights = {
    logger.info(s"Calculating segments weights. Found ${graph.size} segments")
    val startTime = System.currentTimeMillis()
    val segments = graph.keySet.toList.sortBy(segment => graph(segment).count(_.flowsInto(segment)))
    val weights = mutable.Map[Segment, Double]()
    val visited = mutable.Set[Segment]()
    for (segment <- segments) {
      val stack = mutable.Stack[(Segment, Double)]()
      if (!visited.contains(segment)) {
        stack.push((segment, 1))
      }

      while (stack.nonEmpty) {
        val (current, w) = stack.pop()
        if (!visited.contains(current)) {
          visited.add(current)
          weights.update(current, weights.getOrElse(current, 0d) + w)
          val links = graph(current).filter(current.flowsInto)
          stack.pushAll(links.map((_, w + 1)))
        }
      }
    }

    logger.info(s"Done calculating weights in ${System.currentTimeMillis() - startTime}ms")
    weights.toMap
  }
}