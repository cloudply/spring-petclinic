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

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.functors.InvokerTransformer;
import org.apache.commons.collections.map.LazyMap;

/**
 * Anchors the project to Commons Collections 3.x APIs. Migrating to commons-collections4
 * moves the packages to org.apache.commons.collections4.* and will break these imports.
 */
public class LegacyCollectionsHook {

	public Map<String, Object> lazyMapWithToStringTransformer() {
		Transformer transformer = new InvokerTransformer("toString", new Class[0], new Object[0]);
		Map<String, Object> inner = new HashMap<>();
		return LazyMap.decorate(inner, transformer);
	}

}
