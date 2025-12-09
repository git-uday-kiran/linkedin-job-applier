package com.bitbees.jobapplier.linkedinjobapplier.eventlisteners;

import com.bitbees.jobapplier.linkedinjobapplier.events.JobFoundEvent;
import com.bitbees.jobapplier.linkedinjobapplier.models.Page;
import com.bitbees.jobapplier.linkedinjobapplier.pages.JobCard;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Log4j2
@Component
public class JobFoundEventListener extends Page implements ApplicationListener<JobFoundEvent> {

    private static final By EASY_APPLY = By.xpath("//button[.//span[text()='Easy Apply']]");
    private static final By EASY_APPLY_MODEL = By.xpath("//div[@role='dialog']");
    private static final By SHOW_ALL_LOCATION = By.cssSelector(".discovery-templates-vertical-list__footer > a");

    protected JobFoundEventListener(WebDriver webDriver, WebDriverWait wait) {
        super(webDriver, wait);
    }

    @Override
    public void onApplicationEvent(@NonNull JobFoundEvent event) {
        if (!(event.getSource() instanceof JobCard jobCard)) {
            throw new IllegalArgumentException("Job found event object is not a JobCard type");
        }
        log.info("Job found");
        jobCard.init();
        jobCard.click();
        System.out.println("jobCard = " + jobCard);
    }
}
