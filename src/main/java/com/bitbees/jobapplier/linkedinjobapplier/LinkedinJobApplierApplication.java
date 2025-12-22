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
        String url = "https://www.linkedin.com/jobs/view/4325194567/?eBP=CwEAAAGbRJAvTdAk10pOEGVe9MGfWgC15MXN_xqIPjqRixGJq6Owz0QqRaoq_8Dk4DBWgDCvzVg_C7l3zsa1smYG7dG8qIwPjBulgI2LZQ6v99mYP05XN0vXbv1Un053Tv2luUF4sH9-EQV4LTI-cLcWeJNrwH8Zc6AG_YRtI6ZbmgcHhy6G7N2pL732EePDeRp-8dnNZ38wCpao4jlfi0dlW3VQkJ8Czf2_gmo6inwoE_RHm-Xhwd2eC7z9Y42IRB37LOFpwN2CK4HlqklNdIVDzgOXP6rmg4c6imZ5z3EvT9TYbvCXioBdeMkGey5dCLC9XtK9PezyE99SSuit2Z08OaQjPFXuRUImF6-WlIYZV_J1nsd_LSov_P1l0ahQtQ9pOf-p4FtrUPt8TDXujPIw39fQyueG37EqcQXBHi5DsdkUEV7Lg10IHFe4r3_5LLuwM0x3Nt8U8qGJIPGGq51PwmUQltjUgA16ufEIhU42i3sTWnU&trk=flagship3_jobs_discovery_jymbii&refId=YmW1bFm2JDNQEeoKoanAwQ%3D%3D&trackingId=pUYhpNQa1Gvbf35aOS6Peg%3D%3D";
        ConfigurableApplicationContext context = event.getApplicationContext();
        context.publishEvent(new JobFoundEvent(url));
    }

}
