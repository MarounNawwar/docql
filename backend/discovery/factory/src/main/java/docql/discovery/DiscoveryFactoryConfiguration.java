package docql.discovery;

import docql.core.exception.BackendConfigurationException;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring wiring that selects a single {@link DiscoveryFactory} backend and exposes its {@link
 * DiscoveryService}.
 */
@Configuration
public class DiscoveryFactoryConfiguration {

  @Bean
  public DiscoveryService discoveryService(List<DiscoveryFactory> discoveryFactories) {
    return selectFactory(discoveryFactories).discoveryService();
  }

  private DiscoveryFactory selectFactory(List<DiscoveryFactory> discoveryFactories) {
    if (discoveryFactories.isEmpty()) {
      throw new BackendConfigurationException(
          "No DiscoveryFactory backend is available. Ensure a discovery implementation module is on the runtime classpath.");
    }
    if (discoveryFactories.size() > 1) {
      throw new BackendConfigurationException(
          "Multiple DiscoveryFactory backends are active: "
              + discoveryFactories.stream()
                  .map(factory -> factory.getClass().getName())
                  .sorted()
                  .toList());
    }
    return discoveryFactories.getFirst();
  }
}
