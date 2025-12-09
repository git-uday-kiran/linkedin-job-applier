package com.bitbees.jobapplier.linkedinjobapplier.components;

import org.openqa.selenium.WebElement;

public record ListItem(WebElement element) {

    public String getText() {
        return element.getText();
    }

    public void click() {
        element.click();
    }

    public String getAttribute(String attributeName) {
        return element.getAttribute(attributeName);
    }

    public boolean isDisplayed() {
        return element.isDisplayed();
    }

    public boolean isEnabled() {
        return element.isEnabled();
    }

    public boolean isSelected() {
        return element.isSelected();
    }

    public String getCssValue(String propertyName) {
        return element.getCssValue(propertyName);
    }

    public boolean containsText(String text) {
        return getText().contains(text);
    }
}
