package docql.persistence;

/**
 * Factory that produces a {@link DocPackageRepository} and {@link DocFileRepository}.
 * Swap the implementation by changing which factory is loaded (Postgres, in-memory, Nexus, etc.).
 */
public interface DbFactory {

    DocPackageRepository packageRepository();

    DocFileRepository fileRepository();
}
