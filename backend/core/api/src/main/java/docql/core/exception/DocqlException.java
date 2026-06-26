package docql.core.exception;

import static java.util.List.*;

import java.util.List;

/**
 * Base for all docql domain exceptions. Every subclass carries a stable {@link #errorCode} (e.g.
 * {@code DOCQL-PUB-001}) that clients can use for programmatic error handling, plus an optional
 * human-readable {@link #details} list.
 */
public abstract class DocqlException extends RuntimeException {

  private final String errorCode;
  private final List<String> details;

  protected DocqlException(String errorCode, String message) {
    super(message);
    this.errorCode = errorCode;
    this.details = of();
  }

  protected DocqlException(String errorCode, String message, List<String> details) {
    super(message);
    this.errorCode = errorCode;
    this.details = details != null ? copyOf(details) : of();
  }

  protected DocqlException(String errorCode, String message, Throwable cause) {
    super(message, cause);
    this.errorCode = errorCode;
    this.details = of();
  }

  protected DocqlException(
      String errorCode, String message, List<String> details, Throwable cause) {
    super(message, cause);
    this.errorCode = errorCode;
    this.details = details != null ? copyOf(details) : of();
  }

  public String getErrorCode() {
    return errorCode;
  }

  public List<String> getDetails() {
    return details;
  }
}
