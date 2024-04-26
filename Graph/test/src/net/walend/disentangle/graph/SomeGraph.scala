package net.walend.disentangle.graph

/**
 * An example graph for eyeball testing
 *
 * @author dwalend
 * @since v0.1.0
 */

object SomeGraph {

  //exciting example graph

  val A = "A"
  val B = "B"
  val C = "C"
  val D = "D"
  val E = "E"
  val F = "F"
  val G = "G"
  val H = "H"

  val testNodes: Seq[String] = Seq(A,B,C,D,E,F,G,H)

  val ab: (String, String, String) = (A,B,"ab")
  val bc: (String, String, String) = (B,C,"bc")
  val cd: (String, String, String) = (C,D,"cd")
  val de: (String, String, String) = (D,E,"de")
  val ef: (String, String, String) = (E,F,"ef")
  val eb: (String, String, String) = (E,B,"eb")
  val eh: (String, String, String) = (E,H,"eh")
  val hc: (String, String, String) = (H,C,"hc")

  val testEdges: Seq[(String, String, String)] = Seq(ab,bc,cd,de,ef,eb,eh,hc)

  val testDigraph:IndexedLabelDigraph[String,String] = AdjacencyLabelDigraph(testEdges,testNodes,"")

  val af: (String, String, String) = (A,F,"af")
  val be: (String, String, String) = (B,E,"be")

  val brandesTestEdges: Seq[(String, String, String)] = Seq(ab,bc,cd,de,ef,af,be)

  val testLabeledUndirectedEdges: Seq[(NodePair[String], String)] = testEdges.map(x => (NodePair(x._1,x._2),x._3))

  val testLabelUndigraph: AdjacencyLabelUndigraph[String, String] = AdjacencyLabelUndigraph(testLabeledUndirectedEdges,testNodes)

  val testUndirectedEdges: Seq[NodePair[String]] = testLabeledUndirectedEdges.map(_._1)

  val testUndigraph: AdjacencyUndigraph[String] = AdjacencyUndigraph(testUndirectedEdges,testNodes)
}
