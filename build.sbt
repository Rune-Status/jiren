name := "Jiren"

version := "1.0"

scalaVersion := "2.12.1"

libraryDependencies ++= {
  val akkaVersion = "2.5.12"
  val akkaDeps = Seq(
    "com.typesafe.akka" %% "akka-actor" % akkaVersion,
    "com.typesafe.akka" %% "akka-testkit" % akkaVersion
  )

  val scalaTestVersion = "3.1.0-SNAP5"
  val scalaTestDeps = Seq(
    "org.scalatest" %% "scalatest" % scalaTestVersion % Test
  )

  akkaDeps ++ scalaTestDeps
}
        