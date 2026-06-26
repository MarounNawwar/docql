package docql.scan;

/** Result of scanning one uploaded file. */
public record ScanResult(ScanStatus status, String scanner, String message) {

  public static ScanResult clean(String scanner) {
    return new ScanResult(ScanStatus.CLEAN, scanner, "clean");
  }

  public static ScanResult malicious(String scanner, String message) {
    return new ScanResult(ScanStatus.MALICIOUS, scanner, message);
  }

  public static ScanResult error(String scanner, String message) {
    return new ScanResult(ScanStatus.ERROR, scanner, message);
  }
}
