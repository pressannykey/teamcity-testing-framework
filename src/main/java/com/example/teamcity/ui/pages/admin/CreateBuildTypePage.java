package com.example.teamcity.ui.pages.admin;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static org.testng.Assert.assertEquals;

public class CreateBuildTypePage extends CreateBasePage {
    private static final String BUILD_TYPE_SHOW_MODE = "createBuildTypeMenu";

    private final SelenideElement parentProject = $("span[class*='ProjectBuildTypeSelect__name']");
    private final SelenideElement errorMessage = $("span#error_buildTypeName");

    public static CreateBuildTypePage open(String projectId) {
        return Selenide.open(CREATE_URL.formatted(projectId, BUILD_TYPE_SHOW_MODE), CreateBuildTypePage.class);
    }

    public CreateBuildTypePage createForm(String parentProject, String url) {
        checkParentProject(parentProject);
        baseCreateForm(url);
        return this;
    }

    public CreateBuildTypePage setupBuildType(String buildTypeName) {
        buildTypeNameInput.val(buildTypeName);
        submitButton.click();
        return this;
    }

    public void checkErrorMessage() {
        String text = "Build configuration name must not be empty";
        assertEquals(errorMessage.shouldBe(visible).getText(), text,
                "Build type can not be created without name");
    }

    private void checkParentProject(String parentProjectName) {
        assertEquals(parentProject.shouldBe(visible).getText(), parentProjectName,
                String.format("Parent project id should be %s", parentProjectName));
    }
}
