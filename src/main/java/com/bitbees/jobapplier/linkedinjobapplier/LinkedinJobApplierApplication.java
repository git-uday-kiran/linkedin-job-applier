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
        String url = "https://www.linkedin.com/jobs/view/4318667853/?eBP=CwEAAAGbRiErgymWVmLBFRST_U972gPblOCEC5IhA7UIhVimxLYA7feH4fGapDrY6YMWX67BjxdiwAnkjYmU0JUBsecg9SLHejYWm4U63eU1DXw0sGHYVb6MQr2ktGm1bP_-9qjRNIysu-DqHmZizgceVOzyh6osWvAXIEHi3fbpgGNI0yJ2wLG2dc_WoEt7uC-wh3tIQ5ZiEVKFnKi20FirjrdGX40NjNpA2Is0wTzP1Wjh42hNmkopDNAZd3_GS7hXppdqYFkl5C7ZyjbbbU_UQdQC-YzoV76ducx9hy1Dpaj0i7rH40IZckW8nPX1z5C2rpj1759KTryvxvLG6AqnCdaXzVQ0THk3Zaw--t2QMHfxkX-s9SaRsfH7sAFUCE33wZ6_NOvQx2uBrFNVyZ0yrdE0BTRDTTcicIBXUXWNmCu5ScvBCWdq6mfBAunkOnjE__4YO5tQiHAfthLYfXvxbWL_YUpJsp_QJO-_poJLGXzvOnqu8AymeQ&refId=ecsRh9lmDR0JUyJIFIicYQ%3D%3D&trackingId=d3uPlbeAA4HCOiR9whp%2FIA%3D%3D&trk=flagship3_search_srp_jobs";
        ConfigurableApplicationContext context = event.getApplicationContext();
        context.publishEvent(new JobFoundEvent(url));
    }

}
