package com.github.tartakynov.osm.algorithms

import com.github.tartakynov.osm.graph.Graph.Graph
import org.scalatest.matchers.Matcher
import org.scalatest.{FlatSpec, Matchers}

class PageRankSpec extends FlatSpec with Matchers {

  import com.github.tartakynov.osm.Segments._

  //   ^       ^       ^
  // B |     C |     D |
  //   |  A1   |  A2   |
  //   x-------x------>x       H ------>x
  //   ^       ^       ^                |
  // E |     F |     G |              J |
  //   |       |       |                v

  val graph: Graph = Seq(
    A1 -> Set(A2, B, E, C, F),
    A2 -> Set(A1, C, F, D, G),
    B -> Set(A1, E),
    C -> Set(A1, A2, F),
    D -> Set(A2, G),
    E -> Set(A1, B),
    F -> Set(A1, A2, C),
    G -> Set(A2, D),
    H -> Set(J),
    J -> Set(H)
  ).toMap

  "PageRank" should "sum to one" in {
    val ranks = PageRank(d = 0.85, e = 0.001).calculate(graph)
    ranks.values.sum should beAround(1)
  }

  "PageRank" should "match with expected values" in {
    val ranks = PageRank(d = 0.85, e = 0.001).calculate(graph)
    ranks(A1) should beAround(0.08722377761321068)
    ranks(A2) should beAround(0.12429388028266422)
    ranks(B) should beAround(0.08722377761321068)
    ranks(C) should beAround(0.12429388028266422)
    ranks(D) should beAround(0.21888767520020203)
    ranks(E) should beAround(0.06120966981800094)
    ranks(F) should beAround(0.06120966981800094)
    ranks(G) should beAround(0.06120966981800094)
    ranks(H) should beAround(0.06120966981800094)
    ranks(J) should beAround(0.11323788540842042)
  }

  def beAround(value: Double, tolerance: Double = 0.01): Matcher[Double] = {
    be >= (value - tolerance) and be <= (value + tolerance)
  }
}