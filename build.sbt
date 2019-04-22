import com.typesafe.sbt.SbtGit.GitKeys._

name := "jbert"

version := "0.1"

maintainer := "samuelcasa42@gmail.com"

scalaVersion := "2.12.8"

enablePlugins(JavaAppPackaging)

lazy val root = (project in file(".")).
  enablePlugins(BuildInfoPlugin).
  settings(
    buildInfoKeys := Seq[BuildInfoKey](version, name, sbtVersion, gitHeadCommit, gitHeadCommitDate, gitUncommittedChanges, gitDescribedVersion),
    buildInfoPackage := "buildinfo"
  )

libraryDependencies += "com.pi4j" % "pi4j-core" % "1.2"

libraryDependencies += "net.thejavashop" % "javampd" % "6.0.0"
