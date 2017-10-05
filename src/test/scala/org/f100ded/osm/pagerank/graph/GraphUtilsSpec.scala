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
    A -> Set(B, C, D, E, F, G),
    B -> Set(A, E),
    C -> Set(A, F),
    D -> Set(A, G),
    E -> Set(A, B),
    F -> Set(A, C),
    G -> Set(A, D),
    H -> Set[Segment]()
  ).toMap

  "clean" should "leave only links flowing into the segment" in {
    val cleaned = GraphUtils.clean(graph)
    cleaned.size shouldBe graph.size
    cleaned.getOrElse(A, Set()) should contain only (E, F)
    cleaned.getOrElse(B, Set()) should contain only E
    cleaned.getOrElse(C, Set()) should contain only F
    cleaned.getOrElse(D, Set()) should contain only (A, G)
    cleaned.getOrElse(E, Set()) shouldBe empty
    cleaned.getOrElse(F, Set()) shouldBe empty
    cleaned.getOrElse(G, Set()) shouldBe empty
    cleaned.getOrElse(H, Set()) shouldBe empty
  }
}
