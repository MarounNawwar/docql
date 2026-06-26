package docql.publish.impl;

import static java.util.Objects.isNull;
import static java.util.UUID.randomUUID;

import docql.core.DocFile;
import docql.core.DocPackage;
import docql.core.PublishRequest;
import docql.job.CjeEngine;
import docql.job.CjeJob;
import docql.persistence.DocFileRepository;
import docql.persistence.DocPackageRepository;
import docql.publish.PublishService;
import docql.publish.exception.InvalidPublishRequestException;
import docql.publish.exception.PublishContentBlockedException;
import docql.scan.FileScanService;
import docql.scan.ScanStatus;
import docql.storage.StorageBackend;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Default {@link PublishService} implementation. Flow: validate → store blobs → persist metadata →
 * trigger CJE job.
 */
public class DefaultPublishService implements PublishService {

  private static final Logger log = LoggerFactory.getLogger(DefaultPublishService.class);

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
    var violations = new java.util.ArrayList<String>();
    if (isNull(request.team()) || request.team().isBlank()) violations.add("team is required");
    if (isNull(request.product()) || request.product().isBlank())
      violations.add("product is required");
    if (isNull(request.version()) || request.version().isBlank())
      violations.add("version is required");
    if (isNull(request.files()) || request.files().isEmpty())
      violations.add("at least one file is required");
    if (isNull(request.indexFilePath()) || request.indexFilePath().isBlank())
      violations.add("indexFilePath is required");
    if (!violations.isEmpty()) {
      log.warn(
          "Publish request validation failed for team={} product={} version={}: {}",
          request.team(),
          request.product(),
          request.version(),
          violations);
      throw new InvalidPublishRequestException("Publish request validation failed", violations);
    }
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
    log.warn("Content scan blocked file '{}': {}", file.path(), scanResult.message());
    throw new PublishContentBlockedException(
        "File content blocked by scanner: " + file.path(), List.of(scanResult.message()));
  }
}
