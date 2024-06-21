package net.walend.disentangle.graph.semiring

import net.walend.disentangle.graph.semiring.Brandes.BrandesSteps
import net.walend.disentangle.graph.{Digraph, IndexedGraph, Tuple2Digraph}

import scala.collection.immutable.Iterable

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

    def allPairsLeastPaths[SemiringLabel, Key](support: SemiringSupport[SemiringLabel, Key],
                                               labelForEdge: (Node, Node) => SemiringLabel): Seq[(Node, Node, SemiringLabel)] = {
      def labelForEdgeWithUnit(from: Node, to: Node, edge: Unit): SemiringLabel = labelForEdge(from, to)

      self match {
        case indexed: IndexedGraph[Node] => Dijkstra.allPairsLeastPaths(unitEdgeValues, support, labelForEdgeWithUnit, indexed.nodes.asSeq)
        case _ => Dijkstra.allPairsLeastPaths(unitEdgeValues, support, labelForEdgeWithUnit, self.nodes.toSeq)
      }
    }

    def allShortestPathsAndBetweenness(): (IndexedSeq[(Node, Node, Option[BrandesSteps[Node, FewestNodes.Label]])], Map[Node, Double]) = {
      def brandesDefaultLabelForEdge(from: Node, to: Node): FewestNodes.Label = FewestNodes.edgeToLabelConverter(from, to, ())

      allLeastPathsAndBetweenness(FewestNodes,brandesDefaultLabelForEdge)
    }

    def allLeastPathsAndBetweenness[CoreLabel, Key](coreSupport: SemiringSupport[CoreLabel, Key],
                                                    labelForEdge: (Node, Node) => CoreLabel): (IndexedSeq[(Node, Node, Option[BrandesSteps[Node, CoreLabel]])], Map[Node, Double]) = {

      def labelForEdgeWithUnit(from: Node, to: Node, edge: Unit): coreSupport.Label = labelForEdge(from, to)

      self match {
        case indexed: IndexedGraph[Node] => Brandes.allLeastPathsAndBetweenness(unitEdgeValues, indexed.nodes.asSeq, coreSupport, labelForEdgeWithUnit)
        case _ => Brandes.allLeastPathsAndBetweenness(unitEdgeValues, self.nodes.toSeq, coreSupport, labelForEdgeWithUnit)
      }
    }
  }
}
