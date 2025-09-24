package org.springframework.samples.petclinic.owner;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.validation.Errors;

/**
 * Unit tests for {@link PetValidator}
 */
class PetValidatorTests {

    private PetValidator validator;
    private Pet pet;
    private Errors errors;

    @BeforeEach
    void setup() {
        validator = new PetValidator();
        pet = new Pet();
        errors = mock(Errors.class);
    }

    @Test
    void testSupports() {
        assertTrue(validator.supports(Pet.class));
        assertFalse(validator.supports(Object.class));
    }

    @Test
    void testValidateWithEmptyName() {
        // Pet with empty name
        pet.setName("");
        pet.setBirthDate(LocalDate.now());
        PetType petType = new PetType();
        petType.setName("Dog");
        pet.setType(petType);
        
        validator.validate(pet, errors);
        
        verify(errors).rejectValue("name", "required", "required");
    }

    @Test
    void testValidateWithNullName() {
        // Pet with null name
        pet.setName(null);
        pet.setBirthDate(LocalDate.now());
        PetType petType = new PetType();
        petType.setName("Dog");
        pet.setType(petType);
        
        validator.validate(pet, errors);
        
        verify(errors).rejectValue("name", "required", "required");
    }

    @Test
    void testValidateWithNullType() {
        // Pet with valid name but null type
        pet.setName("Fluffy");
        pet.setBirthDate(LocalDate.now());
        pet.setType(null);
        
        validator.validate(pet, errors);
        
        verify(errors).rejectValue("type", "required", "required");
    }

    @Test
    void testValidateWithNullBirthDate() {
        // Pet with valid name and type but null birthdate
        pet.setName("Fluffy");
        PetType petType = new PetType();
        petType.setName("Dog");
        pet.setType(petType);
        pet.setBirthDate(null);
        
        validator.validate(pet, errors);
        
        verify(errors).rejectValue("birthDate", "required", "required");
    }
}
