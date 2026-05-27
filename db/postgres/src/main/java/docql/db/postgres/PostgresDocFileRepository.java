package docql.db.postgres;

import docql.core.DocFile;
import docql.db.DocFileRepository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Placeholder Postgres implementation of {@link DocFileRepository}.
 */
public class PostgresDocFileRepository implements DocFileRepository {

    // TODO: replace with real JDBC / JPA persistence
    private final Map<String, List<DocFile>> store = new ConcurrentHashMap<>();

    @Override
    public DocFile save(DocFile docFile) {
        store.computeIfAbsent(docFile.packageId(), k -> new ArrayList<>()).add(docFile);
        return docFile;
    }

    @Override
    public List<DocFile> saveAll(List<DocFile> files) {
        files.forEach(this::save);
        return files;
    }

    @Override
    public Optional<DocFile> findByPackageIdAndPath(String packageId, String path) {
        return store.getOrDefault(packageId, List.of()).stream()
                .filter(f -> f.path().equals(path))
                .findFirst();
    }

    @Override
    public List<DocFile> findByPackageId(String packageId) {
        return new ArrayList<>(store.getOrDefault(packageId, List.of()));
    }

    @Override
    public void deleteByPackageId(String packageId) {
        store.remove(packageId);
    }
}
