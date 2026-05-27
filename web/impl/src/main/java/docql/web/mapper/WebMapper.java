package docql.web.mapper;

import docql.core.DocFile;
import docql.core.DocPackage;
import docql.core.PublishRequest;
import docql.core.SearchResult;
import docql.web.dto.DocFileDto;
import docql.web.dto.DocPackageDto;
import docql.web.dto.PublishRequestDto;
import docql.web.dto.SearchResultDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * MapStruct-based mapper between domain model and web DTOs.
 * Automatically generated implementation handles conversions.
 */
@Mapper
public interface WebMapper {
    WebMapper INSTANCE = Mappers.getMapper(WebMapper.class);

    @Mapping(target = "packageId", constant = "null")
    DocFile toDocFile(DocFileDto dto);

    PublishRequest toPublishRequest(PublishRequestDto dto);

    DocPackageDto toDocPackageDto(DocPackage pkg);

    SearchResultDto toSearchResultDto(SearchResult result);
}
