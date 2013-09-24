package org.eupathdb.websvccommon.wsfplugin.blast;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import org.gusdb.wsf.plugin.WsfPluginException;

public class BlastConfig {

  // field definitions in the config file
  private static final String FILE_CONFIG = "blast-config.xml";

  /**
   * This is the only required field in the config file.
   */
  public static final String FIELD_BLAST_PATH = "BlastPath";

  // The following properties are optional, and a default is provided for each.
  public static final String FIELD_TEMP_PATH = "TempPath";
  public static final String FIELD_EXTRA_OPTIONS = "ExtraOptions";
  public static final String FIELD_TIMEOUT = "Timeout";
  public static final String FIELD_IDENTIFIER_REGEX = "IdentifierRegex";
  public static final String FIELD_ORGANISM_REGEX = "OrganismRegex";

  // default values for the optional properties
  private static final String DEFAULT_TEMP_PATH = "/var/www/Common/tmp/blast";
  private static final String DEFAULT_EXTRA_OPTIONS = "";
  private static final String DEFAULT_TIMEOUT = "300";
  private static final String DEFAULT_IDENTIFIER_REGEX = "^>*(?:[^\\|]*\\|)?(\\S+)";
  private static final String DEFAULT_ORGANISM_REGEX = "\\|\\s*organism=([^_|\\s]+)";

  private final Properties properties;

  public BlastConfig(String gusHome) throws WsfPluginException {
    this.properties = new Properties();
    String configFile = gusHome + "/config/" + FILE_CONFIG;
    try {
      properties.load(new FileReader(new File(configFile)));
    } catch (IOException ex) {
      throw new WsfPluginException(ex);
    }

    validate();
  }

  public BlastConfig(Properties properties) throws WsfPluginException {
    this.properties = properties;

    validate();
  }

  private void validate() throws WsfPluginException {
    // check if required blastPath is specified.
    if (!properties.containsKey(FIELD_BLAST_PATH))
      throw new WsfPluginException("The required BLAST program path is not "
          + "specified in the config file.");

    // create temp path if it doesn't exist
    File tempDir = getTempDir();
    if (!tempDir.exists())
      tempDir.mkdirs();

    // timeout has to be positive
    long timeout = getTimeout();
    if (timeout < 1)
      throw new WsfPluginException("Invalid timeout for blast: " + timeout
          + " seconds. The value must be a positive integer.");
  }

  public String getBlastPath() {
    return properties.getProperty(FIELD_BLAST_PATH);
  }

  public File getTempDir() {
    return new File(properties.getProperty(FIELD_TEMP_PATH, DEFAULT_TEMP_PATH));
  }

  public String getExtraOptions() {
    return properties.getProperty(FIELD_EXTRA_OPTIONS, DEFAULT_EXTRA_OPTIONS);
  }

  public long getTimeout() {
    return Long.valueOf(properties.getProperty(FIELD_TIMEOUT, DEFAULT_TIMEOUT));
  }

  public String getSourceIdRegex() {
    return properties.getProperty(FIELD_IDENTIFIER_REGEX,
        DEFAULT_IDENTIFIER_REGEX);
  }

  public String getOrganismRegex() {
    return properties.getProperty(FIELD_ORGANISM_REGEX, DEFAULT_ORGANISM_REGEX);
  }

  public String getProperty(String name) {
    return properties.getProperty(name);
  }

  public String getProperty(String name, String defaultValue) {
    return properties.getProperty(name, defaultValue);
  }
}