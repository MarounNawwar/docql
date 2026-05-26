package docql.web.controller;

import docql.publish.PublishService;
import docql.web.dto.DocPackageDto;
import docql.web.dto.PublishRequestDto;
import docql.web.mapper.WebMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for publishing and retracting documentation bundles.
 *
 * POST   /packages              — publish a new bundle
 * DELETE /packages/{team}/{product}/{version} — retract a version
 */
@RestController
@RequestMapping("/packages")
public class PublishController {

    private final PublishService publishService;

    public PublishController(PublishService publishService) {
        this.publishService = publishService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DocPackageDto publish(@RequestBody PublishRequestDto dto) {
        return WebMapper.toDto(publishService.publish(WebMapper.toDomain(dto)));
    }

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

