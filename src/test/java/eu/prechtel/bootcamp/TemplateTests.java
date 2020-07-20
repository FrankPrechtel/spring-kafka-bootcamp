package eu.prechtel.bootcamp;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("kafka-embedded")
@SpringBootTest
@DirtiesContext
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TemplateTests {

	Logger logger = LoggerFactory.getLogger(TemplateTests.class);

	final private EmbeddedKafkaBroker embeddedKafka = new EmbeddedKafkaBroker(1, false, 2, "example.kafka.topic");
	final private HelloController controller;

	TemplateTests(@Autowired HelloController controller) {
		this.controller = controller;

	}

	@BeforeAll
	void setup() {
		embeddedKafka.setZkPort(2181);
		embeddedKafka.kafkaPorts(9092);
		embeddedKafka.afterPropertiesSet();
	}

	@AfterAll
	void destroy() {
		embeddedKafka.destroy();
	}

	@Test
	void helloSync() throws Exception {
		controller.sendSync("Sync my test.");
		Thread.sleep(1000L);
	}

	@Test
	void helloAsync() throws Exception {
		controller.sendAsync("Async my test.");
	}
}
