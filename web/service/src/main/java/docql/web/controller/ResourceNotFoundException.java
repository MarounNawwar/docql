package docql.web.controller;

/**
 * @deprecated Use {@link docql.discovery.exception.PackageNotFoundException} or {@link
 *     docql.discovery.exception.DocFileNotFoundException} instead. This class is retained only for
 *     source compatibility and will be removed in a future release.
 */
@Deprecated(forRemoval = true)
public class ResourceNotFoundException extends docql.discovery.exception.PackageNotFoundException {

  public ResourceNotFoundException(String team, String product, String version) {
    super(team, product, version);
  }
}
