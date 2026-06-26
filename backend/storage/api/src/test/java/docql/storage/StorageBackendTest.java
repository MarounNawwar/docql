package docql.storage;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.InputStream;
import java.util.List;
import org.junit.jupiter.api.Test;

class StorageBackendTest {

  @Test
  void defaultPresignedUploadSupport_isDisabled() {
    StorageBackend backend = new NoopStorageBackend();

    assertFalse(backend.supportsPresignedUpload());
    assertThrows(UnsupportedOperationException.class, () -> backend.createPresignedUpload("k1"));
  }

  private static final class NoopStorageBackend implements StorageBackend {
    @Override
    public void store(String key, InputStream content) {}

    @Override
    public InputStream retrieve(String key) {
      throw new UnsupportedOperationException();
    }

    @Override
    public List<String> list(String prefix) {
      return List.of();
    }

    @Override
    public void deleteByPrefix(String prefix) {}

    @Override
    public boolean exists(String key) {
      return false;
    }
  }
}
