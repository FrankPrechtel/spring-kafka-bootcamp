package eu.prechtel.bootcamp;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.EnableKafka;

@Configuration
@EnableKafka
@Profile("kafka-live")
public class KafkaLiveConfig {
	// nothing to see here
}
