package docql.publish.impl;

import static java.util.UUID.randomUUID;

import docql.core.DocFile;
import docql.core.DocPackage;
import docql.core.PublishRequest;
import docql.job.CjeEngine;
import docql.job.CjeJob;
import docql.persistence.DocFileRepository;
import docql.persistence.DocPackageRepository;
import docql.publish.PublishService;
import docql.scan.FileScanService;
import docql.scan.ScanStatus;
import docql.storage.StorageBackend;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.time.Instant;

/**
 * Default {@link PublishService} implementation. Flow: validate → store blobs → persist metadata →
 * trigger CJE job.
 */
public class DefaultPublishService implements PublishService {

  private final DocPackageRepository packageRepository;
  private final DocFileRepository fileRepository;
  private final StorageBackend storageBackend;
  private final CjeEngine cjeEngine;
  private final FileScanService fileScanService;

  public DefaultPublishService(
      DocPackageRepository packageRepository,
      DocFileRepository fileRepository,
      StorageBackend storageBackend,
      CjeEngine cjeEngine,
      FileScanService fileScanService) {
    this.packageRepository = packageRepository;
    this.fileRepository = fileRepository;
    this.storageBackend = storageBackend;
    this.cjeEngine = cjeEngine;
    this.fileScanService = fileScanService;
  }

  @Override
  public DocPackage publish(PublishRequest request) {
    validate(request);

    String packageId = randomUUID().toString();

    for (DocFile file : request.files()) {
      enforceSafeContent(file);
      String key = storageKey(request.team(), request.product(), request.version(), file.path());
      storageBackend.store(
          key, new ByteArrayInputStream(file.content().getBytes(StandardCharsets.UTF_8)));
    }

    var filesWithId =
        request.files().stream()
            .map(f -> new DocFile(packageId, f.path(), f.title(), f.content()))
            .toList();
    fileRepository.saveAll(filesWithId);

    var docPackage =
        new DocPackage(
            packageId,
            request.team(),
            request.product(),
            request.version(),
            request.tags(),
            Instant.now(),
            request.indexFilePath());
    packageRepository.save(docPackage);

    cjeEngine.submit(new CjeJob(packageId, "INDEX_PACKAGE", packageId));

    return docPackage;
  }

  @Override
  public void retract(String team, String product, String version) {
    packageRepository
        .findByTeamProductAndVersion(team, product, version)
        .ifPresent(docPackage -> removePackage(docPackage, team, product, version));
  }

  private void validate(PublishRequest request) {
    if (request.team() == null || request.team().isBlank())
      throw new IllegalArgumentException("team is required");
    if (request.product() == null || request.product().isBlank())
      throw new IllegalArgumentException("product is required");
    if (request.version() == null || request.version().isBlank())
      throw new IllegalArgumentException("version is required");
    if (request.files() == null || request.files().isEmpty())
      throw new IllegalArgumentException("at least one file is required");
    if (request.indexFilePath() == null || request.indexFilePath().isBlank())
      throw new IllegalArgumentException("indexFilePath is required");
  }

  private String storageKey(String team, String product, String version, String path) {
    return team + "/" + product + "/" + version + "/" + path;
  }

  private void removePackage(DocPackage pkg, String team, String product, String version) {
    fileRepository.deleteByPackageId(pkg.id());
    storageBackend.deleteByPrefix(storageKey(team, product, version, ""));
    packageRepository.deleteById(pkg.id());
  }

  private void enforceSafeContent(DocFile file) {
    var scanResult = fileScanService.scan(file.path(), file.content());
    if (scanResult.status() == ScanStatus.CLEAN) {
      return;
    }
    throw new IllegalArgumentException(
        "file scan failed for " + file.path() + ": " + scanResult.message());
  }
}
