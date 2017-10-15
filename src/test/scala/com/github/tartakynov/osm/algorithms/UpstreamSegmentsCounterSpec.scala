package com.github.tartakynov.osm.algorithms

import com.github.tartakynov.osm.graph.Graph.Graph
import org.scalatest.{FlatSpec, Matchers}

class UpstreamSegmentsCounterSpec extends FlatSpec with Matchers {

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

  "calculate" should "calculate" in {
    PageRank.calculate(graph) shouldBe Map(
      E -> 1,
      F -> 1,
      G -> 1,
      B -> 2,
      A1 -> 2,
      C -> 3,
      A2 -> 3,
      D -> 4,
      H -> 1,
      J -> 2
    )
  }
}
