import com.github.eikek.sbt.openapi._
import com.typesafe.sbt.SbtGit.GitKeys._


name := "jbert"
version := "0.3.0"

scalaVersion := "2.12.8"
val jacksonVersion = "2.9.8"


lazy val backend = (project in file("backend"))
  .settings(
    libraryDependencies += "com.pi4j" % "pi4j-core" % "1.2",
    libraryDependencies += "net.thejavashop" % "javampd" % "6.0.0",
    libraryDependencies += "org.slf4j" % "slf4j-api" % "1.7.25",
    libraryDependencies ++= Seq(
      "com.fasterxml.jackson.core" % "jackson-core" % jacksonVersion,
      "com.fasterxml.jackson.core" % "jackson-annotations" % jacksonVersion,
      "com.fasterxml.jackson.core" % "jackson-databind" % jacksonVersion
    )
  )

lazy val jbert = (project in file("application"))
  .enablePlugins(PlayMinimalJava, OpenApiSchema, BuildInfoPlugin, JavaAppPackaging, DebianDeployPlugin, SystemdPlugin)
  .settings(
    buildInfoKeys := Seq[BuildInfoKey](version, name, sbtVersion, gitHeadCommit, gitHeadCommitDate, gitUncommittedChanges, gitDescribedVersion),
    buildInfoPackage := "buildinfo",
    packageName := name.value,
    packageSummary := "jbert control application",
    packageDescription := "jbert - Audio playback tool for kids",
    maintainer := "samuelcasa9@gmail.com",
    debianPackageDependencies := Seq("openjdk-8-jre-headless"),
    javaOptions in Universal ++= Seq(
      // JVM memory tuning
      "-J-Xmx256m",
      "-J-Xms128m",
      // JMX monitoring
      "-Dcom.sun.management.jmxremote",
      "-Dcom.sun.management.jmxremote.ssl=false",
      "-Dcom.sun.management.jmxremote.authenticate=false",
      "-Dcom.sun.management.jmxremote.port=1077"
    ),
    openapiTargetLanguage := Language.Java,
    openapiPackage := Pkg("models"),
    openapiSpec := (Compile / baseDirectory).value / "public" / "openapi.yml",
    openapiJavaConfig := JavaConfig.default
      .withJson(json = JavaJson.jackson)
      .addMapping(CustomMapping.forName({ case s => s + "Dto" })),
    libraryDependencies += guice,
    libraryDependencies += "org.webjars" % "swagger-ui" % "3.23.8",
    libraryDependencies += "org" % "jaudiotagger" % "2.0.3"
  ).dependsOn(backend)
