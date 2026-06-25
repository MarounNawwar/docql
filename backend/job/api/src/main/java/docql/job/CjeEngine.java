package docql.job;

/**
 * Abstraction over a Content/Job Engine. Implementations: local in-process, Jenkins, GitHub Actions
 * facade, etc.
 */
public interface CjeEngine {
  CjeJobResult submit(CjeJob job);
}
