package org.f100ded.osm.pagerank.graph

import com.vividsolutions.jts.geom.Coordinate
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
    A -> Set(B, C, D, E, F, G),
    B -> Set(A, E),
    C -> Set(A, F),
    D -> Set(A, G),
    E -> Set(A, B),
    F -> Set(A, C),
    G -> Set(A, D),
    H -> Set(J),
    J -> Set(H)
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

  "forget" should "work" in {
    val x = GraphUtils.forget(graph, A)
    x.foreach(println)
    println()
    val y = GraphUtils.merge(x)
    y.foreach(println)
    succeed
  }

  "split" should "work" in {
    val g: Graph = Seq(
      EAD -> Set(B, C, F, G),
      B -> Set(EAD),
      C -> Set(EAD, F),
      F -> Set(EAD, C),
      G -> Set(EAD),
      H -> Set(J),
      J -> Set(H)
    ).toMap

    val x = GraphUtils.split(g)
    x.foreach(println)
    succeed
  }

  "split2" should "work" in {
    val g: Graph = Seq(
      EAD -> Set(B, C, F, G),
      B -> Set(EAD),
      C -> Set(EAD, F),
      F -> Set(EAD, C),
      G -> Set(EAD),
      H -> Set(J),
      J -> Set(H)
    ).toMap
    val x = GraphUtils.split2(g)
    x.foreach {
      case (x, y) => println(s"$x = $y")
    }
    succeed
  }
}
