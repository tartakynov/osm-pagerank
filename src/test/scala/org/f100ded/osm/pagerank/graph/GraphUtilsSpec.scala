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

  "dfs" should "traverse the graph by direction of the flow" in {
    GraphUtils.dfs(graph, A) should contain only (A, E, F)
    GraphUtils.dfs(graph, B) should contain only (B, E)
    GraphUtils.dfs(graph, D) should contain only (D, A, E, F, G)
    GraphUtils.dfs(graph, C) should contain only (C, A, E, F)
    GraphUtils.dfs(graph, E) should contain only E
    GraphUtils.dfs(graph, F) should contain only F
    GraphUtils.dfs(graph, G) should contain only G
    GraphUtils.dfs(graph, H) should contain only H
  }
}
