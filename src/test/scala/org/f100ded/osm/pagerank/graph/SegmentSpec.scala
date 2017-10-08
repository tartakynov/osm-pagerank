package org.f100ded.osm.pagerank.graph

import org.scalatest.{FlatSpec, Matchers}

class SegmentSpec extends FlatSpec with Matchers {
  import org.f100ded.osm.pagerank.Segments._

  //   ^       ^       ^
  // B |     C |     D |
  //   |       |       |
  // A x-------x------>x       H ------>x
  //   ^       ^       ^                |
  // E |     F |     G |              J |
  //   |       |       |                v

  "A" should "concat D" in {
    A.concat(D) should contain (AD)
  }

  it should "flow into C, D" in {
    A.flowsInto(C) should be (true)
    A.flowsInto(D) should be (true)
  }

  it should "be continued by D" in {
    A.continuedBy(D) should be (true)
  }

  it should "not split A, B, C, D, E, F, G, H" in {
    A.splitBy(A) shouldBe empty
    B.splitBy(A) shouldBe empty
    C.splitBy(A) shouldBe empty
    D.splitBy(A) shouldBe empty
    E.splitBy(A) shouldBe empty
    F.splitBy(A) shouldBe empty
    G.splitBy(A) shouldBe empty
    H.splitBy(A) shouldBe empty
  }

  it should "not concat A, B, C, F, G, H" in {
    A.concat(A) shouldBe empty
    A.concat(B) shouldBe empty
    A.concat(C) shouldBe empty
    A.concat(F) shouldBe empty
    A.concat(G) shouldBe empty
    A.concat(H) shouldBe empty
  }

  it should "not flow into A, B, E, F, G, H" in {
    A.flowsInto(A) should be (false)
    A.flowsInto(B) should be (false)
    A.flowsInto(E) should be (false)
    A.flowsInto(F) should be (false)
    A.flowsInto(G) should be (false)
    A.flowsInto(H) should be (false)
  }

  it should "not be continued by B, C, E, F, G, H" in {
    A.continuedBy(B) should be (false)
    A.continuedBy(C) should be (false)
    A.continuedBy(E) should be (false)
    A.continuedBy(F) should be (false)
    A.continuedBy(G) should be (false)
    A.continuedBy(H) should be (false)
  }

  "B" should "split EA" in {
    EA.splitBy(B) should contain ((E, A))
  }

  it should "not be continuedBy A, A1, E" in {
    B.continuedBy(A) should be (false)
    B.continuedBy(A1) should be (false)
    B.continuedBy(E) should be (false)
  }

  "C" should "split A" in {
    A.splitBy(C) should contain ((A1, A2))
  }

  "D" should "not concat A" in {
    D.concat(A) shouldBe empty
  }

  it should "not flow into A" in {
    D.flowsInto(A) should be (false)
  }

  "E" should "flow into A and B" in {
    E.flowsInto(A) should be (true)
    E.flowsInto(B) should be (true)
  }

  it should "concat A" in {
    E.concat(A) should contain (EA)
  }

  it should "be continued by A, B" in {
    E.continuedBy(A) should be (true)
    E.continuedBy(B) should be (true)
  }

  "F" should "split A" in {
    A.splitBy(F) should contain ((A1, A2))
  }

  it should "flow into A" in {
    F.flowsInto(A) should be (true)
  }

  it should "be continued by C, A2" in {
    F.continuedBy(C) should be (true)
    F.continuedBy(A2) should be (true)
  }

  "G" should "split AD" in {
    AD.splitBy(G) should contain ((A, D))
  }

  it should "flow into D" in {
    G.flowsInto(D) should be (true)
  }

  it should "not flow into A" in {
    G.flowsInto(A) should be (false)
  }

}
