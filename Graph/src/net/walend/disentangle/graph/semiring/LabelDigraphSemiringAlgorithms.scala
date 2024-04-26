package net.walend.disentangle.graph.semiring

import net.walend.disentangle.graph.semiring.Brandes.BrandesSteps
import net.walend.disentangle.graph.{IndexedLabelDigraph, LabelDigraph}

/**
 * @since v0.2.1
 *
 *        Helper methods for LabelDigraphs
 */
object LabelDigraphSemiringAlgorithms {
  extension[Node, Label] (self: LabelDigraph[Node, Label]) {

    def allPairsShortestPaths: Seq[(Node, Node, Option[FirstStepsTrait[Node, Int]])] = self match {
      case indexed: IndexedLabelDigraph[Node, Label] => Dijkstra.allPairsShortestPaths(indexed.edges, indexed.nodes.asSeq)
      case _ => Dijkstra.allPairsShortestPaths(self.edges)
    }

    def allPairsLeastPaths[SemiringLabel, Key](support: SemiringSupport[SemiringLabel, Key],
                                               labelForEdge: (Node, Node, Label) => SemiringLabel): Seq[(Node, Node, SemiringLabel)] = self match {
      case indexed: IndexedLabelDigraph[Node, Label] => Dijkstra.allPairsLeastPaths(indexed.edges, support, labelForEdge, indexed.nodes.asSeq)
      case _ => Dijkstra.allPairsLeastPaths(self.edges, support, labelForEdge)
    }

    def allLeastPathsAndBetweenness[CoreLabel, Key](coreSupport: SemiringSupport[CoreLabel, Key] = FewestNodes,
                                                    labelForEdge: (Node, Node, Label) => CoreLabel = FewestNodes.edgeToLabelConverter): (IndexedSeq[(Node, Node, Option[BrandesSteps[Node, CoreLabel]])], Map[Node, Double]) = self match {
      case indexed: IndexedLabelDigraph[Node, Label] => Brandes.allLeastPathsAndBetweenness(indexed.edges, indexed.nodes.asSeq, coreSupport, labelForEdge)
      case _ => Brandes.allLeastPathsAndBetweenness(self.edges, coreSupport = coreSupport, labelForEdge = labelForEdge)
    }
  }
}
