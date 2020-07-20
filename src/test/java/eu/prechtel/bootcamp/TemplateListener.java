package eu.prechtel.bootcamp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class TemplateListener {
	Logger logger = LoggerFactory.getLogger(TemplateListener.class);

	@KafkaListener(groupId = "bootcamp", topics = "example.kafka.topic")
	public void consume(String message) {
		logger.info("MESSAGE: {}", message);
	}
}
