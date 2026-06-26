package docql.publish.exception;

import docql.core.exception.DocqlException;
import docql.core.exception.ErrorCodes;
import java.util.List;

/**
 * Thrown when a {@link docql.core.PublishRequest} fails field-level validation.
 *
 * <p>Error code: {@value ErrorCodes#PUBLISH_VALIDATION_FAILED}
 */
public class InvalidPublishRequestException extends DocqlException {

  public InvalidPublishRequestException(String message) {
    super(ErrorCodes.PUBLISH_VALIDATION_FAILED, message);
  }

  public InvalidPublishRequestException(String message, List<String> details) {
    super(ErrorCodes.PUBLISH_VALIDATION_FAILED, message, details);
  }
}
