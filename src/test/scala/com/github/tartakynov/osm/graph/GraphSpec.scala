package com.github.tartakynov.osm.graph

import com.github.tartakynov.osm.graph.Graph._
import org.scalatest.{FlatSpec, Matchers}

class GraphSpec extends FlatSpec with Matchers {

  import com.github.tartakynov.osm.Segments._

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

  "foldBFS" should "traverse the graph against the direction of the flow" in {
    def x(acc: Set[Segment], segment: Segment): Set[Segment] = acc + segment

    graph.foldBFS(A, Set[Segment]())(x) should contain only(A, E, F)
    graph.foldBFS(B, Set[Segment]())(x) should contain only(B, E)
    graph.foldBFS(C, Set[Segment]())(x) should contain only(C, A, E, F)
    graph.foldBFS(D, Set[Segment]())(x) should contain only(D, A, E, F, G)
    graph.foldBFS(E, Set[Segment]())(x) should contain only E
    graph.foldBFS(F, Set[Segment]())(x) should contain only F
    graph.foldBFS(G, Set[Segment]())(x) should contain only G
    graph.foldBFS(H, Set[Segment]())(x) should contain only H
    graph.foldBFS(J, Set[Segment]())(x) should contain only(H, J)
  }

  "areContractible" should "be true only for H and J" in {
    graph.areContractible(E, B) shouldBe false
    graph.areContractible(B, E) shouldBe false
    graph.areContractible(A, D) shouldBe false
    graph.areContractible(G, D) shouldBe false
    graph.areContractible(H, J) shouldBe true
    graph.areContractible(J, H) shouldBe false
  }

  "add" should "add segment Q to the graph" in {
    val newGraph = graph.add(Q, Set(E, A, F, C))
    newGraph should contain key Q
    newGraph(A) shouldBe (graph(A) + Q)
    newGraph(B) shouldBe graph(B)
    newGraph(C) shouldBe (graph(C) + Q)
    newGraph(E) shouldBe (graph(E) + Q)
    newGraph(F) shouldBe (graph(F) + Q)
  }

  "remove" should "remove segment A from the graph" in {
    val newGraph = graph.remove(A)
    newGraph.contains(A) shouldBe false
    newGraph.filter {
      case (segment, links) => segment.equals(A) || links.contains(A)
    } shouldBe empty
  }

}
