package org.f100ded.osm.pagerank.graph

import org.f100ded.osm.pagerank.graph.GraphUtils.Graph
import org.scalatest.{FlatSpec, Matchers}

class GraphUtilsSpec extends FlatSpec with Matchers {
  import org.f100ded.osm.pagerank.Segments._

  //   ^       ^       ^
  // B |     C |     D |
  //   |       |       |
  // A x-------x------>x       H ------>x
  //   ^       ^       ^                |
  // E |     F |     G |              J |
  //   |       |       |                v

  val graph: Graph = Seq(
    A -> Seq(B, C, D, E, F, G),
    B -> Seq(A, E),
    C -> Seq(A, F),
    D -> Seq(A, G),
    E -> Seq(A, B),
    F -> Seq(A, C),
    G -> Seq(A, D),
    H -> Seq(J),
    J -> Seq(H)
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

  "canMerge" should "be true only for H and J" in {
    GraphUtils.canMerge(graph, E, B) shouldBe false
    GraphUtils.canMerge(graph, B, E) shouldBe false
    GraphUtils.canMerge(graph, A, D) shouldBe false
    GraphUtils.canMerge(graph, G, D) shouldBe false
    GraphUtils.canMerge(graph, H, J) shouldBe true
    GraphUtils.canMerge(graph, J, H) shouldBe false
  }

  "merge" should "should merge H and J into one segment" in {
    val merged = GraphUtils.merge(graph)
    merged should have size 8
    merged.contains(H) shouldBe false
    merged.contains(J) shouldBe false
    merged.contains(A) shouldBe true
    merged.contains(HJ) shouldBe true
  }
}
