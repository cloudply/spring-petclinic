package org.springframework.samples.petclinic.owner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Extended tests for {@link PetTypeFormatter}
 */
@ExtendWith(MockitoExtension.class)
class PetTypeFormatterExtendedTests {

    @Mock
    private OwnerRepository owners;

    private PetTypeFormatter petTypeFormatter;

    private List<PetType> petTypes;

    @BeforeEach
    void setup() {
        petTypeFormatter = new PetTypeFormatter(owners);
        petTypes = new ArrayList<>();
        
        PetType dog = new PetType();
        dog.setName("Dog");
        petTypes.add(dog);
        
        PetType cat = new PetType();
        cat.setName("Cat");
        petTypes.add(cat);
        
        PetType bird = new PetType();
        bird.setName("Bird");
        petTypes.add(bird);
        
        given(this.owners.findPetTypes()).willReturn(petTypes);
    }

    @Test
    void testPrintObject() {
        PetType petType = petTypes.get(0);
        String petTypeName = petTypeFormatter.print(petType, Locale.ENGLISH);
        assertEquals("Dog", petTypeName);
    }

    @Test
    void testPrintNull() {
        String result = petTypeFormatter.print(null, Locale.ENGLISH);
        assertEquals("", result);
    }

    @Test
    void testParseWithExactMatch() throws ParseException {
        PetType petType = petTypeFormatter.parse("Dog", Locale.ENGLISH);
        assertEquals("Dog", petType.getName());
    }

    @Test
    void testParseWithDifferentCase() throws ParseException {
        PetType petType = petTypeFormatter.parse("dog", Locale.ENGLISH);
        assertEquals("Dog", petType.getName());
    }

    @Test
    void testParseWithLeadingAndTrailingSpaces() throws ParseException {
        PetType petType = petTypeFormatter.parse("  Dog  ", Locale.ENGLISH);
        assertEquals("Dog", petType.getName());
    }

    @Test
    void testParseWithNonExistingType() {
        assertThrows(ParseException.class, () -> {
            petTypeFormatter.parse("Tiger", Locale.ENGLISH);
        });
    }

    @Test
    void testParseWithEmptyString() {
        assertThrows(ParseException.class, () -> {
            petTypeFormatter.parse("", Locale.ENGLISH);
        });
    }
}
