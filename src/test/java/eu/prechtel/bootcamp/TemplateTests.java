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
@EmbeddedKafka(partitions = 1, topics = "example-kafka-topic", zookeeperPort = 2181, ports = 9092, count = 1)// bootstrapServersProperty = "spring.kafka.bootstrap-servers")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TemplateTests {

	Logger logger = LoggerFactory.getLogger(TemplateTests.class);

	@Autowired
	private KafkaTemplate<String, String> embeddedKafka;

	@Autowired
	private HelloController controller;

	@Test
	void helloSync() throws Exception {
		logger.info("helloSync()");
		controller.sendSync("synchrotron");
		controller.sendSync("synchrotron");
	}

	@Test
	void helloAsync() throws Exception {
		controller.sendAsync("async my test");
		controller.sendAsync("async my test");
	}
}
