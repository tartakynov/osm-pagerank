package org.f100ded.osm.pagerank

import com.vividsolutions.jts.geom.{LineString, Point}
import org.f100ded.osm.pagerank.Segment.SplitResult

/**
  * A segment of a linear graph, e.g. road graph
  */
case class Segment(geometry: LineString) {
  /**
    * Returns a new segment if the segments can be merged
    */
  def merge(other: Segment): Option[Segment] = {
    if (this.flowsInto(other) || other.flowsInto(this)) {
      val factory = geometry.getFactory
      val a = if (this.flowsInto(other)) this.geometry else other.geometry
      val b = if (this.flowsInto(other)) other.geometry else this.geometry
      Some(Segment(factory.createLineString(a.getCoordinates.dropRight(1) ++ b.getCoordinates)))
    } else {
      None
    }
  }

  /**
    * Returns result of splitting the current segment into 2 segments by an intersection point
    * with another segment if possible
    */
  def split(other: Segment): Option[SplitResult] = {
    ???
  }

  /**
    * Checks whether the current segment's end point is equal to another segment's start point
    */
  def flowsInto(other: Segment): Boolean = {
    geometry.getEndPoint.equalsExact(other.geometry.getStartPoint)
  }
}

object Segment {
  type SplitResult = (Segment, Segment)
}