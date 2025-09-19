package org.springframework.samples.petclinic.model;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class PersonTest {

    @Test
    void testFirstNameGetterAndSetter() {
        Person person = new Person();
        person.setFirstName("John");
        assertThat(person.getFirstName()).isEqualTo("John");
    }

    @Test
    void testLastNameGetterAndSetter() {
        Person person = new Person();
        person.setLastName("Doe");
        assertThat(person.getLastName()).isEqualTo("Doe");
    }
}
