package docql.scan.local;

import static org.junit.jupiter.api.Assertions.assertEquals;

import docql.scan.ScanStatus;
import org.junit.jupiter.api.Test;

class SignatureFileScanServiceTest {

  private final SignatureFileScanService scanner = new SignatureFileScanService();

  @Test
  void scan_eicarSignature_returnsMalicious() {
    String eicar = "X5O!P%@AP[4\\PZX54(P^)7CC)7}$EICAR-STANDARD-ANTIVIRUS-TEST-FILE!$H+H*";

    var result = scanner.scan("payload.txt", eicar);

    assertEquals(ScanStatus.MALICIOUS, result.status());
  }

  @Test
  void scan_markdown_returnsClean() {
    var result = scanner.scan("index.md", "# hello");

    assertEquals(ScanStatus.CLEAN, result.status());
  }
}
