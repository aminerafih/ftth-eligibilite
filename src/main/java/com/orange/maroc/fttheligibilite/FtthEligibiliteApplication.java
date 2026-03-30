package com.orange.maroc.fttheligibilite;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.orange.maroc.fttheligibilite.repository")
@ComponentScan(basePackages = "com.orange.maroc.fttheligibilite")
public class FtthEligibiliteApplication {

    public static void main(String[] args) {
        SpringApplication.run(FtthEligibiliteApplication.class, args);
    }
}