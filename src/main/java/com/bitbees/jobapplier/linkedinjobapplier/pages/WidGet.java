package com.bitbees.jobapplier.linkedinjobapplier.pages;

import com.bitbees.jobapplier.linkedinjobapplier.easyapply.question_solvers.InputQuestions;
import com.bitbees.jobapplier.linkedinjobapplier.models.Page;
import com.bitbees.jobapplier.linkedinjobapplier.models.ShadowRootHelper;
import io.vavr.control.Try;
import lombok.NonNull;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.time.Duration;
import java.util.Objects;


public class WidGet extends Page implements ApplicationContextAware {

    private static ApplicationContext ctx;

    private static final By NEXT = By.cssSelector("button[aria-label='Continue to next step']");
    private static final By REVIEW = By.cssSelector("button[aria-label='Review your application']");
    private static final By SUBMIT_APPLICATION = By.cssSelector("button[aria-label='Submit application']");
    private static final By CLOSE_WIDGET = By.cssSelector("button[aria-label='Dismiss']");
    private static final By CONTINUE_APPLYING = By.xpath("//span[text()='Continue applying']");
    private static final By CLOSE_BUTTON = By.xpath("//span[text()='Done']");

    private final InputQuestions inputQuestions;
    private final ShadowRootHelper shadowRootHelper;

    public WidGet(WebDriver webDriver,
                  WebDriverWait wait,
                  ShadowRootHelper shadowRootHelper,
                  InputQuestions inputQuestions) {
        super(webDriver, wait);
        this.shadowRootHelper = shadowRootHelper;
        this.inputQuestions = inputQuestions;
    }

    public void scan() {
        inputQuestions.scan(shadowRootHelper);
    }

    public boolean hasNextWidget(ShadowRootHelper shadowRootHelper) {
        Try<Void> tried = tryClick(shadowRootHelper, SUBMIT_APPLICATION)
                .orElse(tryClick(shadowRootHelper, REVIEW))
                .orElse(tryClick(shadowRootHelper, NEXT))
                .orElse(tryClick(shadowRootHelper, CONTINUE_APPLYING));

        if (tried.isSuccess()) {
            pause(Duration.ofSeconds(3));
            return true;
        }
        pause(Duration.ofSeconds(3));
        doUntilSuccess(() -> tryClickDismiss(shadowRootHelper));
        return false;
    }

    private Try<Void> tryClickDismiss(ShadowRootHelper shadowRootHelper) {
        return Try.run(() -> shadowRootHelper.click(shadowRootHelper.waitForPresenceAndClickable(CLOSE_WIDGET)));
    }

    private Try<Void> tryClickDone() {
        return Try.run(() -> click(waitForPresenceAndClickable(CLOSE_BUTTON)));
    }

    public Try<Void> tryClick(ShadowRootHelper shadowRootHelper, By locator) {
        return Try.run(() -> shadowRootHelper.findAndClick(locator));
    }

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        Objects.requireNonNull(applicationContext);
        ctx = applicationContext;
    }

}
