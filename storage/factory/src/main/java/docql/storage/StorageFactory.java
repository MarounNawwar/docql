package docql.storage;

/**
 * Factory that produces a {@link StorageBackend}.
 */
public interface StorageFactory {
    StorageBackend storageBackend();
}
