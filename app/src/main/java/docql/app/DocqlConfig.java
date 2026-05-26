package docql.app;

import docql.cje.CjeEngine;
import docql.cje.local.LocalCjeFactory;
import docql.db.DocFileRepository;
import docql.db.DocPackageRepository;
import docql.db.postgres.PostgresDbFactory;
import docql.discovery.DiscoveryService;
import docql.discovery.impl.DefaultDiscoveryFactory;
import docql.publish.PublishService;
import docql.publish.impl.DefaultPublishFactory;
import docql.search.SearchEngine;
import docql.search.lucene.LuceneSearchFactory;
import docql.storage.StorageBackend;
import docql.storage.fs.FilesystemStorageFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;

/**
 * Spring wiring configuration — the single place that knows about concrete implementations.
 *
 * To swap any layer (e.g. Postgres → Nexus, FS → S3, Local CJE → Jenkins):
 *   change only the factory instantiation in the corresponding @Bean method.
 */
@Configuration
public class DocqlConfig {

    // ── DB layer ────────────────────────────────────────────────────────────

    @Bean
    public DocPackageRepository docPackageRepository() {
        return new PostgresDbFactory().packageRepository();
    }

    @Bean
    public DocFileRepository docFileRepository() {
        return new PostgresDbFactory().fileRepository();
    }

    // ── Storage layer ───────────────────────────────────────────────────────

    @Bean
    public StorageBackend storageBackend(
            @Value("${docql.storage.root:docql-storage}") String storageRoot
    ) {
        return new FilesystemStorageFactory(Path.of(storageRoot)).storageBackend();
    }

    // ── CJE layer ───────────────────────────────────────────────────────────

    @Bean
    public CjeEngine cjeEngine() {
        return new LocalCjeFactory().cjeEngine();
    }

    // ── Search layer ────────────────────────────────────────────────────────

    @Bean
    public SearchEngine searchEngine() {
        return new LuceneSearchFactory().searchEngine();
    }

    // ── Publish domain ──────────────────────────────────────────────────────

    @Bean
    public PublishService publishService(
            DocPackageRepository packageRepository,
            DocFileRepository fileRepository,
            StorageBackend storageBackend,
            CjeEngine cjeEngine
    ) {
        return new DefaultPublishFactory(packageRepository, fileRepository, storageBackend, cjeEngine)
                .publishService();
    }

    // ── Discovery domain ────────────────────────────────────────────────────

    @Bean
    public DiscoveryService discoveryService(
            DocPackageRepository packageRepository,
            DocFileRepository fileRepository
    ) {
        return new DefaultDiscoveryFactory(packageRepository, fileRepository)
                .discoveryService();
    }
}

