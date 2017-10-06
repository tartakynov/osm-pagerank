package org.f100ded.osm.pagerank.graph

import scala.io.Source

/**
  * Graph of linear segments, e.g. a road graph or river net
  */
object GraphUtils {
  type Graph = Map[Segment, Seq[Segment]]

  def fromCSV(file: Source): Graph = {
    ???
  }

  /**
    * Merge segments continuing each other into a bigger segment
    */
  def merge(graph: Graph): Graph = {
    // https://stackoverflow.com/questions/5471234/how-to-implement-a-dfs-with-immutable-data-types
    ???
  }

  /**
    * Split segments by intersection points
    */
  def split(graph: Graph): Graph = {
    // https://stackoverflow.com/questions/5471234/how-to-implement-a-dfs-with-immutable-data-types
    ???
  }

  /**
    * Convert the graph into directed by direction of the flow
    */
  def clean(graph: Graph): Graph = graph.map {
    case (segment, neighbours) => segment -> neighbours.filter(_.flowsInto(segment))
  }
}
