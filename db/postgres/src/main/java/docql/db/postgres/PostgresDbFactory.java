package docql.db.postgres;

import docql.db.DbFactory;
import docql.db.DocFileRepository;
import docql.db.DocPackageRepository;

/**
 * Postgres-backed {@link DbFactory}.
 */
public class PostgresDbFactory implements DbFactory {

    private final DocPackageRepository packageRepository = new PostgresDocPackageRepository();
    private final DocFileRepository fileRepository = new PostgresDocFileRepository();

    @Override
    public DocPackageRepository packageRepository() {
        return packageRepository;
    }

    @Override
    public DocFileRepository fileRepository() {
        return fileRepository;
    }
}
