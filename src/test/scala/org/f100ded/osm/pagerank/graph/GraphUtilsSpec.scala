package org.f100ded.osm.pagerank.graph

import org.f100ded.osm.pagerank.graph.GraphUtils.Graph
import org.scalatest.{FlatSpec, Matchers}

class GraphUtilsSpec extends FlatSpec with Matchers {
  import org.f100ded.osm.pagerank.Segments._

  //   ^       ^       ^
  // B |     C |     D |
  //   |       |       |
  // A x-------x------>x       H ------>
  //   ^       ^       ^
  // E |     F |     G |
  //   |       |       |

  val graph: Graph = Seq(
    A -> Seq(B, C, D, E, F, G),
    B -> Seq(A, E),
    C -> Seq(A, F),
    D -> Seq(A, G),
    E -> Seq(A, B),
    F -> Seq(A, C),
    G -> Seq(A, D),
    H -> Nil
  ).toMap

  "clean" should "leave only links flowing into the segment" in {
    val cleaned = GraphUtils.clean(graph)
    cleaned.size shouldBe graph.size
    cleaned.getOrElse(A, Nil) should contain only (E, F)
    cleaned.getOrElse(B, Nil) should contain only E
    cleaned.getOrElse(C, Nil) should contain only F
    cleaned.getOrElse(D, Nil) should contain only (A, G)
    cleaned.getOrElse(E, Nil) shouldBe empty
    cleaned.getOrElse(F, Nil) shouldBe empty
    cleaned.getOrElse(G, Nil) shouldBe empty
    cleaned.getOrElse(H, Nil) shouldBe empty
  }
}
