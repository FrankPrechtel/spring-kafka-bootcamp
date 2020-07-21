package eu.prechtel.bootcamp;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("kafka-embedded")
@SpringBootTest
@DirtiesContext
//@EmbeddedKafka(partitions = 1, topics = "example-kafka-topic", zookeeperPort = 2182, ports = 9093, count = 1)// bootstrapServersProperty = "spring.kafka.bootstrap-servers")
@EmbeddedKafka(
	partitions = 1,
	topics = "example-kafka-topic",
	zookeeperPort = 2181,
	ports = 9092,
	count = 1,
	brokerProperties = {
		"port = 9092",
		"advertised.host.name = localhost",
		"advertised.listeners = PLAINTEXT://127.0.0.1:9092",
		"listeners = PLAINTEXT://127.0.0.1:9092"
	})// bootstrapServersProperty = "spring.kafka.bootstrap-servers")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TemplateTests {

	Logger logger = LoggerFactory.getLogger(TemplateTests.class);

	//@Autowired
	//private KafkaTemplate<String, String> embeddedKafka;

	@Autowired
	private HelloController controller;

	@Test
	void helloSync() throws Exception {
		logger.info("helloSync()");
		//for (int i=0; i<100; i++) Thread.sleep(500L);
		controller.sendSync("sync my test");
		Thread.sleep(2000L);
		controller.sendSync("sync my test");
		//for (int i=0; i<100; i++) Thread.sleep(500L);
	}

	@Test
	void helloAsync() throws Exception {
		controller.sendAsync("async my test");
		Thread.sleep(2000L);
	}

	@KafkaListener(groupId = "template-tests", topics = "example-kafka-topic", containerFactory = "kafkaListenerContainerFactory")
	public void consume(String message) {
		logger.info("MESSAGE: {}", message);
		System.exit(1);
	}


	public static void waitForAssignment(KafkaMessageListenerContainer<String, String> container, int partitions)
		throws Exception {

		Logger logger = LoggerFactory.getLogger(TemplateTests.class);

		logger.info(
			"Waiting for " + container.getContainerProperties().getTopics() + " to connect to " + partitions + " " +
				"partitions.");

		int n = 0;
		int count = 0;
		while (n++ < 600 && count < partitions) {
			count = 0;

			if (container.getAssignedPartitions() != null) {
				count = container.getAssignedPartitions().size();
			}
			if (count < partitions) {
				Thread.sleep(100);
			}
		}
	}

}
