package docql.storage.exception;

import docql.core.exception.DocqlException;
import docql.core.exception.ErrorCodes;
import java.util.List;

/**
 * Thrown when a blob delete operation fails.
 *
 * <p>Error code: {@value ErrorCodes#STORAGE_DELETE_FAILURE}
 */
public class StorageDeleteException extends DocqlException {

  public StorageDeleteException(String prefix, Throwable cause) {
    super(ErrorCodes.STORAGE_DELETE_FAILURE, "Failed to delete storage prefix: " + prefix, cause);
  }

  public StorageDeleteException(String message) {
    super(ErrorCodes.STORAGE_DELETE_FAILURE, message);
  }

  public StorageDeleteException(String message, List<String> details, Throwable cause) {
    super(ErrorCodes.STORAGE_DELETE_FAILURE, message, details, cause);
  }
}
