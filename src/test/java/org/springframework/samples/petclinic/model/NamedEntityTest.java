package org.springframework.samples.petclinic.model;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class NamedEntityTest {

    @Test
    void testNameGetterAndSetter() {
        NamedEntity entity = new NamedEntity();
        entity.setName("TestName");
        assertThat(entity.getName()).isEqualTo("TestName");
    }

    @Test
    void testToStringReturnsName() {
        NamedEntity entity = new NamedEntity();
        entity.setName("EntityName");
        assertThat(entity.toString()).isEqualTo("EntityName");
    }
}
