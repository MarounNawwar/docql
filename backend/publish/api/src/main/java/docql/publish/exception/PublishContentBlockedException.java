package docql.publish.exception;

import docql.core.exception.DocqlException;
import docql.core.exception.ErrorCodes;
import java.util.List;

/**
 * Thrown when a file is rejected by the content / malware scanner during publish.
 *
 * <p>Error code: {@value ErrorCodes#PUBLISH_CONTENT_BLOCKED}
 */
public class PublishContentBlockedException extends DocqlException {

  public PublishContentBlockedException(String message) {
    super(ErrorCodes.PUBLISH_CONTENT_BLOCKED, message);
  }

  public PublishContentBlockedException(String message, List<String> details) {
    super(ErrorCodes.PUBLISH_CONTENT_BLOCKED, message, details);
  }
}
