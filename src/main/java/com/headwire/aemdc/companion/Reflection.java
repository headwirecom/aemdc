package com.headwire.aemdc.companion;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.headwire.aemdc.runner.BasisRunner;
import com.headwire.aemdc.util.PropsUtil;


/**
 * Reflection API to get template type Runner object
 *
 * @author Marat Saitov, 15.11.2016
 */
public class Reflection {

  private static final Logger LOG = LoggerFactory.getLogger(Reflection.class);

  private final Properties props;

  /**
   * Constructor
   *
   * @throws IOException
   */
  public Reflection() throws IOException {
    // Get properties from reflection file
    props = PropsUtil.getPropertiesFromContextClassLoader(Constants.REFLECTION_PROPS_FILE_PATH);
  }

  /**
   * Get Runner according to the template type of resource.
   *
   * @param resource
   *          - resource object
   * @return runner object
   */
  public BasisRunner getRunner(final Resource resource) {
    BasisRunner runner = null;
    final String type = resource.getType();

    if (StringUtils.isNotBlank(type)) {
      final String fullyQualifiedClassName = props.getProperty(type);

      if (StringUtils.isNotBlank(fullyQualifiedClassName)) {
        try {
          final Class<?> c = Class.forName(fullyQualifiedClassName);
          final Constructor<?> ctor = c.getDeclaredConstructor(Resource.class);
          ctor.setAccessible(true);
          runner = (BasisRunner) ctor.newInstance(resource);

        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException | NoSuchMethodException
            | SecurityException | IllegalArgumentException | InvocationTargetException e) {
          LOG.error("Can't get class instance for template type [{}]. ", type, e);
        }
      } else {
        LOG.error("Unknown <type> argument [{}].", type);
      }
    }

    return runner;
  }
}