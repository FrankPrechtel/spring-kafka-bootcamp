package eu.prechtel.bootcamp;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@EnableKafka
public class TemplateListener {
	final Logger log = LoggerFactory.getLogger(TemplateListener.class);

	final KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;


	TemplateListener(@Autowired KafkaListenerEndpointRegistry registry) {
		this.kafkaListenerEndpointRegistry = registry;
	}


	@KafkaListener(
		groupId = "template-listener",
		topics = "example-kafka-topic")
	public void consume(@Payload ConsumerRecord<String, String> record, @Header(KafkaHeaders.OFFSET) long offset, Acknowledgment ack) {

		switch (Long.valueOf(record.offset()).intValue()) {
			case 8:
				log.info("############################################################");
				throw new RuntimeException("when the shit hits the fan");
				/*
				log.info("TemplateListener NACK: [{}] with [{}] and offset [{}]", record.key(), record.value(), offset);
				ack.nack(1_000L);
				break;
				*/
/*			case 10:
				log.info("stop processing");
				kafkaListenerEndpointRegistry.stop();
				// SPOILER: this won't work, the listener is already dead here
				log.info("start processing again");
				kafkaListenerEndpointRegistry.start();
				break;*/
			default:
				log.info("TemplateListener  ACK: [{}] with [{}] and offset [{}]", record.key(), record.value(), offset);
				ack.acknowledge();
		}
	}

	@KafkaListener(
		groupId = "template-listener",
		topics = "example-kafka-topic.DLT",
		containerFactory = "kafkaListenerContainerFactory")
	public void consumeDLT(@Payload ConsumerRecord<String, String> record, @Header(KafkaHeaders.OFFSET) long offset, Acknowledgment ack) {
		log.info("############################################################");
		log.info("received record in dead letter queue " + record.topic());
		log.info("############################################################");
		ack.acknowledge();
	}
}
