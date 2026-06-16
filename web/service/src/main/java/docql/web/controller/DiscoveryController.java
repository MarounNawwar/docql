package docql.web.controller;

import docql.discovery.DiscoveryService;
import docql.web.api.DiscoveryApi;
import docql.web.dto.DocFileDto;
import docql.web.dto.DocPackageDto;
import docql.web.mapper.WebMapper;
import org.springframework.web.bind.annotation.RestController;

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
@RestController
public class DiscoveryController implements DiscoveryApi {

    private final DiscoveryService discoveryService;
    private final WebMapper webMapper;

    public DiscoveryController(DiscoveryService discoveryService, WebMapper webMapper) {
        this.discoveryService = discoveryService;
        this.webMapper        = webMapper;
    }

    @Override
    public List<DocPackageDto> listPackages(
            String team,
            String tag
    ) {
        if (team != null) return discoveryService.listByTeam(team).stream().map(webMapper::toDocPackageDto).toList();
        if (tag  != null) return discoveryService.listByTag(tag).stream().map(webMapper::toDocPackageDto).toList();
        return discoveryService.listAll().stream().map(webMapper::toDocPackageDto).toList();
    }

    @Override
    public DocPackageDto findLatest(
            String team,
            String product
    ) {
        return discoveryService.findLatest(team, product)
                .map(webMapper::toDocPackageDto)
                .orElseThrow(() -> new ResourceNotFoundException(team, product, "latest"));
    }

    @Override
    public DocPackageDto findVersion(
            String team,
            String product,
            String version
    ) {
        return discoveryService.findVersion(team, product, version)
                .map(webMapper::toDocPackageDto)
                .orElseThrow(() -> new ResourceNotFoundException(team, product, version));
    }

    @Override
    public List<DocFileDto> listFiles(
            String team,
            String product,
            String version
    ) {
        return discoveryService.listFiles(team, product, version).stream()
                .map(webMapper::toDocFileDto)
                .toList();
    }

    @Override
    public DocFileDto readIndex(
            String team,
            String product,
            String version
    ) {
        return discoveryService.readIndex(team, product, version)
                .map(webMapper::toDocFileDto)
                .orElseThrow(() -> new ResourceNotFoundException(team, product, version));
    }

    @Override
    public DocFileDto readFile(
            String team,
            String product,
            String version,
            String filePath
    ) {
        return discoveryService.readFile(team, product, version, filePath)
                .map(webMapper::toDocFileDto)
                .orElseThrow(() -> new ResourceNotFoundException(team, product, version));
    }
}
