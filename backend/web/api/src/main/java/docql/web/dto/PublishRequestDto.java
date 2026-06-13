package docql.web.dto;

import java.util.List;

/** Inbound payload for POST /packages */
public record PublishRequestDto(
        String team,
        String product,
        String version,
        List<String> tags,
        String indexFilePath,
        List<DocFileDto> files
) {}
