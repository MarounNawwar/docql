package docql.discovery;

/**
 * Factory that produces a {@link DiscoveryService}.
 */
public interface DiscoveryFactory {
    DiscoveryService discoveryService();
}

