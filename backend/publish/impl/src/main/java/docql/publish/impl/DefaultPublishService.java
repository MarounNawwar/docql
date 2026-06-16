package docql.publish.impl;

import docql.job.CjeEngine;
import docql.job.CjeJob;
import docql.core.DocFile;
import docql.core.DocPackage;
import docql.core.PublishRequest;
import docql.persistence.DocFileRepository;
import docql.persistence.DocPackageRepository;
import docql.publish.PublishService;
import docql.storage.StorageBackend;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.UUID;

/**
 * Default {@link PublishService} implementation.
 * Flow: validate → store blobs → persist metadata → trigger CJE job.
 */
public class DefaultPublishService implements PublishService {

    private final DocPackageRepository packageRepository;
    private final DocFileRepository fileRepository;
    private final StorageBackend storageBackend;
    private final CjeEngine cjeEngine;

    public DefaultPublishService(
            DocPackageRepository packageRepository,
            DocFileRepository fileRepository,
            StorageBackend storageBackend,
            CjeEngine cjeEngine
    ) {
        this.packageRepository = packageRepository;
        this.fileRepository = fileRepository;
        this.storageBackend = storageBackend;
        this.cjeEngine = cjeEngine;
    }

    @Override
    public DocPackage publish(PublishRequest request) {
        validate(request);

        String packageId = UUID.randomUUID().toString();

        for (DocFile file : request.files()) {
            String key = storageKey(request.team(), request.product(), request.version(), file.path());
            storageBackend.store(key, new ByteArrayInputStream(file.content().getBytes(StandardCharsets.UTF_8)));
        }

        var filesWithId = request.files().stream()
                .map(f -> new DocFile(packageId, f.path(), f.title(), f.content()))
                .toList();
        fileRepository.saveAll(filesWithId);

        var docPackage = new DocPackage(packageId, request.team(), request.product(),
                request.version(), request.tags(), Instant.now(), request.indexFilePath());
        packageRepository.save(docPackage);

        cjeEngine.submit(new CjeJob(packageId, "INDEX_PACKAGE", packageId));

        return docPackage;
    }

    @Override
    public void retract(String team, String product, String version) {
        packageRepository.findByTeamProductAndVersion(team, product, version).ifPresent(pkg -> {
            fileRepository.deleteByPackageId(pkg.id());
            storageBackend.deleteByPrefix(storageKey(team, product, version, ""));
            packageRepository.deleteById(pkg.id());
        });
    }

    private void validate(PublishRequest request) {
        if (request.team() == null || request.team().isBlank())          throw new IllegalArgumentException("team is required");
        if (request.product() == null || request.product().isBlank())    throw new IllegalArgumentException("product is required");
        if (request.version() == null || request.version().isBlank())    throw new IllegalArgumentException("version is required");
        if (request.files() == null || request.files().isEmpty())        throw new IllegalArgumentException("at least one file is required");
        if (request.indexFilePath() == null || request.indexFilePath().isBlank()) throw new IllegalArgumentException("indexFilePath is required");
    }

    private String storageKey(String team, String product, String version, String path) {
        return team + "/" + product + "/" + version + "/" + path;
    }
}
