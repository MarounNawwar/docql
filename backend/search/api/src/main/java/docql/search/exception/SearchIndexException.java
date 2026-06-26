package docql.search.exception;

import docql.core.exception.DocqlException;
import docql.core.exception.ErrorCodes;
import java.util.List;

/**
 * Thrown when a search index I/O operation (index, deindex, or query) fails at the infrastructure
 * level.
 *
 * <p>Error code: {@value ErrorCodes#SEARCH_INDEX_FAILURE}
 */
public class SearchIndexException extends DocqlException {

  public SearchIndexException(String message, Throwable cause) {
    super(ErrorCodes.SEARCH_INDEX_FAILURE, message, cause);
  }

  public SearchIndexException(String message) {
    super(ErrorCodes.SEARCH_INDEX_FAILURE, message);
  }

  public SearchIndexException(String message, List<String> details, Throwable cause) {
    super(ErrorCodes.SEARCH_INDEX_FAILURE, message, details, cause);
  }
}
