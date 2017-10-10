package com.github.tartakynov.osm.graph

import com.github.tartakynov.osm.graph.Graph._
import org.scalatest.{FlatSpec, Matchers}

class NormalizerSpec extends FlatSpec with Matchers {

  import com.github.tartakynov.osm.Segments._

  //     ^       ^       ^
  //   B |     C |       |
  //     |       |       |
  //   A x-------x-------x       H ------>x
  //     |       ^       ^                |
  // EAD |     F |     G |              J |
  //     |       |       |                v

  val graph: Graph = Seq(
    EAD -> Set(B, C, F, G),
    B -> Set(EAD),
    C -> Set(EAD, F),
    F -> Set(EAD, C),
    G -> Set(EAD),
    H -> Set(J),
    J -> Set(H)
  ).toMap

  "reduce" should "should contract the edge between H and J" in {
    val newGraph = Normalizer.reduce(graph)
    newGraph should have size 6
    newGraph.contains(H) shouldBe false
    newGraph.contains(J) shouldBe false
    newGraph.contains(EAD) shouldBe true
    newGraph.contains(HJ) shouldBe true
  }

  "split" should "split EAD into 4 segments (E, A1, A2, D) by intersection points" in {
    val newGraph = Normalizer.split(graph)
    newGraph shouldBe Seq(
      E -> Set(B, A1),
      A1 -> Set(B, E, C, F, A2),
      A2 -> Set(C, F, G, D, A1),
      D -> Set(A2, G),
      B -> Set(E, A1),
      C -> Set(A1, A2, F),
      F -> Set(A1, A2, C),
      G -> Set(A2, D),
      H -> Set(J),
      J -> Set(H)
    ).toMap
  }
}
