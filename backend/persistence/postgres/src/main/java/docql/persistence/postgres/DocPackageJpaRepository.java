package docql.persistence.postgres;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for {@link DocPackageEntity}.
 * This interface is a concrete persistence detail — it intentionally lives
 * in {@code :persistence:postgres} and is never referenced by any API module.
 */
public interface DocPackageJpaRepository extends JpaRepository<DocPackageEntity, String> {

    List<DocPackageEntity> findByTeam(String team);

    /** Latest version of a product, resolved by {@code publishedAt} descending. */
    Optional<DocPackageEntity> findTopByTeamAndProductOrderByPublishedAtDesc(String team, String product);

    Optional<DocPackageEntity> findByTeamAndProductAndVersion(String team, String product, String version);

    /** Finds all packages that carry the given tag (via the {@code doc_package_tags} join table). */
    @Query("SELECT DISTINCT p FROM DocPackageEntity p JOIN p.tags t WHERE t = :tag")
    List<DocPackageEntity> findByTag(@Param("tag") String tag);
}
