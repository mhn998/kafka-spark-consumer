
# Project name : Twitter

____

## Contributors:
1. Baraa Batta
2. Kidist Zihon
3. Muhenned Elmughrabi
4. Ranjan Paudel


## Synopsis:

* A project about getting current tweets as a stream in order to be stored in real time, retrieved later and do analysis on the data.

### Mission:

* Because of the huge amount of data that is continuously generated in seconds which we can use them to enhance our decisions, this project demonstrates the big data tools and technologies and how to use them to deal with these scenarios and huge data in real time.

___

## Features:

1. Create a topic you are interested in to collect info and insights about from twitter.

2. persist data in a way that allows us to handle big volume.

3. later on applying analysis and query

___


## Motivation:

**What is the vision of this product?**

> * this small demo will help us simulate and understand how to deal with big amount of data in real time in production and on cluster mode.


**What pain point does this project solve?**

> *efficient way to handle, store and analyze data that is generated at a fast pace

____


## Installation and usage:

### Cloudera quick start - CentOS is preferred

1. `Clone two Repositories` [producer](https://github.com/mhn998/kafka-twitter-producer) | [consumer](https://github.com/mhn998/kafka-spark-consumer)

2. Start Zookeeper server and kafka server using ```bin/zookeeper-server-start.sh config/zookeeper.properties & bin/kafka-server-start.sh config/server.properties ```


3. create kafka topic using bin/kafka-topic --create topic_name(e.g tweets as general in our case) --bootstrapserver localhost:9092`


4.
```
run kafka-twitter-producer project from the main class TwitterProducer.java using intellij/eclipse setting on VM or docker container by building a fat jar (mvn package), copy it to container and run it  
using java -jar twitter-producer-jar-with-dependencies.jar

```

5.
```

run kafka consumer from the main class Listener.java using intellij/eclipse setting on VM or docker container by building a fat jar (mvn package), copy it to container and run it 
using java -jar twitter-consumer-jar-with-dependencies.jar

```


6.
```
fetchig of real time streams will automatically starting when running producer. on the other hand the listener will be listening to these events on that topic continuously 
using spark streaming in order to be able to save this data directly to our hbase table with two main column familis, tweet info and general info

```

7. Optionally start hive script to create external table for hive

8. Optionally start sparkSql class in consumer to do further real time queries on the data in Hbase


### OS:
No specific OS needed. since we are using VM/Virtual Box (any virtualization technology) and Cloudera quick start image or container.
all other dependencies are already included on these images
____


## Software Requirements:

1. any OS(linux, Windows , Mac) has VM with Cloudera quick start or Docker with cloudera quick start image (includes: Hadoop, Spark, Hbase, Hive)

2. Kafka is needed for this project.

3. Maven as build management tool for client dependencies

4. IDE(Intellij/Eclipse)


____

## non-functional requirments:

* Data integrity.
* Portability.
* Reliability.
* Adaptability
____


## API references:

https://developer.twitter.com/en/docs/twitter-api

**We used twitter api v2 to get the data from 2/search/filter endpoint. it needs authorization using a bearer token or OAuth.

____







