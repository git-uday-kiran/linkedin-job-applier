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
        String url = "https://www.linkedin.com/jobs/view/4344298978/?eBP=CwEAAAGbROk0B5WUmBHsAWZRnd-qyXVKqKWyzMmPc31UCh5Cxp0kt4Rs6M-6z4aFqqyoUgZb-wZN5A6PasYN1PaketLdcaVxG3HjW-u2phO-jcHQ5dUOHfMHNT9Ngqsog58Bb-W1Wm_83xscfDApVgsNVJ9LPvwreMYXUIbFmX62yys6tcPJSQb7cRaSuEDRXPqMOemjAXhejN1w65xgw4amT9fQ7BXuPRFOAXwVTL5Y8YUnsiFmXd665_FWU7Y8d63ULOG49TkFsk95_ow2PjgTZ7meCVQrSbkIIJm4sB8UXDJXFhgLwSOcW1njT4GofXLLukDdgTHa6FmgFK6K4bzwviKAef3CTczyKuP_slVTK7_x7LJBFTQjdPd15_afgbbu7qXlY9N9aHvKURI9L7ADpYDedfsA9Vp1o5HK9IPyFFnyH-iZ1TbJFhSRiOO38mVO-u5uAYQ5iPJ4fEU4e4UujPVWZXuN-NS3ZFWCopmAIDyZ2TWi2neUAf-JH5BkuLMe6orKBipbdMf0FJdY&trk=flagship3_search_srp_jobs&refId=WGVy1JIz4sdX%2Bw0QmACmDg%3D%3D&trackingId=MpsxiwedqmnWvQvUCvB2cw%3D%3D";
        ConfigurableApplicationContext context = event.getApplicationContext();
        context.publishEvent(new JobFoundEvent(url));
    }

}
