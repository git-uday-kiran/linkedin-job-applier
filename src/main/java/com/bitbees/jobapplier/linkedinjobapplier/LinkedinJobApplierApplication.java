package com.bitbees.jobapplier.linkedinjobapplier;

import com.bitbees.jobapplier.linkedinjobapplier.easyapply.filters.EasyApplyFilter;
import com.bitbees.jobapplier.linkedinjobapplier.models.JobsPage;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;

import java.util.concurrent.TimeUnit;

@SpringBootApplication
@EnableConfigurationProperties
@PropertySource("classpath:filter.properties")
public class LinkedinJobApplierApplication {

    static void main(String[] args) {
        SpringApplication.run(LinkedinJobApplierApplication.class, args);
    }

    @Bean
    CommandLineRunner commandLineRunner(JobsPage jobsPage, EasyApplyFilter searchFilter) {
        return args -> {
            jobsPage.navigate();
            jobsPage.search("Java Developer");
            jobsPage.searchLocation("Bengaluru, India");
            jobsPage.applyEasyApplyFilters(searchFilter);

            TimeUnit.SECONDS.sleep(1000);
        };
    }

}
