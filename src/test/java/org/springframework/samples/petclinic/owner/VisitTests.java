package org.springframework.samples.petclinic.owner;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link Visit}.
 */
class VisitTests {

	private Visit visit;
	
	@BeforeEach
	void setup() {
		visit = new Visit();
	}
	
	@Test
	void shouldSetAndGetDate() {
		// given
		LocalDate date = LocalDate.of(2023, 5, 15);
		
		// when
		visit.setDate(date);
		
		// then
		assertThat(visit.getDate()).isEqualTo(date);
	}
	
	@Test
	void shouldSetAndGetDescription() {
		// given
		String description = "Annual checkup and vaccination";
		
		// when
		visit.setDescription(description);
		
		// then
		assertThat(visit.getDescription()).isEqualTo(description);
	}
	
	@Test
	void shouldHaveDefaultDateAsToday() {
		// given a new visit
		Visit newVisit = new Visit();
		
		// then
		assertThat(newVisit.getDate()).isEqualTo(LocalDate.now());
	}
}
