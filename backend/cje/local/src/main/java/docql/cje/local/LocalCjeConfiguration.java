package docql.cje.local;

import docql.cje.CjeEngine;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring wiring for the local CJE backend.
 */
@Configuration
@ConditionalOnProperty(prefix = "docql.cje", name = "impl", havingValue = "local", matchIfMissing = true)
public class LocalCjeConfiguration {

    @Bean
    public CjeEngine cjeEngine() {
        return new LocalCjeFactory().cjeEngine();
    }
}
