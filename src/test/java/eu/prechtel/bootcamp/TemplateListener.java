package eu.prechtel.bootcamp;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@EnableKafka
public class TemplateListener {
	Logger logger = LoggerFactory.getLogger(TemplateListener.class);

	@KafkaListener(groupId = "template-listener", topics = "example.kafka.topic", containerFactory = "kafkaListenerContainerFactory")
	public void consume(ConsumerRecord<String, String> message) {
		logger.info("MESSAGE: {} # ", message.key(), message.value());
		System.exit(1);
	}
}
