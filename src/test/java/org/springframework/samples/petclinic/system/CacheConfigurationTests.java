package org.springframework.samples.petclinic.system;

import static org.assertj.core.api.Assertions.assertThat;

import javax.cache.CacheManager;
import javax.cache.configuration.MutableConfiguration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.jcache.JCacheManagerCustomizer;

/**
 * Integration tests for {@link CacheConfiguration}.
 */
@SpringBootTest
class CacheConfigurationTests {

	@Autowired
	private CacheManager cacheManager;
	
	@Autowired
	private JCacheManagerCustomizer customizer;

	@Test
	void shouldHaveVetsCache() {
		assertThat(cacheManager.getCache("vets")).isNotNull();
	}
	
	@Test
	void shouldHaveCustomizer() {
		assertThat(customizer).isNotNull();
		assertThat(customizer).isInstanceOf(JCacheManagerCustomizer.class);
	}
}
