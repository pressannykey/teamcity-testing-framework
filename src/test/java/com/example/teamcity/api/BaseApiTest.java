package com.example.teamcity.api;

import com.example.teamcity.api.enums.Endpoint;
import com.example.teamcity.api.models.ServerAuthSettings;
import com.example.teamcity.api.models.User;
import com.example.teamcity.api.requests.checked.CheckedBase;
import com.example.teamcity.api.spec.Specifications;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;

import static com.example.teamcity.api.generators.TestDataGenerator.generate;
import static io.qameta.allure.Allure.step;

public class BaseApiTest extends BaseTest {

//    @BeforeMethod
//    public void activatePermissions() {
//        step("Set permissions", () -> {
//            var settings = ServerAuthSettings.builder()
//                    .perProjectPermissions(true)
//                    .build();
//
//            var requester = new CheckedBase<ServerAuthSettings>(Specifications.superUserSpec(), Endpoint.AUTH_SETTINGS);
//            requester.update(settings);
//        });
//    }
}
