package docql.persistence.postgres;

import docql.core.DocPackage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostgresDocPackageRepositoryTest {

    @Mock
    private DocPackageJpaRepository jpa;

    private PostgresDocPackageRepository repository;

    private static final Instant NOW = Instant.parse("2026-01-01T00:00:00Z");

    private static DocPackage sampleDomain(String id) {
        return new DocPackage(id, "team-a", "product-x", "1.0.0", List.of("tag1"), NOW, "README.md");
    }

    private static DocPackageEntity sampleEntity(String id) {
        return new DocPackageEntity(id, "team-a", "product-x", "1.0.0", List.of("tag1"), NOW, "README.md");
    }

    @BeforeEach
    void setUp() {
        repository = new PostgresDocPackageRepository(jpa);
    }

    @Test
    void save_delegatesToJpaAndReturnsDomain() {
        when(jpa.save(any())).thenAnswer(inv -> inv.getArgument(0));

        DocPackage result = repository.save(sampleDomain("pkg-1"));

        assertThat(result.id()).isEqualTo("pkg-1");
        verify(jpa).save(any(DocPackageEntity.class));
    }

    @Test
    void findById_mapsEntityToDomain() {
        when(jpa.findById("pkg-1")).thenReturn(Optional.of(sampleEntity("pkg-1")));

        Optional<DocPackage> result = repository.findById("pkg-1");

        assertThat(result).isPresent();
        assertThat(result.get().id()).isEqualTo("pkg-1");
        assertThat(result.get().team()).isEqualTo("team-a");
    }

    @Test
    void findById_returnsEmptyWhenNotFound() {
        when(jpa.findById("missing")).thenReturn(Optional.empty());

        assertThat(repository.findById("missing")).isEmpty();
    }

    @Test
    void findLatestByTeamAndProduct_mapsEntityToDomain() {
        when(jpa.findTopByTeamAndProductOrderByPublishedAtDesc("team-a", "product-x"))
                .thenReturn(Optional.of(sampleEntity("pkg-1")));

        Optional<DocPackage> result = repository.findLatestByTeamAndProduct("team-a", "product-x");

        assertThat(result).isPresent();
        assertThat(result.get().version()).isEqualTo("1.0.0");
    }

    @Test
    void findByTeamProductAndVersion_mapsEntityToDomain() {
        when(jpa.findByTeamAndProductAndVersion("team-a", "product-x", "1.0.0"))
                .thenReturn(Optional.of(sampleEntity("pkg-1")));

        Optional<DocPackage> result = repository.findByTeamProductAndVersion("team-a", "product-x", "1.0.0");

        assertThat(result).isPresent();
    }

    @Test
    void findAll_mapsAllEntitiesToDomain() {
        when(jpa.findAll()).thenReturn(List.of(sampleEntity("pkg-1"), sampleEntity("pkg-2")));

        List<DocPackage> result = repository.findAll();

        assertThat(result).hasSize(2);
        assertThat(result).extracting(DocPackage::id).containsExactlyInAnyOrder("pkg-1", "pkg-2");
    }

    @Test
    void findByTeam_delegatesAndMaps() {
        when(jpa.findByTeam("team-a")).thenReturn(List.of(sampleEntity("pkg-1")));

        List<DocPackage> result = repository.findByTeam("team-a");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).team()).isEqualTo("team-a");
    }

    @Test
    void findByTag_delegatesAndMaps() {
        when(jpa.findByTag("tag1")).thenReturn(List.of(sampleEntity("pkg-1")));

        List<DocPackage> result = repository.findByTag("tag1");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).tags()).contains("tag1");
    }

    @Test
    void deleteById_delegatesToJpa() {
        repository.deleteById("pkg-1");

        verify(jpa).deleteById("pkg-1");
    }

    @Test
    void mapstructMapperPreservesAllFields() {
        DocPackage mapped = DocPackageMapper.INSTANCE.toDomain(sampleEntity("id-42"));

        assertThat(mapped.id()).isEqualTo("id-42");
        assertThat(mapped.team()).isEqualTo("team-a");
        assertThat(mapped.product()).isEqualTo("product-x");
        assertThat(mapped.version()).isEqualTo("1.0.0");
        assertThat(mapped.tags()).containsExactly("tag1");
        assertThat(mapped.publishedAt()).isEqualTo(NOW);
        assertThat(mapped.indexFilePath()).isEqualTo("README.md");
    }
}
