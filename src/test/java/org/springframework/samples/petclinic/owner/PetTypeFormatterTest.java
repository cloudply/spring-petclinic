package org.springframework.samples.petclinic.owner;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PetTypeFormatterTest {

    @Test
    void testPrint() {
        PetType type = new PetType();
        type.setName("Dog");
        PetTypeFormatter formatter = new PetTypeFormatter(Mockito.mock(OwnerRepository.class));
        assertThat(formatter.print(type, Locale.ENGLISH)).isEqualTo("Dog");
    }

    @Test
    void testParseFound() throws Exception {
        PetType dog = new PetType();
        dog.setName("Dog");
        PetType cat = new PetType();
        cat.setName("Cat");
        OwnerRepository repo = Mockito.mock(OwnerRepository.class);
        Mockito.when(repo.findPetTypes()).thenReturn(Arrays.asList(dog, cat));
        PetTypeFormatter formatter = new PetTypeFormatter(repo);
        assertThat(formatter.parse("Dog", Locale.ENGLISH)).isEqualTo(dog);
    }

    @Test
    void testParseNotFoundThrows() {
        OwnerRepository repo = Mockito.mock(OwnerRepository.class);
        Mockito.when(repo.findPetTypes()).thenReturn(Arrays.asList());
        PetTypeFormatter formatter = new PetTypeFormatter(repo);
        assertThrows(ParseException.class, () -> formatter.parse("Hamster", Locale.ENGLISH));
    }
}
