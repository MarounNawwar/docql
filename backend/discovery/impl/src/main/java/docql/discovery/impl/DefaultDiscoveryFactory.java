package docql.discovery.impl;

import docql.persistence.DocFileRepository;
import docql.persistence.DocPackageRepository;
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
