package docql.persistence.postgres;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import docql.core.DocFile;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PostgresDocFileRepositoryTest {

  @Mock private DocFileJpaRepository jpa;

  private PostgresDocFileRepository repository;

  @BeforeEach
  void setUp() {
    repository = new PostgresDocFileRepository(jpa);
  }

  private static DocFile sampleDomain(String pkg, String path) {
    return new DocFile(pkg, path, "Title", "# Hello");
  }

  private static DocFileEntity sampleEntity(String pkg, String path) {
    return new DocFileEntity(pkg, path, "Title", "# Hello");
  }

  @Test
  void save_delegatesToJpaAndReturnsDomain() {
    when(jpa.save(any())).thenAnswer(inv -> inv.getArgument(0));

    DocFile result = repository.save(sampleDomain("pkg-1", "README.md"));

    assertThat(result.packageId()).isEqualTo("pkg-1");
    assertThat(result.path()).isEqualTo("README.md");
    verify(jpa).save(any(DocFileEntity.class));
  }

  @Test
  void saveAll_delegatesAllEntitiesToJpa() {
    when(jpa.saveAll(any())).thenAnswer(inv -> inv.getArgument(0));

    List<DocFile> files =
        List.of(sampleDomain("pkg-1", "README.md"), sampleDomain("pkg-1", "guide.md"));
    List<DocFile> result = repository.saveAll(files);

    assertThat(result).hasSize(2);
    verify(jpa).saveAll(argThat(list -> ((List<?>) list).size() == 2));
  }

  @Test
  void findByPackageIdAndPath_mapsEntityToDomain() {
    DocFileId id = new DocFileId("pkg-1", "README.md");
    when(jpa.findById(id)).thenReturn(Optional.of(sampleEntity("pkg-1", "README.md")));

    Optional<DocFile> result = repository.findByPackageIdAndPath("pkg-1", "README.md");

    assertThat(result).isPresent();
    assertThat(result.get().content()).isEqualTo("# Hello");
  }

  @Test
  void findByPackageIdAndPath_returnsEmptyWhenNotFound() {
    when(jpa.findById(any())).thenReturn(Optional.empty());

    assertThat(repository.findByPackageIdAndPath("pkg-1", "missing.md")).isEmpty();
  }

  @Test
  void findByPackageId_mapsAllEntitiesToDomain() {
    when(jpa.findByIdPackageId("pkg-1"))
        .thenReturn(List.of(sampleEntity("pkg-1", "README.md"), sampleEntity("pkg-1", "guide.md")));

    List<DocFile> result = repository.findByPackageId("pkg-1");

    assertThat(result).hasSize(2);
    assertThat(result).extracting(DocFile::path).containsExactlyInAnyOrder("README.md", "guide.md");
  }

  @Test
  void deleteByPackageId_delegatesToJpa() {
    repository.deleteByPackageId("pkg-1");

    verify(jpa).deleteByIdPackageId("pkg-1");
  }

  @Test
  void mapstructMapperPreservesAllFields() {
    DocFile mapped = DocFileMapper.INSTANCE.toDomain(sampleEntity("pkg-42", "docs/api.md"));

    assertThat(mapped.packageId()).isEqualTo("pkg-42");
    assertThat(mapped.path()).isEqualTo("docs/api.md");
    assertThat(mapped.title()).isEqualTo("Title");
    assertThat(mapped.content()).isEqualTo("# Hello");
  }
}
