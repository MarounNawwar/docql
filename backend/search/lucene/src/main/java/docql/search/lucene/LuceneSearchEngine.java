package docql.search.lucene;

import docql.core.DocFile;
import docql.core.SearchResult;
import docql.search.SearchEngine;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.*;
import org.apache.lucene.store.ByteBuffersDirectory;
import org.apache.lucene.store.Directory;

/**
 * Embedded Lucene {@link SearchEngine}. Uses an in-memory {@link ByteBuffersDirectory} by default.
 * Wire a {@link org.apache.lucene.store.FSDirectory} for persistence.
 */
public class LuceneSearchEngine implements SearchEngine {

  private static final String FIELD_PACKAGE_ID = "packageId";
  private static final String FIELD_TEAM = "team";
  private static final String FIELD_PRODUCT = "product";
  private static final String FIELD_VERSION = "version";
  private static final String FIELD_PATH = "path";
  private static final String FIELD_TITLE = "title";
  private static final String FIELD_CONTENT = "content";

  private final Directory directory;
  private final StandardAnalyzer analyzer;

  public LuceneSearchEngine() {
    this.directory = new ByteBuffersDirectory();
    this.analyzer = new StandardAnalyzer();
  }

  @Override
  public void index(DocFile file, String team, String product, String version) {
    try (IndexWriter writer = new IndexWriter(directory, new IndexWriterConfig(analyzer))) {
      Document doc = new Document();
      doc.add(new StringField(FIELD_PACKAGE_ID, file.packageId(), Field.Store.YES));
      doc.add(new StringField(FIELD_TEAM, team, Field.Store.YES));
      doc.add(new StringField(FIELD_PRODUCT, product, Field.Store.YES));
      doc.add(new StringField(FIELD_VERSION, version, Field.Store.YES));
      doc.add(new StringField(FIELD_PATH, file.path(), Field.Store.YES));
      doc.add(
          new TextField(FIELD_TITLE, file.title() != null ? file.title() : "", Field.Store.YES));
      doc.add(
          new TextField(
              FIELD_CONTENT, file.content() != null ? file.content() : "", Field.Store.NO));
      writer.addDocument(doc);
    } catch (IOException e) {
      throw new UncheckedIOException("Failed to index file: " + file.path(), e);
    }
  }

  @Override
  public void deindex(String packageId) {
    try (IndexWriter writer = new IndexWriter(directory, new IndexWriterConfig(analyzer))) {
      writer.deleteDocuments(new Term(FIELD_PACKAGE_ID, packageId));
    } catch (IOException e) {
      throw new UncheckedIOException("Failed to deindex package: " + packageId, e);
    }
  }

  @Override
  public List<SearchResult> search(String query, List<String> tags, String team) {
    try (DirectoryReader reader = DirectoryReader.open(directory)) {
      IndexSearcher searcher = new IndexSearcher(reader);

      MultiFieldQueryParser parser =
          new MultiFieldQueryParser(new String[] {FIELD_TITLE, FIELD_CONTENT}, analyzer);
      Query luceneQuery = parser.parse(query);

      if (team != null && !team.isBlank()) {
        luceneQuery =
            new BooleanQuery.Builder()
                .add(luceneQuery, BooleanClause.Occur.MUST)
                .add(new TermQuery(new Term(FIELD_TEAM, team)), BooleanClause.Occur.FILTER)
                .build();
      }

      TopDocs topDocs = searcher.search(luceneQuery, 50);
      List<SearchResult> results = new ArrayList<>();
      for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
        Document doc = searcher.storedFields().document(scoreDoc.doc);
        results.add(
            new SearchResult(
                doc.get(FIELD_PACKAGE_ID),
                doc.get(FIELD_TEAM),
                doc.get(FIELD_PRODUCT),
                doc.get(FIELD_VERSION),
                doc.get(FIELD_PATH),
                doc.get(FIELD_TITLE),
                "" // snippet support can be added with Lucene Highlighter
                ));
      }
      return results;
    } catch (IOException e) {
      throw new UncheckedIOException("Search failed", e);
    } catch (ParseException e) {
      throw new IllegalArgumentException("Invalid search query: " + query, e);
    }
  }
}
