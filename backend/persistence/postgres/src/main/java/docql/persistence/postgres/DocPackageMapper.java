package docql.persistence.postgres;

import docql.core.DocPackage;
import org.mapstruct.Mapper;

/**
 * MapStruct mapper between the JPA {@link DocPackageEntity} and the domain {@link DocPackage}
 * record.
 */
@Mapper
public interface DocPackageMapper {

  DocPackageMapper INSTANCE = org.mapstruct.factory.Mappers.getMapper(DocPackageMapper.class);

  DocPackageEntity toEntity(DocPackage domain);

  DocPackage toDomain(DocPackageEntity entity);
}
