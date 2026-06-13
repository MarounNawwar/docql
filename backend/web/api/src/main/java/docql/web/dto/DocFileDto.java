package docql.web.dto;

/** A single file entry inside a {@link PublishRequestDto}. */
public record DocFileDto(
        String path,
        String title,
        String content
) {}
