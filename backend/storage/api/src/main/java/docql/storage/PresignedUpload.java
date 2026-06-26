package docql.storage;

import java.time.Instant;
import java.util.Map;

/** Provider-generated instructions for direct upload to blob storage. */
public record PresignedUpload(
    String key, String method, String url, Map<String, String> headers, Instant expiresAt) {}
