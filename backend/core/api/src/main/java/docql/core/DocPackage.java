package docql.core;

import java.time.Instant;
import java.util.List;

/** Represents a versioned documentation bundle pushed by a team. */
public record DocPackage(
    String id,
    String team,
    String product,
    String version,
    List<String> tags,
    Instant publishedAt,
    String indexFilePath) {}
