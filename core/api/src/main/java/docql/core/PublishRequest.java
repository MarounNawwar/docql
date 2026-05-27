package docql.core;

import java.util.List;

/**
 * A publish request — the raw input a team submits to push a documentation bundle.
 */
public record PublishRequest(
        String team,
        String product,
        String version,
        List<String> tags,
        List<DocFile> files,
        String indexFilePath
) {}
