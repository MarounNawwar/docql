package docql.scan;

/** Factory that produces a {@link FileScanService}. */
public interface ScanFactory {
  FileScanService fileScanService();
}
