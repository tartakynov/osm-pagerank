package org.f100ded.osm.pagerank

import java.io.{BufferedWriter, File, FileWriter}

import com.typesafe.scalalogging.StrictLogging
import org.f100ded.osm.pagerank.graph.Graph.Graph
import org.f100ded.osm.pagerank.graph.{Graph, GraphUtils}

object Main extends StrictLogging {
  def main(args: Array[String]): Unit = {
    val graph = GraphUtils.normalize(Graph.fromCSV("data/vertices.csv", "data/edges.csv"))
    writeSegments(new File(s"data/output_normalized.csv"), graph)
  }

  private def writeSegments(outfile: File, graph: Graph): Unit = {
    logger.info(s"Writing segments to $outfile")
    val output = new BufferedWriter(new FileWriter(outfile))
    graph.keysIterator.foreach { segment =>
      output.write(s""""${segment.geometry.toText}"""")
      output.write("\n")
    }

    output.close()
  }
}
