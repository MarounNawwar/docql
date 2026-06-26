package docql.publish.impl;

import docql.job.CjeEngine;
import docql.persistence.DocFileRepository;
import docql.persistence.DocPackageRepository;
import docql.publish.PublishFactory;
import docql.scan.FileScanService;
import docql.storage.StorageBackend;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Spring wiring for the default publish implementation. */
@Configuration
@ConditionalOnProperty(
    prefix = "docql.publish",
    name = "impl",
    havingValue = "default",
    matchIfMissing = true)
public class DefaultPublishConfiguration {

  @Bean
  public PublishFactory publishFactory(
      DocPackageRepository packageRepository,
      DocFileRepository fileRepository,
      StorageBackend storageBackend,
      CjeEngine cjeEngine,
      FileScanService fileScanService) {
    return new DefaultPublishFactory(
        packageRepository, fileRepository, storageBackend, fileScanService, cjeEngine);
  }
}
