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

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

/**
 * Anchors the project to Apache HttpClient 4.x (org.apache.http). Upgrading to
 * httpclient5 moves packages to org.apache.hc.client5.* and removes getStatusLine(),
 * which will break this class and require code changes to compile.
 */
public class LegacyHttpClient {

	public int ping(String url) throws IOException {
		HttpGet request = new HttpGet(url);
		try (CloseableHttpClient client = HttpClients.createDefault();
				CloseableHttpResponse response = client.execute(request)) {
			return response.getStatusLine().getStatusCode();
		}
	}

}
