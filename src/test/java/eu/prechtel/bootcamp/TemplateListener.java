package eu.prechtel.bootcamp;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@EnableKafka
public class TemplateListener {
	final Logger log = LoggerFactory.getLogger(TemplateListener.class);

	@KafkaListener(
		groupId = "template-listener",
		topics = "example-kafka-topic",
		containerFactory = "kafkaListenerContainerFactory")
	public void consume(@Payload ConsumerRecord<String, String> record, @Header(KafkaHeaders.OFFSET) long offset, Acknowledgment ack) {

		if (System.currentTimeMillis() % 2L == 0) {
			log.info("TemplateListener  ACK: [{}] with [{}] and offset [{}]", record.key(), record.value(), offset);
			ack.acknowledge();
		} else {
			log.info("TemplateListener NACK: [{}] with [{}] and offset [{}]", record.key(), record.value(), offset);
			ack.nack(Duration.ofSeconds(1));
		}
	}
}
