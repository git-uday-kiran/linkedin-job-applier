package com.bitbees.jobapplier.linkedinjobapplier.models;

import io.vavr.control.Try;
import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.WebDriverWait;

@Log4j2
public class ShadowRootHelper {

    private final WebDriverWait wait;
    private final WebDriver webDriver;
    private final SearchContext shadowRoot;

    public ShadowRootHelper(SearchContext shadowRoot, WebDriverWait wait, WebDriver webDriver) {
        this.wait = wait;
        this.webDriver = webDriver;
        this.shadowRoot = shadowRoot;
    }

    public WebElement waitForPresenceAndClickable(By locator) {
        try {
            // Step 1: Wait for element to be present in Shadow DOM (may not be visible yet)
            waitForPresence(locator);

            // Step 2: Wait for element to be visible
            waitForVisible(locator);
            return waitForClickable(locator);
        } catch (TimeoutException e) {
            throw new TimeoutException("Element not found or not clickable in the shadow dom: " + locator, e);
        }
    }

    public void waitForVisible(By locator) {
        wait.until(_ -> {
            try {
                WebElement el = shadowRoot.findElement(locator);
                if (el.isDisplayed()) {
                    return el;
                }
            } catch (Exception _) {
            }
            return null;
        });
    }

    public WebElement waitForClickable(By locator) {
        return wait.until(_ -> {
            try {
                WebElement el = shadowRoot.findElement(locator);
                if (el.isDisplayed() && el.isEnabled()) {
                    return el;
                }
            } catch (Exception _) {
            }
            return null;
        });
    }

    public WebElement waitForPresence(By locator) {
        return wait.until(driver -> {
            try {
                return shadowRoot.findElement(locator);
            } catch (Exception e) {
                return null;
            }
        });
    }

    public void findAndClick(By location) {
        WebElement element = shadowRoot.findElement(location);
        click(element);
    }

    public void click(WebElement element) {
        String clicked = element.getText().replace('\n', ' ');
        if (clicked.isEmpty()) {
            clicked = element.getAccessibleName();
        }

        scrollIntoView(element);
        waitForElementClickable(element);
        highlight(element);
        element.click();
        log.info("Clicked: {}", clicked);
    }

    public void scrollIntoView(WebElement element) {
        ((JavascriptExecutor) webDriver).executeScript("arguments[0].scrollIntoView(true);", element);
        Try.run(() -> waitForElementVisible(element));
    }

    public void waitForElementVisible(WebElement element) {
        wait.until(_ -> {
            try {
                return element.isDisplayed() ? element : null;
            } catch (Exception _) {
                return null;
            }
        });
    }

    public void waitForElementClickable(WebElement element) {
        wait.until(_ -> {
            try {
                return element.isDisplayed() && element.isEnabled() ? element : null;
            } catch (Exception _) {
                return null;
            }
        });
    }

    public void highlight(WebElement... webElements) {
        String script = "arguments[0].setAttribute('style', 'border: 2px solid yellow')";
        JavascriptExecutor js = (JavascriptExecutor) webDriver;
        for (WebElement webElement : webElements) {
            js.executeScript(script, webElement);
        }
    }
}
