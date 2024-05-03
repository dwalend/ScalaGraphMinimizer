package net.walend.disentangle.graph

import munit.FunSuite
import net.walend.disentangle.graph.SomeGraph.testDigraph

import scala.collection.immutable
/**
 *
 *
 * @author David Walend
 * @since v0.0.0
 */
class AdjacencyLabelDigraphTest extends FunSuite {
  import SomeGraph._

  test("Edge API should work") {
    val abExpected = Option(("A","B","ab"))
    val zeroOneResult = testDigraph.edgeForIndex(0,1).map(edge => (edge.from.value,edge.to.value,edge.label))
    assertEquals(abExpected,zeroOneResult)
    val abResult = testDigraph.edge("A","B").map(edge => (edge.from.value,edge.to.value,edge.label))
    assertEquals(abExpected,abResult)

    val innerNodes: IndexedSet[testDigraph.InnerNodeType] = testDigraph.innerNodes
    val innerEdges = testDigraph.innerEdges.toSet
    val innerFromTo = innerEdges.map(ie => (ie.from,ie.to)).toSet

    for(from <- innerNodes; to <- innerNodes) {
      val edgeFromIndexes: Option[testDigraph.InnerEdgeType] = testDigraph.edgeForIndex(from.index,to.index)
      edgeFromIndexes match{
        case Some(ie) =>
          assert(innerEdges.contains(ie))
          assertEquals(ie.from,from)
          assertEquals(ie.to,to)
        case None => assert(!innerFromTo.contains((from,to)))
      }
    }

    for(from <- innerNodes; to <- innerNodes) {
      val maybeInnerEdge: Option[testDigraph.InnerEdgeType] = testDigraph.edge(from,to)
      maybeInnerEdge match{
        case Some(ie) =>
          assert(innerEdges.contains(ie))
          assertEquals(ie.from,from)
          assertEquals(ie.to,to)
        case None => assert(!innerFromTo.contains((from,to)))
      }
    }

    val nodes = testDigraph.nodes
    val edges = testDigraph.edges.toSet
    val fromTos = edges.map(e => (e._1,e._2))

    for (from <- nodes; to <- nodes) {
      val maybeInnerEdge: Option[testDigraph.InnerEdgeType] = testDigraph.edge(from, to)
      maybeInnerEdge match {
        case Some(ie) =>
          assert(innerEdges.contains(ie))
          assertEquals(ie.from.value, from)
          assertEquals(ie.to.value, to)
        case None => assert(!fromTos.contains((from, to)))
      }
    }
  }

}
