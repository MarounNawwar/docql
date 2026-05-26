package docql.cje.local;

import docql.cje.*;

import java.util.Map;

/**
 * Local {@link CjeFactory} — wires the in-process engine with default handlers.
 */
public class LocalCjeFactory implements CjeFactory {

    private final CjeEngine engine;

    public LocalCjeFactory() {
        this.engine = new LocalCjeEngine(Map.of(
                "VALIDATE_PACKAGE", job -> new CjeJobResult(job.id(), CjeJobStatus.SUCCESS, "Validation passed"),
                "INDEX_PACKAGE",    job -> new CjeJobResult(job.id(), CjeJobStatus.SUCCESS, "Indexed successfully")
        ));
    }

    @Override
    public CjeEngine cjeEngine() {
        return engine;
    }
}

