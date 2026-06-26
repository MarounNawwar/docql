package docql.web.controller;

import java.util.List;

/**
 * Uniform error body returned by {@link GlobalExceptionHandler} for every non-2xx response.
 *
 * <ul>
 *   <li>{@code errorCode} — stable, machine-readable constant (e.g. {@code DOCQL-PUB-001}).
 *   <li>{@code errorId} — UUID generated per occurrence; use this to correlate with server logs.
 *   <li>{@code correlationId} — request-level ID sourced from the {@code X-Correlation-ID} header
 *       (or generated if absent); propagated back to the caller.
 *   <li>{@code message} — human-readable summary.
 *   <li>{@code details} — optional extra context lines (field violations, blocked paths, …).
 * </ul>
 */
public record ErrorResponse(
    String errorCode, String errorId, String correlationId, String message, List<String> details) {}
