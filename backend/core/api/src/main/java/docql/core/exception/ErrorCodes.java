package docql.core.exception;

/**
 * Catalogue of stable, machine-readable error codes used across the docql platform.
 *
 * <p>Format: {@code DOCQL-<DOMAIN>-<SEQ>}
 *
 * <ul>
 *   <li>{@code PUB} — publish domain
 *   <li>{@code DISC} — discovery domain
 *   <li>{@code SRCH} — search domain
 *   <li>{@code IDX} — search index infrastructure
 *   <li>{@code STOR} — storage infrastructure
 *   <li>{@code CFG} — backend configuration
 *   <li>{@code ERR} — unclassified / internal
 * </ul>
 */
public final class ErrorCodes {

  private ErrorCodes() {}

  // ── Publish ────────────────────────────────────────────────────────────────
  /** Publish request failed validation (missing or blank required fields). */
  public static final String PUBLISH_VALIDATION_FAILED = "DOCQL-PUB-001";

  /** File content was rejected by the malware / content scanner. */
  public static final String PUBLISH_CONTENT_BLOCKED = "DOCQL-PUB-002";

  /** A package with the same team/product/version already exists. */
  public static final String PUBLISH_ALREADY_EXISTS = "DOCQL-PUB-003";

  // ── Discovery ──────────────────────────────────────────────────────────────
  /** The requested documentation package was not found. */
  public static final String PACKAGE_NOT_FOUND = "DOCQL-DISC-001";

  /** The requested file within a package was not found. */
  public static final String FILE_NOT_FOUND = "DOCQL-DISC-002";

  // ── Search ─────────────────────────────────────────────────────────────────
  /** The search query string could not be parsed. */
  public static final String SEARCH_QUERY_INVALID = "DOCQL-SRCH-001";

  /** A Lucene / search-index I/O operation failed. */
  public static final String SEARCH_INDEX_FAILURE = "DOCQL-IDX-001";

  // ── Storage ────────────────────────────────────────────────────────────────
  /** A blob write operation failed. */
  public static final String STORAGE_WRITE_FAILURE = "DOCQL-STOR-001";

  /** A blob read / list operation failed. */
  public static final String STORAGE_READ_FAILURE = "DOCQL-STOR-002";

  /** A blob delete operation failed. */
  public static final String STORAGE_DELETE_FAILURE = "DOCQL-STOR-003";

  // ── Configuration ──────────────────────────────────────────────────────────
  /** A backend factory is misconfigured (missing or duplicate implementation). */
  public static final String BACKEND_CONFIGURATION_ERROR = "DOCQL-CFG-001";

  // ── Catch-all ──────────────────────────────────────────────────────────────
  /** An unexpected internal error that does not map to any known code. */
  public static final String INTERNAL_ERROR = "DOCQL-ERR-000";
}
