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

package org.springframework.samples.petclinic.system;

import org.springframework.context.annotation.Configuration;
import org.springframework.cache.annotation.EnableCaching;

/**
 * Cache configuration.
 *
 * The previous implementation relied on {@code JCacheManagerCustomizer},
 * which was removed in Spring Boot 4.  This simplified configuration
 * enables Spring's caching abstraction without providing explicit JCache
 * customisation.  The default caches can still be defined via application
 * properties if needed.
 *
 * @author Juergen Hoeller
 */
@Configuration(proxyBeanMethods = false)
@EnableCaching
public class CacheConfiguration {
	// No explicit JCache customisation is required for the current setup.
}
