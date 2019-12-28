import play.sbt.PlayImport
import com.github.eikek.sbt.openapi._
import com.typesafe.sbt.SbtGit.GitKeys._


name := "jbert"
version := "0.3.0"

scalaVersion := "2.13.0"
val jacksonVersion = "2.9.8"


lazy val backend = (project in file("backend")).
  enablePlugins(OpenApiSchema).
  settings(
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

lazy val jbert = (project in file("application")).
  enablePlugins(PlayMinimalJava).
  enablePlugins(BuildInfoPlugin).
  enablePlugins(JavaAppPackaging).
  enablePlugins(DebianDeployPlugin).
  enablePlugins(SystemdPlugin).
  settings(
    buildInfoKeys := Seq[BuildInfoKey](version, name, sbtVersion, gitHeadCommit, gitHeadCommitDate, gitUncommittedChanges, gitDescribedVersion),
    buildInfoPackage := "buildinfo",
    packageName := name.value,
    packageSummary := "jbert control application",
    packageDescription := """jbert - Audio playback tool for kids""",
    maintainer := "samuelcasa9@gmail.com",
    debianPackageDependencies := Seq("openjdk-8-jre-headless"),
    javaOptions in Universal ++= Seq(
      // JVM memory tuning
      "-J-Xmx256m",
      "-J-Xms64m",
      // JMX monitoring
      "-Dcom.sun.management.jmxremote",
      "-Dcom.sun.management.jmxremote.port=7777",
      "-Dcom.sun.management.jmxremote.local.only=false",
      "-Dcom.sun.management.jmxremote.authenticate=false",
      "-Dcom.sun.management.jmxremote.ssl=false"
    ),
    libraryDependencies += PlayImport.guice
  ).dependsOn(backend)
