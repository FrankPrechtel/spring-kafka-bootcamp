package eu.prechtel.bootcamp;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.support.SendResult;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestConstructor;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.context.TestConstructor.AutowireMode.ALL;


@ActiveProfiles("kafka-embedded")
@DirtiesContext
@EmbeddedKafka(
	partitions = 1,
	controlledShutdown = true,
	topics = "example-kafka-topic",
	bootstrapServersProperty = "spring.kafka.bootstrap-servers"
)
@SpringBootTest
@TestConstructor(autowireMode = ALL)
public class TemplateTests {

	final Logger log = LoggerFactory.getLogger(TemplateTests.class);
	final EmbeddedKafkaBroker embeddedKafka;
	final HelloController controller;

	TemplateTests(
		EmbeddedKafkaBroker embeddedKafka,
		HelloController controller) {
		this.embeddedKafka = embeddedKafka;
		this.controller = controller;
	}

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
		// use https://github.com/awaitility/awaitility
		Thread.sleep(20_000L);
	}

	// @Test
	void helloAsync() {
		final CompletableFuture<SendResult<String, String>> async = controller.sendAsyncEvent("async");
	}
}
