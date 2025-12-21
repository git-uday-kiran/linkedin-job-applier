package com.bitbees.jobapplier.linkedinjobapplier.easyapply.question_solvers.input_solvers;

import com.bitbees.jobapplier.linkedinjobapplier.models.ShadowRootHelper;
import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Log4j2
@Service
public class NumericQuestionsSolver {

    private final By questionsLabelLocation = By.cssSelector("label[for^='single-line-text-form-component'][for$='numeric']");

    public void scan(ShadowRootHelper shadowRootHelper) {
        SearchContext shadowRoot = shadowRootHelper.getShadowRoot();
        shadowRoot.findElements(questionsLabelLocation)
                .forEach(labelElement -> solve(shadowRootHelper, labelElement));
    }

    private void solve(ShadowRootHelper shadowRootHelper, WebElement labelElement) {
        shadowRootHelper.highlight(labelElement);
        String forId = labelElement.getAttribute("for");
        Objects.requireNonNull(forId);

        String question = labelElement.getText();
        String answer = solve(question);

        WebElement inputElement = shadowRootHelper.findAndClick(By.id(Objects.requireNonNull(forId)));
        shadowRootHelper.highlight(inputElement);
        inputElement.clear();
        inputElement.sendKeys(answer, Keys.ENTER);
    }

    public String solve(String question) {

        return "0";
    }
}
