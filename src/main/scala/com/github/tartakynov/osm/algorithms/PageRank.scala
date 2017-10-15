package com.github.tartakynov.osm.algorithms

import com.github.tartakynov.osm.graph.Graph.Graph
import com.github.tartakynov.osm.graph.Segment
import com.typesafe.scalalogging.StrictLogging

object PageRank extends WeightsCalculator with StrictLogging {

  override def calculate(graph: Graph): PageRank.Weights = {
    val d = 0.85

    val e = 0.01

    /**
      * Number of segments flowing into the given segment
      */
    def xM(p: Segment): Set[Segment] = graph(p).filter(_.flowsInto(p))

    /**
      * Number of outbound segments of the given segment
      */
    def xL(p: Segment): Int = graph(p).count(p.flowsInto)

    /**
      * Calculates PageRank of the give segment
      */
    def xPR(p: Segment, pr: Weights): Double = (1d - d) + d * xM(p).map(pj => pr(pj) / xL(pj)).sum

    logger.info(s"Calculating PageRank. Found ${graph.size} segments")
    val startTime = System.currentTimeMillis()
    var ranks = graph.map(_._1 -> 0.0)
    var error = 1d
    var iteration = 1
    do {
      val next = ranks.map(p => p._1 -> xPR(p._1, ranks))
      error = ranks.map { case (key, value) => Math.pow(value - next(key), 2) }.sum
      logger.info(s"Iteration $iteration: quadratic_error = $error. The goal is $e")
      iteration += 1
      ranks = next
    } while (error > e)

    logger.info(s"Done calculating PageRank in ${System.currentTimeMillis() - startTime}ms")
    ranks
  }
}