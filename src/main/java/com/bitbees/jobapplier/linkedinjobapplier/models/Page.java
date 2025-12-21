package com.bitbees.jobapplier.linkedinjobapplier.models;

import io.vavr.CheckedRunnable;
import io.vavr.control.Try;
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
        WebElement clickableElement = waitForClickable(element);
        highlight(clickableElement);
        clickableElement.click();
        log.info("Clicked: {}", clicked);
    }

    public void doUntilSuccess(CheckedRunnable action) {
        while (Try.run(action).isFailure()) ;
    }

    public void tryClick(WebElement element) {
        Try.run(() -> click(element));
    }

    protected void findAndClick(By location) {
        WebElement element = webDriver.findElement(location);
        click(element);
    }

    protected void scrollIntoView(WebElement element) {
        ((JavascriptExecutor) webDriver).executeScript("arguments[0].scrollIntoView(true);", element);
        Try.run(() -> waitForVisible(element));
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

    protected WebElement waitForClickable(WebElement searchElement) {
        return wait.until(ExpectedConditions.elementToBeClickable(searchElement));
    }

    protected WebElement findFirstClickableElement(By locator) {
        JavascriptExecutor js = (JavascriptExecutor) webDriver;
        return webDriver.findElements(locator).stream()
//                .peek(this::scrollIntoView)
                .filter(e -> e.isDisplayed() && e.isEnabled())
                .filter(e -> !isElementObscured(e, js))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Can not find first clickable element of location: " + locator));
    }

    protected boolean isElementObscured(WebElement element, JavascriptExecutor js) {
        String script = """
                const elem = arguments[0];
                const box = elem.getBoundingClientRect();
                const cx = box.left + box.width / 2;
                const cy = box.top + box.height / 2;
                const topElement = document.elementFromPoint(cx, cy);
                return topElement !== elem && !elem.contains(topElement);""";
        return Boolean.parseBoolean(String.valueOf(js.executeScript(script, element)));
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

    public List<WebElement> waitForPresenceAll(By location) {
        return wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(location));
    }

    public Optional<WebElement> waitForElementPresence(By location, int waitSeconds) {
        try {
            WebDriverWait customWait = new WebDriverWait(webDriver, Duration.ofSeconds(waitSeconds));
            WebElement element = customWait.until(ExpectedConditions.presenceOfElementLocated(location));
            return Optional.ofNullable(element);
        } catch (TimeoutException _) {
            return Optional.empty();
        }
    }

    public WebElement waitForElementPresence(By location) {
        return wait.until(ExpectedConditions.presenceOfElementLocated(location));
    }

    public WebElement waitForPresence(By location) {
        return wait.until(ExpectedConditions.presenceOfElementLocated(location));
    }

    protected void waitForPageReady() {
        new WebDriverWait(webDriver, Duration.ofSeconds(30))
                .until(d -> Objects.equals(((JavascriptExecutor) d)
                        .executeScript("return document.readyState"), "complete"));
    }

    protected void waitForPageToLoad() {
        wait.until(ExpectedConditions.invisibilityOfElementLocated(
                By.cssSelector(
                        "[aria-busy='true'], " +        // LinkedIn accessibility attribute
                                ".artdeco-loader, " +          // LinkedIn spinner class
                                "[class*='placeholder'], " +   // Skeleton loaders
                                "[class*='skeleton'], " +      // Other skeleton variants
                                "[class*='shimmer']"           // Animated shimmer loading
                )
        ));

    }

    public void moveElementVerticallyJS(WebElement element, int pixels) {
        ((JavascriptExecutor) webDriver).executeScript(
                "arguments[0].style.transform = " +
                        "`translateY(${(arguments[1])}px)`;",
                element,
                pixels
        );
    }

    protected Consumer<Throwable> logError(String message) {
        return throwable -> log.error(message, throwable);
    }
}
