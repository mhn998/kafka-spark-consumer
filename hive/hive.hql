CREATE EXTERNAL TABLE tweets (
    id Bigint,
    username String,
    text String,
    is_retweet boolean,
    hashtags String,
    timestamp_ms Bigint,
    lang String,
    source String,
    geo_bbox String
)
STORED BY 'org.apache.hadoop.hive.hbase.HBaseStorageHandler'
    WITH SERDEPROPERTIES (
        'hbase.columns.mapping'=':key,general-info:username,tweet-info:text,tweet-info:is_retweet,tweet-info:hashtags,general-info:timestamp_ms,general-info:lang,general-info:source,general-info:geo_bbox'
    )
    TBLPROPERTIES(
        'hbase.table.name'='tweets',
        'hbase.mapred.output.outputtable'='tweets'
    );
