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

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * Anchors the project to Log4j 1.x (org.apache.log4j). Upgrading to Log4j 2.x moves the
 * packages to org.apache.logging.log4j.* and changes the API, which will break this
 * class.
 */
public class LegacyLog4jAnchor {

	private static final Logger LOG = Logger.getLogger(LegacyLog4jAnchor.class);

	public void logInfo(String message) {
		LOG.setLevel(Level.INFO);
		LOG.info(message);
	}

}
