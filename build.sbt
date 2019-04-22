import com.github.eikek.sbt.openapi._
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
  ).
  enablePlugins(OpenApiSchema).
  settings(
    openapiTargetLanguage := Language.Java,
    openapiPackage := Pkg("openapi"),
    openapiSpec := (Compile / resourceDirectory).value / "openapi.yaml",
    openapiJavaConfig := JavaConfig.default.
      copy(json = JavaJson.jackson).addMapping(CustomMapping.forName({ case s => s + "Dto"}))
  )

libraryDependencies += "com.pi4j" % "pi4j-core" % "1.2"

libraryDependencies += "net.thejavashop" % "javampd" % "6.0.0"

val jacksonVersion = "2.9.8"
libraryDependencies ++= Seq(
  "com.fasterxml.jackson.core" % "jackson-core" % jacksonVersion,
  "com.fasterxml.jackson.core" % "jackson-annotations" % jacksonVersion,
  "com.fasterxml.jackson.core" % "jackson-databind" % jacksonVersion
)
