package eu.prechtel.bootcamp;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("kafka-embedded")
@SpringBootTest
@DirtiesContext
@EmbeddedKafka(
	partitions = 1,
	topics = "example-kafka-topic",
	//ports = 9092, zookeeperPort = 2181)
	bootstrapServersProperty = "spring.kafka.bootstrap-servers")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TemplateTests {

	Logger log = LoggerFactory.getLogger(TemplateTests.class);

	@Autowired
	private KafkaTemplate<String, String> embeddedKafka;

	@Autowired
	private HelloController controller;

	@Test
	void helloSync() throws Exception {
		log.info("helloSync()");
		controller.sendSync("synchrotron");
		controller.sendSync("synchrotron");
	}

	@Test
	void helloAsync() throws Exception {
		controller.sendAsync("async");
		controller.sendAsync("async");
	}
}
