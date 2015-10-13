name := "lambda-cloudFormation-stack-updater"

assemblyJarName in assembly := s"${name.value}-${version.value}.jar"

scalaVersion := "2.11.7"

libraryDependencies += "com.amazonaws" % "aws-lambda-java-core" % "1.0.0"
libraryDependencies += "com.amazonaws" % "aws-lambda-java-events" % "1.0.0"
libraryDependencies += "com.amazonaws" % "aws-lambda-java-events" % "1.0.0"
libraryDependencies += "com.amazonaws" % "aws-java-sdk-cloudformation" % "1.10.4"
libraryDependencies += "com.typesafe.play" %% "play-json" % "2.4.1"
libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.5" % "test"

