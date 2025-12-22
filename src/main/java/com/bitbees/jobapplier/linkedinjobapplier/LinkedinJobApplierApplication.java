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
        String url = "https://www.linkedin.com/jobs/view/4344980193/?eBP=CwEAAAGbRodZpcozqErBengbA6mC1-Cy9uXyAvSaj64uu0UvE2xYT3K56wA3mm1hi7H_jt0rBi0HSPoElIuFU_cNLQuhcYKg8CLgQVt_Ei0rgUKTpCShLzEdip1IL01n-WfJvBDme6f1yvf18k5xP8x4V9iZ5uUlhBBvfNNn5SwFzC-p2WX9sVA22Tc-Y1xwmqnUo-hKV0a4P-WQGX2_GZiF0NrhsH-KCUVSkjt1zI_WOwYJ-bD5PNZPkEcny_sbHZCPuQKK_9QHWpD_EhF9jnXhr95dt7VG4ccYgW-qHXiSTB_-Fqba67THnK4HL6hnum5OiyAvQM7r1hISQTV0F4_E9txEgbNhrgGXFgk2nKL1uVMzn7qeoFrnBiF-5jfFibnJ5hWYqLmZF3KXcQB0C-VkbTRwjuuZKHuLf_VkPaYoSr4z771kbej9n8ScCNANR7m1U-xI2WVmWfUd1_B8L-u85nF-9x36A908mzN13F769dO0IAaDcuFflNTSzNy08A&trk=flagship3_search_srp_jobs&refId=%2BY4p7kPvIHrAVXultnE0eg%3D%3D&trackingId=AILc6cthSTjgNC92EPaD6Q%3D%3D";
        ConfigurableApplicationContext context = event.getApplicationContext();
        context.publishEvent(new JobFoundEvent(url));
    }

}
