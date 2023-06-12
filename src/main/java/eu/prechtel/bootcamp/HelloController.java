package eu.prechtel.bootcamp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
@EnableKafka
public class HelloController {

	final Logger log = LoggerFactory.getLogger(HelloController.class);
	private final KafkaTemplate<String, String> template;

	HelloController(KafkaTemplate<String, String> template) {
		this.template = template;
	}

	SendResult<String, String> sendSyncEvent(String message) throws ExecutionException, InterruptedException {
		final SendResult<String, String> sendResult =
			template
				.send("example-kafka-topic", UUID.randomUUID().toString(), message)
				.get();
		log.info("sendResult: {}", sendResult.getRecordMetadata().offset());
		return sendResult;
	}

	CompletableFuture<SendResult<String, String>> sendAsyncEvent(String message) {
		CompletableFuture<SendResult<String, String>> future =
			template.send("example-kafka-topic", UUID.randomUUID().toString(), message);
		return future;
	}

	@KafkaListener(groupId = "hello-controller", topics = "example-kafka-topic", containerFactory = "kafkaListenerContainerFactory")
	public void consume(String message) {
		log.info("MESSAGE: {}", message);
	}
}
