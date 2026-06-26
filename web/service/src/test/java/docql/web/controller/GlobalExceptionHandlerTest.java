package docql.web.controller;

import static org.assertj.core.api.Assertions.assertThat;

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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;

class GlobalExceptionHandlerTest {

  private final GlobalExceptionHandler handler = new GlobalExceptionHandler();
  private static final String TEST_CORRELATION_ID = "test-corr-001";

  @BeforeEach
  void seedMdc() {
    MDC.put(CorrelationIdFilter.MDC_KEY, TEST_CORRELATION_ID);
  }

  @AfterEach
  void clearMdc() {
    MDC.remove(CorrelationIdFilter.MDC_KEY);
  }

  // ── 400 Bad Request ────────────────────────────────────────────────────────

  @Test
  void handleInvalidPublishRequest_returns400WithCorrectErrorCode() {
    var ex = new InvalidPublishRequestException("validation failed", List.of("team is required"));
    var response = handler.handleInvalidPublishRequest(ex);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertBody(response.getBody(), ErrorCodes.PUBLISH_VALIDATION_FAILED, "validation failed");
    assertThat(response.getBody().details()).contains("team is required");
  }

  @Test
  void handlePublishContentBlocked_returns400() {
    var ex = new PublishContentBlockedException("blocked", List.of("EICAR detected"));
    var response = handler.handlePublishContentBlocked(ex);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertBody(response.getBody(), ErrorCodes.PUBLISH_CONTENT_BLOCKED, "blocked");
  }

  @Test
  void handleInvalidSearchQuery_returns400() {
    var ex = new InvalidSearchQueryException("bad query");
    var response = handler.handleInvalidSearchQuery(ex);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertBody(response.getBody(), ErrorCodes.SEARCH_QUERY_INVALID, "bad query");
  }

  // ── 404 Not Found ─────────────────────────────────────────────────────────

  @Test
  void handlePackageNotFound_returns404() {
    var ex = new PackageNotFoundException("eng", "docs", "1.0.0");
    var response = handler.handlePackageNotFound(ex);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    assertBody(response.getBody(), ErrorCodes.PACKAGE_NOT_FOUND);
  }

  @Test
  void handleDocFileNotFound_returns404() {
    var ex = new DocFileNotFoundException("eng", "docs", "1.0.0", "guide.md");
    var response = handler.handleDocFileNotFound(ex);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    assertBody(response.getBody(), ErrorCodes.FILE_NOT_FOUND);
  }

  // ── 409 Conflict ──────────────────────────────────────────────────────────

  @Test
  void handlePackageAlreadyExists_returns409() {
    var ex = new PackageAlreadyExistsException("eng", "docs", "1.0.0");
    var response = handler.handlePackageAlreadyExists(ex);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    assertBody(response.getBody(), ErrorCodes.PUBLISH_ALREADY_EXISTS);
  }

  // ── 500 Internal Server Error ─────────────────────────────────────────────

  @Test
  void handleSearchIndex_returns500() {
    var ex = new SearchIndexException("index failed", new RuntimeException("io error"));
    var response = handler.handleSearchIndex(ex);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    assertBody(response.getBody(), ErrorCodes.SEARCH_INDEX_FAILURE, "index failed");
  }

  @Test
  void handleStorageWrite_returns500() {
    var ex = new StorageWriteException("path/to/file", new RuntimeException("disk full"));
    var response = handler.handleStorageWrite(ex);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    assertBody(response.getBody(), ErrorCodes.STORAGE_WRITE_FAILURE);
  }

  @Test
  void handleStorageRead_returns500() {
    var ex = new StorageReadException("path/to/file", new RuntimeException("io error"));
    var response = handler.handleStorageRead(ex);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    assertBody(response.getBody(), ErrorCodes.STORAGE_READ_FAILURE);
  }

  @Test
  void handleStorageDelete_returns500() {
    var ex = new StorageDeleteException("path/prefix/", new RuntimeException("io error"));
    var response = handler.handleStorageDelete(ex);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    assertBody(response.getBody(), ErrorCodes.STORAGE_DELETE_FAILURE);
  }

  @Test
  void handleBackendConfiguration_returns500() {
    var ex = new BackendConfigurationException("no factory found");
    var response = handler.handleBackendConfiguration(ex);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    assertBody(response.getBody(), ErrorCodes.BACKEND_CONFIGURATION_ERROR, "no factory found");
  }

  // ── Catch-all ─────────────────────────────────────────────────────────────

  @Test
  void handleUnexpected_returns500WithInternalErrorCode() {
    var ex = new RuntimeException("surprise");
    var response = handler.handleUnexpected(ex);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    assertBody(response.getBody(), ErrorCodes.INTERNAL_ERROR);
  }

  // ── correlation / errorId propagation ─────────────────────────────────────

  @Test
  void allHandlers_propagateCorrelationIdFromMdc() {
    var ex = new PackageNotFoundException("a", "b", "1.0.0");
    var response = handler.handlePackageNotFound(ex);

    assertThat(response.getBody().correlationId()).isEqualTo(TEST_CORRELATION_ID);
  }

  @Test
  void allHandlers_generateUniqueErrorId() {
    var ex = new PackageNotFoundException("a", "b", "1.0.0");
    var r1 = handler.handlePackageNotFound(ex);
    var r2 = handler.handlePackageNotFound(ex);

    assertThat(r1.getBody().errorId()).isNotBlank();
    assertThat(r2.getBody().errorId()).isNotBlank();
    assertThat(r1.getBody().errorId()).isNotEqualTo(r2.getBody().errorId());
  }

  // ── assertion helpers ──────────────────────────────────────────────────────

  private void assertBody(ErrorResponse body, String expectedCode) {
    assertThat(body).isNotNull();
    assertThat(body.errorCode()).isEqualTo(expectedCode);
    assertThat(body.errorId()).isNotBlank();
    assertThat(body.correlationId()).isEqualTo(TEST_CORRELATION_ID);
    assertThat(body.message()).isNotBlank();
    assertThat(body.details()).isNotNull();
  }

  private void assertBody(ErrorResponse body, String expectedCode, String expectedMessage) {
    assertBody(body, expectedCode);
    assertThat(body.message()).isEqualTo(expectedMessage);
  }
}
