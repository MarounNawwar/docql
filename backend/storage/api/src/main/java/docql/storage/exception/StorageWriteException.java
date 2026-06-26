package docql.storage.exception;

import docql.core.exception.DocqlException;
import docql.core.exception.ErrorCodes;
import java.util.List;

/**
 * Thrown when a blob write (store) operation fails.
 *
 * <p>Error code: {@value ErrorCodes#STORAGE_WRITE_FAILURE}
 */
public class StorageWriteException extends DocqlException {

  public StorageWriteException(String key, Throwable cause) {
    super(ErrorCodes.STORAGE_WRITE_FAILURE, "Failed to write storage key: " + key, cause);
  }

  public StorageWriteException(String message) {
    super(ErrorCodes.STORAGE_WRITE_FAILURE, message);
  }

  public StorageWriteException(String message, List<String> details, Throwable cause) {
    super(ErrorCodes.STORAGE_WRITE_FAILURE, message, details, cause);
  }
}
