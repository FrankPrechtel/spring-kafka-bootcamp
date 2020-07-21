package eu.prechtel.bootcamp;

import org.apache.avro.AvroMissingFieldException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class AvroTest {
	@Test
	void createParticipant() {
		Participant participant = Participant.newBuilder()
			.setSurname("John")
			.setFamilyName("Smith")
			.setUuid(UUID.randomUUID().toString())
			.setFavoriteTextEditor("vi")
			.build();
	}

	// TODO: test the integrity of our type by leaving out a required field
	// @Test
	void missingRequiredFields() {
		assertThrows(AvroMissingFieldException.class, () -> {
			Participant participant = Participant.newBuilder()
				.setSurname("John")
				.setFamilyName("Smith")
				.setUuid(UUID.randomUUID().toString())
				.setFavoriteTextEditor("vi")
				.build();
		});
	}
}
