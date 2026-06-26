package docql.storage;

import java.io.InputStream;
import java.util.List;

/**
 * Abstraction over a binary/blob storage backend. Implementations: local filesystem, S3, Azure
 * Blob, Nexus raw repository, etc.
 */
public interface StorageBackend {

  /** Store content under key: {team}/{product}/{version}/{relativePath} */
  void store(String key, InputStream content);

  InputStream retrieve(String key);

  List<String> list(String prefix);

  void deleteByPrefix(String prefix);

  boolean exists(String key);

  /** Whether this backend can generate provider-specific presigned upload instructions. */
  default boolean supportsPresignedUpload() {
    return false;
  }

  /**
   * Generate one presigned upload instruction for direct client upload.
   *
   * <p>Backends that do not support this capability should keep the default implementation.
   */
  default PresignedUpload createPresignedUpload(String key) {
    throw new UnsupportedOperationException("Presigned upload is not supported by this backend");
  }
}
