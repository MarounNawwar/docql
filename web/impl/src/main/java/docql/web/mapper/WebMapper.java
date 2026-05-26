package docql.web.mapper;

import docql.core.DocFile;
import docql.core.DocPackage;
import docql.core.PublishRequest;
import docql.core.SearchResult;
import docql.web.dto.*;

import java.util.List;

/**
 * Stateless mapper between domain model and web DTOs.
 * Kept separate so controllers stay thin.
 */
public final class WebMapper {

    private WebMapper() {}

    public static PublishRequest toDomain(PublishRequestDto dto) {
        List<DocFile> files = dto.files().stream()
                .map(f -> new DocFile(null, f.path(), f.title(), f.content()))
                .toList();
        return new PublishRequest(dto.team(), dto.product(), dto.version(),
                dto.tags(), files, dto.indexFilePath());
    }

    public static DocPackageDto toDto(DocPackage pkg) {
        return new DocPackageDto(pkg.id(), pkg.team(), pkg.product(),
                pkg.version(), pkg.tags(), pkg.publishedAt(), pkg.indexFilePath());
    }

    public static SearchResultDto toDto(SearchResult result) {
        return new SearchResultDto(result.packageId(), result.team(), result.product(),
                result.version(), result.filePath(), result.title(), result.snippet());
    }
}

