package net.walend.disentangle.graph.semiring

import net.walend.disentangle.graph.semiring.Brandes.BrandesSteps
import net.walend.disentangle.graph.{IndexedLabelDigraph, IndexedLabelUndigraph, LabelDigraph, LabelUndigraph}
import net.walend.disentangle.graph.semiring.{Dijkstra, FewestNodes, FirstStepsTrait, SemiringSupport}

import scala.collection.immutable.Iterable

import scala.collection.parallel.immutable.{ParMap, ParSeq}

/**
 * Semiring algorithms that run in parallel.
 *
 * The cleanest good way to run Dijkstra's algorithm in parallel turns out to be a top-level split of nodes using Scala's parallel collections kit from https://github.com/scala/scala-parallel-collections?tab=readme-ov-file . It would be pretty easy to target Cats Effect or the like to keep your system in just one compute thread pool.
 */
package object par {

}
