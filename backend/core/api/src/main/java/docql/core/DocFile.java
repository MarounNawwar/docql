package docql.core;

/**
 * Represents a single documentation file inside a DocPackage.
 */
public record DocFile(
        String packageId,
        String path,
        String title,
        String content
) {}
