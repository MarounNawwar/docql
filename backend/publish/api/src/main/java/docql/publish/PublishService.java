package docql.publish;

import docql.core.DocPackage;
import docql.core.PublishRequest;

/**
 * Service contract for publishing a documentation bundle.
 */
public interface PublishService {

    /** Validate, store and register a documentation bundle. */
    DocPackage publish(PublishRequest request);

    /** Retract (delete) a specific version of a package. */
    void retract(String team, String product, String version);
}
