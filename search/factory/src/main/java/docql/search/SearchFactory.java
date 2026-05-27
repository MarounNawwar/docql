package docql.search;

/**
 * Factory that produces a {@link SearchEngine}.
 * Swap with ElasticsearchSearchFactory or OpenSearchSearchFactory without touching any other module.
 */
public interface SearchFactory {
    SearchEngine searchEngine();
}
