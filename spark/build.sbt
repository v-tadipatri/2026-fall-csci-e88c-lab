import sbt.librarymanagement.Configurations.Provided

name := "spark"

exportJars := false
bgCopyClasspath := false
//add this as VM option when running locally
//--add-exports=java.base/sun.nio.ch=ALL-UNNAMED
//flag to switch how spark libraries are included
val isLocal = sys.props.get("LOCAL_MODE").contains("1")

  libraryDependencies ++= {
    val scope = if (isLocal) Compile else Provided
    println(s"Local mode = ${isLocal} , scope = ${scope}")
    //In local mode (IDE), we want the spark libraries
    //when building a jar to deploy, do not include spark
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
  //discard the test files in assembly
  case PathList("movies", xs @ _*)  => MergeStrategy.discard
  case "application.conf"            => MergeStrategy.concat
  case x =>
    val oldStrategy = (assembly / assemblyMergeStrategy).value
    oldStrategy(x)
}
// see shading feature at https://github.com/sbt/sbt-assembly#shading
assembly / assemblyShadeRules := Seq(
  ShadeRule.rename("shapeless.**" -> "shadeshapeless.@1").inAll
)
