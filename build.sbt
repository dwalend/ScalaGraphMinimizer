import SonatypeKeys._

// Import default settings. This changes `publishTo` settings to use the Sonatype repository and add several commands for publishing.
sonatypeSettings

//todo maybe remove release
releaseSettings

name := "Graph4ScalaSemirings"

organization := "net.walend"

// Project version. Only release version (w/o SNAPSHOT suffix) can be promoted.
version := "0.0.1-SNAPSHOT"

scalaVersion := "2.11.0"

crossScalaVersions := Seq(scalaVersion.value)

libraryDependencies += "com.assembla.scala-incubator" % "graph-core_2.11" % "1.8.1"

libraryDependencies += "org.scala-lang" % "scala-reflect" % scalaVersion.value


libraryDependencies += "org.scalatest" % "scalatest_2.11" % "2.1.5" % "test"

libraryDependencies += "net.sf.jung" % "jung-graph-impl" % "2.0.1" % "test" //for timing comparisons

libraryDependencies += "net.sf.jung" % "jung-algorithms" % "2.0.1" % "test" //for timing comparisons


fork in test := true

javaOptions in test += "-Xmx3G" //prevents big GC

javaOptions in test += "-server" //does hotspot optimizations earlier

scalacOptions ++= Seq("-feature")

// Your project orgnization (package name)
organization := "net.walend"
//todo organization := "net.walend.scalagraph"

// Your profile name of the sonatype account. The default is the same with the organization
profileName := "net.walend"

// To sync with Maven central, you need to supply the following information:
pomExtra := {
  <url>https://github.com/dwalend/ScalaGraphMinimizer</url>
    <licenses>
      <license>
        <name>MIT License</name>
        <url>http://www.opensource.org/licenses/mit-license.php</url>
      </license>
    </licenses>
    <scm>
      <connection>scm:git:github.com:dwalend/ScalaGraphMinimizer.git</connection>
      <url>github.com/dwalend/ScalaGraphMinimizer.git</url>
    </scm>
    <developers>
      <developer>
        <id>dwalend</id>
        <name>David Walend</name>
        <url>https://github.com/dwalend</url>
      </developer>
    </developers>
}
