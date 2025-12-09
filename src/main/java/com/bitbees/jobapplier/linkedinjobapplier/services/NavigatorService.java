package com.bitbees.jobapplier.linkedinjobapplier.services;

import com.bitbees.jobapplier.linkedinjobapplier.models.Page;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WindowType;
import org.openqa.selenium.interactions.Actions;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class NavigatorService {

    private final WebDriver driver;

    public void goTo(Page page, Page.Config pageConfig) {
        if (pageConfig.onNewTab()) {
            openNewTab();
        }
        driver.get(page.url());
    }

    private void openNewTab() {
        driver.switchTo().newWindow(WindowType.TAB);
        new Actions(driver)
                .pause(Duration.ofSeconds(1))
                .build()
                .perform();
    }
}