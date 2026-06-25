package docql.web.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import docql.core.DocPackage;
import docql.publish.PublishService;
import docql.web.dto.DocPackageDto;
import docql.web.dto.PublishRequestDto;
import docql.web.mapper.WebMapper;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PublishControllerTest {

  @Mock private PublishService publishService;

  @Mock private WebMapper webMapper;

  private PublishController controller;

  private static final Instant NOW = Instant.parse("2026-01-01T00:00:00Z");

  @BeforeEach
  void setUp() {
    controller = new PublishController(publishService, webMapper);
  }

  @Test
  void publish_mapsRequestAndReturnsDto() {
    var requestDto =
        new PublishRequestDto("eng", "docs", "1.0.0", List.of(), "index.md", List.of());
    var domainRequest =
        new docql.core.PublishRequest("eng", "docs", "1.0.0", List.of(), List.of(), "index.md");
    var domainPkg = new DocPackage("p1", "eng", "docs", "1.0.0", List.of(), NOW, "index.md");
    var resultDto = new DocPackageDto("p1", "eng", "docs", "1.0.0", List.of(), NOW, "index.md");

    when(webMapper.toPublishRequest(requestDto)).thenReturn(domainRequest);
    when(publishService.publish(domainRequest)).thenReturn(domainPkg);
    when(webMapper.toDocPackageDto(domainPkg)).thenReturn(resultDto);

    DocPackageDto result = controller.publish(requestDto);

    assertThat(result.getId()).isEqualTo("p1");
    verify(publishService).publish(domainRequest);
  }

  @Test
  void retract_delegatesToService() {
    controller.retract("eng", "docs", "1.0.0");

    verify(publishService).retract("eng", "docs", "1.0.0");
  }
}
