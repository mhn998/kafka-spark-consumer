package org.twitter.hbase;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.io.compress.Compression.Algorithm;
import org.apache.hadoop.hbase.util.Bytes;
import org.twitter.dto.Tweet;

import java.io.IOException;


public class TweetHbaseTableIn {
	
	public static Configuration config = HBaseConfiguration.create();
	public static Connection connection = null;
	
	public static Admin admin = null;
	
	private static final String TABLE_NAME = "tweets";
	private static final String CF_DEFAULT = "tweet-info";
	private static final String CF_GENERAL = "general-info";

	private final static byte[] CF_DEFAULT_BYTES = CF_DEFAULT.getBytes();
	private final static byte[] CF_GENERAL_BYTES = CF_GENERAL.getBytes();
		
	private static Table tweets;
	
	public static void init() {
		try {
			connection = ConnectionFactory.createConnection(config);
			admin = connection.getAdmin();

			// HTableDescriptor table = new HTableDescriptor(
			// 		TableName.valueOf(TABLE_NAME));
			// table.addFamily(new HColumnDescriptor(CF_DEFAULT)
			// 		.setCompressionType(Algorithm.NONE));
			// table.addFamily(new HColumnDescriptor(CF_GENERAL)
			// 		.setCompressionType(Algorithm.NONE));

			// System.out.print("Creating table.... ");

			// if (admin.tableExists(table.getTableName())) {
			// 	admin.disableTable(table.getTableName());
			// 	admin.deleteTable(table.getTableName());
			// }
			// admin.createTable(table);
			
			tweets = connection.getTable(TableName.valueOf(TABLE_NAME));

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static byte[] getBytes(String str) {
		if(str == null)
			return null;
		
		return Bytes.toBytes(str);
	}

	public static void populateData(Tweet tweet) throws IOException {
		Put row = new Put(tweet.getId().getBytes());

		row.addColumn(CF_DEFAULT_BYTES, "text".getBytes(), getBytes(tweet.getText()));

		row.addColumn(CF_DEFAULT_BYTES, "hashtags".getBytes(),
				getBytes(String.join(", ", tweet.getHashTags())));


		row.addColumn(CF_DEFAULT_BYTES, "is_retweet".getBytes(), getBytes(String.valueOf(tweet.isRetweet())));

		row.addColumn(CF_GENERAL_BYTES, "username".getBytes(), getBytes(tweet.getUsername()));

		row.addColumn(CF_GENERAL_BYTES, "timestamp_ms".getBytes(), getBytes(tweet.getTimeStamp()));
		row.addColumn(CF_GENERAL_BYTES, "lang".getBytes(), getBytes(tweet.getLang()));
		row.addColumn(CF_GENERAL_BYTES, "source".getBytes(), getBytes(tweet.getSource()));
		row.addColumn(CF_GENERAL_BYTES, "geo_bbox".getBytes(), getBytes(tweet.getGeoBbox()));

		tweets.put(row);
	}
}
