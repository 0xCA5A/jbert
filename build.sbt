import com.github.eikek.sbt.openapi._
import com.typesafe.sbt.SbtGit.GitKeys._

name := "jbert"

version := "0.1"

maintainer := "samuelcasa42@gmail.com"

scalaVersion := "2.12.8"


val jacksonVersion = "2.9.8"

lazy val core = (project in file("core")).
  enablePlugins(BuildInfoPlugin).
  enablePlugins(OpenApiSchema).
  settings(
    buildInfoKeys := Seq[BuildInfoKey](version, name, sbtVersion, gitHeadCommit, gitHeadCommitDate, gitUncommittedChanges, gitDescribedVersion),
    buildInfoPackage := "buildinfo",
    openapiTargetLanguage := Language.Java,
    openapiPackage := Pkg("openapi"),
    openapiSpec := (Compile / resourceDirectory).value / "openapi.yaml",
    openapiJavaConfig := JavaConfig.default.copy(json = JavaJson.jackson).addMapping(CustomMapping.forName({ case s => s + "Dto" })),
    libraryDependencies += "com.pi4j" % "pi4j-core" % "1.2",
    libraryDependencies += "net.thejavashop" % "javampd" % "6.0.0",
    libraryDependencies += "org.slf4j" % "slf4j-api" % "1.7.25",
    libraryDependencies ++= Seq(
      "com.fasterxml.jackson.core" % "jackson-core" % jacksonVersion,
      "com.fasterxml.jackson.core" % "jackson-annotations" % jacksonVersion,
      "com.fasterxml.jackson.core" % "jackson-databind" % jacksonVersion
    )
  )

lazy val application = (project in file("application")).
  enablePlugins(JavaAppPackaging).
  enablePlugins(DebianDeployPlugin).
  enablePlugins(PlayMinimalJava).
  settings(
    libraryDependencies += guice
  ).dependsOn(core)
