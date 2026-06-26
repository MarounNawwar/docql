package docql.publish.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import docql.core.DocFile;
import docql.core.DocPackage;
import docql.core.PublishRequest;
import docql.job.CjeEngine;
import docql.job.CjeJob;
import docql.job.CjeJobResult;
import docql.job.CjeJobStatus;
import docql.persistence.DocFileRepository;
import docql.persistence.DocPackageRepository;
import docql.scan.FileScanService;
import docql.scan.ScanResult;
import docql.storage.StorageBackend;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class DefaultPublishServiceTest {

  @Test
  void publish_cleanFile_persistsAndSubmitsJob() {
    var packageRepository = new InMemoryDocPackageRepository();
    var fileRepository = new InMemoryDocFileRepository();
    var storageBackend = new RecordingStorageBackend();
    var cjeEngine = new RecordingCjeEngine();
    FileScanService scanner = (path, content) -> ScanResult.clean("test");
    var service =
        new DefaultPublishService(
            packageRepository, fileRepository, storageBackend, cjeEngine, scanner);

    var request =
        new PublishRequest(
            "team-a",
            "product-a",
            "1.0.0",
            List.of("docs"),
            List.of(new DocFile("", "index.md", "Index", "# hello")),
            "index.md");

    DocPackage result = service.publish(request);

    assertEquals("team-a", result.team());
    assertEquals(1, fileRepository.files.size());
    assertEquals(1, storageBackend.storedKeys.size());
    assertEquals("team-a/product-a/1.0.0/index.md", storageBackend.storedKeys.getFirst());
    assertEquals(1, cjeEngine.jobs.size());
    assertEquals("INDEX_PACKAGE", cjeEngine.jobs.getFirst().type());
  }

  @Test
  void publish_maliciousFile_throwsAndSkipsStorageAndPersistence() {
    var packageRepository = new InMemoryDocPackageRepository();
    var fileRepository = new InMemoryDocFileRepository();
    var storageBackend = new RecordingStorageBackend();
    var cjeEngine = new RecordingCjeEngine();
    FileScanService scanner = (path, content) -> ScanResult.malicious("test", "blocked");
    var service =
        new DefaultPublishService(
            packageRepository, fileRepository, storageBackend, cjeEngine, scanner);

    var request =
        new PublishRequest(
            "team-a",
            "product-a",
            "1.0.0",
            List.of(),
            List.of(new DocFile("", "index.md", "Index", "bad")),
            "index.md");

    assertThrows(IllegalArgumentException.class, () -> service.publish(request));
    assertEquals(0, storageBackend.storedKeys.size());
    assertEquals(0, fileRepository.files.size());
    assertEquals(0, packageRepository.packages.size());
    assertEquals(0, cjeEngine.jobs.size());
  }

  private static final class RecordingStorageBackend implements StorageBackend {
    private final List<String> storedKeys = new ArrayList<>();

    @Override
    public void store(String key, InputStream content) {
      storedKeys.add(key);
    }

    @Override
    public InputStream retrieve(String key) {
      throw new UnsupportedOperationException();
    }

    @Override
    public List<String> list(String prefix) {
      return List.of();
    }

    @Override
    public void deleteByPrefix(String prefix) {
      // not needed in this test
    }

    @Override
    public boolean exists(String key) {
      return false;
    }
  }

  private static final class InMemoryDocFileRepository implements DocFileRepository {
    private final List<DocFile> files = new ArrayList<>();

    @Override
    public DocFile save(DocFile docFile) {
      files.add(docFile);
      return docFile;
    }

    @Override
    public List<DocFile> saveAll(List<DocFile> files) {
      this.files.addAll(files);
      return files;
    }

    @Override
    public Optional<DocFile> findByPackageIdAndPath(String packageId, String path) {
      return files.stream()
          .filter(f -> f.packageId().equals(packageId) && f.path().equals(path))
          .findFirst();
    }

    @Override
    public List<DocFile> findByPackageId(String packageId) {
      return files.stream().filter(f -> f.packageId().equals(packageId)).toList();
    }

    @Override
    public void deleteByPackageId(String packageId) {
      files.removeIf(f -> f.packageId().equals(packageId));
    }
  }

  private static final class InMemoryDocPackageRepository implements DocPackageRepository {
    private final List<DocPackage> packages = new ArrayList<>();

    @Override
    public DocPackage save(DocPackage docPackage) {
      packages.add(docPackage);
      return docPackage;
    }

    @Override
    public Optional<DocPackage> findById(String id) {
      return packages.stream().filter(p -> p.id().equals(id)).findFirst();
    }

    @Override
    public Optional<DocPackage> findLatestByTeamAndProduct(String team, String product) {
      return packages.stream()
          .filter(p -> p.team().equals(team) && p.product().equals(product))
          .findFirst();
    }

    @Override
    public Optional<DocPackage> findByTeamProductAndVersion(
        String team, String product, String version) {
      return packages.stream()
          .filter(
              p ->
                  p.team().equals(team)
                      && p.product().equals(product)
                      && p.version().equals(version))
          .findFirst();
    }

    @Override
    public List<DocPackage> findAll(Integer offset, Integer limit) {
      return List.copyOf(packages);
    }

    @Override
    public List<DocPackage> findByTeam(String team, Integer offset, Integer limit) {
      return packages.stream().filter(p -> p.team().equals(team)).toList();
    }

    @Override
    public List<DocPackage> findByTag(String tag, Integer offset, Integer limit) {
      return packages.stream().filter(p -> p.tags() != null && p.tags().contains(tag)).toList();
    }

    @Override
    public void deleteById(String id) {
      packages.removeIf(p -> p.id().equals(id));
    }
  }

  private static final class RecordingCjeEngine implements CjeEngine {
    private final List<CjeJob> jobs = new ArrayList<>();

    @Override
    public CjeJobResult submit(CjeJob job) {
      jobs.add(job);
      return new CjeJobResult(job.id(), CjeJobStatus.SUCCESS, "ok");
    }
  }
}
