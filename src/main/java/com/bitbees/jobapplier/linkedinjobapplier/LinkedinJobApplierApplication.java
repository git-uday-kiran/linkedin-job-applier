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
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@EnableConfigurationProperties
@PropertySource("classpath:filter.properties")
public class LinkedinJobApplierApplication {

    static void main(String[] args) {
        SpringApplication.run(LinkedinJobApplierApplication.class, args);
    }

    @Bean
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

    //    @EventListener(ApplicationStartedEvent.class)
    void jobFoundEvent(ApplicationStartedEvent event) {
        String url = "https://www.linkedin.com/jobs/view/4344664720/?eBP=CwEAAAGbRQV400bZ5bCqvmlapjIChWlBQE7V5yMRrsce_JHnW5DTAT6khXb6QuhkWlYSjjLKGocTo2rrGK86K4kDoLzvoDLavnmxLVtSQmZ9L8uC3wDgzNk8eBb6DhBCk7wqMkj6uAR23vvtAy_fTmjdoBz09jHBEXvMcRYiVyBnZWb2XcGwEoigXbmAyZ_TZnIzi3qLKXH2PfCfmhGzJy9p0ukX8tu72KIYtgIJwAg5Y6cqnrNQvmeZnXF38anHWbBUGuD8f2SU8Z-jF2mx3orM4biFg-ZNSSGC_0Dpe32-TIH-X-1P5GcnOTkE5AImeSQm94qGusB0kZ_EjwHRhdYJ0G0YY9wYFQSZbGE1uO33GGyB4aYPqQn_URSs2e5mx2tl3mw971a3odwGszLo61EDOMlK4T9cVGHQNNaDJdHRiG2lGJFl5Cpaa1GlJJUT8yoB42J-Kp-hTVDO54tVhxk4M62YvS8uPw9rPqmryFa-&trk=flagship3_search_srp_jobs&refId=FU6OMYUCgEGyp7eJDBlCXw%3D%3D&trackingId=PRiVILmtTstsp7n%2BZUXOJQ%3D%3D";
        ConfigurableApplicationContext context = event.getApplicationContext();
        context.publishEvent(new JobFoundEvent(url));
    }

}
