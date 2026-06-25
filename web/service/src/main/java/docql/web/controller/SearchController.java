package docql.web.controller;

import docql.search.SearchEngine;
import docql.web.api.SearchApi;
import docql.web.dto.SearchResultDto;
import docql.web.mapper.WebMapper;
import java.util.List;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for full-text search across indexed documentation.
 *
 * <ul>
 *   <li>GET /search?q={query} — search all teams
 *   <li>GET /search?q={query}&amp;team={team} — restrict to a team
 *   <li>GET /search?q={query}&amp;tags={t1,t2} — restrict by tags
 * </ul>
 */
@RestController
public class SearchController implements SearchApi {

  private final SearchEngine searchEngine;
  private final WebMapper webMapper;

  public SearchController(SearchEngine searchEngine, WebMapper webMapper) {
    this.searchEngine = searchEngine;
    this.webMapper = webMapper;
  }

  @Override
  public List<SearchResultDto> search(String q, String team, List<String> tags) {
    var safeTags = tags == null ? List.<String>of() : tags;

    return searchEngine.search(q, safeTags, team).stream()
        .map(webMapper::toSearchResultDto)
        .toList();
  }
}
