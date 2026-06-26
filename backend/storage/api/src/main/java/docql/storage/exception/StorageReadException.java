package docql.storage.exception;

import docql.core.exception.DocqlException;
import docql.core.exception.ErrorCodes;
import java.util.List;

/**
 * Thrown when a blob read or list operation fails.
 *
 * <p>Error code: {@value ErrorCodes#STORAGE_READ_FAILURE}
 */
public class StorageReadException extends DocqlException {

  public StorageReadException(String key, Throwable cause) {
    super(ErrorCodes.STORAGE_READ_FAILURE, "Failed to read storage key: " + key, cause);
  }

  public StorageReadException(String message) {
    super(ErrorCodes.STORAGE_READ_FAILURE, message);
  }

  public StorageReadException(String message, List<String> details, Throwable cause) {
    super(ErrorCodes.STORAGE_READ_FAILURE, message, details, cause);
  }
}
