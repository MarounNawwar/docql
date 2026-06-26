package docql.discovery;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import docql.core.DocFile;
import docql.core.DocPackage;
import docql.core.exception.BackendConfigurationException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class DiscoveryFactoryConfigurationTest {

  private final DiscoveryFactoryConfiguration configuration = new DiscoveryFactoryConfiguration();

  @Test
  void discoveryService_singleFactory_returnsFactoryService() {
    var discoveryService = new StubDiscoveryService();

    var result = configuration.discoveryService(List.of(() -> discoveryService));

    assertSame(discoveryService, result);
  }

  @Test
  void discoveryService_noFactories_failsFast() {
    assertThrows(
        BackendConfigurationException.class, () -> configuration.discoveryService(List.of()));
  }

  @Test
  void discoveryService_multipleFactories_failsFast() {
    var firstFactory = (DiscoveryFactory) StubDiscoveryService::new;
    var secondFactory = (DiscoveryFactory) StubDiscoveryService::new;

    assertThrows(
        BackendConfigurationException.class,
        () -> configuration.discoveryService(List.of(firstFactory, secondFactory)));
  }

  private static final class StubDiscoveryService implements DiscoveryService {

    @Override
    public List<DocPackage> listAll(Integer offset, Integer limit) {
      return List.of();
    }

    @Override
    public List<DocPackage> listByTeam(String team, Integer offset, Integer limit) {
      return List.of();
    }

    @Override
    public List<DocPackage> listByTag(String tag, Integer offset, Integer limit) {
      return List.of();
    }

    @Override
    public Optional<DocPackage> findLatest(String team, String product) {
      return Optional.of(
          new DocPackage("pkg-1", team, product, "1.0.0", List.of(), Instant.EPOCH, "index.md"));
    }

    @Override
    public Optional<DocPackage> findVersion(String team, String product, String version) {
      return Optional.of(
          new DocPackage("pkg-1", team, product, version, List.of(), Instant.EPOCH, "index.md"));
    }

    @Override
    public Optional<DocFile> readIndex(String team, String product, String version) {
      return Optional.empty();
    }

    @Override
    public Optional<DocFile> readFile(
        String team, String product, String version, String filePath) {
      return Optional.empty();
    }

    @Override
    public List<DocFile> listFiles(String team, String product, String version) {
      return List.of();
    }
  }
}
