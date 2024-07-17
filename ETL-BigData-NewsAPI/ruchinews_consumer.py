from kafka import KafkaConsumer
from hdfs import InsecureClient
import json
from datetime import datetime

# Kafka consumer configuration
brokers = "localhost:9092"

# Create the Kafka consumer
consumer = KafkaConsumer(
    "ruchi-news",
    bootstrap_servers=brokers,
    value_deserializer=lambda x: json.loads(x.decode("utf-8")),
)

# HDFS configuration
hdfs_client = InsecureClient("http://localhost:50070", user="hdfs")


# Function to process message and write to HDFS
def process_and_write_to_hdfs(message):
    # Add a processed timestamp field
    message["processed_timestamp"] = datetime.utcnow().isoformat()

    processed_data = json.dumps(message, indent=2).encode()

    # Write processed data to HDFS
    with hdfs_client.write(
        "/midterm/news_data.json", encoding="utf-8", overwrite=False, append=True
    ) as writer:
        writer.write(processed_data + "\n")


# Poll for new messages from Kafka and write to HDFS
try:
    for msg in consumer:
        print(msg)
        # Process and write the message to HDFS
        process_and_write_to_hdfs(msg.value)
except KeyboardInterrupt:
    pass
finally:
    # Close the consumer
    consumer.close()
