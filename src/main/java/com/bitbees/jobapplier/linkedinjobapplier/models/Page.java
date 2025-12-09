package com.bitbees.jobapplier.linkedinjobapplier.models;

import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

import static io.vavr.API.run;

@Log4j2
public class Page {

    protected final WebDriver webDriver;
    protected final WebDriverWait wait;

    protected Page(WebDriver webDriver, WebDriverWait wait) {
        this.webDriver = webDriver;
        this.wait = wait;
    }

    protected Optional<WebElement> tryFindElement(By location) {
        List<WebElement> elements = webDriver.findElements(location);
        return elements.isEmpty() ? Optional.empty() : Optional.of(elements.getFirst());
    }

    protected void click(WebElement element) {
        String clicked = element.getText().replace('\n', ' ');
        if (clicked.isEmpty()) {
            clicked = element.getAccessibleName();
        }

        scrollIntoView(element);
        waitForClickable(element);
        highlight(element);

        element.click();
        log.info("Clicked: {}", clicked);
    }

    protected void findAndClick(By location) {
        WebElement element = webDriver.findElement(location);
        click(element);
    }

    protected void scrollIntoView(WebElement element) {
        ((JavascriptExecutor) webDriver).executeScript("arguments[0].scrollIntoView(true);", element);
        run(() -> waitForVisible(element));
    }

    protected void waitForVisible(WebElement element) {
        wait.until(ExpectedConditions.visibilityOf(element));
    }

    protected void highlight(WebElement... webElements) {
        String script = "arguments[0].setAttribute('style', 'border: 2px solid yellow')";
        JavascriptExecutor js = (JavascriptExecutor) webDriver;
        for (WebElement webElement : webElements) {
            js.executeScript(script, webElement);
        }
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

            wait.until(ExpectedConditions.elementToBeClickable(locator));

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
