package docql.web.controller;

import docql.discovery.DiscoveryService;
import docql.web.dto.DocFileDto;
import docql.web.dto.DocPackageDto;
import docql.web.mapper.WebMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for browsing and reading published documentation packages.
 *
 * <ul>
 *   <li>GET /packages                                          — list all packages</li>
 *   <li>GET /packages?team={team}                              — filter by team</li>
 *   <li>GET /packages?tag={tag}                                — filter by tag</li>
 *   <li>GET /packages/{team}/{product}                         — latest version metadata</li>
 *   <li>GET /packages/{team}/{product}/{version}               — specific version metadata</li>
 *   <li>GET /packages/{team}/{product}/{version}/files         — list all files</li>
 *   <li>GET /packages/{team}/{product}/{version}/index         — read the index file</li>
 *   <li>GET /packages/{team}/{product}/{version}/files/{*path} — read a specific file</li>
 * </ul>
 */
@Tag(name = "Discovery", description = "Browse and read published documentation packages")
@RestController
@RequestMapping("/packages")
public class DiscoveryController {

    private final DiscoveryService discoveryService;
    private final WebMapper webMapper;

    public DiscoveryController(DiscoveryService discoveryService, WebMapper webMapper) {
        this.discoveryService = discoveryService;
        this.webMapper        = webMapper;
    }

    @Operation(summary = "List packages", description = "Returns all packages, optionally filtered by team or tag.")
    @ApiResponse(responseCode = "200", description = "Package list returned")
    @GetMapping
    public List<DocPackageDto> list(
            @Parameter(description = "Filter by team name") @RequestParam(required = false) String team,
            @Parameter(description = "Filter by tag")       @RequestParam(required = false) String tag
    ) {
        if (team != null) return discoveryService.listByTeam(team).stream().map(webMapper::toDocPackageDto).toList();
        if (tag  != null) return discoveryService.listByTag(tag).stream().map(webMapper::toDocPackageDto).toList();
        return discoveryService.listAll().stream().map(webMapper::toDocPackageDto).toList();
    }

    @Operation(summary = "Get latest version of a product")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Package metadata returned"),
        @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @GetMapping("/{team}/{product}")
    public DocPackageDto findLatest(
            @PathVariable String team,
            @PathVariable String product
    ) {
        return discoveryService.findLatest(team, product)
                .map(webMapper::toDocPackageDto)
                .orElseThrow(() -> new ResourceNotFoundException(team, product, "latest"));
    }

    @Operation(summary = "Get a specific version of a product")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Package metadata returned"),
        @ApiResponse(responseCode = "404", description = "Version not found")
    })
    @GetMapping("/{team}/{product}/{version}")
    public DocPackageDto findVersion(
            @PathVariable String team,
            @PathVariable String product,
            @PathVariable String version
    ) {
        return discoveryService.findVersion(team, product, version)
                .map(webMapper::toDocPackageDto)
                .orElseThrow(() -> new ResourceNotFoundException(team, product, version));
    }

    @Operation(summary = "List all files in a package version")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "File list returned"),
        @ApiResponse(responseCode = "404", description = "Version not found")
    })
    @GetMapping("/{team}/{product}/{version}/files")
    public List<DocFileDto> listFiles(
            @PathVariable String team,
            @PathVariable String product,
            @PathVariable String version
    ) {
        return discoveryService.listFiles(team, product, version).stream()
                .map(webMapper::toDocFileDto)
                .toList();
    }

    @Operation(summary = "Read the index file of a package version")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Index file content returned"),
        @ApiResponse(responseCode = "404", description = "Version or index file not found")
    })
    @GetMapping("/{team}/{product}/{version}/index")
    public DocFileDto readIndex(
            @PathVariable String team,
            @PathVariable String product,
            @PathVariable String version
    ) {
        return discoveryService.readIndex(team, product, version)
                .map(webMapper::toDocFileDto)
                .orElseThrow(() -> new ResourceNotFoundException(team, product, version));
    }

    @Operation(summary = "Read a specific file from a package version")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "File content returned"),
        @ApiResponse(responseCode = "404", description = "File not found")
    })
    @GetMapping("/{team}/{product}/{version}/files/{*filePath}")
    public DocFileDto readFile(
            @PathVariable String team,
            @PathVariable String product,
            @PathVariable String version,
            @PathVariable String filePath
    ) {
        return discoveryService.readFile(team, product, version, filePath)
                .map(webMapper::toDocFileDto)
                .orElseThrow(() -> new ResourceNotFoundException(team, product, version));
    }
}
