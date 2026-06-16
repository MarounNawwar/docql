package docql.persistence.postgres;

import docql.core.DocFile;
import docql.persistence.DocFileRepository;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * JPA-backed implementation of {@link DocFileRepository}.
 * Delegates to {@link DocFileJpaRepository} and converts
 * {@link DocFileEntity} ↔ {@link DocFile} domain records.
 */
public class PostgresDocFileRepository implements DocFileRepository {

    private final DocFileJpaRepository jpa;
    private final DocFileMapper mapper = DocFileMapper.INSTANCE;

    public PostgresDocFileRepository(DocFileJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public DocFile save(DocFile file) {
        jpa.save(mapper.toEntity(file));
        return file;
    }

    @Override
    public List<DocFile> saveAll(List<DocFile> files) {
        jpa.saveAll(files.stream().map(mapper::toEntity).toList());
        return files;
    }

    @Override
    public Optional<DocFile> findByPackageIdAndPath(String packageId, String path) {
        return jpa.findById(new DocFileId(packageId, path)).map(mapper::toDomain);
    }

    @Override
    public List<DocFile> findByPackageId(String packageId) {
        return jpa.findByIdPackageId(packageId).stream().map(mapper::toDomain).toList();
    }

    @Override
    @Transactional
    public void deleteByPackageId(String packageId) {
        jpa.deleteByIdPackageId(packageId);
    }
}
