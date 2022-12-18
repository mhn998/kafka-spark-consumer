package org.twitter.consumer;

import com.google.gson.Gson;
import kafka.serializer.StringDecoder;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.io.compress.Compression;
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

import java.io.IOException;
import java.util.*;

public class Listener {

	private static Configuration config = HBaseConfiguration.create();
	private static Connection connection = null;
	public static Admin admin = null;

	private static final String TABLE_NAME = "tweets";
	private static final String CF_DEFAULT = "tweet-info";
	private static final String CF_GENERAL = "general-info";

	private static Table tweets;


	public static void main(String[] args) throws Exception {
		SparkConf conf = new SparkConf().setMaster("local[*]").setAppName("first-topic-listener");
		JavaSparkContext jsc = new JavaSparkContext(conf);
		JavaStreamingContext ssc = new JavaStreamingContext(jsc,
				Durations.seconds(5));

		try {
			connection = ConnectionFactory.createConnection(config);
			admin = connection.getAdmin();

			HTableDescriptor table = new HTableDescriptor(
					TableName.valueOf(TABLE_NAME));
			table.addFamily(new HColumnDescriptor(CF_DEFAULT)
					.setCompressionType(Compression.Algorithm.NONE));
			table.addFamily(new HColumnDescriptor(CF_GENERAL)
					.setCompressionType(Compression.Algorithm.NONE));

			System.out.print("Creating table.... ");

			if (admin.tableExists(table.getTableName())) {
				admin.disableTable(table.getTableName());
				admin.deleteTable(table.getTableName());
			}
			admin.createTable(table);

			tweets = connection.getTable(TableName.valueOf(TABLE_NAME));

			System.out.println("tweet table" + tweets);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


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

				return tweet;
			});

			jrdd.foreach(t -> {
				System.out.println("listener tweet" + t);
				TweetHbaseTableIn.populateData(t);
			});
			return null;
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
