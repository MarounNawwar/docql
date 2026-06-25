package docql.search.lucene;

import docql.search.SearchEngine;
import docql.search.SearchFactory;

/** Lucene-backed {@link SearchFactory}. */
public class LuceneSearchFactory implements SearchFactory {

  private final SearchEngine searchEngine = new LuceneSearchEngine();

  @Override
  public SearchEngine searchEngine() {
    return searchEngine;
  }
}
