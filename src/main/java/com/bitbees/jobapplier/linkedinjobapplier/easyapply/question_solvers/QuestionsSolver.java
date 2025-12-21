package com.bitbees.jobapplier.linkedinjobapplier.easyapply.question_solvers;

import com.bitbees.jobapplier.linkedinjobapplier.models.ShadowRootHelper;
import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;

public interface QuestionsSolver {

    Logger logger = LoggerFactory.getLogger(QuestionsSolver.class);

    default void scan(ShadowRootHelper shadowRootHelper) {
        logger.info("Scanning by {}", this.getClass().getSimpleName());
        SearchContext shadowRoot = shadowRootHelper.getShadowRoot();
        List<WebElement> questionLabels = shadowRoot.findElements(getQuestionLabelLocation());
        for (WebElement questionLabel : questionLabels) {
            WebElement answerInput = getAnswerInput(shadowRootHelper, questionLabel);
            solve(shadowRootHelper, questionLabel, answerInput);
        }
    }

    By getQuestionLabelLocation();

    default WebElement getAnswerInput(ShadowRootHelper shadowRootHelper, WebElement questionLabel) {
        shadowRootHelper.highlight(questionLabel);
        String forId = questionLabel.getAttribute("for");
        Objects.requireNonNull(forId);
        return shadowRootHelper.findAndClick(By.id(forId));
    }

    void solve(ShadowRootHelper shadowRootHelper, WebElement questionLabel, WebElement answerInput);

}
