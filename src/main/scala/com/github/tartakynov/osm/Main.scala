package com.github.tartakynov.osm

import java.io.{BufferedWriter, File, FileWriter}

import com.github.tartakynov.osm.algorithms.WeightCalculator
import com.github.tartakynov.osm.algorithms.WeightCalculator.Weights
import com.github.tartakynov.osm.graph.Graph.Graph
import com.github.tartakynov.osm.graph.{Graph, Normalizer}
import com.typesafe.scalalogging.StrictLogging

object Main extends StrictLogging {
  def main(args: Array[String]): Unit = {
    val graph = Normalizer.normalize(Graph.fromCSV("data/vertices.csv", "data/edges.csv"))
    val weights = WeightCalculator.calculate(graph)
    writeSegments(new File(s"data/output_weighted.csv"), weights, graph)
  }

  private def writeSegments(outfile: File, weights: Weights, graph: Graph): Unit = {
    logger.info(s"Writing segments to $outfile")
    val output = new BufferedWriter(new FileWriter(outfile))
    graph.keysIterator.foreach { segment =>
      output.write(s"""${weights(segment)},"${segment.geometry.toText}"""")
      output.write("\n")
    }

    output.close()
  }
}
