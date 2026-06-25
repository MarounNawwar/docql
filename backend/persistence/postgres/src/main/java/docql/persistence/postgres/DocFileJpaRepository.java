package docql.persistence.postgres;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA repository for {@link DocFileEntity}. Scoped to {@code :persistence:postgres} —
 * never referenced outside this module.
 */
public interface DocFileJpaRepository extends JpaRepository<DocFileEntity, DocFileId> {

  List<DocFileEntity> findByIdPackageId(String packageId);

  void deleteByIdPackageId(String packageId);
}
