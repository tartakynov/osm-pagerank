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

  def line(coordinates: Coordinate*): LineString = factory.createLineString(coordinates.toArray)

  val A = Segment(line(new Coordinate(0d, 3d), new Coordinate(6d, 3d)))

  val A1 = Segment(line(new Coordinate(0d, 3d), new Coordinate(3d, 3d)))

  val A2 = Segment(line(new Coordinate(3d, 3d), new Coordinate(6d, 3d)))

  val AD = Segment(line(new Coordinate(0d, 3d), new Coordinate(6d, 3d), new Coordinate(6d, 6d)))

  val B = Segment(line(new Coordinate(0d, 3d), new Coordinate(0d, 6d)))

  val C = Segment(line(new Coordinate(3d, 3d), new Coordinate(3d, 6d)))

  val D = Segment(line(new Coordinate(6d, 3d), new Coordinate(6d, 6d)))

  val E = Segment(line(new Coordinate(0d, 0d), new Coordinate(0d, 3d)))

  val EA = Segment(line(new Coordinate(0d, 0d), new Coordinate(0d, 3d), new Coordinate(6d, 3d)))

  val EAD = Segment(line(
    new Coordinate(0d, 0d), new Coordinate(0d, 3d), new Coordinate(6d, 3d), new Coordinate(6d, 6d)))

  val Q = Segment(line(new Coordinate(3d, 3d), new Coordinate(0d, 0d)))

  val F = Segment(line(new Coordinate(3d, 0d), new Coordinate(3d, 3d)))

  val G = Segment(line(new Coordinate(6d, 0d), new Coordinate(6d, 3d)))

  val H = Segment(line(new Coordinate(10d, 3d), new Coordinate(14d, 3d)))

  val J = Segment(line(new Coordinate(14d, 3d), new Coordinate(14d, 0d)))

  val HJ = Segment(line(new Coordinate(10d, 3d), new Coordinate(14d, 3d), new Coordinate(14d, 0d)))
}
