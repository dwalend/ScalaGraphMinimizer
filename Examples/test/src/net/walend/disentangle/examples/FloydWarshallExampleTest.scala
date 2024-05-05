package net.walend.disentangle.examples

import munit.FunSuite
import net.walend.disentangle.graph.IndexedLabelDigraph
import net.walend.disentangle.graph.mutable.MatrixLabelDigraph
import net.walend.disentangle.graph.semiring.{AllPathsFirstSteps, FirstSteps, FirstStepsTrait, FloydWarshall}

/**
 *
 *
 * @author dwalend
 * @since v0.2.0
 */
class FloydWarshallExampleTest extends FunSuite {
  import net.walend.disentangle.graph.SomeGraph.*

  val support: AllPathsFirstSteps[String, Int, Int] = FloydWarshall.defaultSupport[String]

  test("The Floyd-Warshall example should produce expected results") {

    val expectedShortPathGraph = MatrixLabelDigraph(
      edges = Vector((A,A,Some(FirstSteps(0,Set()))),
                      (A,B,Some(FirstSteps(1,Set(B)))),
                      (A,C,Some(FirstSteps(2,Set(B)))),
                      (A,D,Some(FirstSteps(3,Set(B)))),
                      (A,E,Some(FirstSteps(4,Set(B)))),
                      (A,H,Some(FirstSteps(5,Set(B)))),
                      (A,F,Some(FirstSteps(5,Set(B)))),
                      (B,B,Some(FirstSteps(0,Set()))),
                      (B,C,Some(FirstSteps(1,Set(C)))),
                      (B,D,Some(FirstSteps(2,Set(C)))),
                      (B,E,Some(FirstSteps(3,Set(C)))),
                      (B,H,Some(FirstSteps(4,Set(C)))),
                      (B,F,Some(FirstSteps(4,Set(C)))),
                      (C,B,Some(FirstSteps(3,Set(D)))),
                      (C,C,Some(FirstSteps(0,Set()))),
                      (C,D,Some(FirstSteps(1,Set(D)))),
                      (C,E,Some(FirstSteps(2,Set(D)))),
                      (C,H,Some(FirstSteps(3,Set(D)))),
                      (C,F,Some(FirstSteps(3,Set(D)))),
                      (D,B,Some(FirstSteps(2,Set(E)))),
                      (D,C,Some(FirstSteps(3,Set(E)))),
                      (D,D,Some(FirstSteps(0,Set()))),
                      (D,E,Some(FirstSteps(1,Set(E)))),
                      (D,H,Some(FirstSteps(2,Set(E)))),
                      (D,F,Some(FirstSteps(2,Set(E)))),
                      (E,B,Some(FirstSteps(1,Set(B)))),
                      (E,C,Some(FirstSteps(2,Set(B, H)))),
                      (E,D,Some(FirstSteps(3,Set(B, H)))),
                      (E,E,Some(FirstSteps(0,Set()))),
                      (E,H,Some(FirstSteps(1,Set(H)))),
                      (E,F,Some(FirstSteps(1,Set(F)))),
                      (H,B,Some(FirstSteps(4,Set(C)))),
                      (H,C,Some(FirstSteps(1,Set(C)))),
                      (H,D,Some(FirstSteps(2,Set(C)))),
                      (H,E,Some(FirstSteps(3,Set(C)))),
                      (H,H,Some(FirstSteps(0,Set()))),
                      (H,F,Some(FirstSteps(4,Set(C)))),
                      (F,F,Some(FirstSteps(0,Set())))
      ),
      nodes = Seq(A, B, C, D, E, H, F),
      noEdgeExistsValue = None
    )


    val shortPathGraph: IndexedLabelDigraph[String, Option[FirstStepsTrait[String, Int]]] = FloydWarshallExample.simpleShortPathGraph

    assert(shortPathGraph == expectedShortPathGraph)
    
    val expectedSubgraphEdges: Set[shortPathGraph.InnerEdgeType] = Set(
      shortPathGraph.edge(H,C),
      shortPathGraph.edge(E,B),
      shortPathGraph.edge(C,D),
      shortPathGraph.edge(E,H),
      shortPathGraph.edge(B,C)
    ).filter(_.isDefined).map(_.get)

    val subgraphEdges = FloydWarshallExample.subgraph

    assert(subgraphEdges == expectedSubgraphEdges)

    val expectedPaths = Vector(
      List(shortPathGraph.innerNode(E).get, shortPathGraph.innerNode(B).get, shortPathGraph.innerNode(C).get, shortPathGraph.innerNode(D).get),
      List(shortPathGraph.innerNode(E).get, shortPathGraph.innerNode(H).get, shortPathGraph.innerNode(C).get, shortPathGraph.innerNode(D).get)
    )
    
    val paths = FloydWarshallExample.paths

    assert(paths == expectedPaths)
  }
}
