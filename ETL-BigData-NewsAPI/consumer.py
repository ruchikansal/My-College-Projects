from kafka import KafkaConsumer
import json
import csv
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

# Function to write messages to CSV
def write_messages_to_csv(messages):
    data_file = open("ruchi_news.csv", "w", newline="")

    # create the csv writer object
    csv_writer = csv.writer(data_file)

    count = 0

    for msg in messages:
        if count == 0:
            # Writing headers of CSV file
            header = [
                "source",
                "author",
                "title",
                "description",
                "url",
                "publishedAt"
            ]
            csv_writer.writerow(header)
            count += 1

        # Writing data of CSV file
        csv_writer.writerow(msg.values())

    data_file.close()

    print(f"CSV file created locally with {len(messages)} messages.")

# List to store messages
messages = []

# Consume messages from Kafka
for message in consumer:
    data = message.value
    data["title"] = data["title"].replace(",", "")
    data["description"] = data["description"].replace(",", "")
    print(data)
    messages.append(data)

    if len(messages) >= 85:
        break

consumer.close()

write_messages_to_csv(messages)

# Copy CSV file to HDFS
local_csv_path = "./ruchi_news.csv"
hdfs_path = "/midtermproject/ruchi_news.csv"

copy_command = f"hdfs dfs -copyFromLocal {local_csv_path} {hdfs_path}"

result = subprocess.run(copy_command, shell=True, check=True)

# Check if the copy was successful
if result.returncode == 0:
    print(f"CSV file '{local_csv_path}' copied to HDFS successfully at '{hdfs_path}'.")
else:
    print(f"Error copying CSV file to HDFS.")
