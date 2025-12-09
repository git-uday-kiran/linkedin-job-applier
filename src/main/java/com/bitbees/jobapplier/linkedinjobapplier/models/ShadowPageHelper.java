package com.bitbees.jobapplier.linkedinjobapplier.models;

import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;

@Service
public class ShadowPageHelper {

    private final WebDriverWait wait;

    public ShadowPageHelper(WebDriverWait wait) {
        this.wait = wait;
    }

    public WebElement waitForPresenceAndClickable(SearchContext shadowRoot, By locator) {
        try {
            // Step 1: Wait for element to be present in Shadow DOM (may not be visible yet)
            waitForPresence(shadowRoot, locator);

            // Step 2: Wait for element to be visible
            waitForVisible(shadowRoot, locator);
            return waitForClickable(shadowRoot, locator);
        } catch (TimeoutException e) {
            throw new TimeoutException("Element not found or not clickable in the shadow dom: " + locator, e);
        }
    }

    public void waitForVisible(SearchContext shadowRoot, By locator) {
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

    public WebElement waitForClickable(SearchContext shadowRoot, By locator) {
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

    public WebElement waitForPresence(SearchContext shadowRoot, By locator) {
        return wait.until(driver -> {
            try {
                return shadowRoot.findElement(locator);
            } catch (Exception e) {
                return null;
            }
        });
    }
}
