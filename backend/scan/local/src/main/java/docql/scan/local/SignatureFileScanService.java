package docql.scan.local;

import static docql.scan.ScanResult.*;

import docql.scan.FileScanService;
import docql.scan.ScanResult;

/**
 * Minimal local scanner. Detects a small set of known signatures and is intended as a replaceable
 * default for development.
 */
public class SignatureFileScanService implements FileScanService {

  private static final String SCANNER_NAME = "local-signature";
  private static final String EICAR_SIGNATURE =
      "X5O!P%@AP[4\\PZX54(P^)7CC)7}$EICAR-STANDARD-ANTIVIRUS-TEST-FILE!$H+H*";

  @Override
  public ScanResult scan(String path, String content) {
    if (content == null) {
      return error(SCANNER_NAME, "content is null for path " + path);
    }
    if (content.contains(EICAR_SIGNATURE)) {
      return malicious(SCANNER_NAME, "EICAR signature detected in " + path);
    }
    return clean(SCANNER_NAME);
  }
}
