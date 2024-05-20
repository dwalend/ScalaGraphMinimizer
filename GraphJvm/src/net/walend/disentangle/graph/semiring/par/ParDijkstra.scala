package net.walend.disentangle.graph.semiring.par

import net.walend.disentangle.graph.semiring.{Dijkstra, FewestNodes, FirstStepsTrait, SemiringSupport}

import scala.collection.parallel.immutable.ParSeq

/**
 * An implementation of Dijkstra's algorithm for general graph minimization for both single-source and single-sink.
 *
 * @author dwalend
 * @since v0.1.0
 */

object ParDijkstra {

  /**
   * O(n&#94;2 ln(n) + na) / cores
   */
  def allPairsLeastPaths[Node,EdgeLabel,Label,Key](
                                                    edges: Iterable[(Node, Node, EdgeLabel)],
                                                    support: SemiringSupport[Label, Key],
                                                    labelForEdge: (Node, Node, EdgeLabel) => Label,
                                                    nodeOrder: Seq[Node] = Seq.empty
                                                  ):ParSeq[(Node, Node, Label)] = {
    val labelDigraph = Dijkstra.createLabelDigraph(edges, support, labelForEdge, nodeOrder)

    //profiler blamed both flatten and fold of IndexedSets as trouble
    ParSeq.fromSpecific(labelDigraph.innerNodes).flatMap(source => Dijkstra.dijkstraSingleSource(labelDigraph, support)(source))
  }

  def allPairsShortestPaths[Node,EdgeLabel](
                                             edges:Iterable[(Node,Node,EdgeLabel)],
                                             nodeOrder:Seq[Node] = Seq.empty
                                           ):ParSeq[(Node,Node,Option[FirstStepsTrait[Node, Int]])] = {
    val support = Dijkstra.defaultSupport[Node]
    allPairsLeastPaths(edges, support, support.convertEdgeToLabel(FewestNodes.convertEdgeToLabel), nodeOrder)
  }

}

