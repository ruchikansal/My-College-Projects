from newsapi import NewsApiClient
import json
from kafka import KafkaProducer


# My free API key
key = "d4839af10a154cbd9977a97d26860a17"


# Initialize api endpoint
newsapi = NewsApiClient(api_key=key)


# Define the list of media sources
sources = (
   "bbc-news,cnn,fox-news,nbc-news,the-new-york-times,usa-today,independent,daily-mail,bbc.co.uk,"
   "techcrunch.com,engadget.com,cbc-news,the-hindu,the-times-of-india,google-news-ca"
)


# /v2/everything
all_articles = newsapi.get_everything(q="Bitcoin", sources=sources, language="en")


# Send the formatted news to the kafka topic
for article in all_articles["articles"]:
   if article["source"]["name"] != "[Removed]":
       print(article["title"])
       message = {
           "source": article["source"]["name"],
           "author": article["author"],
           "title": article["title"],
           "description": article["description"],
           "url": article["url"],
           "publishedAt": article["publishedAt"]
       }
       message_str = json.dumps(message).encode("utf-8")
       producer = KafkaProducer(bootstrap_servers="localhost:9092")
       producer.send("ruchi-news", message_str)