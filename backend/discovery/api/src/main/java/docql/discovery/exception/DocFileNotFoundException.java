package docql.discovery.exception;

import docql.core.exception.DocqlException;
import docql.core.exception.ErrorCodes;
import java.util.List;

/**
 * Thrown when a specific file within a package cannot be found.
 *
 * <p>Error code: {@value ErrorCodes#FILE_NOT_FOUND}
 */
public class DocFileNotFoundException extends DocqlException {

  public DocFileNotFoundException(String team, String product, String version, String filePath) {
    super(
        ErrorCodes.FILE_NOT_FOUND,
        "File not found: " + team + "/" + product + "/" + version + "/" + filePath);
  }

  public DocFileNotFoundException(String message) {
    super(ErrorCodes.FILE_NOT_FOUND, message);
  }

  public DocFileNotFoundException(String message, List<String> details) {
    super(ErrorCodes.FILE_NOT_FOUND, message, details);
  }
}
