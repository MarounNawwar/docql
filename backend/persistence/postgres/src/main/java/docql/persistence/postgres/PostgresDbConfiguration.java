package docql.persistence.postgres;

import docql.persistence.DocFileRepository;
import docql.persistence.DocPackageRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Spring wiring for the Postgres DB backend.
 */
@Configuration
@ConditionalOnProperty(prefix = "docql.persistence", name = "impl", havingValue = "postgres", matchIfMissing = true)
@EnableJpaRepositories(basePackages = "docql.persistence.postgres")
@EntityScan(basePackages = "docql.persistence.postgres")
public class PostgresDbConfiguration {

    @Bean
    public PostgresDbFactory postgresDbFactory(
            DocPackageJpaRepository packageJpaRepo,
            DocFileJpaRepository fileJpaRepo
    ) {
        return new PostgresDbFactory(packageJpaRepo, fileJpaRepo);
    }

    @Bean
    public DocPackageRepository docPackageRepository(PostgresDbFactory factory) {
        return factory.packageRepository();
    }

    @Bean
    public DocFileRepository docFileRepository(PostgresDbFactory factory) {
        return factory.fileRepository();
    }
}
