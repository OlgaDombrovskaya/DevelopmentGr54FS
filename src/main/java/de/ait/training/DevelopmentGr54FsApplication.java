package de.ait.training;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;

@SpringBootApplication
@Slf4j
@RequestMapping("/api/cars")
public class DevelopmentGr54FsApplication {

    public static void main(String[] args) {
        log.info("Starting DevelopmentGr54FsApplication");
        SpringApplication.run(DevelopmentGr54FsApplication.class, args);
    }

}

