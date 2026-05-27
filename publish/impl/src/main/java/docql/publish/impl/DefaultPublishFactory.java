package docql.publish.impl;

import docql.cje.CjeEngine;
import docql.db.DocFileRepository;
import docql.db.DocPackageRepository;
import docql.publish.PublishFactory;
import docql.publish.PublishService;
import docql.storage.StorageBackend;

/**
 * Default {@link PublishFactory}.
 */
public class DefaultPublishFactory implements PublishFactory {

    private final PublishService publishService;

    public DefaultPublishFactory(
            DocPackageRepository packageRepository,
            DocFileRepository fileRepository,
            StorageBackend storageBackend,
            CjeEngine cjeEngine
    ) {
        this.publishService = new DefaultPublishService(packageRepository, fileRepository, storageBackend, cjeEngine);
    }

    @Override
    public PublishService publishService() {
        return publishService;
    }
}
