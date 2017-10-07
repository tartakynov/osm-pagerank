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

  "dfs" should "dfs" in {
    GraphUtils.dfs(graph, A) should contain only (A, E, F)
    GraphUtils.dfs(graph, B) should contain only (B, E)
    GraphUtils.dfs(graph, D) should contain only (D, A, E, F, G)
    GraphUtils.dfs(graph, C) should contain only (C, A, E, F)
  }
}
