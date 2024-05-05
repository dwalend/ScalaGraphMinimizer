package net.walend.disentangle.graph.semiring

import munit.FunSuite
import net.walend.disentangle.graph.SomeGraph
import net.walend.disentangle.graph.semiring.LabelDigraphSemiringAlgorithms.allPairsShortestPaths

/**
 *
 *
 * @author dwalend
 * @since v0.1.0
 */
class AllPathsFirstStepsTest extends FunSuite {
  import SomeGraph._

  val support: AllPathsFirstSteps[String, Int, Int] = new AllPathsFirstSteps[String,Int,Int](FewestNodes)

  val expectedArcs: Set[(String, String, Option[FirstStepsTrait[String, Int]])] = Set[(String,String,Option[FirstStepsTrait[String,Int]])](
    (A,A,Some(FirstSteps(0,Set()))),
    (A,B,Some(FirstSteps(1,Set(B)))),
    (A,C,Some(FirstSteps(2,Set(B)))),
    (A,D,Some(FirstSteps(3,Set(B)))),
    (A,E,Some(FirstSteps(4,Set(B)))),
    (A,F,Some(FirstSteps(5,Set(B)))),
    (A,H,Some(FirstSteps(5,Set(B)))),
    (B,B,Some(FirstSteps(0,Set()))),
    (B,C,Some(FirstSteps(1,Set(C)))),
    (B,D,Some(FirstSteps(2,Set(C)))),
    (B,E,Some(FirstSteps(3,Set(C)))),
    (B,F,Some(FirstSteps(4,Set(C)))),
    (B,H,Some(FirstSteps(4,Set(C)))),
    (C,B,Some(FirstSteps(3,Set(D)))),
    (C,C,Some(FirstSteps(0,Set()))),
    (C,D,Some(FirstSteps(1,Set(D)))),
    (C,E,Some(FirstSteps(2,Set(D)))),
    (C,F,Some(FirstSteps(3,Set(D)))),
    (C,H,Some(FirstSteps(3,Set(D)))),
    (D,B,Some(FirstSteps(2,Set(E)))),
    (D,C,Some(FirstSteps(3,Set(E)))),
    (D,D,Some(FirstSteps(0,Set()))),
    (D,E,Some(FirstSteps(1,Set(E)))),
    (D,F,Some(FirstSteps(2,Set(E)))),
    (D,H,Some(FirstSteps(2,Set(E)))),
    (E,B,Some(FirstSteps(1,Set(B)))),
    (E,C,Some(FirstSteps(2,Set(B, H)))),
    (E,D,Some(FirstSteps(3,Set(B, H)))),
    (E,E,Some(FirstSteps(0,Set()))),
    (E,F,Some(FirstSteps(1,Set(F)))),
    (E,H,Some(FirstSteps(1,Set(H)))),
    (F,F,Some(FirstSteps(0,Set()))),
    (G,G,Some(FirstSteps(0,Set()))),
    (H,B,Some(FirstSteps(4,Set(C)))),
    (H,C,Some(FirstSteps(1,Set(C)))),
    (H,D,Some(FirstSteps(2,Set(C)))),
    (H,E,Some(FirstSteps(3,Set(C)))),
    (H,F,Some(FirstSteps(4,Set(C)))),
    (H,H,Some(FirstSteps(0,Set())))
  )

  test("The Floyd-Warshall algorithm should produce the correct label graph for Somegraph") {

    val labelGraph = FloydWarshall.allPairsLeastPaths(testDigraph.edges,Seq.from(testDigraph.nodes),support,support.convertEdgeToLabelFunc[String](FewestNodes.convertEdgeToLabel))

    assertEquals(Set.from(labelGraph.edges),expectedArcs)
  }

  test("Dijkstra's algorithm should produce the correct label graph for Somegraph") {

    val arcs = Dijkstra.allPairsLeastPaths(testDigraph.edges, support, support.convertEdgeToLabelFunc[String](FewestNodes.convertEdgeToLabel), Seq.from(testDigraph.nodes))

    assertEquals(arcs.size,expectedArcs.size)
    assertEquals(Set.from(arcs),expectedArcs)
  }

  test("Dijkstra's algorithm should produce the correct label graph for Somegraph using default values for the algorithm") {

    val arcs = Dijkstra.allPairsShortestPaths(testDigraph.edges,Seq.from(testDigraph.nodes))

    assertEquals(arcs.size,expectedArcs.size)
    assertEquals(Set.from(arcs),expectedArcs)
  }

  test("Dijkstra's algorithm should produce the correct label graph for Somegraph when used with the implicit extension method") {

    val labels = testDigraph.allPairsShortestPaths

    assertEquals(labels.size, expectedArcs.size)
    assertEquals(Set.from(labels),expectedArcs)
  }
  
  test("AllPathsFirstSteps and the Floyd-Warshall algorithm should produce the correct subgraphs for minimal paths and the correct minimal paths") {

    val expectedSubgraphs = Map(
      (A,A) -> Set(),
      (A,B) -> Set((A,B)),
      (A,C) -> Set((A,B), (B,C)),
      (A,D) -> Set((A,B), (B,C), (C,D)),
      (A,E) -> Set((A,B), (B,C), (C,D), (D,E)),
      (A,F) -> Set((A,B), (B,C), (C,D), (D,E), (E,F)),
      (A,H) -> Set((E,H), (A,B), (B,C), (C,D), (D,E)),
      (B,B) -> Set(),
      (B,C) -> Set((B,C)),
      (B,D) -> Set((B,C), (C,D)),
      (B,E) -> Set((B,C), (C,D), (D,E)),
      (B,F) -> Set((B,C), (C,D), (D,E), (E,F)),
      (B,H) -> Set((B,C), (C,D), (D,E), (E,H)),
      (C,B) -> Set((C,D), (D,E), (E,B)),
      (C,C) -> Set(),
      (C,D) -> Set((C,D)),
      (C,E) -> Set((C,D), (D,E)),
      (C,F) -> Set((C,D), (D,E), (E,F)),
      (C,H) -> Set((C,D), (D,E), (E,H)),
      (D,B) -> Set((D,E), (E,B)),
      (D,C) -> Set((E,H), (H,C), (B,C), (E,B), (D,E)),
      (D,D) -> Set(),
      (D,E) -> Set((D,E)),
      (D,F) -> Set((D,E), (E,F)),
      (D,H) -> Set((D,E), (E,H)),
      (E,B) -> Set((E,B)),
      (E,C) -> Set((E,B), (E,H), (B,C), (H,C)),
      (E,D) -> Set((E,H), (H,C), (B,C), (C,D), (E,B)),
      (E,E) -> Set(),
      (E,F) -> Set((E,F)),
      (E,H) -> Set((E,H)),
      (F,F) -> Set(),
      (G,G) -> Set(),
      (H,B) -> Set((H,C), (C,D), (D,E), (E,B)),
      (H,C) -> Set((H,C)),
      (H,D) -> Set((H,C), (C,D)),
      (H,E) -> Set((H,C), (C,D), (D,E)),
      (H,F) -> Set((H,C), (C,D), (D,E), (E,F)),
      (H,H) -> Set()
    )
    
    val expectedShortPaths = Map(
      (A, A) -> Seq(Seq(A)),
      (A, B) -> Seq(Seq(A, B)),
      (A, C) -> Seq(Seq(A, B, C)),
      (A, D) -> Seq(Seq(A, B, C, D)),
      (A, E) -> Seq(Seq(A, B, C, D, E)),
      (A, F) -> Seq(Seq(A, B, C, D, E, F)),
      (A, H) -> Seq(Seq(A, B, C, D, E, H)),
      (B, B) -> Seq(Seq(B)),
      (B, C) -> Seq(Seq(B, C)),
      (B, D) -> Seq(Seq(B, C, D)),
      (B, E) -> Seq(Seq(B, C, D, E)),
      (B, F) -> Seq(Seq(B, C, D, E, F)),
      (B, H) -> Seq(Seq(B, C, D, E, H)),
      (C, B) -> Seq(Seq(C, D, E, B)),
      (C, C) -> Seq(Seq(C)),
      (C, D) -> Seq(Seq(C, D)),
      (C, E) -> Seq(Seq(C, D, E)),
      (C, F) -> Seq(Seq(C, D, E, F)),
      (C, H) -> Seq(Seq(C, D, E, H)),
      (D, B) -> Seq(Seq(D, E, B)),
      (D, C) -> Seq(Seq(D, E, B, C), Seq(D, E, H, C)),
      (D, D) -> Seq(Seq(D)),
      (D, E) -> Seq(Seq(D, E)),
      (D, F) -> Seq(Seq(D, E, F)),
      (D, H) -> Seq(Seq(D, E, H)),
      (E, B) -> Seq(Seq(E, B)),
      (E, C) -> Seq(Seq(E, B, C), Seq(E, H, C)),
      (E, D) -> Seq(Seq(E, B, C, D), Seq(E, H, C, D)),
      (E, E) -> Seq(Seq(E)),
      (E, F) -> Seq(Seq(E, F)),
      (E, H) -> Seq(Seq(E, H)),
      (F, F) -> Seq(Seq(F)),
      (G, G) -> Seq(Seq(G)),
      (H, B) -> Seq(Seq(H, C, D, E, B)),
      (H, C) -> Seq(Seq(H, C)),
      (H, D) -> Seq(Seq(H, C, D)),
      (H, E) -> Seq(Seq(H, C, D, E)),
      (H, F) -> Seq(Seq(H, C, D, E, F)),
      (H, H) -> Seq(Seq(H))
    )
    
    val labelGraph = FloydWarshall.allPairsLeastPaths(testDigraph.edges,Seq.from(testDigraph.nodes),support,support.convertEdgeToLabelFunc[String](FewestNodes.convertEdgeToLabel))

    val subgraphs = labelGraph.edges.map(edge => ((edge._1,edge._2),support.subgraphEdges(labelGraph,edge._1,edge._2).map(edge => (edge.from.value,edge.to.value)))).toMap

    assertEquals(subgraphs, expectedSubgraphs)

    val shortestPaths = labelGraph.edges.map(edge => ((edge._1,edge._2),support.allLeastPaths(labelGraph,edge._1,edge._2)))

    val shortestOuterPaths:Iterable[((String,String),Seq[Seq[String]])] = for(shortestPathsBetweenNodes <- shortestPaths) yield {
      val shortOuterPaths = for(shortPath <- shortestPathsBetweenNodes._2) yield {
        shortPath.map(node => node.value)
      }
      (shortestPathsBetweenNodes._1,shortOuterPaths)
    }
    val pairsToShortestOuter = shortestOuterPaths.toMap

    assertEquals(pairsToShortestOuter,expectedShortPaths)
  }
}
