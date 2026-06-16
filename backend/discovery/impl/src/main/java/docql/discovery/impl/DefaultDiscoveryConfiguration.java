package docql.discovery.impl;

import docql.persistence.DocFileRepository;
import docql.persistence.DocPackageRepository;
import docql.discovery.DiscoveryService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring wiring for the default discovery implementation.
 */
@Configuration
@ConditionalOnProperty(prefix = "docql.discovery", name = "impl", havingValue = "default", matchIfMissing = true)
public class DefaultDiscoveryConfiguration {

    @Bean
    public DiscoveryService discoveryService(
            DocPackageRepository packageRepository,
            DocFileRepository fileRepository
    ) {
        return new DefaultDiscoveryFactory(packageRepository, fileRepository)
                .discoveryService();
    }
}
