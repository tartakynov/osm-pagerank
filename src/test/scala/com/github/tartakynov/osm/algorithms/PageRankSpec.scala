package com.github.tartakynov.osm.algorithms

import com.github.tartakynov.osm.graph.Graph.Graph
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
    val beAroundOne = be >= .99 and be <= 1.01
    val ranks = PageRank(d = 0.85, e = 0.001).calculate(graph)
    ranks.values.sum should beAroundOne
  }
}