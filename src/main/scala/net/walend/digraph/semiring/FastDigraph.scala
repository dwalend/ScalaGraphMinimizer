package net.walend.digraph.semiring

import scala.collection.mutable.ArrayBuffer

/**
 * Provides constant-time access and mutators for edges. Stores Nodes in a Vector and Edges in an ArrayBuffer of ArrayBuffers.
 *
 * @author dwalend
 * @since v0.1.0
 */
/**
 */
//TODO store edge indices in an ArrayBuffer per node to allow for constant-tme successors and predecessors

//TODO make that edgeMatrix a Vector of ArrayBuffers, or even a Vector of Vector of mutable cells.
//TODO for the Atomic version, make it a Vector of AtomicReferences
class FastDigraph[Node,Edge](outNodes:Vector[Node], //provides the master index values for each node.
                             edgeMatrix:ArrayBuffer[ArrayBuffer[Edge]], // (row,column) is (start,end), indexed by node.
                             val noEdgeExistsValue:Edge //value for no edge
                              ) extends Digraph[Node,Edge] {

  val inNodes:Vector[InNode] = outNodes.zipWithIndex.map(x => InNode(x._1,x._2))
  val nodeToInNode:Map[Node,InNode] = inNodes.map(x => x.value -> x).toMap

  //todo rewrite with maps and filters if it is not too crazy
  val predecessorIndices:ArrayBuffer[ArrayBuffer[InNode]] = {
    //for predecessors, if a is reachable from b then
    //b's ArrayBuffer should have a's index
    val result:ArrayBuffer[ArrayBuffer[InNode]] = edgeMatrix.map(x => ArrayBuffer[InNode]())
    for(rowIndex <- 0 until edgeMatrix.size) {
      for(columnIndex <- 0 until edgeMatrix.size) {
        if (edgeMatrix(rowIndex)(columnIndex) != noEdgeExistsValue) {
          result(columnIndex).append(inNodes(rowIndex))
        }
      }
    }
    result
  }

  val successorIndices:ArrayBuffer[ArrayBuffer[InNode]] = {
    //for successors, if a is reachable from b then
    //a's ArrayBuffer should have b's index
    val result:ArrayBuffer[ArrayBuffer[InNode]] = edgeMatrix.map(x => ArrayBuffer[InNode]())
    for(rowIndex <- 0 until edgeMatrix.size) {
      for(columnIndex <- 0 until edgeMatrix.size) {
        if (edgeMatrix(rowIndex)(columnIndex) != noEdgeExistsValue) {
          result(rowIndex).append(inNodes(columnIndex))
        }
      }
    }
    result
  }

  override def nodes: Seq[Node] = outNodes

  type InnerNodeType = InNode

  case class InNode(override val value:Node,index:Int) extends this.InnerNodeTrait {

    override def successors: Seq[InNode] = {
      successorIndices(index)
    }

    override def predecessors: Seq[InNode] = {
      predecessorIndices(index)
    }

    override def hashCode(): Int = index

    override def equals(obj: Any): Boolean = {
      obj match {
        case inNode:InNode => inNode.index == index
        case _ => false
      }
    }
  }

  override def innerNode(value: Node): Option[InNode] = {
    nodeToInNode.get(value)
  }

  override def innerNodes: Seq[InNode] = {
    outNodes.zipWithIndex.map(x => InNode(x._1,x._2))
  }

  /**
   * @return All of the edges in the graph
   */
  override def edges: Seq[(Node, Node, Edge)] = {

    def edgesInRow(row:(ArrayBuffer[Edge],Int)):Seq[(Node,Node,Edge)] = {
      val rowIndex = row._2
      val cellsWithIndex = row._1.zipWithIndex
      val cellsWithEdges = cellsWithIndex.filter(x => (x._1 != noEdgeExistsValue))
      cellsWithEdges.map(x => (outNodes(rowIndex),outNodes(x._2),x._1))
    }

    edgeMatrix.zipWithIndex.map(row => edgesInRow(row)).flatten
  }

  //todo apply?
  override def edge(from: InNode, to: InNode):Edge = {

    edgeMatrix(from.index)(to.index)
  }

  override def updateEdge(from: InNode, to: InNode, edge: Edge): Unit = {
    if (edgeMatrix(from.index)(to.index) == noEdgeExistsValue) {
      predecessorIndices(to.index).append(from)
      successorIndices(from.index).append(to)
    }
    edgeMatrix(from.index)(to.index) = edge
  }

}

object FastDigraph{

  def apply[Node,Edge](edgeSeq:Seq[(Node,Node,Edge)] = Seq.empty,
                       extraNodes:Seq[Node] = Seq.empty,
                       noEdgeExistsValue:Edge = null) = {

    val nodeValues:Vector[Node] = (extraNodes ++ edgeSeq.map(_._1) ++ edgeSeq.map(_._2)).distinct.to[Vector]

    val size = nodeValues.size

    val matrix:ArrayBuffer[ArrayBuffer[Edge]] = nodeValues.map(x => ArrayBuffer.fill(size)(noEdgeExistsValue)).to[ArrayBuffer]

    for (edgeTriple <- edgeSeq) {
      val row = nodeValues.indexOf(edgeTriple._1)
      val column = nodeValues.indexOf(edgeTriple._2)

      assert(matrix(row)(column)==noEdgeExistsValue,s"edgeSeq includes two edges between ${edgeTriple._1} and ${edgeTriple._2}, ${matrix(row)(column)} and ${edgeTriple._3}")

      matrix(row)(column) = edgeTriple._3
    }

    new FastDigraph(nodeValues,matrix,noEdgeExistsValue)
  }

}