package docql.web.controller;

import docql.core.DocPackage;
import docql.discovery.DiscoveryService;
import docql.web.dto.DocFileDto;
import docql.web.dto.DocPackageDto;
import docql.web.mapper.WebMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DiscoveryControllerTest {

    @Mock
    private DiscoveryService discoveryService;

    @Mock
    private WebMapper webMapper;

    private DiscoveryController controller;

    private static final Instant NOW = Instant.parse("2026-01-01T00:00:00Z");

    @BeforeEach
    void setUp() {
        controller = new DiscoveryController(discoveryService, webMapper);
    }

    private DocPackage pkg(String id) {
        return new DocPackage(id, "eng", "docs", "1.0.0", List.of("api"), NOW, "index.md");
    }

    private DocPackageDto dto(String id) {
        return new DocPackageDto(id, "eng", "docs", "1.0.0", List.of("api"), NOW, "index.md");
    }

    @Test
    void list_noParams_returnsAll() {
        when(discoveryService.listAll()).thenReturn(List.of(pkg("p1"), pkg("p2")));
        when(webMapper.toDocPackageDto(pkg("p1"))).thenReturn(dto("p1"));
        when(webMapper.toDocPackageDto(pkg("p2"))).thenReturn(dto("p2"));

        List<DocPackageDto> result = controller.listPackages(null, null);

        assertThat(result).hasSize(2);
        verify(discoveryService).listAll();
        verify(discoveryService, never()).listByTeam(any());
        verify(discoveryService, never()).listByTag(any());
    }

    @Test
    void list_teamParam_filtersByTeam() {
        when(discoveryService.listByTeam("eng")).thenReturn(List.of(pkg("p1")));
        when(webMapper.toDocPackageDto(pkg("p1"))).thenReturn(dto("p1"));

        List<DocPackageDto> result = controller.listPackages("eng", null);

        assertThat(result).hasSize(1);
        verify(discoveryService).listByTeam("eng");
    }

    @Test
    void list_tagParam_filtersByTag() {
        when(discoveryService.listByTag("api")).thenReturn(List.of(pkg("p1")));
        when(webMapper.toDocPackageDto(pkg("p1"))).thenReturn(dto("p1"));

        List<DocPackageDto> result = controller.listPackages(null, "api");

        assertThat(result).hasSize(1);
        verify(discoveryService).listByTag("api");
    }

    @Test
    void findLatest_returnsDto() {
        when(discoveryService.findLatest("eng", "docs")).thenReturn(Optional.of(pkg("p1")));
        when(webMapper.toDocPackageDto(pkg("p1"))).thenReturn(dto("p1"));

        DocPackageDto result = controller.findLatest("eng", "docs");

        assertThat(result.getId()).isEqualTo("p1");
    }

    @Test
    void findLatest_notFound_throws404() {
        when(discoveryService.findLatest("eng", "missing")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> controller.findLatest("eng", "missing"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void findVersion_returnsDto() {
        when(discoveryService.findVersion("eng", "docs", "1.0.0")).thenReturn(Optional.of(pkg("p1")));
        when(webMapper.toDocPackageDto(pkg("p1"))).thenReturn(dto("p1"));

        DocPackageDto result = controller.findVersion("eng", "docs", "1.0.0");

        assertThat(result.getVersion()).isEqualTo("1.0.0");
    }

    @Test
    void findVersion_notFound_throws404() {
        when(discoveryService.findVersion("eng", "docs", "9.9.9")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> controller.findVersion("eng", "docs", "9.9.9"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void listFiles_returnsDtoList() {
        var file = new docql.core.DocFile("p1", "README.md", "Read Me", "# Hello");
        var fileDto = new DocFileDto("README.md", "Read Me", "# Hello");
        when(discoveryService.listFiles("eng", "docs", "1.0.0")).thenReturn(List.of(file));
        when(webMapper.toDocFileDto(file)).thenReturn(fileDto);

        List<DocFileDto> result = controller.listFiles("eng", "docs", "1.0.0");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getPath()).isEqualTo("README.md");
    }

    @Test
    void readIndex_returnsFileDto() {
        var file = new docql.core.DocFile("p1", "index.md", "Index", "# Index");
        var fileDto = new DocFileDto("index.md", "Index", "# Index");
        when(discoveryService.readIndex("eng", "docs", "1.0.0")).thenReturn(Optional.of(file));
        when(webMapper.toDocFileDto(file)).thenReturn(fileDto);

        DocFileDto result = controller.readIndex("eng", "docs", "1.0.0");

        assertThat(result.getPath()).isEqualTo("index.md");
        assertThat(result.getContent()).isEqualTo("# Index");
    }

    @Test
    void readIndex_notFound_throws404() {
        when(discoveryService.readIndex("eng", "docs", "1.0.0")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> controller.readIndex("eng", "docs", "1.0.0"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void readFile_returnsFileDto() {
        var file = new docql.core.DocFile("p1", "guide.md", "Guide", "## Guide");
        var fileDto = new DocFileDto("guide.md", "Guide", "## Guide");
        when(discoveryService.readFile("eng", "docs", "1.0.0", "guide.md")).thenReturn(Optional.of(file));
        when(webMapper.toDocFileDto(file)).thenReturn(fileDto);

        DocFileDto result = controller.readFile("eng", "docs", "1.0.0", "guide.md");

        assertThat(result.getPath()).isEqualTo("guide.md");
    }

    @Test
    void readFile_notFound_throws404() {
        when(discoveryService.readFile("eng", "docs", "1.0.0", "nope.md")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> controller.readFile("eng", "docs", "1.0.0", "nope.md"))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
