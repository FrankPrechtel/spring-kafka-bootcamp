package eu.prechtel.bootcamp;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles("kafka-embedded")
@SpringBootTest
@DirtiesContext
@EmbeddedKafka(
	partitions = 1,
	controlledShutdown = true,
	topics = "example-kafka-topic",
	ports = 9092, zookeeperPort = 2181
//	bootstrapServersProperty = "spring.kafka.bootstrap-servers"
)
//@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TemplateTests {

	final Logger log = LoggerFactory.getLogger(TemplateTests.class);

	@Autowired
	private HelloController controller;

	@Test
	void helloSync() throws ExecutionException, InterruptedException {
		final long startOffset = controller.sendSyncEvent("synchrotron").getRecordMetadata().offset();
		log.debug("startOffset: {}", startOffset);
		for (int i = 0; i < 10; i++) {
			controller.sendSyncEvent("synchrotron");
		}
		final long stopOffset = controller.sendSyncEvent("synchrotron").getRecordMetadata().offset();
		log.debug("stopOffset: {}", stopOffset);
		assertEquals(11, stopOffset - startOffset);
	}

	@Test
	void helloAsync() {
		controller.sendAsyncEvent("async");
		controller.sendAsyncEvent("async");
	}
}
