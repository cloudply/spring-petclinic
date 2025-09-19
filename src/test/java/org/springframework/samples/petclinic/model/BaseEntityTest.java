package org.springframework.samples.petclinic.model;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class BaseEntityTest {

    @Test
    void testIdGetterAndSetter() {
        BaseEntity entity = new BaseEntity();
        assertThat(entity.getId()).isNull();
        entity.setId(42);
        assertThat(entity.getId()).isEqualTo(42);
    }

    @Test
    void testIsNew() {
        BaseEntity entity = new BaseEntity();
        assertThat(entity.isNew()).isTrue();
        entity.setId(1);
        assertThat(entity.isNew()).isFalse();
    }
}
