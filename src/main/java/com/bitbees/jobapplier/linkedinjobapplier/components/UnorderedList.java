package com.bitbees.jobapplier.linkedinjobapplier.components;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;

public record UnorderedList(WebElement ulElement) {

    public List<WebElement> getLiElements() {
        return ulElement.findElements(By.tagName("li"));
    }

    public List<ListItem> getListItems() {
        return getLiElements().stream()
                .map(ListItem::new)
                .toList();
    }

    public List<String> getLiTexts() {
        return getLiElements().stream()
                .map(WebElement::getText)
                .toList();
    }

    public WebElement getLiByText(String text) {
        return ulElement.findElement(By.xpath(".//li[contains(text(), '" + text + "')]"));
    }
}
