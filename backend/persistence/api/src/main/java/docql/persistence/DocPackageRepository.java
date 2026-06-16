package docql.persistence;

import docql.core.DocPackage;

import java.util.List;
import java.util.Optional;

/**
 * Contract for persisting and retrieving DocPackage metadata.
 * Implementations may be backed by Postgres, an in-memory store, Nexus, etc.
 */
public interface DocPackageRepository {

    DocPackage save(DocPackage docPackage);

    Optional<DocPackage> findById(String id);

    Optional<DocPackage> findLatestByTeamAndProduct(String team, String product);

    Optional<DocPackage> findByTeamProductAndVersion(String team, String product, String version);

    List<DocPackage> findAll();

    List<DocPackage> findByTeam(String team);

    List<DocPackage> findByTag(String tag);

    void deleteById(String id);
}
