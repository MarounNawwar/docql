package docql.search.exception;

import docql.core.exception.DocqlException;
import docql.core.exception.ErrorCodes;
import java.util.List;

/**
 * Thrown when a search query string cannot be parsed by the search engine.
 *
 * <p>Error code: {@value ErrorCodes#SEARCH_QUERY_INVALID}
 */
public class InvalidSearchQueryException extends DocqlException {

  public InvalidSearchQueryException(String query, Throwable cause) {
    super(ErrorCodes.SEARCH_QUERY_INVALID, "Invalid search query: " + query, cause);
  }

  public InvalidSearchQueryException(String message) {
    super(ErrorCodes.SEARCH_QUERY_INVALID, message);
  }

  public InvalidSearchQueryException(String message, List<String> details) {
    super(ErrorCodes.SEARCH_QUERY_INVALID, message, details);
  }
}
