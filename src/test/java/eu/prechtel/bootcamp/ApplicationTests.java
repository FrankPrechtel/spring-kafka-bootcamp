package eu.prechtel.bootcamp;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DirtiesContext
@EmbeddedKafka(partitions = 2, topics = {"example.kafka.topic"}, controlledShutdown = true, ports = 9092, zookeeperPort = 2181)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ApplicationTests {

	static final String TOPIC = "example.kafka.topic";
	final private EmbeddedKafkaBroker embeddedKafka;
	Logger logger = LoggerFactory.getLogger(ApplicationTests.class);

	ApplicationTests(@Autowired EmbeddedKafkaBroker embeddedKafka) {
		this.embeddedKafka = embeddedKafka;
	}

	@Test
	void basicCheck() {
		logger.info("BROKER: " + embeddedKafka.getBrokersAsString());
	}

	@Test
	void send() throws Exception {
		final String value = "Hello Kafka!";
		Map<String, Object> producerProps = new HashMap<>(KafkaTestUtils.producerProps(embeddedKafka));
		Producer<Integer, String> producer = new DefaultKafkaProducerFactory<Integer, String>(producerProps).createProducer();
		producer.send(new ProducerRecord<Integer, String>(TOPIC, new Random().nextInt(), value));
		producer.flush();

		Map<String, Object> consumerProps = KafkaTestUtils.consumerProps(TOPIC, "false", embeddedKafka);
		KafkaConsumer<Integer, String> consumer = new KafkaConsumer<>(consumerProps);
		embeddedKafka.consumeFromAllEmbeddedTopics(consumer);
		final ConsumerRecord<Integer, String> singleRecord = KafkaTestUtils.getSingleRecord(consumer, TOPIC);
		logger.info("received record with key '{}', value '{}' on partition {}",
			singleRecord.key(),
			singleRecord.value(),
			singleRecord.partition());
		assertNotNull(singleRecord);
		assertEquals(singleRecord.value(), value);
	}
}
