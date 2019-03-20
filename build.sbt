name := """svg2-browser"""
organization := "com.crag"

version := "1.0"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.12.8"

libraryDependencies += guice
libraryDependencies += "org.yaml" % "snakeyaml" % "1.17"
libraryDependencies += "com.typesafe.play" %% "play-json" % "2.6.10"
//libraryDependencies += "org.json" % "json" % "20180813"
libraryDependencies += "com.googlecode.json-simple" % "json-simple" % "1.1.1"
// acabaremos quedandonos con este
libraryDependencies += "com.google.code.gson" % "gson" % "2.8.5"

libraryDependencies += "org.rosuda.REngine" % "REngine" % "2.1.0"

// libraries installed with R
libraryDependencies += "org.rosuda" % "JRI" % "1.0" from "file:///Library/Frameworks/R.framework/Versions/3.4/Resources/library/rJava/jri/JRI.jar"
libraryDependencies += "org.rosuda.REngine.JRI" % "JRIEngine" % "1.0" from "file:///Library/Frameworks/R.framework/Versions/3.4/Resources/library/rJava/jri/JRIEngine.jar"

// dont works anymore
// javaOptions in run += "-Djava.library.path=/Library/Frameworks/R.framework/Versions/3.4/Resources/library/rJava/jri"


libraryDependencies += "com.typesafe" % "config" % "1.3.2"
