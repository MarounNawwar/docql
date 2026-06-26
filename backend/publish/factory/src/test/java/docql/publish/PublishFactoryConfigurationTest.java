package docql.publish;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import docql.core.DocPackage;
import docql.core.PublishRequest;
import docql.core.exception.BackendConfigurationException;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;

class PublishFactoryConfigurationTest {

  private final PublishFactoryConfiguration configuration = new PublishFactoryConfiguration();

  @Test
  void publishService_singleFactory_returnsFactoryService() {
    var publishService = new StubPublishService();

    var result = configuration.publishService(List.of(() -> publishService));

    assertSame(publishService, result);
  }

  @Test
  void publishService_noFactories_failsFast() {
    assertThrows(
        BackendConfigurationException.class, () -> configuration.publishService(List.of()));
  }

  @Test
  void publishService_multipleFactories_failsFast() {
    var firstFactory = (PublishFactory) StubPublishService::new;
    var secondFactory = (PublishFactory) StubPublishService::new;

    assertThrows(
        BackendConfigurationException.class,
        () -> configuration.publishService(List.of(firstFactory, secondFactory)));
  }

  private static final class StubPublishService implements PublishService {

    @Override
    public DocPackage publish(PublishRequest request) {
      return new DocPackage(
          "pkg-1",
          request.team(),
          request.product(),
          request.version(),
          List.of(),
          Instant.EPOCH,
          "index.md");
    }

    @Override
    public void retract(String team, String product, String version) {
      // no-op for selector tests
    }
  }
}
