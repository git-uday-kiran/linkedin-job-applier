package com.bitbees.jobapplier.linkedinjobapplier.models;

import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.Objects;
import java.util.function.Consumer;

@Log4j2
public class Page {

    protected final WebDriver webDriver;
    protected final WebDriverWait wait;

    public Page(WebDriver webDriver, WebDriverWait wait) {
        this.webDriver = webDriver;
        this.wait = wait;
    }

    protected void waitForClickable(WebElement searchElement) {
        wait.until(ExpectedConditions.elementToBeClickable(searchElement));
    }

    protected void pause(Duration duration) {
        new Actions(webDriver)
                .pause(duration)
                .perform();
    }

    protected WebElement waitForPresenceAndClickable(By locator) {
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

    protected void waitForPageReady() {
        new WebDriverWait(webDriver, Duration.ofSeconds(30))
                .until(d -> Objects.equals(((JavascriptExecutor) d)
                        .executeScript("return document.readyState"), "complete"));
    }

    protected Consumer<Throwable> logError(String message) {
        return throwable -> log.error(message, throwable);
    }

}
