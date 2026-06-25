package docql.web.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Thrown when a requested doc package or file is not found. Resolved to HTTP 404 by Spring's
 * exception-handling mechanism.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {

  public ResourceNotFoundException(String team, String product, String version) {
    super("Package not found: " + team + "/" + product + "/" + version);
  }
}
