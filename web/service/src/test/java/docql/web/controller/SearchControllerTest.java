package docql.web.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import docql.core.SearchResult;
import docql.search.SearchEngine;
import docql.web.dto.SearchResultDto;
import docql.web.mapper.WebMapper;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SearchControllerTest {

  @Mock private SearchEngine searchEngine;

  @Mock private WebMapper webMapper;

  private SearchController controller;

  @BeforeEach
  void setUp() {
    controller = new SearchController(searchEngine, webMapper);
  }

  @Test
  void search_delegatesAndMapsResults() {
    var result =
        new SearchResult("p1", "eng", "docs", "1.0.0", "guide.md", "Guide", "...snippet...");
    var resultDto =
        new SearchResultDto("p1", "eng", "docs", "1.0.0", "guide.md", "Guide", "...snippet...");

    when(searchEngine.search("query", List.of(), "eng")).thenReturn(List.of(result));
    when(webMapper.toSearchResultDto(result)).thenReturn(resultDto);

    List<SearchResultDto> response = controller.search("query", "eng", List.of());

    assertThat(response).hasSize(1);
    assertThat(response.get(0).getPackageId()).isEqualTo("p1");
    verify(searchEngine).search("query", List.of(), "eng");
  }

  @Test
  void search_noTeam_searchesAllTeams() {
    when(searchEngine.search("query", List.of(), null)).thenReturn(List.of());

    List<SearchResultDto> response = controller.search("query", null, List.of());

    assertThat(response).isEmpty();
    verify(searchEngine).search("query", List.of(), null);
  }

  @Test
  void search_nullTags_usesEmptyTagFilter() {
    when(searchEngine.search("query", List.of(), "eng")).thenReturn(List.of());

    List<SearchResultDto> response = controller.search("query", "eng", null);

    assertThat(response).isEmpty();
    verify(searchEngine).search("query", List.of(), "eng");
  }
}
