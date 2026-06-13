package docql.publish.impl;

import docql.cje.CjeEngine;
import docql.db.DocFileRepository;
import docql.db.DocPackageRepository;
import docql.publish.PublishService;
import docql.storage.StorageBackend;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring wiring for the default publish implementation.
 */
@Configuration
@ConditionalOnProperty(prefix = "docql.publish", name = "impl", havingValue = "default", matchIfMissing = true)
public class DefaultPublishConfiguration {

    @Bean
    public PublishService publishService(
            DocPackageRepository packageRepository,
            DocFileRepository fileRepository,
            StorageBackend storageBackend,
            CjeEngine cjeEngine
    ) {
        return new DefaultPublishFactory(packageRepository, fileRepository, storageBackend, cjeEngine)
                .publishService();
    }
}
