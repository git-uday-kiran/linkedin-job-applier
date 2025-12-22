package com.bitbees.jobapplier.linkedinjobapplier.easyapply.enums;

import lombok.Getter;
import org.openqa.selenium.By;

@Getter
public enum Location {

    BENGALURU(By.xpath("//span[contains(normalize-space(.), 'Bengaluru')]")),
    HYDERABAD(By.xpath("//span[text()='Hyderabad']")),
    PUNE(By.xpath("//span[text()='Pune']")),
    CHENNAI(By.xpath("//span[text()='Chennai']")),
    NOIDA(By.xpath("//span[text()='Noida']")),
    GURUGRAM(By.xpath("//span[text()='Gurugram']")),
    GURGAON(By.xpath("//span[text()='Gurgaon']")),
    MUMBAI(By.xpath("//span[text()='Mumbai']")),
    AHMEDABAD(By.xpath("//span[text()='Ahmedabad']")),
    TRIVANDRUM(By.xpath("//span[text()='Trivandrum']")),
    KOCHI(By.xpath("//span[text()='Kochi']")),
    DELHI(By.xpath("//span[text()='Delhi']")),
    COIMBATORE(By.xpath("//span[text()='Coimbatore']"));

    private final By location;

    Location(By location) {
        this.location = location;
    }
}
