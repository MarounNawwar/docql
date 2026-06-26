package docql.publish;

import docql.core.exception.BackendConfigurationException;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring wiring that selects a single {@link PublishFactory} backend and exposes its {@link
 * PublishService}.
 */
@Configuration
public class PublishFactoryConfiguration {

  @Bean
  public PublishService publishService(List<PublishFactory> publishFactories) {
    return selectFactory(publishFactories).publishService();
  }

  private PublishFactory selectFactory(List<PublishFactory> publishFactories) {
    if (publishFactories.isEmpty()) {
      throw new BackendConfigurationException(
          "No PublishFactory backend is available. Ensure a publish implementation module is on the runtime classpath.");
    }
    if (publishFactories.size() > 1) {
      throw new BackendConfigurationException(
          "Multiple PublishFactory backends are active: "
              + publishFactories.stream()
                  .map(factory -> factory.getClass().getName())
                  .sorted()
                  .toList());
    }
    return publishFactories.getFirst();
  }
}
