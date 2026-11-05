package com.immogest.licenseserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@org.springframework.boot.context.properties.EnableConfigurationProperties(Proprietes.class)
public class LicenseServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(LicenseServerApplication.class, args);
    }
}
