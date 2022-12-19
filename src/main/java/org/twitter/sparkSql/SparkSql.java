package org.twitter.sparkSql;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableInputFormat;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.SQLContext;
import org.twitter.dto.Tweet;

import java.util.Collections;


public class SparkSql {
    private static final String TABLE_NAME = "tweets";
    private static final String CF_DEFAULT = "tweet-info";
    private static final String CF_GENERAL = "general-info";

    static Configuration config;
    static JavaSparkContext jsc;

    public static void main(String[] args) {

        SparkConf sconf = new SparkConf().setAppName("SparkSQL")
                .setMaster("local[*]");
        sconf.registerKryoClasses(new Class[] { org.apache.hadoop.hbase.io.ImmutableBytesWritable.class });

        config = HBaseConfiguration.create();
        config.set(TableInputFormat.INPUT_TABLE, TABLE_NAME);

        jsc = new JavaSparkContext(sconf);
        SQLContext sqlContext = new SQLContext(jsc.sc());

        JavaPairRDD<ImmutableBytesWritable, Result> hBaseRDD = readTableByJavaPairRDD();
        System.out.println("Number of rows in hbase table: " + hBaseRDD.count());

        JavaRDD<Tweet> rows = hBaseRDD.map(x -> {
            Tweet tweet = new Tweet();

            tweet.setId(Bytes.toString(x._1.get()));
            tweet.setText(Bytes.toString(x._2.getValue(Bytes.toBytes(CF_DEFAULT), Bytes.toBytes("text"))));
            tweet.setRetweet(Bytes.toBoolean(x._2.getValue(Bytes.toBytes(CF_DEFAULT), Bytes.toBytes("is_retweet"))));

            tweet.setUsername(Bytes.toString(x._2.getValue(Bytes.toBytes(CF_GENERAL), Bytes.toBytes("username"))));
            tweet.setHashTags(Collections.singletonList(Bytes.toString(x._2.getValue(Bytes.toBytes(CF_DEFAULT), Bytes.toBytes("hashtags")))));

            return tweet;
        });

        DataFrame tabledata = sqlContext
                .createDataFrame(rows, Tweet.class);

        tabledata.registerTempTable(TABLE_NAME);
        tabledata.printSchema();


        DataFrame query1 = sqlContext
                .sql("select username, count(*) from tweets group by username order by count(*) desc limit 10");
        query1.show();

        jsc.stop();

    }

    public static JavaPairRDD<ImmutableBytesWritable, Result> readTableByJavaPairRDD() {

        JavaPairRDD<ImmutableBytesWritable, Result> hBaseRDD = jsc
                .newAPIHadoopRDD(
                        config,
                        TableInputFormat.class,
                        org.apache.hadoop.hbase.io.ImmutableBytesWritable.class,
                        org.apache.hadoop.hbase.client.Result.class);
        return hBaseRDD;
    }


}
