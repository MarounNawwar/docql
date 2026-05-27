package docql.db.postgres;

import docql.core.DocPackage;
import docql.db.DocPackageRepository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Placeholder Postgres implementation of {@link DocPackageRepository}.
 * Replace the in-memory map with actual JDBC / JPA calls when wiring a real DataSource.
 */
public class PostgresDocPackageRepository implements DocPackageRepository {

    // TODO: replace with real JDBC DataSource / EntityManager
    private final Map<String, DocPackage> store = new ConcurrentHashMap<>();

    @Override
    public DocPackage save(DocPackage docPackage) {
        store.put(docPackage.id(), docPackage);
        return docPackage;
    }

    @Override
    public Optional<DocPackage> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public Optional<DocPackage> findLatestByTeamAndProduct(String team, String product) {
        return store.values().stream()
                .filter(p -> p.team().equals(team) && p.product().equals(product))
                .max(Comparator.comparing(DocPackage::publishedAt));
    }

    @Override
    public Optional<DocPackage> findByTeamProductAndVersion(String team, String product, String version) {
        return store.values().stream()
                .filter(p -> p.team().equals(team) && p.product().equals(product) && p.version().equals(version))
                .findFirst();
    }

    @Override
    public List<DocPackage> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public List<DocPackage> findByTeam(String team) {
        return store.values().stream().filter(p -> p.team().equals(team)).toList();
    }

    @Override
    public List<DocPackage> findByTag(String tag) {
        return store.values().stream().filter(p -> p.tags().contains(tag)).toList();
    }

    @Override
    public void deleteById(String id) {
        store.remove(id);
    }
}
