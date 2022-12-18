CREATE EXTERNAL TABLE tweets (
    id Bigint,
    username String,
    text String,
    is_retweet boolean,
    hashtags String
)
STORED BY 'org.apache.hadoop.hive.hbase.HBaseStorageHandler'
    WITH SERDEPROPERTIES (
        'hbase.columns.mapping'=':key,general-info:username,tweet-info:text,tweet-info:is_retweet,tweet-info:hashtags'
    )
    TBLPROPERTIES(
        'hbase.table.name'='tweets',
        'hbase.mapred.output.outputtable'='tweets'
    );