package docql.core;

/**
 * A structured search result for a documentation discovery query.
 */
public record SearchResult(
        String packageId,
        String team,
        String product,
        String version,
        String filePath,
        String title,
        String snippet
) {}
