package docql.persistence.postgres;

import docql.core.DocFile;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/** MapStruct mapper between the JPA {@link DocFileEntity} and the domain {@link DocFile} record. */
@Mapper
public interface DocFileMapper {

  DocFileMapper INSTANCE = org.mapstruct.factory.Mappers.getMapper(DocFileMapper.class);

  default DocFileEntity toEntity(DocFile domain) {
    return new DocFileEntity(domain.packageId(), domain.path(), domain.title(), domain.content());
  }

  @Mapping(target = "packageId", source = "packageId")
  @Mapping(target = "path", source = "path")
  DocFile toDomain(DocFileEntity entity);
}
