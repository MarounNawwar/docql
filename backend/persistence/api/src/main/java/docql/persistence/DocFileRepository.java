package docql.persistence;

import docql.core.DocFile;
import java.util.List;
import java.util.Optional;

/** Contract for persisting and retrieving DocFile records. */
public interface DocFileRepository {

  DocFile save(DocFile docFile);

  List<DocFile> saveAll(List<DocFile> files);

  Optional<DocFile> findByPackageIdAndPath(String packageId, String path);

  List<DocFile> findByPackageId(String packageId);

  void deleteByPackageId(String packageId);
}
