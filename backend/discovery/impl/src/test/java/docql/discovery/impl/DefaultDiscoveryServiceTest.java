package docql.discovery.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import docql.core.DocFile;
import docql.core.DocPackage;
import docql.persistence.DocFileRepository;
import docql.persistence.DocPackageRepository;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class DefaultDiscoveryServiceTest {

  @Test
  void listByTeam_returnsMatchingPackages() {
    var packageRepository = new InMemoryDocPackageRepository();
    var fileRepository = new InMemoryDocFileRepository();
    var service = new DefaultDiscoveryService(packageRepository, fileRepository);

    packageRepository.save(
        new DocPackage("pkg-1", "team-a", "docs", "1.0.0", List.of(), Instant.EPOCH, "index.md"));
    packageRepository.save(
        new DocPackage("pkg-2", "team-b", "docs", "1.0.0", List.of(), Instant.EPOCH, "index.md"));

    var result = service.listByTeam("team-a", 0, 10);

    assertEquals(1, result.size());
    assertEquals("pkg-1", result.getFirst().id());
  }

  @Test
  void readIndex_readsConfiguredIndexFile() {
    var packageRepository = new InMemoryDocPackageRepository();
    var fileRepository = new InMemoryDocFileRepository();
    var service = new DefaultDiscoveryService(packageRepository, fileRepository);
    packageRepository.save(
        new DocPackage("pkg-1", "team-a", "docs", "1.0.0", List.of(), Instant.EPOCH, "README.md"));
    fileRepository.save(new DocFile("pkg-1", "README.md", "Home", "# hello"));
    fileRepository.save(new DocFile("pkg-1", "guide.md", "Guide", "content"));

    var result = service.readIndex("team-a", "docs", "1.0.0");

    assertTrue(result.isPresent());
    assertEquals("README.md", result.orElseThrow().path());
  }

  @Test
  void listFiles_missingPackage_returnsEmptyList() {
    var service =
        new DefaultDiscoveryService(
            new InMemoryDocPackageRepository(), new InMemoryDocFileRepository());

    var result = service.listFiles("team-a", "docs", "1.0.0");

    assertTrue(result.isEmpty());
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
          .filter(file -> file.packageId().equals(packageId) && file.path().equals(path))
          .findFirst();
    }

    @Override
    public List<DocFile> findByPackageId(String packageId) {
      return files.stream().filter(file -> file.packageId().equals(packageId)).toList();
    }

    @Override
    public void deleteByPackageId(String packageId) {
      files.removeIf(file -> file.packageId().equals(packageId));
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
      return packages.stream().filter(pkg -> pkg.id().equals(id)).findFirst();
    }

    @Override
    public Optional<DocPackage> findLatestByTeamAndProduct(String team, String product) {
      return packages.stream()
          .filter(pkg -> pkg.team().equals(team) && pkg.product().equals(product))
          .findFirst();
    }

    @Override
    public Optional<DocPackage> findByTeamProductAndVersion(
        String team, String product, String version) {
      return packages.stream()
          .filter(
              pkg ->
                  pkg.team().equals(team)
                      && pkg.product().equals(product)
                      && pkg.version().equals(version))
          .findFirst();
    }

    @Override
    public List<DocPackage> findAll(Integer offset, Integer limit) {
      return List.copyOf(packages);
    }

    @Override
    public List<DocPackage> findByTeam(String team, Integer offset, Integer limit) {
      return packages.stream().filter(pkg -> pkg.team().equals(team)).toList();
    }

    @Override
    public List<DocPackage> findByTag(String tag, Integer offset, Integer limit) {
      return packages.stream().filter(pkg -> pkg.tags().contains(tag)).toList();
    }

    @Override
    public void deleteById(String id) {
      packages.removeIf(pkg -> pkg.id().equals(id));
    }
  }
}
