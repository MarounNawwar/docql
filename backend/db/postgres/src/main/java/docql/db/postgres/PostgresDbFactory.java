package docql.db.postgres;

import docql.db.DbFactory;
import docql.db.DocFileRepository;
import docql.db.DocPackageRepository;

/**
 * Postgres-backed {@link DbFactory}.
 * Receives the Spring Data JPA repositories as constructor parameters so that
 * DocqlConfig remains the only class that knows about Spring beans.
 */
public class PostgresDbFactory implements DbFactory {

    private final DocPackageRepository packageRepository;
    private final DocFileRepository fileRepository;

    public PostgresDbFactory(DocPackageJpaRepository packageJpaRepo, DocFileJpaRepository fileJpaRepo) {
        this.packageRepository = new PostgresDocPackageRepository(packageJpaRepo);
        this.fileRepository    = new PostgresDocFileRepository(fileJpaRepo);
    }

    @Override
    public DocPackageRepository packageRepository() {
        return packageRepository;
    }

    @Override
    public DocFileRepository fileRepository() {
        return fileRepository;
    }
}
