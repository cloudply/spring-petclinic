package org.springframework.samples.petclinic.system;

import java.io.IOException;
import java.util.Map;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

/**
 * Anchors the project to Jackson 1.x (org.codehaus.jackson).
 * Upgrading to Jackson 2.x moves packages to com.fasterxml.jackson, which will break
 * these imports and require code changes to compile.
 */
public class LegacyJacksonAnchor {

  private static final ObjectMapper MAPPER = new ObjectMapper();

  public Map<String, Object> parse(String json) throws IOException {
    return MAPPER.readValue(json, new TypeReference<Map<String, Object>>() {});
  }
}
