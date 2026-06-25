package docql.web.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import docql.core.DocPackage;
import docql.core.PublishRequest;
import docql.discovery.DiscoveryService;
import docql.publish.PublishService;
import docql.search.SearchEngine;
import docql.web.dto.DocPackageDto;
import docql.web.dto.PublishRequestDto;
import docql.web.mapper.WebMapper;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest({PublishController.class, DiscoveryController.class, SearchController.class})
@ContextConfiguration(
    classes = {PublishController.class, DiscoveryController.class, SearchController.class})
class OpenApiControllerContractIntegrationTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @MockBean private PublishService publishService;

  @MockBean private DiscoveryService discoveryService;

  @MockBean private SearchEngine searchEngine;

  @MockBean private WebMapper webMapper;

  @Test
  void publish_endpointUsesGeneratedContractAndReturnsCreated() throws Exception {
    var requestDto =
        new PublishRequestDto("eng", "docs", "1.0.0", List.of("api"), "index.md", List.of());
    var publishRequest =
        new PublishRequest("eng", "docs", "1.0.0", List.of("api"), List.of(), "index.md");
    var pkg =
        new DocPackage(
            "p1",
            "eng",
            "docs",
            "1.0.0",
            List.of("api"),
            Instant.parse("2026-01-01T00:00:00Z"),
            "index.md");
    var responseDto =
        new DocPackageDto(
            "p1",
            "eng",
            "docs",
            "1.0.0",
            List.of("api"),
            Instant.parse("2026-01-01T00:00:00Z"),
            "index.md");

    when(webMapper.toPublishRequest(any(PublishRequestDto.class))).thenReturn(publishRequest);
    when(publishService.publish(publishRequest)).thenReturn(pkg);
    when(webMapper.toDocPackageDto(pkg)).thenReturn(responseDto);

    mockMvc
        .perform(
            post("/packages")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value("p1"))
        .andExpect(jsonPath("$.team").value("eng"));
  }

  @Test
  void discovery_endpointUsesGeneratedContractAndReturnsList() throws Exception {
    var pkg =
        new DocPackage(
            "p1",
            "eng",
            "docs",
            "1.0.0",
            List.of("api"),
            Instant.parse("2026-01-01T00:00:00Z"),
            "index.md");
    var responseDto =
        new DocPackageDto(
            "p1",
            "eng",
            "docs",
            "1.0.0",
            List.of("api"),
            Instant.parse("2026-01-01T00:00:00Z"),
            "index.md");

    when(discoveryService.listByTeam("eng", null, null)).thenReturn(List.of(pkg));
    when(webMapper.toDocPackageDto(pkg)).thenReturn(responseDto);

    mockMvc
        .perform(get("/packages").queryParam("team", "eng"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id").value("p1"))
        .andExpect(jsonPath("$[0].product").value("docs"));
  }

  @Test
  void search_endpointUsesGeneratedContractAndReturnsOk() throws Exception {
    when(searchEngine.search("guide", List.of("api"), "eng")).thenReturn(List.of());

    mockMvc
        .perform(
            get("/search")
                .queryParam("q", "guide")
                .queryParam("team", "eng")
                .queryParam("tags", "api"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray());
  }
}
