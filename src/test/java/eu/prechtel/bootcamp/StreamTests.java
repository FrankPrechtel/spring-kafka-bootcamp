package eu.prechtel.bootcamp;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestConstructor;

import java.util.Properties;
import java.util.UUID;
import java.util.stream.IntStream;

import static org.springframework.test.context.TestConstructor.AutowireMode.ALL;

@ActiveProfiles("kafka-embedded")
@DirtiesContext
@EmbeddedKafka(
	partitions = 1,
	controlledShutdown = true,
	topics = {"input-kafka-topic", "output-kafka-topic"})
@SpringBootTest
@TestConstructor(autowireMode = ALL)
public class StreamTests {
	final Logger log = LoggerFactory.getLogger(StreamTests.class);

	final KafkaTemplate<String, String> template;

	public StreamTests(KafkaTemplate<String, String> template) {
		this.template = template;
	}

	@Test
	void fireAndForget() throws InterruptedException {
		IntStream.rangeClosed(0, 20).forEach(i ->
			template.send(
				"input-kafka-topic",
				UUID.randomUUID().toString(),
				"Streaming counter: " + i));
		// use https://github.com/awaitility/awaitility
		Thread.sleep(1_000L);
	}

	// @Test
	public void streamer() {
		Properties props = new Properties();
		props.put(StreamsConfig.APPLICATION_ID_CONFIG, "wordcount-application");
		props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
		props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
		props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());

		StreamsBuilder builder = new StreamsBuilder();
		KStream<String, String> textLines = builder.stream("input-kafka-topic");
		textLines.mapValues(value -> String.valueOf(value.length())).to("output-kafka-topic");

		KafkaStreams streams = new KafkaStreams(builder.build(), props);
		streams.start();
	}

	@KafkaListener(groupId = "input-listener", topics = "input-kafka-topic", containerFactory = "kafkaListenerContainerFactory")
	public void consumeInput(@Payload ConsumerRecord<String, String> record, @Header(KafkaHeaders.OFFSET) long offset, Acknowledgment ack) {
		log.info("MESSAGE  ACK: [{}] with [{}] and offset [{}]", record.key(), record.value(), offset);
		ack.acknowledge();
	}

	@KafkaListener(groupId = "output-listener", topics = "output-kafka-topic", containerFactory = "kafkaListenerContainerFactory")
	public void consumeOutput(@Payload ConsumerRecord<String, Long> record, @Header(KafkaHeaders.OFFSET) long offset, Acknowledgment ack) {
		log.info("MESSAGE  ACK: [{}] with [{}] and offset [{}]", record.key(), record.value(), offset);
		ack.acknowledge();
	}
}
