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

/**
 * MapStruct mapper between domain model and web DTOs. Uses Spring component model so it is
 * injectable as a {@code @Bean}.
 */
@Mapper(componentModel = "spring")
public interface WebMapper {

  @Mapping(target = "packageId", constant = "")
  DocFile toDocFile(DocFileDto dto);

  /** Maps a domain DocFile to its DTO representation (path, title, content only). */
  DocFileDto toDocFileDto(DocFile file);

  PublishRequest toPublishRequest(PublishRequestDto dto);

  DocPackageDto toDocPackageDto(DocPackage pkg);

  SearchResultDto toSearchResultDto(SearchResult result);
}
