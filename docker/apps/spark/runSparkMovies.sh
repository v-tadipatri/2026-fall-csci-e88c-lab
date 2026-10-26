#!/bin/sh
#Make sure that you chmod 777 the output folder on HOST before running
/opt/spark/bin/spark-submit \
    --class org.cscie88c.spark.SparkJob \
    --master spark://spark-master:7077 \
    /opt/spark-apps/SparkJob.jar \
    /opt/spark-data/movies \
    /opt/spark-results/movie_results
