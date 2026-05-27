package docql.storage.fs;

import docql.storage.StorageBackend;
import docql.storage.StorageFactory;

import java.nio.file.Path;

/**
 * Filesystem-backed {@link StorageFactory}.
 */
public class FilesystemStorageFactory implements StorageFactory {

    private final StorageBackend backend;

    public FilesystemStorageFactory(Path rootDir) {
        this.backend = new FilesystemStorageBackend(rootDir);
    }

    @Override
    public StorageBackend storageBackend() {
        return backend;
    }
}
