package net.walend.disentangle.graph

import scala.collection.{GenMap, GenSeq, GenTraversable}

/**
  * Provides constant-time access for edges of a node.
  *
  * The constructor is O(n + a ln(n))
  *
  * @author dwalend
  * @since v0.2.1
  */
//todo for noEdgeExistsLabel, make it a function => Label, and throw an exception by default. Also in Digraphs.
class AdjacencyLabelUndigraph[Node,Label](outNodes:IndexedSet[Node], //provides the master index values for each node.
                                          outEdges:Vector[IndexedSet[(NodePair[Node],Label)]], // (i) is the edges for node i, (j) is the NodePair[node,node],edge pair to reach that second node.
                                          val noEdgeExistsLabel:Label //value for no edge
                                       ) extends IndexedLabelUndigraph[Node,Label] {

  val inNodes:IndexedSet[InNode] =outNodes.zipWithIndex.map(x => InNode(x._1,x._2))
  val nodeToInNode:Map[Node,InNode] = inNodes.map(x => x.value -> x).toMap

  //todo really should be a Set, not an IndexedSet
  def neighborSet(indexedSet:IndexedSet[OuterEdgeType]):IndexedSet[InnerEdgeType] = {
    indexedSet.map(e => (NodePair(nodeToInNode(e._1._1),nodeToInNode(e._1._2)),e._2))
  }

  //todo really should be a Set, not an IndexedSet
  val inEdges:Vector[IndexedSet[InnerEdgeType]] = outEdges.map(neighborSet)

  def nodes = outNodes

  override def nodeCount: Int = outNodes.size

  type InnerNodeType = InNode

  case class InNode(override val value:Node,override val index:Int) extends this.InnerIndexedNodeTrait {

    override def edges: IndexedSet[InnerEdgeType] = {
      inEdges(index)
    }

    override def hashCode(): Int = index

    override def equals(thing: Any): Boolean = {
      thing match {
        case inNode:InNode => inNode.index == index
        case _ => false
      }
    }

  }

  /**
    * O(ln(n))
    *
    * @return Some inner node if it exists in the digraph or None
    */
  override def innerNode(value: Node): Option[InNode] = {
    nodeToInNode.get(value)
  }

  /**
    * O(1)
    *
    * @return InnerNode representation of all of the nodes in the graph.
    */
  override def innerNodes: IndexedSet[InNode] = inNodes

  override type InnerEdgeType = (NodePair[InNode],Label)

  /**
    * @return A Traversable of the edges as represented in the graph
    */
  override def innerEdges:Vector[InnerEdgeType] = inEdges.flatten

  /**
    * O(n&#94;2)
    *
    * @return All of the edges in the graph
    */
  override def edges: Seq[OuterEdgeType] = outEdges.flatten

  /**
    * O(n)
    *
    * @return the edge between start and end or noEdgeExistsValue
    */
  override def label(between:NodePair[InnerNodeType]):Label = {

    val indexedSet = inEdges(between._1.index).filter(x => x._1.contains(between._2))
    indexedSet.size match {
      case 0 => noEdgeExistsLabel
      case 1 => indexedSet.iterator.next()._2
      case _ => throw new IllegalStateException(s"Multiple edges between $between: "+indexedSet)
    }
  }

  /**
    * O(1)
    *
    * @return
    */
  override def node(i: Int): Node = outNodes.get(i)

  /**
    * O(1)
    *
    * @return
    */
  override def innerNodeForIndex(i: Int): InNode = innerNodes.get(i)

  /**
    * O(n)
    *
    * @return
    */
  override def label(i: Int, j: Int): Label = {
    val indexedSet = inEdges(i).filter(x => x._2 == inNodes.get(j))
    indexedSet.size match {
      case 0 => noEdgeExistsLabel
      case 1 => indexedSet.iterator.next()._2
      case _ => throw new IllegalStateException(s"Multiple edges from ${node(i)} to ${node(j)}: "+indexedSet)
    }
  }

  override def toString:String = {
    s"${this.getClass.getSimpleName}(edges = $edges,nodes = $outNodes,noEdgeExistsValue = $noEdgeExistsLabel)"
  }
}

/**
  * O(n ln(n) + e ln(n))
  */
object AdjacencyLabelUndigraph{

  def apply[Node,Label](edges:GenTraversable[(NodePair[Node],Label)] = Seq.empty,
                        nodes:GenSeq[Node] = Seq.empty,
                        noEdgeExistsValue:Label = null) = {

    val nodeValues:Vector[Node] = (nodes ++ edges.map(_._1._1) ++ edges.map(_._1._2)).distinct.to[Vector]

    val successorMap:GenMap[Node,GenTraversable[(NodePair[Node],Label)]] = edges.groupBy(x => x._1._1)
    val predecessorMap:GenMap[Node,GenTraversable[(NodePair[Node],Label)]] = edges.groupBy(x => x._1._2)

    def getOrEmpty(n:Node,nodeToTrav:GenMap[Node,GenTraversable[(NodePair[Node],Label)]]):IndexedSet[(NodePair[Node],Label)] = {
      nodeToTrav.getOrElse(n,Vector.empty[(NodePair[Node],Label)]).to[IndexedSet]
    }

    val edgeAdjacencies:Vector[IndexedSet[(NodePair[Node],Label)]] = nodeValues.map(n => getOrEmpty(n,successorMap) ++ getOrEmpty(n,predecessorMap) )

    new AdjacencyLabelUndigraph(nodeValues.to[IndexedSet],edgeAdjacencies,noEdgeExistsValue)
  }

}