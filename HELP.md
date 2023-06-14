https://docs.cloudera.com/documentation/kafka/latest/topics/kafka_command_line.html

./kafka-topics --zookeeper localhost:2181 --list

./kafka-console-producer --broker-list localhost:9092 --topic example-kafka-topic < message.txt

bin/zookeeper-server-start etc/kafka/zookeeper.properties
bin/kafka-server-start etc/kafka/server.properties
bin/kafka-topics --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1 --topic example-kafka-topic
bin/kafka-consumer-groups.sh --bootstrap-server kafka-host:9092 --group my-group --reset-offsets
--to-datetime 2020-11-01T00:00:00Z --topic sales_topic --execute
