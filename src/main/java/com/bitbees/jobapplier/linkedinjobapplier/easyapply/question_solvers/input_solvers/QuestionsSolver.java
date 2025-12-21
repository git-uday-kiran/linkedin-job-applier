package com.bitbees.jobapplier.linkedinjobapplier.easyapply.question_solvers.input_solvers;

import com.bitbees.jobapplier.linkedinjobapplier.models.ShadowRootHelper;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Objects;

public interface QuestionsSolver {

    default void scan(ShadowRootHelper shadowRootHelper) {
        SearchContext shadowRoot = shadowRootHelper.getShadowRoot();
        List<WebElement> questionLabels = shadowRoot.findElements(getQuestionLabelLocation());
        for (WebElement questionLabel : questionLabels) {
            WebElement answerInput = getAnswerInput(shadowRootHelper, questionLabel);
            solve(shadowRootHelper, questionLabel, answerInput);
        }
    }

    default WebElement getAnswerInput(ShadowRootHelper shadowRootHelper, WebElement questionLabel) {
        shadowRootHelper.highlight(questionLabel);
        String forId = questionLabel.getAttribute("for");
        Objects.requireNonNull(forId);
        return shadowRootHelper.findAndClick(By.id(forId));
    }

    default void solve(ShadowRootHelper shadowRootHelper, WebElement questionLabel, WebElement answerInput) {
        String question = questionLabel.getText();
        String answer = solveQuestion(question);

        shadowRootHelper.highlight(answerInput);
        answerInput.clear();
        answerInput.sendKeys(answer, Keys.ENTER);
    }


    By getQuestionLabelLocation();

    String solveQuestion(String question);
}
