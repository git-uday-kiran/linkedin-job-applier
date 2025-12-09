package com.bitbees.jobapplier.linkedinjobapplier;

import com.bitbees.jobapplier.linkedinjobapplier.models.JobsPage;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class LinkedinJobApplierApplication {

    static void main(String[] args) {
        SpringApplication.run(LinkedinJobApplierApplication.class, args);
    }

    @Bean
    CommandLineRunner commandLineRunner(JobsPage jobsPage) {
        return args -> {

            jobsPage.navigate();
            jobsPage.search("Java Developer");
            jobsPage.searchLocation("Bengaluru, India");
        };
    }

}
