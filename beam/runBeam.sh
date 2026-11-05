#!/bin/bash
project=homedeveloper-166415 
bucket=gcp-bucket-ven
dtime=`date +%Y%m%d-%H%M%S`
#Set streaming=true for streaming jobs
java -cp target/scala-2.13/BeamJob.jar org.cscie88c.beam.week10lab.BeamDemoBatchWordCount \
  --jobName=top-words-streaming-$dtime \
  --runner=DataflowRunner \
  --project=$project \
  --region=us-central1 \
  --tempLocation=gs://$bucket/beamlab/temp \
  --input-file=gs://$bucket/beamlab/input/alice_in_wonderland.txt \
  --output-dir=gs://$bucket/beamlab/output/wordcount/words-$dtime \
  --num-words=50
