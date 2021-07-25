package com.cgi.silver.bar.marketplace;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableJpaRepositories
@EnableTransactionManagement
@Slf4j
public class LiveOrderBoardApplication {

    public static void main(String[] args) {
        SpringApplication.run(LiveOrderBoardApplication.class, args);
    }

}
