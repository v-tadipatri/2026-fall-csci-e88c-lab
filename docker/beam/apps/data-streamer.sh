#!/bin/bash
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
ROOT_FOLDER=${SCRIPT_DIR}/../../../
SOURCE_FILE=${1?Enter input file}
REAL_DEST_FOLDER=${2?Enter dest dir - local,s3, or gs}
BATCH_SIZE=$3
if [ "$REAL_DEST_FOLDER" = "-" ]
then
    REAL_DEST_FOLDER="$ROOT_FOLDER/docker/beam/stream_in"
    echo "Using default dest: $DEST_FOLDER"
fi
CLOUD=
if [[ "$REAL_DEST_FOLDER" =~ s3:* || "$REAL_DEST_FOLDER" =~ gs:* ]]
then
    CLOUD=${REAL_DEST_FOLDER:0:2}
    DEST_FOLDER="cloud_${CLOUD}/${REAL_DEST_FOLDER:5}"
    rm -Rf $DEST_FOLDER 
    mkdir -p $DEST_FOLDER
    echo "Location = $CLOUD , local = $DEST_FOLDER"
else
    DEST_FOLDER=$REAL_DEST_FOLDER
fi


if [ "$BATCH_SIZE" = "" ]
then
    BATCH_SIZE=10              # Number of lines to copy per batch
    echo "No batch size specified , defaulting to $BATCH_SIZE"
fi

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
    output_curr="batch_${batch_number}_${tstamp}.txt"
    output_file="$DEST_FOLDER/$output_curr"

    # Append the current line to the appropriate batch file
    echo "$line" >> "$output_file"

    # Increment the line counter
    line_count=$((line_count + 1))

    # If we've reached the batch size, pause for the specified duration
    if (( line_count % BATCH_SIZE == 0 ))
    then
        output_remote=$REAL_DEST_FOLDER/$output_curr
        if [ "$CLOUD" = "s3" ]
        then
            aws s3 cp $output_file $output_remote
        fi
        if [ "$CLOUD" = "gs" ]
        then
            gcloud storage cp $output_file $output_remote
        fi
        tstamp_full=`date`
        echo "$tstamp_full - Batch $batch_number : output $line_count lines, sleeping for $PAUSE_DURATION..."
        sleep "$PAUSE_DURATION"
    fi
done < "$SOURCE_FILE"

echo "Done copying in batches of $BATCH_SIZE lines."
