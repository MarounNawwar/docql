package docql.web.controller;

import docql.publish.PublishService;
import docql.web.api.PublishApi;
import docql.web.dto.DocPackageDto;
import docql.web.dto.PublishRequestDto;
import docql.web.mapper.WebMapper;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for publishing and retracting documentation bundles.
 *
 * <ul>
 *   <li>POST /packages — publish a new doc package
 *   <li>DELETE /packages/{team}/{product}/{version} — retract a specific version
 * </ul>
 */
@RestController
public class PublishController implements PublishApi {

  private final PublishService publishService;
  private final WebMapper webMapper;

  public PublishController(PublishService publishService, WebMapper webMapper) {
    this.publishService = publishService;
    this.webMapper = webMapper;
  }

  @Override
  public DocPackageDto publish(PublishRequestDto dto) {
    return webMapper.toDocPackageDto(publishService.publish(webMapper.toPublishRequest(dto)));
  }

  @Override
  public void retract(String team, String product, String version) {
    publishService.retract(team, product, version);
  }
}
