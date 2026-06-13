package docql.storage.fs;

import docql.storage.StorageBackend;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;

/**
 * Spring wiring for the filesystem storage backend.
 */
@Configuration
@ConditionalOnProperty(prefix = "docql.storage", name = "impl", havingValue = "fs", matchIfMissing = true)
public class FilesystemStorageConfiguration {

    @Bean
    public StorageBackend storageBackend(
            @Value("${docql.storage.root:docql-storage}") String storageRoot
    ) {
        return new FilesystemStorageFactory(Path.of(storageRoot)).storageBackend();
    }
}
