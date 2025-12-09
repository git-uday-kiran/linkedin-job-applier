package com.bitbees.jobapplier.linkedinjobapplier.models;

import com.bitbees.jobapplier.linkedinjobapplier.components.UnorderedList;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

@Log4j2
@Service
@RequiredArgsConstructor
public class JobsPage {

    private final WebDriver webDriver;
    private final WebDriverWait wait;

    private final By searchLocation = By.xpath("//header/div/div/div/div[2]/div/div/div/div/div/div/div/input[1]");
    private final By addressLocation = By.xpath("//div[contains(@class, 'jobs-search-box')]/div[2]//input[1]");
    private final By locationCompletePopupLocation = By.xpath("//div[contains(@class, 'jobs-search-box__typeahead-results--location')]/div/ul");

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

        System.out.println("(new UnorderedList(autoCompletionPopupElement)).getLiTexts() = " + (new UnorderedList(autoCompletionPopupElement)).getLiTexts());

        suggestedLocations.getFirst().click();
        new Actions(webDriver)
                .pause(Duration.ofSeconds(100))
                .perform();
    }


    private void waitForClickable(WebElement searchElement) {
        wait.until(ExpectedConditions.elementToBeClickable(searchElement));
    }

    private void pause(Duration duration) {
        new Actions(webDriver)
                .pause(duration)
                .perform();
    }

    private WebElement waitForPresenceAndClickable(By locator) {
        try {
            // Step 1: Wait for element to be present in DOM (may not be visible yet)
            wait.until(ExpectedConditions.presenceOfElementLocated(locator));

            // Step 2: Wait for element to be visible
            wait.until(ExpectedConditions.visibilityOfElementLocated(locator));

            // Step 3: Wait for element to be clickable
            return wait
                    .ignoring(StaleElementReferenceException.class)
                    .until(ExpectedConditions.elementToBeClickable(locator));
        } catch (TimeoutException e) {
            throw new TimeoutException("Element not found or not clickable: " + locator, e);
        }
    }

    private void waitForPageReady() {
        new WebDriverWait(webDriver, Duration.ofSeconds(30))
                .until(d -> Objects.equals(((JavascriptExecutor) d)
                        .executeScript("return document.readyState"), "complete"));
    }

    private Consumer<Throwable> logError(String message) {
        return throwable -> log.error(message, throwable);
    }

}
