package org.springframework.samples.petclinic.owner;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for {@link OwnerRepository} age-related functionality
 */
@DataJpaTest
@ActiveProfiles("test")
class OwnerRepositoryAgeTests {

    @Autowired
    private OwnerRepository ownerRepository;

    @Test
    @DisplayName("Should find owners by age")
    void testFindByAge() {
        // Arrange - Create owners with specific ages
        Owner owner1 = createOwner("John", "Doe", LocalDate.now().minusYears(35));
        Owner owner2 = createOwner("Jane", "Smith", LocalDate.now().minusYears(35));
        Owner owner3 = createOwner("Bob", "Johnson", LocalDate.now().minusYears(40));
        
        ownerRepository.save(owner1);
        ownerRepository.save(owner2);
        ownerRepository.save(owner3);
        
        // Act - Find owners by age
        Page<Owner> ownersAge35 = ownerRepository.findByAge(35, PageRequest.of(0, 10));
        Page<Owner> ownersAge40 = ownerRepository.findByAge(40, PageRequest.of(0, 10));
        Page<Owner> ownersAge50 = ownerRepository.findByAge(50, PageRequest.of(0, 10));
        
        // Assert
        assertThat(ownersAge35.getTotalElements()).isEqualTo(2);
        assertThat(ownersAge35.getContent())
            .extracting(Owner::getLastName)
            .containsExactlyInAnyOrder("Doe", "Smith");
            
        assertThat(ownersAge40.getTotalElements()).isEqualTo(1);
        assertThat(ownersAge40.getContent().get(0).getLastName()).isEqualTo("Johnson");
        
        assertThat(ownersAge50.isEmpty()).isTrue();
    }
    
    @Test
    @DisplayName("Should handle pagination in findByAge")
    void testFindByAgeWithPagination() {
        // Arrange - Create multiple owners with the same age
        for (int i = 0; i < 8; i++) {
            Owner owner = createOwner("Owner" + i, "LastName" + i, LocalDate.now().minusYears(45));
            ownerRepository.save(owner);
        }
        
        // Act - Get first page (size 5)
        Page<Owner> firstPage = ownerRepository.findByAge(45, PageRequest.of(0, 5));
        // Get second page (size 5)
        Page<Owner> secondPage = ownerRepository.findByAge(45, PageRequest.of(1, 5));
        
        // Assert
        assertThat(firstPage.getTotalElements()).isEqualTo(8);
        assertThat(firstPage.getTotalPages()).isEqualTo(2);
        assertThat(firstPage.getContent()).hasSize(5);
        
        assertThat(secondPage.getTotalElements()).isEqualTo(8);
        assertThat(secondPage.getContent()).hasSize(3);
    }
    
    @Test
    @DisplayName("Should handle edge cases in findByAge")
    void testFindByAgeEdgeCases() {
        // Arrange - Create owners with edge case ages
        Owner youngOwner = createOwner("Young", "Person", LocalDate.now().minusYears(1));
        Owner oldOwner = createOwner("Old", "Person", LocalDate.now().minusYears(100));
        Owner bornTodayOwner = createOwner("Born", "Today", LocalDate.now());
        
        ownerRepository.save(youngOwner);
        ownerRepository.save(oldOwner);
        ownerRepository.save(bornTodayOwner);
        
        // Act & Assert
        Page<Owner> age1 = ownerRepository.findByAge(1, PageRequest.of(0, 10));
        assertThat(age1.getTotalElements()).isEqualTo(1);
        assertThat(age1.getContent().get(0).getLastName()).isEqualTo("Person");
        
        Page<Owner> age100 = ownerRepository.findByAge(100, PageRequest.of(0, 10));
        assertThat(age100.getTotalElements()).isEqualTo(1);
        assertThat(age100.getContent().get(0).getFirstName()).isEqualTo("Old");
        
        Page<Owner> age0 = ownerRepository.findByAge(0, PageRequest.of(0, 10));
        assertThat(age0.getTotalElements()).isEqualTo(1);
        assertThat(age0.getContent().get(0).getFirstName()).isEqualTo("Born");
    }
    
    /**
     * Helper method to create an owner with the given details
     */
    private Owner createOwner(String firstName, String lastName, LocalDate birthDate) {
        Owner owner = new Owner();
        owner.setFirstName(firstName);
        owner.setLastName(lastName);
        owner.setAddress("123 Test St");
        owner.setCity("Test City");
        owner.setTelephone("1234567890");
        owner.setBirthDate(birthDate);
        return owner;
    }
}
