package com.example.teamcity.ui;

import com.codeborne.selenide.Condition;
import com.example.teamcity.api.enums.Endpoint;
import com.example.teamcity.api.generators.TestDataStorage;
import com.example.teamcity.api.models.BuildType;
import com.example.teamcity.api.models.Project;
import com.example.teamcity.api.requests.CheckedRequests;
import com.example.teamcity.api.spec.Specifications;
import com.example.teamcity.ui.pages.BuildTypePage;
import com.example.teamcity.ui.pages.admin.CreateBuildTypePage;
import org.testng.annotations.Test;

import static com.example.teamcity.api.enums.Endpoint.PROJECTS;
import static com.example.teamcity.ui.pages.admin.CreateBasePage.REPO_URL;

@Test(groups = {"Regression"})
public class CreateBuildTypeTest extends BaseUiTest {
    @Test(description = "User should be able to create buildType", groups = {"Positive"})
    public void userCreatesBuildType() {
        // login
        loginAs(testData.getUser());

        // create project
        var userCheckRequests = new CheckedRequests(Specifications.authSpec(testData.getUser()));
        userCheckRequests.<Project>getRequest(PROJECTS).create(testData.getProject());

        // create build type
        CreateBuildTypePage.open(testData.getProject().getId())
                .createForm(testData.getProject().getName(), REPO_URL)
                .setupBuildType(testData.getBuildType().getName());

        // check build type was created
        var createdBuildType = userCheckRequests.<BuildType>getRequest(Endpoint.BUILD_TYPES).read("name:" + testData.getBuildType().getName());
        softy.assertNotNull(createdBuildType);
        TestDataStorage.getStorage().addCreatedEntity(Endpoint.BUILD_TYPES, createdBuildType);

        // open build type page, check title
        BuildTypePage.open(createdBuildType.getId())
                .title.shouldHave(Condition.exactText(testData.getBuildType().getName()));
    }

    @Test(description = "User should not be able to create buildType without name", groups = {"Negative"})
    public void userCanNotCreateBuildTypeWithoutName() {
        // login
        loginAs(testData.getUser());

        // create project
        var userCheckRequests = new CheckedRequests(Specifications.authSpec(testData.getUser()));
        userCheckRequests.<Project>getRequest(PROJECTS).create(testData.getProject());

        // check build type can not be created without name
        CreateBuildTypePage.open(testData.getProject().getId())
                .createForm(testData.getProject().getName(), REPO_URL)
                .setupBuildType("")
                .checkErrorMessage();
    }
}
