package com.bitbees.jobapplier.linkedinjobapplier.pages;

import com.bitbees.jobapplier.linkedinjobapplier.easyapply.enums.WorkType;
import com.bitbees.jobapplier.linkedinjobapplier.models.Page;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;


public class JobCard extends Page {

    private static final By JOB_DESCRIPTION = By.id("job-details");
    private static final By EASY_APPLY = By.xpath("//button[.//span[text()='Easy Apply']]");

    private String url;
    private String title;
    private String description;
    private String location;
    private String company;
    private boolean applied;
    private boolean viewed;

    private final WebElement jobElement;

    public JobCard(WebDriver webDriver,
                   WebDriverWait wait,
                   WebElement jobElement) {
        super(webDriver, wait);
        this.jobElement = jobElement;
    }

    public void init() {
        scrollIntoView(jobElement);

        String jobText = jobElement.getText().toLowerCase();
        String[] lines = jobText.split("\\n");
        this.title = lines[0];
        this.company = lines[2];
        this.location = lines[3];

        applied = jobText.contains("applied");
        viewed = jobText.contains("viewed");
    }

    public WorkType getWorkType() {
        String workType = location.substring(location.indexOf('(') + 1, location.indexOf(')'));
        return switch (workType) {
            case "on-site" -> WorkType.ONSITE;
            case "remote" -> WorkType.REMOTE;
            case "hybrid" -> WorkType.HYBRID;
            default -> throw new IllegalStateException("Unexpected value: " + workType);
        };
    }

    public String getDescription() {
        if (description == null) click();
        return description;
    }

    public void click() {
        tryClick(jobElement);
        if (description == null) {
            pause(Duration.ofSeconds(3));
            findAndStoreDescription();
            this.url = webDriver.getCurrentUrl();
        }
    }

    private void findAndStoreDescription() {
        description = waitForPresence(JOB_DESCRIPTION)
                .getText()
                .toLowerCase();
    }

    @Override
    public String toString() {
        return "JobCard{" +
                "title='" + title + '\'' +
                ", applied=" + applied +
                ", viewed=" + viewed +
                ", location='" + location + '\'' +
                ", company='" + company + '\'' +
                ", description='" + getDescription().replace("\\n", " ") + '\'' +
                ", url='" + url + '\'' +
                '}';
    }

}
