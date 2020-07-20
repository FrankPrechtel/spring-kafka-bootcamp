package eu.prechtel.bootcamp;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.BlockingQueue;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@EmbeddedKafka(partitions = 1, topics = { "example.kafka.topic" }, ports = 9092, zookeeperPort = 2181)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ApplicationTests {

	static final String TOPIC = "example.kafka.topic";

	Logger logger = LoggerFactory.getLogger(ApplicationTests.class);

	final private EmbeddedKafkaBroker embeddedKafka;

	private BlockingQueue<ConsumerRecord<String, String>> records;

	private KafkaMessageListenerContainer<String, String> container;
	;

	ApplicationTests(@Autowired EmbeddedKafkaBroker embeddedKafka) {
		this.embeddedKafka = embeddedKafka;
	}
/*
	@BeforeAll
	void setUp() {
		Map<String, Object> consumerProps = new HashMap<>(KafkaTestUtils.consumerProps("consumer", "false", broker));
		// consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest"); // already set by Spring Kafka 2.5
		DefaultKafkaConsumerFactory<String, String> consumerFactory = new DefaultKafkaConsumerFactory<>(consumerProps);
		ContainerProperties containerProperties = new ContainerProperties(TOPIC);
		container = new KafkaMessageListenerContainer<>(consumerFactory, containerProperties);
		records = new LinkedBlockingQueue<>();
		container.setupMessageListener((MessageListener<String, String>) records::add);
		container.start();
		ContainerTestUtils.waitForAssignment(container, broker.getPartitionsPerTopic());
	}
*/

	@AfterAll
	void tearDown() {
//		container.stop();
	}

	@Test
	void basicCheck() {
		logger.info("BROKER: " + embeddedKafka.getBrokersAsString());
	}

	@Test
	void send() throws Exception {
		KafkaTemplate<Integer, String> kafkaTemplate = kafkaTemplate();
		final String value = "Hello Kafka!";
//		Map<String, Object> producerProps = new HashMap<>(KafkaTestUtils.producerProps(embeddedKafka));
//		Producer<String, String> producer = new DefaultKafkaProducerFactory<>(producerProps, new StringSerializer(), new StringSerializer()).createProducer();
//		producer.send(new ProducerRecord<>(UUID.randomUUID().toString(), value)).get(10000, TimeUnit.MILLISECONDS);
		logger.info("TESTTEST: SEND");
//		Thread.sleep(1000);
//		producer.flush();
		kafkaTemplate.send(TOPIC, new Random().nextInt(), value);
		kafkaTemplate.flush();
//		Thread.sleep(1000);

		Map<String, Object> consumerProps = KafkaTestUtils.consumerProps(TOPIC, "false", embeddedKafka);
		KafkaConsumer<Integer, String> consumer = new KafkaConsumer<>(consumerProps);
		embeddedKafka.consumeFromAllEmbeddedTopics(consumer);
		final ConsumerRecord<Integer, String> singleRecord = KafkaTestUtils.getSingleRecord(consumer, TOPIC);
		//ConsumerRecord<String, String> singleRecord = records.poll(10000, TimeUnit.MILLISECONDS);
		assertNotNull(singleRecord);
		assertEquals(singleRecord.value(), value);
	}

	private KafkaTemplate<Integer, String> kafkaTemplate() {
		KafkaTemplate<Integer, String> kafkaTemplate = new KafkaTemplate<>(producerFactory());
		kafkaTemplate.setDefaultTopic(TOPIC);
		return kafkaTemplate;
	}

	private ProducerFactory<Integer, String> producerFactory() {
		return new DefaultKafkaProducerFactory<>(KafkaTestUtils.producerProps(embeddedKafka));
	}
}
