package docql.scan.local;

import docql.scan.FileScanService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Spring wiring for the local scan backend. */
@Configuration
@ConditionalOnProperty(
    prefix = "docql.scan",
    name = "impl",
    havingValue = "local",
    matchIfMissing = true)
public class LocalScanConfiguration {

  @Bean
  public FileScanService fileScanService() {
    return new LocalScanFactory().fileScanService();
  }
}
