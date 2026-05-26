package docql.web.controller;

import docql.search.SearchEngine;
import docql.web.dto.SearchResultDto;
import docql.web.mapper.WebMapper;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for full-text search across indexed documentation.
 * <p>
 * GET /search?q={query}&amp;team={team}
 * </p>
 */
@RestController
@RequestMapping("/search")
public class SearchController {

    private final SearchEngine searchEngine;

    public SearchController(SearchEngine searchEngine) {
        this.searchEngine = searchEngine;
    }

    @GetMapping
    public List<SearchResultDto> search(
            @RequestParam String q,
            @RequestParam(required = false) String team,
            @RequestParam(required = false, defaultValue = "") List<String> tags
    ) {
        return searchEngine.search(q, tags, team).stream()
                .map(WebMapper::toDto)
                .toList();
    }
}

