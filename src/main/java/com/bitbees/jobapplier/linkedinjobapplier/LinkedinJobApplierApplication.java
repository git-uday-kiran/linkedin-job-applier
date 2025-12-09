package com.bitbees.jobapplier.linkedinjobapplier;

import com.bitbees.jobapplier.linkedinjobapplier.locations.LinkedinPages;
import com.bitbees.jobapplier.linkedinjobapplier.models.Page;
import com.bitbees.jobapplier.linkedinjobapplier.services.NavigatorService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;

@SpringBootApplication
public class LinkedinJobApplierApplication {

    static void main(String[] args) {
        SpringApplication.run(LinkedinJobApplierApplication.class, args);
    }

    @EventListener(ApplicationStartedEvent.class)
    public void onApplicationEvent(ApplicationStartedEvent event) {
        var ctx = event.getApplicationContext();
        var navigatorService = ctx.getBean(NavigatorService.class);
        navigatorService.goTo(LinkedinPages.JOBS_PAGE, new Page.Config(true));
    }


    private LinkedinJobApplierApplication() {
    }
}
