package docql.web.controller;

import docql.core.DocFile;
import docql.discovery.DiscoveryService;
import docql.web.dto.DocPackageDto;
import docql.web.mapper.WebMapper;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST controller for discovering documentation bundles.
 *
 * GET /packages                                        — list all packages
 * GET /packages?team={team}                            — filter by team
 * GET /packages?tag={tag}                              — filter by tag
 * GET /packages/{team}/{product}                       — latest version of a product
 * GET /packages/{team}/{product}/{version}             — specific version metadata
 * GET /packages/{team}/{product}/{version}/files       — list all files in a version
 * GET /packages/{team}/{product}/{version}/index       — read index file content
 * GET /packages/{team}/{product}/{version}/files/{*path} — read a specific file
 */
@RestController
@RequestMapping("/packages")
public class DiscoveryController {

    private final DiscoveryService discoveryService;

    public DiscoveryController(DiscoveryService discoveryService) {
        this.discoveryService = discoveryService;
    }

    @GetMapping
    public List<DocPackageDto> list(
            @RequestParam(required = false) String team,
            @RequestParam(required = false) String tag
    ) {
        if (team != null) return discoveryService.listByTeam(team).stream().map(WebMapper::toDto).toList();
        if (tag  != null) return discoveryService.listByTag(tag).stream().map(WebMapper::toDto).toList();
        return discoveryService.listAll().stream().map(WebMapper::toDto).toList();
    }

    @GetMapping("/{team}/{product}")
    public DocPackageDto findLatest(@PathVariable String team, @PathVariable String product) {
        return discoveryService.findLatest(team, product)
                .map(WebMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException(team, product, "latest"));
    }

    @GetMapping("/{team}/{product}/{version}")
    public DocPackageDto findVersion(
            @PathVariable String team,
            @PathVariable String product,
            @PathVariable String version
    ) {
        return discoveryService.findVersion(team, product, version)
                .map(WebMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException(team, product, version));
    }

    @GetMapping("/{team}/{product}/{version}/files")
    public List<Map<String, String>> listFiles(
            @PathVariable String team,
            @PathVariable String product,
            @PathVariable String version
    ) {
        return discoveryService.listFiles(team, product, version).stream()
                .map(f -> Map.of("path", f.path(), "title", f.title() != null ? f.title() : ""))
                .toList();
    }

    @GetMapping("/{team}/{product}/{version}/index")
    public Map<String, String> readIndex(
            @PathVariable String team,
            @PathVariable String product,
            @PathVariable String version
    ) {
        return discoveryService.readIndex(team, product, version)
                .map(f -> Map.of("path", f.path(), "title", coalesce(f.title()), "content", coalesce(f.content())))
                .orElseThrow(() -> new ResourceNotFoundException(team, product, version));
    }

    @GetMapping("/{team}/{product}/{version}/files/{*filePath}")
    public Map<String, String> readFile(
            @PathVariable String team,
            @PathVariable String product,
            @PathVariable String version,
            @PathVariable String filePath
    ) {
        return discoveryService.readFile(team, product, version, filePath)
                .map(f -> Map.of("path", f.path(), "title", coalesce(f.title()), "content", coalesce(f.content())))
                .orElseThrow(() -> new ResourceNotFoundException(team, product, version));
    }

    private String coalesce(String value) {
        return value != null ? value : "";
    }
}

