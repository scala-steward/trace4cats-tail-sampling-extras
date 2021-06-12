lazy val commonSettings = Seq(
  libraryDependencies += compilerPlugin(("org.typelevel" %% "kind-projector" % "0.13.0").cross(CrossVersion.patch)),
  libraryDependencies ++= {
    CrossVersion.partialVersion(scalaVersion.value) match {
      case Some((2, _)) => compilerPlugin("com.olegpy" %% "better-monadic-for" % "0.3.1") :: Nil
      case _ => Nil
    }
  },
  Compile / compile / javacOptions ++= Seq("-source", "1.8", "-target", "1.8"),
  scalacOptions := {
    val opts = scalacOptions.value :+ "-Wconf:src=src_managed/.*:s,any:wv"

    CrossVersion.partialVersion(scalaVersion.value) match {
      case Some((2, 12)) => opts.filterNot(Set("-Xfatal-warnings"))
      case _ => opts
    }
  },
  Test / fork := true,
  resolvers += Resolver.sonatypeRepo("releases"),
  ThisBuild / evictionErrorLevel := Level.Warn,
)

lazy val noPublishSettings =
  commonSettings ++ Seq(publish := {}, publishArtifact := false, publishTo := None, publish / skip := true)

lazy val publishSettings = commonSettings ++ Seq(
  publishMavenStyle := true,
  pomIncludeRepository := { _ =>
    false
  },
  Test / publishArtifact := false
)

lazy val root = (project in file("."))
  .settings(noPublishSettings)
  .settings(name := "Trace4Cats Tail Sampling Extras")
  .aggregate(`tail-sampling-cache-store`, `tail-sampling-redis-store`)

lazy val `tail-sampling-cache-store` = (project in file("modules/tail-sampling-cache-store"))
  .settings(publishSettings)
  .settings(
    name := "trace4cats-tail-sampling-cache-store",
    libraryDependencies ++= Seq(Dependencies.trace4catsTailSampling, Dependencies.scaffeine),
    libraryDependencies ++= Seq(Dependencies.trace4catsTestkit).map(_ % Test)
  )

lazy val `tail-sampling-redis-store` = (project in file("modules/tail-sampling-redis-store"))
  .settings(publishSettings)
  .settings(
    name := "trace4cats-tail-sampling-redis-store",
    libraryDependencies ++= Seq(
      Dependencies.trace4catsTailSampling,
      Dependencies.redis4cats,
      Dependencies.redis4catsLog4cats,
      Dependencies.scaffeine
    ),
    libraryDependencies ++= Seq(Dependencies.trace4catsTestkit, Dependencies.embeddedRedis).map(_ % Test)
  )
