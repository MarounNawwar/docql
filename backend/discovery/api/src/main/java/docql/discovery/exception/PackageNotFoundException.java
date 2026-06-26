package docql.discovery.exception;

import docql.core.exception.DocqlException;
import docql.core.exception.ErrorCodes;
import java.util.List;

/**
 * Thrown when a requested documentation package does not exist.
 *
 * <p>Error code: {@value ErrorCodes#PACKAGE_NOT_FOUND}
 */
public class PackageNotFoundException extends DocqlException {

  public PackageNotFoundException(String team, String product, String version) {
    super(
        ErrorCodes.PACKAGE_NOT_FOUND, "Package not found: " + team + "/" + product + "/" + version);
  }

  public PackageNotFoundException(String message) {
    super(ErrorCodes.PACKAGE_NOT_FOUND, message);
  }

  public PackageNotFoundException(String message, List<String> details) {
    super(ErrorCodes.PACKAGE_NOT_FOUND, message, details);
  }
}
