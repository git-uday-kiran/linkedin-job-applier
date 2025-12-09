package com.bitbees.jobapplier.linkedinjobapplier.models;

import com.bitbees.jobapplier.linkedinjobapplier.components.UnorderedList;
import io.vavr.control.Try;
import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

@Log4j2
@Service
public class JobsPage extends Page {

    private final By searchLocation = By.xpath("//header/div/div/div/div[2]/div/div/div/div/div/div/div/input[1]");
    private final By addressLocation = By.xpath("//div[contains(@class, 'jobs-search-box')]/div[2]//input[1]");
    private final By locationCompletePopupLocation = By.xpath("//div[contains(@class, 'jobs-search-box__typeahead-results--location')]/div/ul");

    public JobsPage(WebDriver webDriver, WebDriverWait wait) {
        super(webDriver, wait);
    }

    public void navigate() {
        log.info("Navigating to jobs page in another tab");
        webDriver.switchTo().newWindow(WindowType.TAB);
        String jobsPageUrl = "https://www.linkedin.com/jobs/";
        webDriver.get(jobsPageUrl);
        waitForPageReady();
    }

    public void search(String query) {
        WebElement searchElement = waitForPresenceAndClickable(searchLocation);
        waitForClickable(searchElement);
        pause(Duration.ofSeconds(1));
        searchElement.clear();
        searchElement.sendKeys(query, Keys.ENTER);
        pause(Duration.ofSeconds(1));
    }

    public void searchLocation(String location) {
        // Click location search input box
        WebElement addressLocationElement = waitForPresenceAndClickable(addressLocation);
        pause(Duration.ofSeconds(2));
        addressLocationElement.clear();
        addressLocationElement.sendKeys(location);

        new Actions(webDriver)
                .click(addressLocationElement)
                .pause(Duration.ofSeconds(1))
                .perform();

        // Get autocompletion popup
        Try.of(() -> waitForPresenceAndClickable(locationCompletePopupLocation))
                .andThen(this::resolveLocationsAutoCompletion)
                .orElseRun(logError("Location auto completion fillup failed"));
    }

    private void resolveLocationsAutoCompletion(WebElement autoCompletionPopupElement) {
        // All autocompletion location
        List<WebElement> suggestedLocations = (new UnorderedList(autoCompletionPopupElement)).getLiElements();
        suggestedLocations.getFirst().click();
        pause(Duration.ofSeconds(2));
    }

}
