package org.twitter.consumer;

import com.google.gson.Gson;
import kafka.serializer.StringDecoder;
import org.apache.hadoop.io.IntWritable;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaPairInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka.KafkaUtils;

import org.json.JSONObject;
import org.twitter.dto.Tweet;
import org.twitter.hbase.TweetHbaseTableIn;

import java.util.*;

public class Listener {

//	static JavaSparkContext jsc;

	public static void main(String[] args) throws Exception {
		SparkConf conf = new SparkConf().setMaster("local[*]").setAppName("first-topic-listener");
		JavaSparkContext jsc = new JavaSparkContext(conf);
		JavaStreamingContext ssc = new JavaStreamingContext(jsc,
				Durations.seconds(5));

//		SQLContext sqlContext = new SQLContext(jsc.sc());


		Set<String> topics = new HashSet<>(Collections.singletonList("tweets"));
		Map<String, String> kafkaParams = new HashMap<>();
		kafkaParams.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
				"localhost:9092");
		kafkaParams.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
				"StringDeserializer");
		kafkaParams.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
				"StringDeserializer");
		kafkaParams.put(ConsumerConfig.GROUP_ID_CONFIG, "group1");

		JavaPairInputDStream<String, String> stream = KafkaUtils
				.createDirectStream(ssc, String.class, String.class,
						StringDecoder.class, StringDecoder.class, kafkaParams,
						topics);

		TweetHbaseTableIn.init();
		IntWritable count = new IntWritable(0);
		stream.foreachRDD(rdd -> {
			count.set(count.get() + 1);
			System.out.println("number of records processed:  "  + count);

			JavaRDD<Tweet> jrdd = rdd.map(f -> {

				JSONObject json = new JSONObject(f._2);
				String data = json.get("data").toString();


				Tweet tweet = new Gson().fromJson(data,
						Tweet.class);
				setNonPopulatedFields(tweet);

//				System.out.println(tweet);

//				TweetHbaseTableIn.populateData(tweet);

				return tweet;
			});

			jrdd.foreach(t -> {
//				DataFrame df = sqlContext.createDataFrame(jrdd, Tweet.class);
//				df.write().format("parquet").mode("append").save(args[0]);

				System.out.println("listener tweet" + t);
				TweetHbaseTableIn.populateData(t);
			});
		});

		ssc.start();
		ssc.awaitTermination();
	}

	public static void setNonPopulatedFields(Tweet tweet) {
		tweet.setHashTags();
		tweet.setUsername();
		tweet.setRetweet();
	}
}
