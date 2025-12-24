package com.bitbees.jobapplier.linkedinjobapplier;

import com.bitbees.jobapplier.linkedinjobapplier.easyapply.configuration.EasyApplyFilter;
import com.bitbees.jobapplier.linkedinjobapplier.events.JobFoundEvent;
import com.bitbees.jobapplier.linkedinjobapplier.models.JobsPage;
import com.bitbees.jobapplier.linkedinjobapplier.services.JobsFinder;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.event.EventListener;

@SpringBootApplication
@EnableConfigurationProperties
@PropertySource("classpath:filter.properties")
public class LinkedinJobApplierApplication {

    static void main(String[] args) {
        SpringApplication.run(LinkedinJobApplierApplication.class, args);
    }

    //    @Bean
    CommandLineRunner commandLineRunner(JobsPage jobsPage,
                                        JobsFinder jobsFinder,
                                        EasyApplyFilter searchFilter) {
        return args -> {
            jobsPage.navigate();
            jobsPage.search(searchFilter.getSearchQuery());
            jobsPage.searchLocation(searchFilter.getSearchLocation());
            jobsPage.applyEasyApplyFilters(searchFilter);
            jobsFinder.scanJobs();
        };
    }

    @EventListener(ApplicationStartedEvent.class)
    void jobFoundEvent(ApplicationStartedEvent event) {
        String url = "https://www.linkedin.com/jobs/view/4329006483/?eBP=NON_CHARGEABLE_CHANNEL&refId=hsV0JyV6Z5UHsrwukOuIfg%3D%3D&trackingId=ZO%2BYNyZwZ3kEH4E6Jq564Q%3D%3D&trk=flagship3_search_srp_jobs&lipi=urn%3Ali%3Apage%3Ad_flagship3_search_srp_jobs%3B%2B3HExv%2B7Tx21qQq8eZJd9Q%3D%3D&lici=ZO%2BYNyZwZ3kEH4E6Jq564Q%3D%3D";
        ConfigurableApplicationContext context = event.getApplicationContext();
        context.publishEvent(new JobFoundEvent(url));
    }

}
