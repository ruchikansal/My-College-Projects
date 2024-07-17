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

csv_filename = "ruchi_news.csv"
csv_fields = [
    "source",
    "author",
    "title",
    "description",
    "url",
    "publishedAt",
    "content",
]


# Function to write messages to CSV
def write_messages_to_csv(messages):
    with open(csv_filename, mode="w", newline="", encoding="utf-8") as csv_file:
        writer = csv.DictWriter(csv_file, fieldnames=csv_fields)
        writer.writeheader()

        for message in messages:
            data = message

            # Write data to CSV
            writer.writerow(
                {
                    "source": data.get("source", ""),
                    "author": data.get("author", ""),
                    "title": data.get("title", ""),
                    "description": data.get("description", ""),
                    "url": data.get("url", ""),
                    "publishedAt": data.get("publishedAt", ""),
                    "content": data.get("content", ""),
                }
            )

    print(f"CSV file '{csv_filename}' created locally with {len(messages)} messages.")


# List to store messages
messages = []

# Consume messages from Kafka
for message in consumer:
    data = message.value
    messages.append(data)

    if len(messages) >= 100:
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
