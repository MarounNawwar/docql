package docql.discovery.impl;

import docql.db.DocFileRepository;
import docql.db.DocPackageRepository;
import docql.discovery.DiscoveryFactory;
import docql.discovery.DiscoveryService;

/**
 * Default {@link DiscoveryFactory}.
 */
public class DefaultDiscoveryFactory implements DiscoveryFactory {

    private final DiscoveryService discoveryService;

    public DefaultDiscoveryFactory(DocPackageRepository packageRepository, DocFileRepository fileRepository) {
        this.discoveryService = new DefaultDiscoveryService(packageRepository, fileRepository);
    }

    @Override
    public DiscoveryService discoveryService() {
        return discoveryService;
    }
}

