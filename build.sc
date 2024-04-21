import mill._
import scalalib._
import mill.define.{Command, Target}
import mill.scalajslib.ScalaJSModule

object Shared {
  val scalacOptions = Seq("-deprecation")
  val scalaJSVersion = "1.16.0" //todo see javascript work
  val scalaVersion = "3.3.3"
  val javaVersion = "17.0.6"
  val munitVersion = "0.7.29"
}

object Graph extends ScalaJSModule {
  override def artifactName: T[String] = "Disentangle-Graph"

  override def scalaJSVersion: T[String] = Shared.scalaJSVersion
  override def scalaVersion: T[String] = Shared.scalaVersion
  def javaVersion: String = Shared.javaVersion
  def munitVersion: String = Shared.munitVersion

  override def scalacOptions: Target[Seq[String]] = Shared.scalacOptions

  object test extends Tests with TestModule.Munit {
    override def ivyDeps = Agg(
      ivy"""org.scalameta::munit::$munitVersion"""
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

//todo move the rest of the tests to Munit

object GraphJvm extends ScalaModule {
  override def artifactName: T[String] = "Disentangle-Graph-JVM"

  override def scalaVersion: T[String] = Shared.scalaVersion

  def javaVersion = Shared.javaVersion

  override def scalacOptions = Shared.scalacOptions

  override def ivyDeps = Agg(
    ivy"org.scala-lang.modules::scala-parallel-collections:1.0.4"
  )

  override def moduleDeps: Seq[JavaModule] = super.moduleDeps ++ Seq(Graph)

  object test extends Tests with TestModule.Munit {
    override def moduleDeps: Seq[JavaModule] = super.moduleDeps ++ Seq(Graph.test)

    override def ivyDeps = Agg(
      ivy"org.scalameta::munit::0.7.29",
      ivy"net.sf.jung:jung-graph-impl:2.1.1",
      ivy"net.sf.jung:jung-algorithms:2.1.1"
    )
  }
}

object Examples extends ScalaModule {
  override def artifactName: T[String] = "Disentangle-Examples"

  def scalaJSVersion = Shared.scalaJSVersion
  override def scalaVersion: T[String] = Shared.scalaVersion
  def javaVersion = Shared.javaVersion

  override def scalacOptions: Target[Seq[String]] = Shared.scalacOptions

  override def moduleDeps: Seq[JavaModule] = super.moduleDeps ++ Seq(Graph,GraphJvm)

  object test extends Tests with TestModule.Munit {
    override def moduleDeps: Seq[JavaModule] = super.moduleDeps ++ Seq(Graph.test)
    override def ivyDeps = Agg(
      ivy"org.scalameta::munit::0.7.29"
    )
  }
}

object Benchmark extends ScalaModule {
  override def artifactName: T[String] = "Disentangle-Benchmark"

  def scalaJSVersion = Shared.scalaJSVersion
  override def scalaVersion: T[String] = Shared.scalaVersion
  def javaVersion = Shared.javaVersion

  override def scalacOptions = Shared.scalacOptions

  override def moduleDeps: Seq[JavaModule] = super.moduleDeps ++ Seq(Graph,GraphJvm)

  override def ivyDeps = Agg(
    ivy"org.scalatest::scalatest:3.2.15",
    ivy"net.sf.jung:jung-graph-impl:2.1.1",
    ivy"net.sf.jung:jung-algorithms:2.1.1",
    ivy"com.github.scopt::scopt:4.1.0"
  )

  object test extends Tests with TestModule.ScalaTest {

    override def moduleDeps: Seq[JavaModule] = super.moduleDeps ++ Seq(Graph.test)

    override def ivyDeps = Agg(
      ivy"org.scalatest::scalatest:3.2.15"
    )
  }
}

object Experiments extends ScalaModule {
  override def artifactName: T[String] = "Disentangle-Experiments"

  def scalaJSVersion = Shared.scalaJSVersion

  override def scalaVersion: T[String] = Shared.scalaVersion

  def javaVersion = Shared.javaVersion

  override def scalacOptions: Target[Seq[String]] = Shared.scalacOptions

  override def moduleDeps: Seq[JavaModule] = super.moduleDeps ++ Seq(Graph)

  object test extends Tests with TestModule.Munit {
    override def moduleDeps: Seq[JavaModule] = super.moduleDeps ++ Seq(Graph.test)

    override def ivyDeps = Agg(
      ivy"org.scalameta::munit::0.7.29"
    )
  }
}