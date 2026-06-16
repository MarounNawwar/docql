package docql.job;

public record CjeJobResult(
        String jobId,
        CjeJobStatus status,
        String message
) {}
