package eu.prechtel.bootcamp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.UUID;
import java.util.concurrent.ExecutionException;

@Service
@EnableKafka
public class HelloController {

	final Logger log = LoggerFactory.getLogger(HelloController.class);
	private final KafkaTemplate<String, String> template;

	HelloController(@Autowired KafkaTemplate<String, String> template) {
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

	ListenableFuture<SendResult<String, String>> sendAsyncEvent(String message) {
		ListenableFuture<SendResult<String, String>> future =
			template.send("example-kafka-topic", UUID.randomUUID().toString(), message);
		template.flush();
		future.addCallback(new ListenableFutureCallback<>() {

			@Override
			public void onSuccess(SendResult<String, String> result) {
				log.info("Sent message=[{}] with offset=[{}]", message, result.getRecordMetadata().offset());
			}

			@Override
			public void onFailure(Throwable ex) {
				log.error("Unable to send message=[{}] due to: {}", message, ex.getMessage());
			}
		});
		return future;
	}

	@KafkaListener(groupId = "hello-controller", topics = "example-kafka-topic", containerFactory = "kafkaListenerContainerFactory")
	public void consume(String message) {
		log.info("MESSAGE: {}", message);
	}
}
