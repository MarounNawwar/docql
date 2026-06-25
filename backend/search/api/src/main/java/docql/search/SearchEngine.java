package docql.search;

import docql.core.DocFile;
import docql.core.SearchResult;
import java.util.List;

/**
 * Abstraction over a full-text search engine. Implementations: Lucene (embedded), Elasticsearch,
 * OpenSearch, etc.
 */
public interface SearchEngine {

  /** Index a single doc file so it is discoverable via query. */
  void index(DocFile file, String team, String product, String version);

  /** Remove all indexed entries for a given package id. */
  void deindex(String packageId);

  /**
   * Search across all indexed content.
   *
   * @param query free-text query
   * @param tags optional tag filter (empty = no filter)
   * @param team optional team filter (null = no filter)
   * @return ranked list of matching results
   */
  List<SearchResult> search(String query, List<String> tags, String team);
}
