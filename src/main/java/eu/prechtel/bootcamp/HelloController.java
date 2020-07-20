package eu.prechtel.bootcamp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.UUID;
import java.util.concurrent.ExecutionException;

@Service
public class HelloController {

	private final KafkaTemplate<String, String> template;
	Logger logger = LoggerFactory.getLogger(HelloController.class);

	HelloController(@Autowired KafkaTemplate<String, String> template) {
		this.template = template;
	}

	void sendSync(String message) throws ExecutionException, InterruptedException {
		template.send(UUID.randomUUID().toString(), message).get();
	}

	void sendAsync(String message) {
		ListenableFuture<SendResult<String, String>> future =
			template.send(UUID.randomUUID().toString(), message);
		future.addCallback(new ListenableFutureCallback<SendResult<String, String>>() {

			@Override
			public void onSuccess(SendResult<String, String> result) {
				System.out.println("Sent message=[" + message +
					"] with offset=[" + result.getRecordMetadata().offset() + "]");
			}

			@Override
			public void onFailure(Throwable ex) {
				System.out.println("Unable to send message=["
					+ message + "] due to: " + ex.getMessage());
			}
		});
	}
}
