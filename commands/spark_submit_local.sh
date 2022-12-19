# Submit to local (1 thread per core)
spark-submit \
--class "org.twitter.consumer.Listener" \
--master local[*] \
sparkConsumer.jar