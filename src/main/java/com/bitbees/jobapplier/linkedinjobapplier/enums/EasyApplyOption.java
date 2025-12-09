package com.bitbees.jobapplier.linkedinjobapplier.enums;

import lombok.Getter;
import org.openqa.selenium.By;

@Getter
public enum EasyApplyOption {

    ENABLE(By.xpath("//h3[text()='Easy Apply']/following-sibling::div/div")),
    DISABLE(By.xpath("//h3[text()='Easy Apply']/following-sibling::div/div"));

    private final By location;

    EasyApplyOption(By location) {
        this.location = location;
    }
}

