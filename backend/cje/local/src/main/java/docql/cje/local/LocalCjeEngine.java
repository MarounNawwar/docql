package docql.cje.local;

import docql.cje.*;

import java.util.Map;
import java.util.function.Function;
import java.util.logging.Logger;

/**
 * In-process, synchronous {@link CjeEngine}.
 * Jobs are dispatched to registered handlers by type.
 */
public class LocalCjeEngine implements CjeEngine {

    private static final Logger log = Logger.getLogger(LocalCjeEngine.class.getName());

    private final Map<String, Function<CjeJob, CjeJobResult>> handlers;

    public LocalCjeEngine(Map<String, Function<CjeJob, CjeJobResult>> handlers) {
        this.handlers = handlers;
    }

    @Override
    public CjeJobResult submit(CjeJob job) {
        var handler = handlers.get(job.type());
        if (handler == null) {
            log.warning("No handler for job type: " + job.type());
            return new CjeJobResult(job.id(), CjeJobStatus.SKIPPED, "No handler for type: " + job.type());
        }
        try {
            log.info("Executing job [" + job.id() + "] type=" + job.type());
            return handler.apply(job);
        } catch (Exception e) {
            log.severe("Job [" + job.id() + "] failed: " + e.getMessage());
            return new CjeJobResult(job.id(), CjeJobStatus.FAILED, e.getMessage());
        }
    }
}
