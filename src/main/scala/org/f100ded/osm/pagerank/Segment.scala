package org.f100ded.osm.pagerank

import com.vividsolutions.jts.geom.{Coordinate, LineString, Point}
import org.f100ded.osm.pagerank.Segment.SplitResult

/**
  * A segment of a linear graph, e.g. road graph
  */
case class Segment(geometry: LineString) {
  private lazy val factory = this.geometry.getFactory

  /**
    * Returns a new segment if the segments can be merged
    */
  def merge(other: Segment): Option[Segment] = {
    if (this.flowsInto(other) || other.flowsInto(this)) {
      val first = if (this.flowsInto(other)) this.geometry else other.geometry
      val second = if (this.flowsInto(other)) other.geometry else this.geometry
      Some(Segment(factory.createLineString(first.getCoordinates.dropRight(1) ++ second.getCoordinates)))
    } else {
      None
    }
  }

  /**
    * Returns result of splitting the current segment into 2 segments by an intersection point
    * with another segment if possible
    */
  def splitBy(other: Segment): Option[SplitResult] = {
    def distance(c1: Coordinate, c2: Coordinate): Double = {
      Math.sqrt(Math.pow(c1.x - c2.x, 2) + Math.pow(c1.y - c2.y, 2))
    }

    def isInSegment(start: Coordinate, end: Coordinate, test: Coordinate): Boolean = {
      distance(start, test) + distance(end, test) == distance(start, end)
    }

    if (this.geometry.intersects(other.geometry) && !this.geometry.equalsExact(other.geometry)) {
      val point = this.geometry.intersection(other.geometry).asInstanceOf[Point]
      if (point.equalsExact(this.geometry.getStartPoint) || point.equalsExact(this.geometry.getEndPoint)) {
        None
      } else {
        val coordinates = this.geometry.getCoordinates
        var result: Option[SplitResult] = None
        for (i <- 0 until coordinates.length - 1; if result.isEmpty) {
          val a = coordinates(i)
          val b = coordinates(i + 1)
          val c = point.getCoordinate
          if (isInSegment(a, b, c)) {
            val first = factory.createLineString(coordinates.take(i + 1).filterNot(_.equals2D(c)) :+ c)
            val second = factory.createLineString(
              point.getCoordinate +: coordinates.takeRight(coordinates.length - i - 1).filterNot(_.equals2D(c))
            )

            result = Some(Tuple2(Segment(first), Segment(second)))
          }
        }

        result
      }
    } else {
      None
    }
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