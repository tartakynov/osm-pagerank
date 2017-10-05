package org.f100ded.osm.pagerank.graph

import org.scalatest.{FlatSpec, Matchers}

class SegmentSpec extends FlatSpec with Matchers {
  import org.f100ded.osm.pagerank.Segments._

  //   ^       ^       ^
  // B |     C |     D |
  //   |       |       |
  // A x-------x------>x       H ------>
  //   ^       ^       ^
  // E |     F |     G |
  //   |       |       |

  "A" should "not split A" in {
    A.splitBy(A) shouldBe empty
  }

  it should "not split B" in {
    B.splitBy(A) shouldBe empty
  }

  it should "not split C" in {
    C.splitBy(A) shouldBe empty
  }

  it should "not split D" in {
    D.splitBy(A) shouldBe empty
  }

  it should "not split E" in {
    E.splitBy(A) shouldBe empty
  }

  it should "not split F" in {
    F.splitBy(A) shouldBe empty
  }

  it should "not split G" in {
    G.splitBy(A) shouldBe empty
  }

  it should "not split H" in {
    H.splitBy(A) shouldBe empty
  }

  it should "not merge A" in {
    A.merge(A) shouldBe empty
  }

  it should "not merge B" in {
    A.merge(B) shouldBe empty
  }

  it should "not merge C" in {
    A.merge(C) shouldBe empty
  }

  it should "merge D" in {
    A.merge(D) should contain (AD)
  }

  it should "merge E" in {
    A.merge(E) should contain (EA)
  }

  it should "not merge F" in {
    A.merge(F) shouldBe empty
  }

  it should "not merge G" in {
    A.merge(G) shouldBe empty
  }

  it should "not merge H" in {
    A.merge(H) shouldBe empty
  }

  it should "not flow into A" in {
    A.flowsInto(A) should be (false)
  }

  it should "not flow into B" in {
    A.flowsInto(B) should be (false)
  }

  it should "not flow into C" in {
    A.flowsInto(C) should be (false)
  }

  it should "flow into D" in {
    A.flowsInto(D) should be (true)
  }

  it should "not flow into E" in {
    A.flowsInto(E) should be (false)
  }

  it should "not flow into F" in {
    A.flowsInto(F) should be (false)
  }

  it should "not flow into G" in {
    A.flowsInto(G) should be (false)
  }

  it should "not flow into H" in {
    A.flowsInto(H) should be (false)
  }

  "B" should "split EA" in {
    EA.splitBy(B) should contain ((E, A))
  }

  "C" should "split A" in {
    A.splitBy(C) should contain ((A1, A2))
  }

  "D" should "merge A" in {
    D.merge(A) should contain (AD)
  }

  it should "not flow into A" in {
    D.flowsInto(A) should be (false)
  }

  "E" should "flow into A" in {
    E.flowsInto(A) should be (true)
  }

  "F" should "split A" in {
    A.splitBy(F) should contain ((A1, A2))
  }

  it should "flow into A" in {
    F.flowsInto(A) should be (true)
  }

  "G" should "split AD" in {
    AD.splitBy(G) should contain ((A, D))
  }

  it should "not flow into A" in {
    G.flowsInto(A) should be (false)
  }

  it should "flow into D" in {
    G.flowsInto(D) should be (true)
  }
}
