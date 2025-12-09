package com.bitbees.jobapplier.linkedinjobapplier.pages;

import com.bitbees.jobapplier.linkedinjobapplier.easyapply.question_solvers.CheckBoxQuestions;
import com.bitbees.jobapplier.linkedinjobapplier.easyapply.question_solvers.InputQuestions;
import com.bitbees.jobapplier.linkedinjobapplier.easyapply.question_solvers.RadioOptionsQuestions;
import com.bitbees.jobapplier.linkedinjobapplier.easyapply.question_solvers.SelectOptionsQuestions;
import com.bitbees.jobapplier.linkedinjobapplier.models.Page;
import io.vavr.control.Try;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Component;

import java.time.Duration;


@Component
public class WidGet extends Page {

    private static final By NEXT = By.xpath("//span[text()='Next']");
    private static final By REVIEW = By.xpath("//span[text()='Review']");
    private static final By SUBMIT_APPLICATION = By.xpath("//span[text()='Submit application']");
    private static final By CLOSE_WIDGET = By.xpath("//button[@data-test-modal-close-btn]");
    private static final By CONTINUE_APPLYING = By.xpath("//span[text()='Continue applying']");
    private static final By CLOSE_BUTTON = By.xpath("//span[text()='Done']");

    private static final Logger log = org.apache.logging.log4j.LogManager.getLogger(WidGet.class);
    private final RadioOptionsQuestions radioOptionsQuestions;
    private final SelectOptionsQuestions selectOptionsQuestions;
    private final InputQuestions inputQuestions;
    private final CheckBoxQuestions checkBoxQuestions;

    protected WidGet(WebDriver webDriver, WebDriverWait wait,
                     RadioOptionsQuestions radioOptionsQuestions,
                     SelectOptionsQuestions selectOptionsQuestions,
                     InputQuestions inputQuestions,
                     CheckBoxQuestions checkBoxQuestions) {
        super(webDriver, wait);
        this.radioOptionsQuestions = radioOptionsQuestions;
        this.selectOptionsQuestions = selectOptionsQuestions;
        this.inputQuestions = inputQuestions;
        this.checkBoxQuestions = checkBoxQuestions;
    }

    public void scan() {
        selectOptionsQuestions.scan();
        radioOptionsQuestions.scan();
        inputQuestions.scan();
        checkBoxQuestions.scan();
    }

    public boolean hasNextWidget() {
        Try<Void> tried = tryClick(SUBMIT_APPLICATION)
                .orElse(tryClick(REVIEW))
                .orElse(tryClick(NEXT))
                .orElse(tryClick(CONTINUE_APPLYING));

        if (tried.isSuccess()) {
            pause(Duration.ofSeconds(3));
            return true;
        }
        pause(Duration.ofSeconds(3));
        doUntilSuccess(() -> tryClickDismiss().orElse(this::tryClickDone));
        return false;
    }

    private Try<Void> tryClickDismiss() {
        return Try.run(() -> click(waitForPresenceAndClickable(CLOSE_WIDGET)));
    }

    private Try<Void> tryClickDone() {
        return Try.run(() -> click(waitForPresenceAndClickable(CLOSE_BUTTON)));
    }
}
