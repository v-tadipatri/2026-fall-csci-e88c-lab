#!/bin/bash
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
ROOT_FOLDER=${SCRIPT_DIR}/../../../
SOURCE_FILE=${1?Enter input file}
BATCH_SIZE=$2
if [ "$BATCH_SIZE" = "" ]
then
    BATCH_SIZE=10              # Number of lines to copy per batch
    echo "No batch size specified , defaulting to $BATCH_SIZE"
fi

# Variables
#SOURCE_FILE="$ROOT_FOLDER/docker/beam/data/taxi_tripdata.csv"
DEST_FOLDER="$ROOT_FOLDER/docker/beam/stream_in"
mkdir -p $DEST_FOLDER
rm -f $DEST_FOLDER/*
echo "ROOT_FOLDER: $ROOT_FOLDER"
echo "DEST_FOLDER: $DEST_FOLDER"
# SOURCE_FILE="$ROOT_FOLDER/src/main/resources/data/alice-in-wonderland.txt"
# DEST_FOLDER="$ROOT_FOLDER/target/streaming-word-count-input"
PAUSE_DURATION=5           # Pause duration in seconds

#Initial sleep
#sleep 30

# Create the destination folder if it doesn't exist
mkdir -p "$DEST_FOLDER"

# Initialize the line counter
line_count=0

# Read the source file line by line
while IFS= read -r line
do
    # Determine the output file for the current batch
    # Avoid collisions with previous files
    batch_number=$((line_count / BATCH_SIZE + 1))
    tstamp=`date +%H%M%S`
    output_file="$DEST_FOLDER/batch_${batch_number}_${tstamp}.txt"

    # Append the current line to the appropriate batch file
    echo "$line" >> "$output_file"

    # Increment the line counter
    line_count=$((line_count + 1))

    # If we've reached the batch size, pause for the specified duration
    if (( line_count % BATCH_SIZE == 0 )); then
        tstamp_full=`date`
        echo "$tstamp_full - Batch $batch_number : output $line_count lines, sleeping for $PAUSE_DURATION..."
        sleep "$PAUSE_DURATION"
    fi
done < "$SOURCE_FILE"

echo "Done copying in batches of $BATCH_SIZE lines."
