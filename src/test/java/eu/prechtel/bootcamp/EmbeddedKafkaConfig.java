package eu.prechtel.bootcamp;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;

import java.util.HashMap;

@Configuration
@EnableKafka
@Profile("kafka-embedded")
public class EmbeddedKafkaConfig {

	Logger logger = LoggerFactory.getLogger(EmbeddedKafkaConfig.class);

	@Value("${spring.kafka.bootstrap-servers:localhost:9092}")
	private String bootstrapServers;

	@Bean
	public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory() {
		ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory();
		factory.setConsumerFactory(consumerFactory());
		factory.setConcurrency(1);
		return factory;
	}

	@Bean
	KafkaTemplate<String, String> kafkaTemplate() {
		KafkaTemplate<String, String> kafkaTemplate = new KafkaTemplate<>(producerFactory());
		return kafkaTemplate;
	}

	private ConsumerFactory<String, String> consumerFactory() {
		final DefaultKafkaConsumerFactory<String, String> consumerFactory =
			new DefaultKafkaConsumerFactory<>(
				getConsumerConfig(),
				new StringDeserializer(),
				new StringDeserializer());
		return consumerFactory;
	}

	private ProducerFactory<String, String> producerFactory() {
		HashMap<String, Object> producerConfig = getProducerConfig();
		return new DefaultKafkaProducerFactory<>(producerConfig, new StringSerializer(), new StringSerializer());
	}

	private HashMap<String, Object> getConsumerConfig() {
		HashMap<String, Object> consumerConfig = new HashMap<>();
		consumerConfig.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
		// https://docs.confluent.io/current/installation/configuration/consumer-configs.html#enable.auto.commit
		consumerConfig.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
		consumerConfig.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
		consumerConfig.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
		logger.info("consumerConfig: {}", consumerConfig);
		return consumerConfig;
	}

	private HashMap<String, Object> getProducerConfig() {
		HashMap<String, Object> producerConfig = new HashMap<>();
		producerConfig.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
		producerConfig.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		producerConfig.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		producerConfig.put(ProducerConfig.RETRIES_CONFIG, 1);
		// https://docs.confluent.io/current/installation/configuration/producer-configs.html#acks
		//producerConfig.put(ProducerConfig.ACKS_CONFIG, "1"); // or "all"
		logger.info("producerConfig: {}", producerConfig);
		return producerConfig;
	}
}
