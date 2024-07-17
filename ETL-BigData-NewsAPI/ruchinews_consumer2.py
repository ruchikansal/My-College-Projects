from kafka import KafkaConsumer
import json
from datetime import datetime
import os
import subprocess

# Kafka consumer setup
consumer = KafkaConsumer(
    "ruchi-news",
    bootstrap_servers=["localhost:9092"],  # Replace with your Kafka broker address
    auto_offset_reset="earliest",
    enable_auto_commit=True,
    group_id="newsapi-consumer",
    value_deserializer=lambda x: json.loads(x.decode("utf-8")),
)


# Function to write messages to a local JSON file
def write_to_local_file(messages, local_filename):
    with open(local_filename, "w", encoding="utf-8") as file:
        json.dump(messages, file, indent=2)


# Function to copy the local file to HDFS
def copy_to_hdfs(local_filename, hdfs_path):
    command = f"hdfs dfs -copyFromLocal {local_filename} {hdfs_path}"
    result = subprocess.run(command, shell=True, capture_output=True)
    if result.returncode != 0:
        print(f"Failed to copy {local_filename} to HDFS: {result.stderr}")
    else:
        print(f"Successfully copied {local_filename} to HDFS")


# Collect messages and write to local file and then to HDFS
messages = []
try:
    for msg in consumer:
        messages.append(msg.value)
        if (
            len(messages) >= 10
        ):  # Write to local file every 100 messages, adjust as needed
            timestamp = datetime.utcnow().strftime("%Y%m%d%H%M%S")
            local_filename = f"news_data_{timestamp}.json"
            hdfs_path = (
                f"/midtermproject/{local_filename}"  # Adjust HDFS path as needed
            )

            # Write messages to local JSON file
            write_to_local_file(messages, local_filename)

            # Copy the local file to HDFS
            copy_to_hdfs(local_filename, hdfs_path)

            # Clear the messages list
            messages = []

            # Optionally, delete the local file after copying to HDFS
            os.remove(local_filename)

except KeyboardInterrupt:
    pass
finally:
    # Write any remaining messages to local file and HDFS
    if messages:
        timestamp = datetime.utcnow().strftime("%Y%m%d%H%M%S")
        local_filename = f"news_data_{timestamp}.json"
        hdfs_path = (
            f"/midtermproject/{local_filename}"  # Adjust HDFS path as needed
        )

        # Write messages to local JSON file
        write_to_local_file(messages, local_filename)

        # Copy the local file to HDFS
        copy_to_hdfs(local_filename, hdfs_path)

        # Optionally, delete the local file after copying to HDFS
        os.remove(local_filename)

    consumer.close()
