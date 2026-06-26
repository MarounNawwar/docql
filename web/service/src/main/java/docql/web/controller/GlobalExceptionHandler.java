package docql.web.controller;

import docql.core.exception.BackendConfigurationException;
import docql.core.exception.ErrorCodes;
import docql.discovery.exception.DocFileNotFoundException;
import docql.discovery.exception.PackageNotFoundException;
import docql.publish.exception.InvalidPublishRequestException;
import docql.publish.exception.PackageAlreadyExistsException;
import docql.publish.exception.PublishContentBlockedException;
import docql.search.exception.InvalidSearchQueryException;
import docql.search.exception.SearchIndexException;
import docql.storage.exception.StorageDeleteException;
import docql.storage.exception.StorageReadException;
import docql.storage.exception.StorageWriteException;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Central exception-to-HTTP-response translator for all docql REST endpoints.
 *
 * <p>Every handler:
 *
 * <ol>
 *   <li>Generates a unique {@code errorId} UUID for log correlation.
 *   <li>Reads the {@code correlationId} from SLF4J {@link MDC} (populated by {@link
 *       CorrelationIdFilter}).
 *   <li>Logs at WARN (4xx) or ERROR (5xx) with both IDs.
 *   <li>Returns a structured {@link ErrorResponse} body.
 * </ol>
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

  private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  // ── 400 Bad Request ────────────────────────────────────────────────────────

  @ExceptionHandler(InvalidPublishRequestException.class)
  public ResponseEntity<ErrorResponse> handleInvalidPublishRequest(
      InvalidPublishRequestException ex) {
    return warn(ex, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(PublishContentBlockedException.class)
  public ResponseEntity<ErrorResponse> handlePublishContentBlocked(
      PublishContentBlockedException ex) {
    return warn(ex, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(InvalidSearchQueryException.class)
  public ResponseEntity<ErrorResponse> handleInvalidSearchQuery(InvalidSearchQueryException ex) {
    return warn(ex, HttpStatus.BAD_REQUEST);
  }

  // ── 404 Not Found ─────────────────────────────────────────────────────────

  @ExceptionHandler(PackageNotFoundException.class)
  public ResponseEntity<ErrorResponse> handlePackageNotFound(PackageNotFoundException ex) {
    return warn(ex, HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(DocFileNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleDocFileNotFound(DocFileNotFoundException ex) {
    return warn(ex, HttpStatus.NOT_FOUND);
  }

  // ── 409 Conflict ──────────────────────────────────────────────────────────

  @ExceptionHandler(PackageAlreadyExistsException.class)
  public ResponseEntity<ErrorResponse> handlePackageAlreadyExists(
      PackageAlreadyExistsException ex) {
    return warn(ex, HttpStatus.CONFLICT);
  }

  // ── 500 Internal Server Error ─────────────────────────────────────────────

  @ExceptionHandler(SearchIndexException.class)
  public ResponseEntity<ErrorResponse> handleSearchIndex(SearchIndexException ex) {
    return error(ex, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler(StorageWriteException.class)
  public ResponseEntity<ErrorResponse> handleStorageWrite(StorageWriteException ex) {
    return error(ex, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler(StorageReadException.class)
  public ResponseEntity<ErrorResponse> handleStorageRead(StorageReadException ex) {
    return error(ex, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler(StorageDeleteException.class)
  public ResponseEntity<ErrorResponse> handleStorageDelete(StorageDeleteException ex) {
    return error(ex, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler(BackendConfigurationException.class)
  public ResponseEntity<ErrorResponse> handleBackendConfiguration(
      BackendConfigurationException ex) {
    return error(ex, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  // ── Catch-all ─────────────────────────────────────────────────────────────

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleUnexpected(Exception ex) {
    String errorId = UUID.randomUUID().toString();
    String correlationId = MDC.get(CorrelationIdFilter.MDC_KEY);
    log.error("Unhandled exception [errorId={}, correlationId={}]", errorId, correlationId, ex);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(
            new ErrorResponse(
                ErrorCodes.INTERNAL_ERROR,
                errorId,
                correlationId,
                "An unexpected internal error occurred.",
                List.of()));
  }

  // ── Helpers ───────────────────────────────────────────────────────────────

  private ResponseEntity<ErrorResponse> warn(
      docql.core.exception.DocqlException ex, HttpStatus status) {
    String errorId = UUID.randomUUID().toString();
    String correlationId = MDC.get(CorrelationIdFilter.MDC_KEY);
    log.warn(
        "{} [errorCode={}, errorId={}, correlationId={}]: {}",
        ex.getClass().getSimpleName(),
        ex.getErrorCode(),
        errorId,
        correlationId,
        ex.getMessage());
    return ResponseEntity.status(status)
        .body(
            new ErrorResponse(
                ex.getErrorCode(), errorId, correlationId, ex.getMessage(), ex.getDetails()));
  }

  private ResponseEntity<ErrorResponse> error(
      docql.core.exception.DocqlException ex, HttpStatus status) {
    String errorId = UUID.randomUUID().toString();
    String correlationId = MDC.get(CorrelationIdFilter.MDC_KEY);
    log.error(
        "{} [errorCode={}, errorId={}, correlationId={}]: {}",
        ex.getClass().getSimpleName(),
        ex.getErrorCode(),
        errorId,
        correlationId,
        ex.getMessage(),
        ex);
    return ResponseEntity.status(status)
        .body(
            new ErrorResponse(
                ex.getErrorCode(), errorId, correlationId, ex.getMessage(), ex.getDetails()));
  }
}
