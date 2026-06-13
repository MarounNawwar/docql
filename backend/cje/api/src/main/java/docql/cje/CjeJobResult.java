package docql.cje;

public record CjeJobResult(
        String jobId,
        CjeJobStatus status,
        String message
) {}
