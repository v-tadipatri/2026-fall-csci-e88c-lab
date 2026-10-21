import sbt.librarymanagement.Configurations.Provided
import sbt.librarymanagement.Configurations.Default

name := "spark"
//add this as VM option when running locally
//--add-exports=java.base/sun.nio.ch=ALL-UNNAMED
//flag to switch how spark libraries are included
//Hard to set path in intellij, so override on cmdline
val isLocal = sys.env.get("REMOTE_MODE").contains("1")==false

  libraryDependencies ++= {
    println("Local mode = "+isLocal)
    val scope = if (isLocal) Default else Provided
    //In local mode (IDE), we want the spark libraries
    //when building a jar to deploy, do not include spark
    //export REMOTE_MODE=1 when running sbt assembly!
    Seq(
      "org.apache.spark" %% "spark-core" % "4.0.2" % scope,
      "org.apache.spark" %% "spark-sql" % "4.0.2" % scope
    )
  }
//}

Compile / mainClass := Some("org.cscie88c.spark.SparkJob")

assembly / mainClass := Some("org.cscie88c.spark.SparkJob")
assembly / assemblyJarName := "SparkJob.jar"
assembly / test := {}
assembly / assemblyMergeStrategy := {
  case PathList("META-INF", xs @ _*) => MergeStrategy.discard
  case PathList("movies", xs @ _*) => MergeStrategy.discard
  case "application.conf"            => MergeStrategy.concat
  case x =>
    val oldStrategy = (assembly / assemblyMergeStrategy).value
    oldStrategy(x)
}
// see shading feature at https://github.com/sbt/sbt-assembly#shading
assembly / assemblyShadeRules := Seq(
  ShadeRule.rename("shapeless.**" -> "shadeshapeless.@1").inAll
)
