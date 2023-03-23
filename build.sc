import mill._
import scalalib._
import coursier.maven.MavenRepository
import coursier.Repository
import mill.api.Loose
import mill.api.Result
import mill.define.{Command, Target}
import mill.scalajslib.ScalaJSModule
import os.{CommandResult, Path}

object Shared {
  val scalacOptions = Seq("-deprecation")
  val scalaJSVersion = "1.13.0" //todo set up javascript again
  val scalaVersion = "2.12.17" //todo move to scala 2.13.8, then scala 3.0.2, then scala 3.1.2
  val javaVersion = "17.0.6"
}

object Graph extends ScalaModule {  //todo ScalaJSModule does not play nice with ScalaTest
  override def artifactName: T[String] = "Disentangle-Graph"

  def scalaJSVersion = Shared.scalaJSVersion
  def scalaVersion = Shared.scalaVersion
  def javaVersion = Shared.javaVersion

  override def scalacOptions = Shared.scalacOptions

  object test extends Tests with TestModule.ScalaTest {
    def ivyDeps = Agg(
      ivy"org.scalatest::scalatest:3.2.15",
      ivy"net.sf.jung:jung-graph-impl:2.1.1",
      ivy"net.sf.jung:jung-algorithms:2.1.1"
    )
  }

  def millw(): Command[PathRef] = T.command {
    val target = mill.modules.Util.download("https://raw.githubusercontent.com/lefou/millw/main/millw")
    val millw = millSourcePath / "millw"
    os.copy.over(target.path, millw)
    os.perms.set(millw, os.perms(millw) + java.nio.file.attribute.PosixFilePermission.OWNER_EXECUTE)
    target
  }
}

object Examples extends ScalaModule {
  override def artifactName: T[String] = "Disentangle-Examples"

  def scalaJSVersion = Shared.scalaJSVersion
  def scalaVersion = Shared.scalaVersion
  def javaVersion = Shared.javaVersion

  override def scalacOptions = Shared.scalacOptions

  override def moduleDeps: Seq[JavaModule] = super.moduleDeps ++ Seq(Graph)

  object test extends Tests with TestModule.ScalaTest {

    override def moduleDeps: Seq[JavaModule] = super.moduleDeps ++ Seq(Graph.test)

    def ivyDeps = Agg(
      ivy"org.scalatest::scalatest:3.2.15"
    )
  }
}

object Benchmark extends ScalaModule {
  override def artifactName: T[String] = "Disentangle-Benchmark"

  def scalaJSVersion = Shared.scalaJSVersion
  def scalaVersion = Shared.scalaVersion
  def javaVersion = Shared.javaVersion

  override def scalacOptions = Shared.scalacOptions

  override def moduleDeps: Seq[JavaModule] = super.moduleDeps ++ Seq(Graph)

  def ivyDeps = Agg(
    ivy"org.scalatest::scalatest:3.2.15",
    ivy"net.sf.jung:jung-graph-impl:2.1.1",
    ivy"net.sf.jung:jung-algorithms:2.1.1",
    ivy"com.github.scopt::scopt:4.1.0"
  )

  object test extends Tests with TestModule.ScalaTest {

    override def moduleDeps: Seq[JavaModule] = super.moduleDeps ++ Seq(Graph.test)

    def ivyDeps = Agg(
      ivy"org.scalatest::scalatest:3.2.15"
    )
  }
}