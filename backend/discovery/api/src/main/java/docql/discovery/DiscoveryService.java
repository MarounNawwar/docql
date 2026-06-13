package docql.discovery;

import docql.core.DocFile;
import docql.core.DocPackage;

import java.util.List;
import java.util.Optional;

/**
 * Service contract for discovering and reading published documentation bundles.
 */
public interface DiscoveryService {

    List<DocPackage> listAll();

    List<DocPackage> listByTeam(String team);

    List<DocPackage> listByTag(String tag);

    Optional<DocPackage> findLatest(String team, String product);

    Optional<DocPackage> findVersion(String team, String product, String version);

    Optional<DocFile> readIndex(String team, String product, String version);

    Optional<DocFile> readFile(String team, String product, String version, String filePath);

    List<DocFile> listFiles(String team, String product, String version);
}
