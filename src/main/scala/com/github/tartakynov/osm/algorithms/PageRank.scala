package com.github.tartakynov.osm.algorithms

import com.github.tartakynov.osm.graph.Graph.Graph
import com.github.tartakynov.osm.graph.Segment
import com.github.tartakynov.osm.graph.Weights.Weights
import com.typesafe.scalalogging.StrictLogging

/**
  * Naive and surprisingly ineffective implementation of PageRank algorithm
  *
  * @param d Damping factor
  * @param e Error when convergence is assumed
  */
class PageRank(d: Double, e: Double) extends WeightsCalculator with StrictLogging {
  override def calculate(graph: Graph): Weights = {
    val n = graph.keySet.size

    /**
      * Segments flowing into the given segment
      */
    def flowingIn(p: Segment): Set[Segment] = graph(p).filter(_.flowsInto(p))

    /**
      * Number of outbound segments of the given segment
      */
    def outCount(p: Segment): Int = graph(p).count(p.flowsInto)

    logger.info(s"Calculating PageRank. Found ${graph.size} segments")
    val startTime = System.currentTimeMillis()
    var ranks = graph.map(_._1 -> 1.0)
    var error = 1.0
    var iteration = 0
    do {
      val next = ranks.map {
        case (p, _) => p -> ((1.0 - d) / n + d * flowingIn(p).map(pj => ranks(pj) / outCount(pj)).sum)
      }

      error = rmsd(next, ranks)
      iteration += 1
      ranks = next

      logger.info("Iteration %d: delta = %f. The goal is %f".format(iteration, error, e))
    } while (error > e)

    logger.info(s"Done calculating PageRank in ${System.currentTimeMillis() - startTime}ms")
    ranks.mapValues(_ / ranks.values.sum)
  }

  /**
    * Root-mean-square deviation
    */
  private def rmsd(a: Weights, b: Weights): Double = {
    Math.sqrt(a.map { case (key, value) => Math.pow(b(key) - value, 2) }.sum / a.size)
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