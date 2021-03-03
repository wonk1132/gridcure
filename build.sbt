val Http4sVersion = "1.0.0-M10"
val CirceVersion = "0.13.0"
val MunitVersion = "0.7.20"
val LogbackVersion = "1.2.3"
val MunitCatsEffectVersion = "0.12.0"
val PotgresqlVersion = "42.2.2"

lazy val root = (project in file("."))
  .settings(
    organization := "com.example",
    name := "quickstart",
    version := "latest",
    scalaVersion := "2.13.4",
    libraryDependencies ++= Seq(
      "org.http4s"      %% "http4s-blaze-server" % Http4sVersion,
      "org.http4s"      %% "http4s-blaze-client" % Http4sVersion,
      "org.http4s"      %% "http4s-circe"        % Http4sVersion,
      "org.http4s"      %% "http4s-dsl"          % Http4sVersion,
      "io.circe"        %% "circe-generic"       % CirceVersion,
      "org.scalameta"   %% "munit"               % MunitVersion           % Test,
      "org.typelevel"   %% "munit-cats-effect-2" % MunitCatsEffectVersion % Test,
      "ch.qos.logback"  %  "logback-classic"     % LogbackVersion,
      "org.scalameta"   %% "svm-subs"            % "20.2.0",
      "com.google.cloud" % "google-cloud-bigquery" % "1.127.0",
      "com.google.cloud" % "google-cloud-secretmanager" % "1.2.9",
      "org.postgresql"  % "postgresql"           % PotgresqlVersion,
      "org.scalikejdbc" %% "scalikejdbc"         % "3.5.0",
      "com.zaxxer" % "HikariCP" % "3.4.5",
      "org.scalikejdbc" %% "scalikejdbc" % "3.5.0",
      "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2",
    ),
    addCompilerPlugin("org.typelevel" %% "kind-projector"     % "0.10.3"),
    addCompilerPlugin("com.olegpy"    %% "better-monadic-for" % "0.3.1"),
    testFrameworks += new TestFramework("munit.Framework")
  )

dockerImageCreationTask := (publishLocal in Docker).value
dockerBaseImage         := "adoptopenjdk/openjdk8:alpine-jre"
dockerAlias             := DockerAlias(
  Some("gcr.io"),
  Some("gridcure-dev"),
  "quickstart",
  Some("latest")
)

enablePlugins(JavaAppPackaging)
enablePlugins(AshScriptPlugin)
enablePlugins(DockerComposePlugin)

