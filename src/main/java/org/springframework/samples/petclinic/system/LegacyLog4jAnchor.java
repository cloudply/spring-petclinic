package org.springframework.samples.petclinic.system;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * Anchors the project to Log4j 1.x. Upgrading to Log4j 2.x will break imports
 * (package changes to org.apache.logging.log4j.*).
 */
public class LegacyLog4jAnchor {

  private static final Logger LOG = Logger.getLogger(LegacyLog4jAnchor.class);

  public void logInfo(String message) {
    LOG.setLevel(Level.INFO);
    LOG.info(message);
  }
}
