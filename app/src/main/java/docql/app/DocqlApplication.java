package docql.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"docql"})
public class DocqlApplication {

    public static void main(String[] args) {
        SpringApplication.run(DocqlApplication.class, args);
    }
}

