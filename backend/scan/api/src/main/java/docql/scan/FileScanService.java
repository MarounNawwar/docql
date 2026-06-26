package docql.scan;

/** Abstraction for malware/safety scanning of uploaded file content. */
public interface FileScanService {

  ScanResult scan(String path, String content);
}
