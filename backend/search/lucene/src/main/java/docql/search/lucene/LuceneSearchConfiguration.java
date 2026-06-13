package docql.search.lucene;

import docql.search.SearchEngine;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring wiring for the Lucene search backend.
 */
@Configuration
@ConditionalOnProperty(prefix = "docql.search", name = "impl", havingValue = "lucene", matchIfMissing = true)
public class LuceneSearchConfiguration {

    @Bean
    public SearchEngine searchEngine() {
        return new LuceneSearchFactory().searchEngine();
    }
}
