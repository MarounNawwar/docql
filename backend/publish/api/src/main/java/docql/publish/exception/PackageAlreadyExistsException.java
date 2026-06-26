package docql.publish.exception;

import docql.core.exception.DocqlException;
import docql.core.exception.ErrorCodes;
import java.util.List;

/**
 * Thrown when a publish attempt targets a team/product/version that already exists.
 *
 * <p>Error code: {@value ErrorCodes#PUBLISH_ALREADY_EXISTS}
 */
public class PackageAlreadyExistsException extends DocqlException {

  public PackageAlreadyExistsException(String team, String product, String version) {
    super(
        ErrorCodes.PUBLISH_ALREADY_EXISTS,
        "Package already exists: " + team + "/" + product + "/" + version);
  }

  public PackageAlreadyExistsException(
      String team, String product, String version, List<String> details) {
    super(
        ErrorCodes.PUBLISH_ALREADY_EXISTS,
        "Package already exists: " + team + "/" + product + "/" + version,
        details);
  }
}
