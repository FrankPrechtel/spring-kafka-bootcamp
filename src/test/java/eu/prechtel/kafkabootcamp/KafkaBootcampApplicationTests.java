package eu.prechtel.kafkabootcamp;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;

@EmbeddedKafka
class KafkaBootcampApplicationTests {

    private EmbeddedKafkaBroker broker;

	public KafkaBootcampApplicationTests(@Autowired EmbeddedKafkaBroker broker) {
		this.broker = broker;
	}

	@Test
    void basicCheck() {
        System.out.println(broker.getBrokersAsString());
    }
}
