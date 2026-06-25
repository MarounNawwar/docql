package docql.persistence.postgres;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * JPA entity for the {@code doc_packages} table. Maps to/from the {@link docql.core.DocPackage}
 * domain record via {@link PostgresDocPackageRepository}.
 */
@Entity
@Table(name = "doc_packages")
public class DocPackageEntity {

  @Id
  @Column(nullable = false)
  private String id;

  @Column(nullable = false)
  private String team;

  @Column(nullable = false)
  private String product;

  @Column(nullable = false)
  private String version;

  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "doc_package_tags", joinColumns = @JoinColumn(name = "package_id"))
  @Column(name = "tag", nullable = false)
  private List<String> tags = new ArrayList<>();

  @Column(name = "published_at", nullable = false)
  private Instant publishedAt;

  @Column(name = "index_file_path", nullable = false)
  private String indexFilePath;

  /** Required by JPA. */
  protected DocPackageEntity() {}

  public DocPackageEntity(
      String id,
      String team,
      String product,
      String version,
      List<String> tags,
      Instant publishedAt,
      String indexFilePath) {
    this.id = id;
    this.team = team;
    this.product = product;
    this.version = version;
    this.tags = new ArrayList<>(tags);
    this.publishedAt = publishedAt;
    this.indexFilePath = indexFilePath;
  }

  public String getId() {
    return id;
  }

  public String getTeam() {
    return team;
  }

  public String getProduct() {
    return product;
  }

  public String getVersion() {
    return version;
  }

  public List<String> getTags() {
    return tags;
  }

  public Instant getPublishedAt() {
    return publishedAt;
  }

  public String getIndexFilePath() {
    return indexFilePath;
  }
}
