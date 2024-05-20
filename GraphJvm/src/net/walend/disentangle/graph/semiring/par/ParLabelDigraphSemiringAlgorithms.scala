package net.walend.disentangle.graph.semiring.par

import net.walend.disentangle.graph.semiring.Brandes.BrandesSteps
import net.walend.disentangle.graph.semiring.{FewestNodes, FirstStepsTrait, SemiringSupport}
import net.walend.disentangle.graph.{IndexedLabelDigraph, LabelDigraph}

import scala.collection.parallel.immutable.{ParMap, ParSeq}

/**
 * @since v0.2.1
 *
 *        Helper methods for LabelDigraphs
 */
object ParLabelDigraphSemiringAlgorithms {
  extension[Node, Label] (self: LabelDigraph[Node, Label]) {

    def parAllPairsShortestPaths: ParSeq[(Node, Node, Option[FirstStepsTrait[Node, Int]])] = self match {
      case indexed: IndexedLabelDigraph[Node, Label] => ParDijkstra.allPairsShortestPaths(indexed.edges, indexed.nodes.asSeq)
      case _ => ParDijkstra.allPairsShortestPaths(self.edges)
    }

    def parAllPairsLeastPaths[SemiringLabel, Key](support: SemiringSupport[SemiringLabel, Key],
                                                  labelForEdge: (Node, Node, Label) => SemiringLabel): ParSeq[(Node, Node, SemiringLabel)] = self match {
      case indexed: IndexedLabelDigraph[Node, Label] => ParDijkstra.allPairsLeastPaths(indexed.edges, support, labelForEdge, indexed.nodes.asSeq)
      case _ => ParDijkstra.allPairsLeastPaths(self.edges, support, labelForEdge)
    }

    def parAllLeastPathsAndBetweenness[CoreLabel, Key](
                                                     coreSupport: SemiringSupport[CoreLabel, Key] = FewestNodes,
                                                      labelForEdge: (Node, Node, Label) => CoreLabel = FewestNodes.edgeToLabelConverter
                                                   ): (ParSeq[(Node, Node, Option[BrandesSteps[Node, CoreLabel]])], ParMap[Node, Double]) = self match {
      case indexed: IndexedLabelDigraph[Node, Label] => ParBrandes.allLeastPathsAndBetweenness(indexed.edges, indexed.nodes.asSeq, coreSupport, labelForEdge)
      case _ => ParBrandes.allLeastPathsAndBetweenness(self.edges, coreSupport = coreSupport, labelForEdge = labelForEdge)
    }
  }
}
