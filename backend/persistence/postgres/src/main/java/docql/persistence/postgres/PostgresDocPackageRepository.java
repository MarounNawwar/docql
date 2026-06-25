package docql.persistence.postgres;

import static docql.persistence.utils.pagination.PaginationUtils.pagination;

import docql.core.DocPackage;
import docql.persistence.DocPackageRepository;
import java.util.List;
import java.util.Optional;

/**
 * JPA-backed implementation of {@link DocPackageRepository}. Delegates to {@link
 * DocPackageJpaRepository} and converts {@link DocPackageEntity} ↔ {@link DocPackage} domain
 * records.
 */
public class PostgresDocPackageRepository implements DocPackageRepository {

  private final DocPackageJpaRepository jpa;
  private final DocPackageMapper mapper = DocPackageMapper.INSTANCE;

  public PostgresDocPackageRepository(DocPackageJpaRepository jpa) {
    this.jpa = jpa;
  }

  @Override
  public DocPackage save(DocPackage pkg) {
    jpa.save(mapper.toEntity(pkg));
    return pkg;
  }

  @Override
  public Optional<DocPackage> findById(String id) {
    return jpa.findById(id).map(mapper::toDomain);
  }

  @Override
  public Optional<DocPackage> findLatestByTeamAndProduct(String team, String product) {
    return jpa.findTopByTeamAndProductOrderByPublishedAtDesc(team, product).map(mapper::toDomain);
  }

  @Override
  public Optional<DocPackage> findByTeamProductAndVersion(
      String team, String product, String version) {
    return jpa.findByTeamAndProductAndVersion(team, product, version).map(mapper::toDomain);
  }

  @Override
  public List<DocPackage> findAll(Integer offset, Integer limit) {
    return jpa.findPage(pagination(limit, offset)).stream().map(mapper::toDomain).toList();
  }

  @Override
  public List<DocPackage> findByTeam(String team, Integer offset, Integer limit) {
    return jpa.findByTeam(team, pagination(limit, offset)).stream().map(mapper::toDomain).toList();
  }

  @Override
  public List<DocPackage> findByTag(String tag, Integer offset, Integer limit) {
    return jpa.findByTag(tag, pagination(limit, offset)).stream().map(mapper::toDomain).toList();
  }

  @Override
  public void deleteById(String id) {
    jpa.deleteById(id);
  }
}
