package org.f100ded.osm.pagerank.graph

import com.vividsolutions.jts.geom.{Coordinate, LineString, Point}
import org.f100ded.osm.pagerank.graph.Segment.SplitResult

/**
  * A segment of a linear graph, e.g. road graph
  */
case class Segment(name: String, geometry: LineString) {
  private lazy val factory = this.geometry.getFactory

  /**
    * Returns a new segment if the segments can be merged
    */
  def merge(other: Segment): Option[Segment] = {
    if (this.continuedBy(other) || other.continuedBy(this)) {
      val first = if (this.continuedBy(other)) this else other
      val second = if (this.continuedBy(other)) other else this
      val mergedGeometry = factory.createLineString(
        first.geometry.getCoordinates.dropRight(1) ++ second.geometry.getCoordinates
      )

      Some(Segment(first.name + second.name, mergedGeometry))
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

            result = Some(Tuple2(Segment(name + "1", first), Segment(name + "2", second)))
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
  def continuedBy(other: Segment): Boolean = {
    geometry.getEndPoint.equalsExact(other.geometry.getStartPoint)
  }

  /**
    * Checks whether the given segment flows into the current segment
    *
    * case 1 ('this' flows into 'other')
    * --------*-->
    * this    |
    *         |
    *         | other
    *         v
    *
    * case 2 ('this' flows into 'other')
    *         |
    * ------->*
    * this    |
    *         |
    *         | other
    *         v
    */
  def flowsInto(other: Segment): Boolean = {
    val case1 = other.geometry.getStartPoint.intersects(this.geometry) &&
      !other.geometry.getStartPoint.equalsExact(this.geometry.getStartPoint)
    val case2 = this.geometry.getEndPoint.intersects(other.geometry) &&
      !other.geometry.getEndPoint.equalsExact(this.geometry.getEndPoint)
    case1 || case2
  }
}

object Segment {
  type SplitResult = (Segment, Segment)
}