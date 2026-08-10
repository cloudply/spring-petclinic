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

import java.io.IOException;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

/**
 * Anchors the project to Jackson 1.x (org.codehaus.jackson). Upgrading to Jackson 2.x
 * moves the packages to com.fasterxml.jackson.* and will break these imports.
 */
public class LegacyJacksonAnchor {

	private static final ObjectMapper MAPPER = new ObjectMapper();

	public Map<String, Object> parse(String json) throws IOException {
		return MAPPER.readValue(json, new TypeReference<Map<String, Object>>() {
		});
	}

}
