package docql.storage.fs;

import static java.nio.file.Files.*;

import docql.storage.StorageBackend;
import java.io.*;
import java.nio.file.*;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

/** Local filesystem implementation of {@link StorageBackend}. */
public class FilesystemStorageBackend implements StorageBackend {

  private final Path rootDir;

  public FilesystemStorageBackend(Path rootDir) {
    this.rootDir = rootDir;
    initializeIfNeeded(rootDir);
  }

  @Override
  public void store(String key, InputStream content) {
    Path target = rootDir.resolve(key);
    try {
      createDirectories(target.getParent());
      copy(content, target, StandardCopyOption.REPLACE_EXISTING);
    } catch (IOException e) {
      throw new UncheckedIOException("Failed to store: " + key, e);
    }
  }

  @Override
  public InputStream retrieve(String key) {
    try {
      return newInputStream(rootDir.resolve(key));
    } catch (IOException e) {
      throw new UncheckedIOException("Failed to retrieve: " + key, e);
    }
  }

  @Override
  public List<String> list(String prefix) {
    Path base = rootDir.resolve(prefix);
    if (!Files.exists(base)) {
      return List.of();
    }
    try (Stream<Path> paths = walk(base)) {
      return paths
          .filter(Files::isRegularFile)
          .map(p -> rootDir.relativize(p).toString().replace(File.separatorChar, '/'))
          .toList();
    } catch (IOException e) {
      throw new UncheckedIOException("Failed to list prefix: " + prefix, e);
    }
  }

  @Override
  public void deleteByPrefix(String prefix) {
    Path base = rootDir.resolve(prefix);
    if (!Files.exists(base)) {
      return;
    }
    try (Stream<Path> paths = walk(base)) {
      paths.sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
    } catch (IOException e) {
      throw new UncheckedIOException("Failed to delete prefix: " + prefix, e);
    }
  }

  @Override
  public boolean exists(String key) {
    return Files.exists(rootDir.resolve(key));
  }

  private static void initializeIfNeeded(Path rootDir) {
    try {
      createDirectories(rootDir);
    } catch (IOException e) {
      throw new UncheckedIOException("Cannot create storage root: " + rootDir, e);
    }
  }
}
