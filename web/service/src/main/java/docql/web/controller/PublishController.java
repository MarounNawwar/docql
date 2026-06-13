package docql.web.controller;

import docql.publish.PublishService;
import docql.web.dto.DocPackageDto;
import docql.web.dto.PublishRequestDto;
import docql.web.mapper.WebMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for publishing and retracting documentation bundles.
 *
 * <ul>
 *   <li>POST   /packages                          — publish a new doc package</li>
 *   <li>DELETE /packages/{team}/{product}/{version} — retract a specific version</li>
 * </ul>
 */
@Tag(name = "Publish", description = "Publish and retract versioned documentation packages")
@RestController
@RequestMapping("/packages")
public class PublishController {

    private final PublishService publishService;
    private final WebMapper webMapper;

    public PublishController(PublishService publishService, WebMapper webMapper) {
        this.publishService = publishService;
        this.webMapper      = webMapper;
    }

    @Operation(summary = "Publish a documentation package",
               description = "Stores all files, persists metadata, and triggers indexing.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Package published successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid publish request")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DocPackageDto publish(@RequestBody PublishRequestDto dto) {
        return webMapper.toDocPackageDto(
                publishService.publish(webMapper.toPublishRequest(dto))
        );
    }

    @Operation(summary = "Retract a documentation package version",
               description = "Removes all stored files, database records and search-index entries for the version.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Package retracted successfully"),
        @ApiResponse(responseCode = "404", description = "Package not found")
    })
    @DeleteMapping("/{team}/{product}/{version}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void retract(
            @PathVariable String team,
            @PathVariable String product,
            @PathVariable String version
    ) {
        publishService.retract(team, product, version);
    }
}
