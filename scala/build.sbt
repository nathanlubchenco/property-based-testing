name := "property-based-testing"

version := "0.1"

libraryDependencies ++= Seq(
        "org.scalaz" %% "scalaz-core" % "7.0.6",
        "joda-time"                     %  "joda-time"            % "2.3",
        "org.joda"                      %  "joda-convert"         % "1.3.1",
        "org.specs2" %% "specs2" % "2.3.13" % "test",
        "org.scalacheck" %% "scalacheck" % "1.11.4" % "test"
         )


resolvers ++= Seq(
  "Sonatype Releases" at "http://oss.sonatype.org/content/repositories/releases",
  "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"
  )


