package com.wms.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class WasteManagementBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(WasteManagementBackendApplication.class, args);
    }

}
