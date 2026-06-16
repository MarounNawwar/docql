package docql.persistence.postgres;

import jakarta.persistence.*;

/**
 * JPA entity for the {@code doc_files} table.
 * Maps to/from the {@link docql.core.DocFile} domain record via
 * {@link PostgresDocFileRepository}.
 * <p>
 * File content is stored as a {@code TEXT} column so that
 * DiscoveryService can serve Markdown in a single DB read.
 */
@Entity
@Table(name = "doc_files")
public class DocFileEntity {

    @EmbeddedId
    private DocFileId id;

    @Column(length = 1000)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    /** Required by JPA. */
    protected DocFileEntity() {}

    public DocFileEntity(String packageId, String path, String title, String content) {
        this.id      = new DocFileId(packageId, path);
        this.title   = title;
        this.content = content;
    }

    public String getPackageId() { return id.getPackageId(); }
    public String getPath()      { return id.getPath(); }
    public String getTitle()     { return title; }
    public String getContent()   { return content; }
}
