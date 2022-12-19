# Submit to yarn cluster with driver running in the cluster
spark-submit \
--class "org.twitter.consumer.Listener" \
--master yarn \
--deploy-mode cluster \
sparkConsumer.jar