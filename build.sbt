val scala3Version    = "3.2.2"
val scalatestVersion = "3.2.14"
ThisBuild / resolvers ++= Seq(
  Resolver.mavenLocal,
  "Sonatype OSS Snapshots" at "https://s01.oss.sonatype.org/content/repositories/snapshots/",
  "Sonatype OSS Releases" at "https://s01.oss.sonatype.org/content/repositories/releases"
)
ThisBuild / version := "0.1.0"
inThisBuild(
  List(
    organization           := "org.bitlap",
    sonatypeCredentialHost := "s01.oss.sonatype.org",
    sonatypeRepository :=
      "https://s01.oss.sonatype.org/service/local",
    homepage := Some(url("https://github.com/bitlap/bitlap")),
    licenses := List("Apache-2.0" -> url("https://www.apache.org/licenses/LICENSE-2.0")),
    developers := List(
      Developer(
        id = "dreamylost",
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

lazy val `scalikejdbc-helper` = project
  .in(file("."))
  .aggregate(`postgres`)
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
      "org.postgresql"   % "postgresql"  % "42.5.4"  % Provided,
      "org.scalikejdbc" %% "scalikejdbc" % "4.0.0",
      "com.h2database"   % "h2"          % "2.1.214" % Test
    )
  )
  .settings(commonSettings)
