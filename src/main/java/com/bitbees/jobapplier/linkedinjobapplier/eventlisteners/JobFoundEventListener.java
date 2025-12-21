package com.bitbees.jobapplier.linkedinjobapplier.eventlisteners;

import com.bitbees.jobapplier.linkedinjobapplier.configuration.Beans;
import com.bitbees.jobapplier.linkedinjobapplier.events.JobFoundEvent;
import com.bitbees.jobapplier.linkedinjobapplier.models.Page;
import com.bitbees.jobapplier.linkedinjobapplier.models.ShadowRootHelper;
import com.bitbees.jobapplier.linkedinjobapplier.pages.WidGet;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Log4j2
@Component
public class JobFoundEventListener extends Page implements ApplicationListener<JobFoundEvent> {

        private static final By EASY_APPLY = By.xpath("//span[text()='Easy Apply']/../..");
//    private static final By EASY_APPLY = By.xpath("//span[text()='Save']/../../../../following-sibling::div/div//span[text()='Easy Apply']/../..");
    private static final By EASY_APPLY_MODEL = By.cssSelector("div[data-test-modal-id='easy-apply-modal']");
    private static final By DISCARD_APPLICATION = By.xpath("");
    private static final By SHOW_ALL_LOCATION = By.cssSelector(".discovery-templates-vertical-list__footer > a");
    public static final By SHADOW_PARENT_LOCATION = By.xpath("//div[@id='interop-outlet']");

    protected JobFoundEventListener(WebDriver webDriver, WebDriverWait wait) {
        super(webDriver, wait);
    }

    @Override
    public void onApplicationEvent(@NonNull JobFoundEvent event) {
        if (!(event.getSource() instanceof String jobLink)) {
            throw new IllegalArgumentException("Job found event object is not a JobCard type");
        }
        log.info("Processing the job...");
        log.info("jobLink = {}", jobLink);

        String currentTab = webDriver.getWindowHandle();
        WebDriver newWindowDriver = webDriver.switchTo().newWindow(WindowType.TAB);
        newWindowDriver.get(jobLink);

        try {
            waitForPageReady();
            var easyApply = waitForElementPresence(By.xpath("//span[text()='Easy Apply']"), 5);
            if (easyApply.isPresent()) {
                if (!isJobApplicable()) {
                    log.warn("Job is not applicable");
                }
                var easyApplyElement = waitForPresenceAndClickable(EASY_APPLY);
//                scrollIntoView(easyApplyElement);
                applyEasyApplyJob(easyApplyElement);
            } else {
                log.warn("Easy Apply not found.");
            }
        } finally {
            webDriver.close();
            webDriver.switchTo().window(currentTab);
        }
    }

    private boolean isJobApplicable() {
        return true;
    }

    private void applyEasyApplyJob(WebElement easyApplyElement) {
        log.info("Applying job... ");
        click(easyApplyElement);

        WebElement shadowParent = webDriver.findElement(SHADOW_PARENT_LOCATION);
        SearchContext shadowRoot = shadowParent.getShadowRoot();
        ShadowRootHelper shadowRootHelper = new ShadowRootHelper(shadowRoot, wait, webDriver);
        WidGet widGet = Beans.widGet(shadowRootHelper);

        shadowRootHelper.waitForPresence(EASY_APPLY_MODEL);
        System.out.println("Widget scanning....");
        do {
            widGet.scan();
        } while (widGet.hasNextWidget(shadowRootHelper));
    }
}
