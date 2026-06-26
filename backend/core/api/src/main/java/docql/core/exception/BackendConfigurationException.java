package docql.core.exception;

import java.util.List;

/**
 * Thrown when a backend factory is misconfigured at startup — either no implementation is present
 * on the classpath, or more than one active implementation was detected simultaneously.
 *
 * <p>Error code: {@value ErrorCodes#BACKEND_CONFIGURATION_ERROR}
 */
public class BackendConfigurationException extends DocqlException {

  public BackendConfigurationException(String message) {
    super(ErrorCodes.BACKEND_CONFIGURATION_ERROR, message);
  }

  public BackendConfigurationException(String message, List<String> details) {
    super(ErrorCodes.BACKEND_CONFIGURATION_ERROR, message, details);
  }
}
