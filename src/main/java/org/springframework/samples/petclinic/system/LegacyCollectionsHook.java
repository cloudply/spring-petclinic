package org.springframework.samples.petclinic.system;

import java.util.HashMap;
import java.util.Map;
import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.functors.InvokerTransformer;
import org.apache.commons.collections.map.LazyMap;

/**
 * Anchors the project to Commons Collections 3.x APIs. Migrating to
 * commons-collections4 will break these imports (package changes).
 */
public class LegacyCollectionsHook {

  /**
   * Creates a LazyMap using a 3.x InvokerTransformer. This will fail to compile
   * if the project is upgraded to commons-collections4, because the packages
   * and APIs move to org.apache.commons.collections4.*.
   */
  public Map<String, Object> lazyMapWithToStringTransformer() {
    Transformer transformer = new InvokerTransformer("toString", new Class[0], new Object[0]);
    Map<String, Object> inner = new HashMap<>();
    return LazyMap.decorate(inner, transformer);
  }
}
