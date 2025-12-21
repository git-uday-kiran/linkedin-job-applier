package com.bitbees.jobapplier.linkedinjobapplier.easyapply.question_solvers;

import com.bitbees.jobapplier.linkedinjobapplier.models.ShadowRootHelper;
import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.springframework.stereotype.Service;

import java.util.List;

@Log4j2
@Service
public class SelectQuestionSolver implements QuestionsSolver {
    @Override
    public By getQuestionLabelLocation() {
        return By.cssSelector("label[for*='form-component'][for$='multipleChoice']");
    }

    @Override
    public void solve(ShadowRootHelper shadowRootHelper, WebElement questionLabel, WebElement answerInput) {
        String question = questionLabel.getText();
        Select select = new Select(answerInput);
        solveQuestion(question, select);
    }

    public void solveQuestion(String question, Select select) {
        List<WebElement> options = select.getOptions();

        List<String> optionsText = options.stream()
                .map(WebElement::getText)
                .toList();
        int option = selectOption(question, optionsText);

        select.selectByVisibleText(options.get(option).getText());
    }

    public int selectOption(String question, List<String> options) {
        System.out.println("question = " + question);
        System.out.println("question = " + question);

        return 1;
    }
}
