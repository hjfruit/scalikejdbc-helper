val scala3Version    = "3.3.0"
val scalatestVersion = "3.2.14"
ThisBuild / resolvers ++= Seq(
  Resolver.mavenLocal,
  "Sonatype OSS Snapshots" at "https://s01.oss.sonatype.org/content/repositories/snapshots/",
  "Sonatype OSS Releases" at "https://s01.oss.sonatype.org/content/repositories/releases"
)
inThisBuild(
  List(
    ThisBuild / useCoursier := false,
    organization            := "org.bitlap",
    sonatypeCredentialHost  := "s01.oss.sonatype.org",
    sonatypeRepository :=
      "https://s01.oss.sonatype.org/service/local",
    homepage := Some(url("https://github.com/bitlap/scalikejdbc-helper")),
    licenses := List("Apache-2.0" -> url("https://www.apache.org/licenses/LICENSE-2.0")),
    developers := List(
      Developer(
        id = "jxnu-liguobin",
        name = "梦境迷离",
        email = "dreamylost@outlook.com",
        url = url("https://blog.dreamylost.cn")
      )
    )
  )
)

Global / onChangedBuildSource := ReloadOnSourceChanges
lazy val commonSettings =
  Seq(
    organization                  := "org.bitlap",
    startYear                     := Some(2023),
    scalaVersion                  := scala3Version,
    Compile / compile             := (Compile / compile).dependsOn(Compile / headerCreateAll).value,
    Global / onChangedBuildSource := ReloadOnSourceChanges,
    headerLicense                 := Some(HeaderLicense.MIT("2023", "bitlap")),
    Test / testOptions += Tests.Argument("-oDF"),
    Test / fork               := true,
    publishConfiguration      := publishConfiguration.value.withOverwrite(true),
    publishLocalConfiguration := publishLocalConfiguration.value.withOverwrite(true),
    scalacOptions ++= Seq(
      /** "-Ycheck:all",** */
      "-explain",
      "unchecked",
      "-deprecation",
      "-feature",
      "-Werror"
    ),
    libraryDependencies ++= Seq(
      "org.scalatest" %% "scalatest" % scalatestVersion % Test
    )
  )

lazy val scalikejdbcDep      = "org.scalikejdbc" %% "scalikejdbc"                      % "4.0.0"
lazy val zioDep              = "dev.zio"         %% "zio"                              % "2.0.13" % Provided
lazy val postgresqlDep       = "org.postgresql"   % "postgresql"                       % "42.5.4" % Provided
lazy val embeddedPostgresDep = "io.zonky.test"    % "embedded-postgres"                % "2.0.3"  % Test
lazy val scalikejdbcMacroDep = "org.scalikejdbc" %% "scalikejdbc-syntax-support-macro" % "4.0.0"  % Test
lazy val slf4jSimpleDep      = "org.slf4j"        % "slf4j-simple"                     % "1.7.32" % Test
lazy val zioLoggingDep = Seq(
  "dev.zio" %% "zio-logging-slf4j" % "2.1.13" % Test,
  "dev.zio" %% "zio-logging"       % "2.1.13"
)

lazy val `scalikejdbc-helper` = project
  .in(file("."))
  .aggregate(`postgres`, `core`)
  .settings(
    commands ++= Commands.value,
    crossScalaVersions := Nil,
    publish / skip     := true
  )

lazy val `postgres` = project
  .in(file("postgres"))
  .settings(
    name := "scalikejdbc-helper-postgres",
    libraryDependencies ++= Seq(
      postgresqlDep,
      scalikejdbcDep,
      embeddedPostgresDep,
      scalikejdbcMacroDep,
      slf4jSimpleDep
    ) ++ zioLoggingDep
  )
  .settings(commonSettings)
  .dependsOn(`core` % "provided->provided;compile->compile;test->test")

lazy val `core` = project
  .in(file("core"))
  .settings(
    name := "scalikejdbc-helper-core",
    libraryDependencies ++= Seq(scalikejdbcDep, zioDep)
  )
  .settings(commonSettings)
