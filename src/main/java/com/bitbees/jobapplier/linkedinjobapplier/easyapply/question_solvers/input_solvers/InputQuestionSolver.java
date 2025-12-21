package com.bitbees.jobapplier.linkedinjobapplier.easyapply.question_solvers.input_solvers;

import com.bitbees.jobapplier.linkedinjobapplier.easyapply.question_solvers.QuestionsSolver;
import com.bitbees.jobapplier.linkedinjobapplier.models.ShadowRootHelper;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

public interface InputQuestionSolver extends QuestionsSolver {
    default void solve(ShadowRootHelper shadowRootHelper, WebElement questionLabel, WebElement answerInput) {
        String question = questionLabel.getText();
        String answer = solveQuestion(question);

        shadowRootHelper.highlight(answerInput);
        answerInput.clear();
        answerInput.sendKeys(answer, Keys.ENTER);
    }

    String solveQuestion(String question);
}
