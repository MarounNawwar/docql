package docql.db.postgres;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Spring Data JPA repository for {@link DocFileEntity}.
 * Scoped to {@code :db:postgres} — never referenced outside this module.
 */
public interface DocFileJpaRepository extends JpaRepository<DocFileEntity, DocFileId> {

    List<DocFileEntity> findByIdPackageId(String packageId);

    void deleteByIdPackageId(String packageId);
}
