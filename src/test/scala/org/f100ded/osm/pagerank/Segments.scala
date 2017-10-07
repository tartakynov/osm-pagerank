package org.f100ded.osm.pagerank

import com.vividsolutions.jts.geom.{Coordinate, GeometryFactory, LineString}
import org.f100ded.osm.pagerank.graph.Segment

/**
  *   ^       ^       ^
  * B |     C |     D |
  *   |       |       |
  * A x-------x------>x       H ------>
  *   ^       ^       ^
  * E |     F |     G |
  *   |       |       |
  */
object Segments {
  private val factory = new GeometryFactory()

  private def line(coordinates: Coordinate*): LineString = factory.createLineString(coordinates.toArray)

  val A = Segment("A", line(new Coordinate(0d, 3d), new Coordinate(6d, 3d)))

  val A1 = Segment("A1", line(new Coordinate(0d, 3d), new Coordinate(3d, 3d)))

  val A2 = Segment("A2", line(new Coordinate(3d, 3d), new Coordinate(6d, 3d)))

  val AD = Segment("AD", line(new Coordinate(0d, 3d), new Coordinate(6d, 3d), new Coordinate(6d, 6d)))

  val B = Segment("B", line(new Coordinate(0d, 3d), new Coordinate(0d, 6d)))

  val C = Segment("C", line(new Coordinate(3d, 3d), new Coordinate(3d, 6d)))

  val D = Segment("D", line(new Coordinate(6d, 3d), new Coordinate(6d, 6d)))

  val E = Segment("E", line(new Coordinate(0d, 0d), new Coordinate(0d, 3d)))

  val EA = Segment("EA", line(new Coordinate(0d, 0d), new Coordinate(0d, 3d), new Coordinate(6d, 3d)))

  val F = Segment("F", line(new Coordinate(3d, 0d), new Coordinate(3d, 3d)))

  val G = Segment("G", line(new Coordinate(6d, 0d), new Coordinate(6d, 3d)))

  val H = Segment("H", line(new Coordinate(10d, 3d), new Coordinate(14d, 3d)))
}
