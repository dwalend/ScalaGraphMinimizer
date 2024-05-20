package net.walend.disentangle.graph.semiring.par

import net.walend.disentangle.graph.semiring.Brandes.BrandesSteps
import net.walend.disentangle.graph.{IndexedLabelDigraph, IndexedLabelUndigraph, LabelUndigraph}
import net.walend.disentangle.graph.semiring.{FewestNodes, FirstStepsTrait, SemiringSupport}

import scala.collection.immutable.Iterable
import scala.collection.parallel.immutable.{ParMap, ParSeq}

/**
 * @since v0.2.1
 *
 * Helper methods for LabelUndigraphs
 */
object ParLabelUndigraphSemiringAlgorithms {
  extension[Node, Label] (self: LabelUndigraph[Node, Label]) {

    def parAllPairsShortestPaths: ParSeq[(Node, Node, Option[FirstStepsTrait[Node, Int]])] = self match {
      case indexed: IndexedLabelUndigraph[Node, Label] => ParDijkstra.allPairsShortestPaths(diEdges, indexed.nodes.asSeq)
      case _ => ParDijkstra.allPairsShortestPaths(diEdges)
    }

    def parAllPairsLeastPaths[SemiringLabel, Key](support: SemiringSupport[SemiringLabel, Key],
                                               labelForEdge: (Node, Node, Label) => SemiringLabel): ParSeq[(Node, Node, SemiringLabel)] = self match {
      case indexed: IndexedLabelDigraph[_, _] => ParDijkstra.allPairsLeastPaths(diEdges, support, labelForEdge, indexed.nodes.asSeq)
      case _ => ParDijkstra.allPairsLeastPaths(diEdges, support, labelForEdge)
    }

    def parAllLeastPathsAndBetweenness[CoreLabel, Key](coreSupport: SemiringSupport[CoreLabel, Key] = FewestNodes,
                                                    labelForEdge: (Node, Node, Label) => CoreLabel = FewestNodes.edgeToLabelConverter): (ParSeq[(Node, Node, Option[BrandesSteps[Node, CoreLabel]])], ParMap[Node, Double]) = {
      val digraphResult: (ParSeq[(Node, Node, Option[BrandesSteps[Node, CoreLabel]])], ParMap[Node, Double]) = self match {
        case indexed: IndexedLabelDigraph[_, _] => ParBrandes.allLeastPathsAndBetweenness(diEdges, indexed.nodes.asSeq, coreSupport, labelForEdge)
        case _ => ParBrandes.allLeastPathsAndBetweenness(diEdges, coreSupport = coreSupport, labelForEdge = labelForEdge)
      }
      correctForUndigraph(digraphResult)
    }

    //todo call method from non-parallel? Maybe this is really part of Undigraph ?
    private def diEdges: Iterable[(Node, Node, Label)] = {
      self.edges.map(e => (e._1._1, e._1._2, e._2)) ++ self.edges.map(e => (e._1._2, e._1._1, e._2))
    }

    //todo call method from non-parallel? Maybe this is really part of Brandes ?
    private def correctForUndigraph[CoreLabel](
                                        digraphResult: (ParSeq[(Node, Node, Option[BrandesSteps[Node, CoreLabel]])], ParMap[Node, Double])
                                      ) = {
      val halfMap = digraphResult._2.map(x => (x._1, x._2 / 2))
      (digraphResult._1, halfMap)
    }

  }
}
