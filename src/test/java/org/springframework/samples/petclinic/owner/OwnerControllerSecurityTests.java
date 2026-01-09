/*
 * Copyright 2012-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.samples.petclinic.owner;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledInNativeImage;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.aot.DisabledInAotMode;
import org.springframework.test.web.servlet.MockMvc;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

/**
 * Test class for {@link OwnerController} focusing on security vulnerabilities
 */
@WebMvcTest(OwnerController.class)
@DisabledInNativeImage
@DisabledInAotMode
class OwnerControllerSecurityTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OwnerRepository owners;

    @MockBean
    private EntityManager entityManager;

    @Captor
    private ArgumentCaptor<String> sqlCaptor;

    private Query query;

    @BeforeEach
    void setup() {
        query = mock(Query.class);
        when(entityManager.createQuery(anyString())).thenReturn(query);
        when(query.getResultList()).thenReturn(Collections.emptyList());
    }

    @Test
    void testSqlInjectionVulnerability() throws Exception {
        // Test with normal input
        mockMvc.perform(get("/owners/search")
                .param("firstName", "John")
                .param("lastName", "Doe"))
            .andExpect(status().isOk())
            .andExpect(view().name("owners/searchResults"))
            .andExpect(model().attributeExists("owners"));

        verify(entityManager).createQuery(sqlCaptor.capture());
        String normalQuery = sqlCaptor.getValue();
        assertThat(normalQuery).contains("John").contains("Doe");

        // Test with malicious input attempting SQL injection
        String maliciousInput = "' OR '1'='1";
        mockMvc.perform(get("/owners/search")
                .param("firstName", maliciousInput)
                .param("lastName", "Doe"))
            .andExpect(status().isOk());

        verify(entityManager).createQuery(sqlCaptor.capture());
        String injectedQuery = sqlCaptor.getValue();
        
        // This test demonstrates the vulnerability exists
        assertThat(injectedQuery).contains("' OR '1'='1");
    }

    @Test
    void testPathTraversalVulnerability() throws Exception {
        // Test with normal filename
        mockMvc.perform(get("/owners/files")
                .param("filename", "owner_data.txt"))
            .andExpect(status().isOk())
            .andExpect(view().name("owners/fileView"));

        // Test with path traversal attempt
        String maliciousPath = "../../../etc/passwd";
        mockMvc.perform(get("/owners/files")
                .param("filename", maliciousPath))
            .andExpect(status().isOk())
            .andExpect(view().name("owners/fileView"));

        // This test demonstrates the vulnerability exists - the controller would attempt to access
        // a file outside the intended directory
    }

    @Test
    void testOpenRedirectVulnerability() throws Exception {
        // Test with normal internal URL
        mockMvc.perform(get("/owners/redirect")
                .param("url", "/owners/find"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/owners/find"));

        // Test with malicious external URL
        String maliciousUrl = "https://malicious-site.com";
        mockMvc.perform(get("/owners/redirect")
                .param("url", maliciousUrl))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl(maliciousUrl));

        // This test demonstrates the vulnerability exists - the controller would redirect
        // to an external malicious site
    }

    @Test
    void testCommandInjectionVulnerability() throws Exception {
        // Test with normal format parameter
        mockMvc.perform(get("/owners/export")
                .param("format", "csv"))
            .andExpect(status().isOk())
            .andExpect(view().name("owners/exportStatus"))
            .andExpect(model().attributeExists("message"));

        // Test with command injection attempt
        String maliciousCommand = "csv; rm -rf /";
        mockMvc.perform(get("/owners/export")
                .param("format", maliciousCommand))
            .andExpect(status().isOk())
            .andExpect(view().name("owners/exportStatus"));

        // This test demonstrates the vulnerability exists - the controller would execute
        // the malicious command
    }
}
