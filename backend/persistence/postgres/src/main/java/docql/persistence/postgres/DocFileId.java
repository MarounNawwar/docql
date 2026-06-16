package docql.persistence.postgres;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

/**
 * Composite primary key for {@link DocFileEntity}: (package_id, path).
 */
@Embeddable
public class DocFileId implements Serializable {

    @Column(name = "package_id", nullable = false)
    private String packageId;

    @Column(nullable = false, length = 1000)
    private String path;

    /** Required by JPA. */
    protected DocFileId() {}

    public DocFileId(String packageId, String path) {
        this.packageId = packageId;
        this.path      = path;
    }

    public String getPackageId() { return packageId; }
    public String getPath()      { return path; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DocFileId other)) return false;
        return Objects.equals(packageId, other.packageId) && Objects.equals(path, other.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(packageId, path);
    }
}
