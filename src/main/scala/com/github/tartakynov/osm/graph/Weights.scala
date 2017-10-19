package com.github.tartakynov.osm.graph

import java.io.{BufferedWriter, FileWriter}

import com.typesafe.scalalogging.StrictLogging

object Weights extends StrictLogging {
  type Weights = Map[Segment, Double]

  implicit class WeightsEx(weights: Weights) {
    def save(file: String): Unit = {
      val writer = new BufferedWriter(new FileWriter(file))
      try {
        logger.info(s"Saving weights to $file")
        weights.foreach {
          case (segment, weight) => writer.write("%d,%.10f\n".format(segment.id, weight))
        }
      } finally {
        writer.close()
      }
    }
  }

}
