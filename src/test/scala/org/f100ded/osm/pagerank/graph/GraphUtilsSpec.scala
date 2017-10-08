package org.f100ded.osm.pagerank.graph

import org.f100ded.osm.pagerank.graph.Graph._
import org.scalatest.{FlatSpec, Matchers}

class GraphUtilsSpec extends FlatSpec with Matchers {
  import org.f100ded.osm.pagerank.Segments._

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

  "merge" should "should merge H and J into one segment" in {
    val newGraph = GraphUtils.merge(graph)
    newGraph should have size 6
    newGraph.contains(H) shouldBe false
    newGraph.contains(J) shouldBe false
    newGraph.contains(EAD) shouldBe true
    newGraph.contains(HJ) shouldBe true
  }

  "split" should "split EAD into 4 segments (E, A1, A2, D) by intersection points" in {
    val newGraph = GraphUtils.split(graph)
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
