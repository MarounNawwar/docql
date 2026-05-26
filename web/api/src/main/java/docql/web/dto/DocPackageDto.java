package docql.web.dto;

import java.time.Instant;
import java.util.List;

/** Outbound representation of a DocPackage. */
public record DocPackageDto(
        String id,
        String team,
        String product,
        String version,
        List<String> tags,
        Instant publishedAt,
        String indexFilePath
) {}

