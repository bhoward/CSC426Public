lazy val root =
  project
    .in(file("."))
    .settings(
      name := "TraversalDemo",
      scalaVersion := "2.13.3",
      organization := "edu.depauw"
    )