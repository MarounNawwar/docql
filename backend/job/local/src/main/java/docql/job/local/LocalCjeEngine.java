package docql.job.local;

import docql.job.*;
import java.util.Map;
import java.util.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * In-process, synchronous {@link CjeEngine}. Jobs are dispatched to registered handlers by type.
 */
public class LocalCjeEngine implements CjeEngine {

  private static final Logger log = LoggerFactory.getLogger(LocalCjeEngine.class);

  private final Map<String, Function<CjeJob, CjeJobResult>> handlers;

  public LocalCjeEngine(Map<String, Function<CjeJob, CjeJobResult>> handlers) {
    this.handlers = handlers;
  }

  @Override
  public CjeJobResult submit(CjeJob job) {
    var handler = handlers.get(job.type());
    if (handler == null) {
      log.warn("No handler registered for job type '{}', skipping job [{}]", job.type(), job.id());
      return new CjeJobResult(job.id(), CjeJobStatus.SKIPPED, "No handler for type: " + job.type());
    }
    try {
      log.info("Executing job [{}] type='{}'", job.id(), job.type());
      return handler.apply(job);
    } catch (Exception e) {
      log.error("Job [{}] type='{}' failed: {}", job.id(), job.type(), e.getMessage(), e);
      return new CjeJobResult(job.id(), CjeJobStatus.FAILED, e.getMessage());
    }
  }
}
