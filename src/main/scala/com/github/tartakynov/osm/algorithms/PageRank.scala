package com.github.tartakynov.osm.algorithms

import com.github.tartakynov.osm.graph.Graph.Graph
import com.github.tartakynov.osm.graph.Segment
import com.typesafe.scalalogging.StrictLogging

/**
  * Naive and surprisingly ineffective implementation of PageRank algorithm
  *
  * @param d Damping factor
  * @param e Error when convergence is assumed
  */
class PageRank(d: Double, e: Double) extends WeightsCalculator with StrictLogging {
  override def calculate(graph: Graph): Weights = {
    val n = graph.size

    /**
      * Segments flowing into the given segment
      */
    def xM(p: Segment): Set[Segment] = graph(p).filter(_.flowsInto(p))

    /**
      * Number of outbound segments of the given segment
      */
    def xL(p: Segment): Int = graph(p).count(p.flowsInto)

    /**
      * Calculates PageRank of the give segment
      */
    def xPR(p: Segment, pr: Weights): Double = (1d - d) / n + d * xM(p).map(pj => pr(pj) / xL(pj)).sum

    logger.info(s"Calculating PageRank. Found ${graph.size} segments")
    val startTime = System.currentTimeMillis()
    var ranks = graph.map(_._1 -> 1.0 / n)
    var error = 1d
    var iteration = 1
    do {
      val next = ranks.map(p => p._1 -> xPR(p._1, ranks))
      error = Math.sqrt(ranks.map { case (key, value) => Math.pow(next(key) - value, 2) }.sum / n)
      logger.info(s"Iteration $iteration: error = $error. The goal is $e")
      iteration += 1
      ranks = next
    } while (error > e)

    logger.info(s"Done calculating PageRank in ${System.currentTimeMillis() - startTime}ms")
    ranks
  }
}

object PageRank {
  /**
    * Creates an instance of PageRank calculator
    *
    * @param d Damping factor
    * @param e Error when convergence is assumed
    * @return
    */
  def apply(d: Double, e: Double): WeightsCalculator = new PageRank(d, e)
}