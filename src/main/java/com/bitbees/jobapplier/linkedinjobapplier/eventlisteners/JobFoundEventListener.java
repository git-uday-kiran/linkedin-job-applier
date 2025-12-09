package com.bitbees.jobapplier.linkedinjobapplier.eventlisteners;

import com.bitbees.jobapplier.linkedinjobapplier.events.JobFoundEvent;
import com.bitbees.jobapplier.linkedinjobapplier.models.Page;
import com.bitbees.jobapplier.linkedinjobapplier.pages.JobCard;
import com.bitbees.jobapplier.linkedinjobapplier.pages.WidGet;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Log4j2
@Component
public class JobFoundEventListener extends Page implements ApplicationListener<JobFoundEvent> {

    private static final By EASY_APPLY = By.xpath("//button[.//span[text()='Easy Apply']]");
    private static final By EASY_APPLY_MODEL = By.xpath("//div[@role='dialog']");
    private static final By SHOW_ALL_LOCATION = By.cssSelector(".discovery-templates-vertical-list__footer > a");

    private final WidGet widGet;

    protected JobFoundEventListener(WebDriver webDriver, WebDriverWait wait, WidGet widGet) {
        super(webDriver, wait);
        this.widGet = widGet;
    }

    @Override
    public void onApplicationEvent(@NonNull JobFoundEvent event) {
        if (!(event.getSource() instanceof JobCard jobCard)) {
            throw new IllegalArgumentException("Job found event object is not a JobCard type");
        }
        log.info("Processing the job card...");
        jobCard.init();
        jobCard.click();
        var easyApply = waitForElementPresence(EASY_APPLY, 2);
        if (easyApply.isPresent()) {
            applyEasyApplyJob(easyApply.get());
        } else {
            log.warn("Easy Apply not found.");
        }
    }

    private void applyEasyApplyJob(WebElement easyApplyElement) {
        log.info("Applying job... ");
        click(easyApplyElement);
        waitForVisible(waitForElementPresence(EASY_APPLY_MODEL));
        do {
            widGet.scan();
        } while (widGet.hasNextWidget());
    }
}
