package com.bitbees.jobapplier.linkedinjobapplier.services;

import lombok.RequiredArgsConstructor;
import org.openqa.selenium.WebDriver;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NavigatorService {

    private final WebDriver driver;

}