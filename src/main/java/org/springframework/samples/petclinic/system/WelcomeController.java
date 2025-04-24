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
import java.util.Properties;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
class WelcomeController {

	@GetMapping("/")
	public String welcome() {
		return "welcome";
	}
	
	// VULNERABILITY 10: Sensitive Data Exposure
	@GetMapping("/debug")
	@ResponseBody
	public Map<String, String> debugInfo() {
		Map<String, String> debugInfo = new HashMap<>();
		debugInfo.put("dbPassword", "s3cr3t_p@ssw0rd");
		debugInfo.put("apiKey", "AIza5yc2FtZS1wcm9qZWN0LTEyMzQ1Njc4OTA=");
		debugInfo.put("jwtSecret", "verySecretKeyThatShouldNotBeExposed12345");
		
		// VULNERABILITY 11: System Information Disclosure
		Properties props = System.getProperties();
		for (Object key : props.keySet()) {
			debugInfo.put(key.toString(), props.get(key).toString());
		}
		
		return debugInfo;
	}
}
