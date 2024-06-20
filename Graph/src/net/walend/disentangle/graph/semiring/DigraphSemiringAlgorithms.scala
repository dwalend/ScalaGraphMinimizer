package net.walend.disentangle.graph.semiring

import net.walend.disentangle.graph.semiring.Brandes.BrandesSteps
import net.walend.disentangle.graph.{Digraph, IndexedGraph, Tuple2Digraph}

import scala.collection.immutable

/**
 * @since v0.3.0
 *
 *        Helper methods for LabelDigraphs
 */
object DigraphSemiringAlgorithms {
  extension[Node] (self: Tuple2Digraph[Node]) {
    private def unitEdgeValues: Iterable[(Node, Node, Unit)] = self.edges.map(e => (e._1,e._2,()))

    def allPairsShortestPaths: Seq[(Node, Node, Option[FirstStepsTrait[Node, Int]])] = {
      self match {
        case indexed: IndexedGraph[Node] => Dijkstra.allPairsShortestPaths(unitEdgeValues, indexed.nodes.asSeq)
        case _ => Dijkstra.allPairsShortestPaths(unitEdgeValues,self.nodes.toSeq)
      }
    }
     /*
    def allPairsLeastPaths[SemiringLabel, Key](support: SemiringSupport[SemiringLabel, Key],
                                               labelForEdge: (Node, Node) => SemiringLabel): Seq[(Node, Node, SemiringLabel)] = {
      def labelForEdgeWithUnit(from: Node, to: Node, edge: Unit): SemiringLabel = labelForEdge(from, to)

      Dijkstra.allPairsLeastPaths(unitEdgeValues, support, labelForEdgeWithUnit, self.nodes.asSeq)
    }

    def allLeastPathsAndBetweenness[CoreLabel, Key](coreSupport: SemiringSupport[CoreLabel, Key] = FewestNodes,
                                                    labelForEdge: (Node, Node) => CoreLabel = FewestNodes.edgeToLabelConverter): (IndexedSeq[(Node, Node, Option[BrandesSteps[Node, CoreLabel]])], Map[Node, Double]) = {
      Brandes.allLeastPathsAndBetweenness(unitEdgeValues, self.nodes.asSeq, coreSupport, labelForEdge)
    }

      */
  }
}
