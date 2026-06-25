package docql.discovery.impl;

import docql.core.DocFile;
import docql.core.DocPackage;
import docql.discovery.DiscoveryService;
import docql.persistence.DocFileRepository;
import docql.persistence.DocPackageRepository;
import java.util.List;
import java.util.Optional;

/** Default {@link DiscoveryService} backed by the db repositories. */
public class DefaultDiscoveryService implements DiscoveryService {

  private final DocPackageRepository packageRepository;
  private final DocFileRepository fileRepository;

  public DefaultDiscoveryService(
      DocPackageRepository packageRepository, DocFileRepository fileRepository) {
    this.packageRepository = packageRepository;
    this.fileRepository = fileRepository;
  }

  @Override
  public List<DocPackage> listAll(Integer offset, Integer limit) {
    return packageRepository.findAll(offset, limit);
  }

  @Override
  public List<DocPackage> listByTeam(String team, Integer offset, Integer limit) {
    return packageRepository.findByTeam(team, offset, limit);
  }

  @Override
  public List<DocPackage> listByTag(String tag, Integer offset, Integer limit) {
    return packageRepository.findByTag(tag, offset, limit);
  }

  @Override
  public Optional<DocPackage> findLatest(String team, String product) {
    return packageRepository.findLatestByTeamAndProduct(team, product);
  }

  @Override
  public Optional<DocPackage> findVersion(String team, String product, String version) {
    return packageRepository.findByTeamProductAndVersion(team, product, version);
  }

  @Override
  public Optional<DocFile> readIndex(String team, String product, String version) {
    return findVersion(team, product, version)
        .flatMap(pkg -> fileRepository.findByPackageIdAndPath(pkg.id(), pkg.indexFilePath()));
  }

  @Override
  public Optional<DocFile> readFile(String team, String product, String version, String filePath) {
    return findVersion(team, product, version)
        .flatMap(pkg -> fileRepository.findByPackageIdAndPath(pkg.id(), filePath));
  }

  @Override
  public List<DocFile> listFiles(String team, String product, String version) {
    return findVersion(team, product, version)
        .map(pkg -> fileRepository.findByPackageId(pkg.id()))
        .orElse(List.of());
  }
}
