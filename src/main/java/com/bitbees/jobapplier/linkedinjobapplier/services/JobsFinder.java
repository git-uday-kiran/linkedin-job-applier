package com.bitbees.jobapplier.linkedinjobapplier.services;

import com.bitbees.jobapplier.linkedinjobapplier.events.JobFoundEvent;
import com.bitbees.jobapplier.linkedinjobapplier.models.Page;
import com.bitbees.jobapplier.linkedinjobapplier.pages.JobCard;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Log4j2
@Service
public class JobsFinder extends Page implements ApplicationContextAware {

    private static final By JOB_CARDS_LOCATION = By.xpath("//main[@id='main']/div/div[2]/div/div/ul/./li");

    private ApplicationContext ctx;

    protected JobsFinder(WebDriver webDriver, WebDriverWait wait) {
        super(webDriver, wait);
    }

    public void scanJobs() {
        log.info("Scanning jobs...");
        for (int page = 1; page <= 100; page++) {
            List<WebElement> jobs = getNewJobs();
            jobs.stream()
                    .map(element -> new JobCard(webDriver, wait, element))
                    .map(JobFoundEvent::new)
                    .forEach(ctx::publishEvent);
            if (!tryGotToPage(page + 1)) {
                log.info("All jobs scanning completed");
                break;
            }
        }
    }

    private List<WebElement> getNewJobs() {
        List<WebElement> jobs = waitForPresenceAll(JOB_CARDS_LOCATION);
        log.info("Found {} jobs", jobs.size());
        return jobs;
    }

    private boolean tryGotToPage(int page) {
        log.info("Going to next page: {}", page);
        By locator = By.xpath("//button[@aria-label='Page " + page + "']");
        Optional<WebElement> opNextPage = tryFindElement(locator);
        if (opNextPage.isPresent()) {
            WebElement nextPage = opNextPage.get();
            scrollIntoView(nextPage);
            click(nextPage);
            log.info("New page: {}", page);
            return true;
        } else {
            log.warn("Next page {} not found.", page);
            return false;
        }
    }

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        this.ctx = Objects.requireNonNull(applicationContext, "context must not be null");
    }
}
