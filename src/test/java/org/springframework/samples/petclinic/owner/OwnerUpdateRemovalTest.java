package org.springframework.samples.petclinic.owner;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests to verify that owner update functionality has been removed
 */
@WebMvcTest(OwnerController.class)
public class OwnerUpdateRemovalTest {

    private static final int TEST_OWNER_ID = 1;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OwnerRepository owners;

    private Owner george;

    @BeforeEach
    void setup() {
        george = new Owner();
        george.setId(TEST_OWNER_ID);
        george.setFirstName("George");
        george.setLastName("Franklin");
        george.setAddress("110 W. Liberty St.");
        george.setCity("Madison");
        george.setTelephone("6085551023");
        george.setAge(40);

        given(this.owners.findById(TEST_OWNER_ID)).willReturn(george);
    }

    @Test
    void testOwnerDetailsDoesNotContainEditButton() throws Exception {
        MvcResult result = mockMvc.perform(get("/owners/{ownerId}", TEST_OWNER_ID))
            .andExpect(status().isOk())
            .andExpect(model().attribute("owner", george))
            .andExpect(view().name("owners/ownerDetails"))
            .andReturn();
        
        String content = result.getResponse().getContentAsString();
        assertThat(content).doesNotContain("Edit Owner");
        assertThat(content).doesNotContain("/edit");
    }

    @Test
    void testEditOwnerEndpointNotAvailable() throws Exception {
        mockMvc.perform(get("/owners/{ownerId}/edit", TEST_OWNER_ID))
            .andExpect(status().isNotFound());
    }

    @Test
    void testUpdateOwnerEndpointNotAvailable() throws Exception {
        mockMvc.perform(post("/owners/{ownerId}/edit", TEST_OWNER_ID)
            .param("firstName", "Updated")
            .param("lastName", "Owner")
            .param("address", "Updated Address")
            .param("city", "Updated City")
            .param("telephone", "0987654321")
            .param("age", "35"))
            .andExpect(status().isNotFound());
    }
}
