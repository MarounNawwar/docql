package docql.web.controller;

import docql.search.SearchEngine;
import docql.web.dto.SearchResultDto;
import docql.web.mapper.WebMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for full-text search across indexed documentation.
 *
 * <ul>
 *   <li>GET /search?q={query}                    — search all teams</li>
 *   <li>GET /search?q={query}&amp;team={team}    — restrict to a team</li>
 *   <li>GET /search?q={query}&amp;tags={t1,t2}   — restrict by tags</li>
 * </ul>
 */
@Tag(name = "Search", description = "Full-text search across all indexed documentation")
@RestController
@RequestMapping("/search")
public class SearchController {

    private final SearchEngine searchEngine;
    private final WebMapper webMapper;

    public SearchController(SearchEngine searchEngine, WebMapper webMapper) {
        this.searchEngine = searchEngine;
        this.webMapper    = webMapper;
    }

    @Operation(summary = "Search documentation",
               description = "Full-text search over title and content fields. "
                           + "Optionally restrict results to a specific team or set of tags.")
    @ApiResponse(responseCode = "200", description = "Ranked list of matching documents returned")
    @GetMapping
    public List<SearchResultDto> search(
            @Parameter(description = "Free-text search query", required = true) @RequestParam String q,
            @Parameter(description = "Restrict to a team")  @RequestParam(required = false) String team,
            @Parameter(description = "Filter by tags")      @RequestParam(required = false, defaultValue = "") List<String> tags
    ) {
        return searchEngine.search(q, tags, team).stream()
                .map(webMapper::toSearchResultDto)
                .toList();
    }
}
