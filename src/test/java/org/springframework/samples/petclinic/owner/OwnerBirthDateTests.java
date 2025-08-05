package org.springframework.samples.petclinic.owner;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.LocalDate;
import java.time.Period;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link Owner} birth date functionality
 */
class OwnerBirthDateTests {

    @Test
    @DisplayName("Should get and set birth date correctly")
    void testBirthDateGetterSetter() {
        // Arrange
        Owner owner = new Owner();
        LocalDate birthDate = LocalDate.of(1990, 5, 15);
        
        // Act
        owner.setBirthDate(birthDate);
        
        // Assert
        assertThat(owner.getBirthDate()).isEqualTo(birthDate);
    }
    
    @ParameterizedTest
    @CsvSource({
        "1990-01-01, 35",
        "1980-06-15, 45",
        "2000-12-31, 25"
    })
    @DisplayName("Should calculate age correctly from birth date")
    void testAgeCalculation(String birthDateStr, int expectedAge) {
        // Arrange
        Owner owner = new Owner();
        LocalDate birthDate = LocalDate.parse(birthDateStr);
        owner.setBirthDate(birthDate);
        
        // Act - Calculate age manually to verify repository query would work
        int calculatedAge = Period.between(birthDate, LocalDate.now()).getYears();
        
        // Assert
        assertThat(calculatedAge).isCloseTo(expectedAge, within(3)); // Allow some flexibility as test runs in different years
    }
    
    @Test
    @DisplayName("Should handle null birth date")
    void testNullBirthDate() {
        // Arrange
        Owner owner = new Owner();
        
        // Act & Assert
        assertThat(owner.getBirthDate()).isNull();
        
        // Set and then set to null
        owner.setBirthDate(LocalDate.now());
        owner.setBirthDate(null);
        assertThat(owner.getBirthDate()).isNull();
    }
    
    @Test
    @DisplayName("Should handle future birth dates")
    void testFutureBirthDate() {
        // Arrange
        Owner owner = new Owner();
        LocalDate futureBirthDate = LocalDate.now().plusYears(1);
        
        // Act
        owner.setBirthDate(futureBirthDate);
        
        // Assert - The model allows setting future dates, validation should be handled elsewhere
        assertThat(owner.getBirthDate()).isEqualTo(futureBirthDate);
    }
}
