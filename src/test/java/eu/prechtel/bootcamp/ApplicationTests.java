package eu.prechtel.bootcamp;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.TopicPartition;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

@DirtiesContext
@EmbeddedKafka(
	partitions = 2,
	topics = "example-kafka-topic",
	controlledShutdown = true,
	zookeeperPort = 2181,
	ports = 9092)//bootstrapServersProperty = "spring.kafka.bootstrap-servers")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ApplicationTests {

	static final String TOPIC = "example-kafka-topic";
	static final String HELLO_KAFKA = "Hello Kafka!";
	final private EmbeddedKafkaBroker embeddedKafka;
	final Logger logger = LoggerFactory.getLogger(ApplicationTests.class);

	ApplicationTests(@Autowired EmbeddedKafkaBroker embeddedKafka) {
		this.embeddedKafka = embeddedKafka;
	}

	private Producer<Integer, String> getIntegerStringProducer() {
		Map<String, Object> producerProps = new HashMap<>(KafkaTestUtils.producerProps(embeddedKafka));
		return new DefaultKafkaProducerFactory<Integer, String>(producerProps).createProducer();
	}

	@Test
	void basicCheck() {
		logger.info("BROKER: " + embeddedKafka.getBrokersAsString());
	}

	@Test
	void simpleSend() throws Exception {
		Producer<Integer, String> producer = getIntegerStringProducer();
		producer.send(new ProducerRecord<Integer, String>(TOPIC, new Random().nextInt(), HELLO_KAFKA));
		producer.flush();
		producer.close();

		KafkaConsumer<Integer, String> consumer = getIntegerStringKafkaConsumer();
		embeddedKafka.consumeFromAllEmbeddedTopics(consumer);
		final ConsumerRecord<Integer, String> singleRecord = KafkaTestUtils.getSingleRecord(consumer, TOPIC, 1000L);
		logger.info("received record with key '{}', value '{}' on partition {}",
			singleRecord.key(),
			singleRecord.value(),
			singleRecord.partition());
		consumer.close();
		assertNotNull(singleRecord);
		assertEquals(singleRecord.value(), HELLO_KAFKA);
	}

	@Test
	void partitionDistribution() {
		Producer<Integer, String> producer = getIntegerStringProducer();

		for (int i=0; i<100; i++) {
			// should be random, but we try it with a fixed key first
			//producer.send(new ProducerRecord<Integer, String>(TOPIC, 0, value));
			producer.send(new ProducerRecord<Integer, String>(TOPIC, new Random().nextInt(), HELLO_KAFKA));
		}
		producer.flush();
		producer.close();

		KafkaConsumer<Integer, String> consumer = getIntegerStringKafkaConsumer();
		embeddedKafka.consumeFromAllEmbeddedTopics(consumer);
		final ConsumerRecords<Integer, String> records = KafkaTestUtils.getRecords(consumer, 1000L, 100);
		// TODO: output partition of each message
		// TODO: assert that both partitions are present in the resulting records
	}

	private KafkaConsumer<Integer, String> getIntegerStringKafkaConsumer() {
		Map<String, Object> consumerProps = KafkaTestUtils.consumerProps("my-consumer-group", "false", embeddedKafka);
		return new KafkaConsumer<>(consumerProps);
	}

	@Test
	void timeMachine() {
		Producer<Integer, String> producer = getIntegerStringProducer();
		for (int i=0; i<100; i++) {
			// should be random, but we try it with a fixed key first
			//producer.send(new ProducerRecord<Integer, String>(TOPIC, 0, value));
			producer.send(new ProducerRecord<Integer, String>(TOPIC, new Random().nextInt(), HELLO_KAFKA));
		}
		producer.flush();
		producer.close();

		KafkaConsumer<Integer, String> consumer = getIntegerStringKafkaConsumer();
		embeddedKafka.consumeFromAllEmbeddedTopics(consumer);
		final ConsumerRecords<Integer, String> records = KafkaTestUtils.getRecords(consumer, 1000L, 100);
		TopicPartition partition = new TopicPartition(TOPIC, 0);
		//consumer.seek(partition, 10L);
		final ConsumerRecords<Integer, String> replays = KafkaTestUtils.getRecords(consumer, 1000L);
		consumer.close();
		logger.info("replays: {}", replays.count());
		assertTrue(replays.isEmpty());
		//assertFalse(replays.isEmpty());
	}
}
