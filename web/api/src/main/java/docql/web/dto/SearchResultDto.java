package docql.web.dto;

/** Outbound search result entry. */
public record SearchResultDto(
        String packageId,
        String team,
        String product,
        String version,
        String filePath,
        String title,
        String snippet
) {}
