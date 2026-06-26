package docql.scan.local;

import docql.scan.FileScanService;
import docql.scan.ScanFactory;

/** Local {@link ScanFactory}. */
public class LocalScanFactory implements ScanFactory {

  private final FileScanService fileScanService;

  public LocalScanFactory() {
    this.fileScanService = new SignatureFileScanService();
  }

  @Override
  public FileScanService fileScanService() {
    return fileScanService;
  }
}
