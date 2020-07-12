package eu.prechtel.kafkabootcamp;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;

@SpringBootTest
@EmbeddedKafka
class KafkaBootcampApplicationTests {

    // TODO: constructor based injection with exception
    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    @Test
    void basicCheck() {
        System.out.println(embeddedKafkaBroker.getBrokersAsString());
    }
}
