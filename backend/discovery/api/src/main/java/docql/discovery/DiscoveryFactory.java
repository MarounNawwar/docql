package docql.discovery;

/** Backend factory that produces a {@link DiscoveryService}. */
public interface DiscoveryFactory {
  DiscoveryService discoveryService();
}
