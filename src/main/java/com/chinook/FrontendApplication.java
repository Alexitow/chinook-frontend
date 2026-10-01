package com.chinook;

import com.chinook.repository.LocalRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableAsync
@EnableScheduling
public class FrontendApplication {
    public static void main(String[] args) {
        SpringApplication.run(FrontendApplication.class, args);
    }

    // Crea las tablas locales (invoice_line, invoice_temp) en chinook_local.db si no existen.
    @Bean
    CommandLineRunner initLocalDb(LocalRepository localRepository) {
        return args -> localRepository.initSchema();
    }
}
