package net.walend.disentangle.graph.semiring

import net.walend.disentangle.graph.semiring.Brandes.BrandesSteps
import net.walend.disentangle.graph.{IndexedLabelDigraph, IndexedLabelUndigraph, LabelUndigraph}

import scala.collection.immutable.Iterable
/**
 * @since v0.2.1
 *
 * Helper methods for LabelUndigraphs
 */
object LabelUndigraphSemiringAlgorithms {
  extension[Node, Label] (self: LabelUndigraph[Node, Label]) {

    def allPairsShortestPaths: Seq[(Node, Node, Option[FirstStepsTrait[Node, Int]])] = self match {
      case indexed: IndexedLabelUndigraph[Node, Label] => Dijkstra.allPairsShortestPaths(diEdges, indexed.nodes.asSeq)
      case _ => Dijkstra.allPairsShortestPaths(diEdges)
    }

    def allPairsLeastPaths[SemiringLabel, Key](support: SemiringSupport[SemiringLabel, Key],
                                               labelForEdge: (Node, Node, Label) => SemiringLabel): Seq[(Node, Node, SemiringLabel)] = self match {
      case indexed: IndexedLabelDigraph[_, _] => Dijkstra.allPairsLeastPaths(diEdges, support, labelForEdge, indexed.nodes.asSeq)
      case _ => Dijkstra.allPairsLeastPaths(diEdges, support, labelForEdge)
    }

    def allLeastPathsAndBetweenness[CoreLabel, Key](coreSupport: SemiringSupport[CoreLabel, Key] = FewestNodes,
                                                    labelForEdge: (Node, Node, Label) => CoreLabel = FewestNodes.edgeToLabelConverter): (IndexedSeq[(Node, Node, Option[BrandesSteps[Node, CoreLabel]])], Map[Node, Double]) = {
      val digraphResult = self match {
        case indexed: IndexedLabelDigraph[_, _] => Brandes.allLeastPathsAndBetweenness(diEdges, indexed.nodes.asSeq, coreSupport, labelForEdge)
        case _ => Brandes.allLeastPathsAndBetweenness(diEdges, coreSupport = coreSupport, labelForEdge = labelForEdge)
      }
      correctForUndigraph(digraphResult)
    }

    private def diEdges: Iterable[(Node, Node, Label)] = {
      self.edges.map(e => (e._1._1, e._1._2, e._2)) ++ self.edges.map(e => (e._1._2, e._1._1, e._2))
    }

    private def correctForUndigraph[CoreLabel](
                                        digraphResult: (IndexedSeq[(Node, Node, Option[BrandesSteps[Node, CoreLabel]])], Map[Node, Double])
                                      ): (IndexedSeq[(Node, Node, Option[BrandesSteps[Node, CoreLabel]])], Map[Node, Double]) = {
      val halfMap = digraphResult._2.map(x => (x._1, x._2 / 2))
      (digraphResult._1, halfMap)
    }

  }
}
