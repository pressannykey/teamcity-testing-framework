package com.example.teamcity.ui.pages.admin;

import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.SelenideElement;
import com.example.teamcity.ui.pages.BasePage;

import static com.codeborne.selenide.Condition.appear;
import static com.codeborne.selenide.Selenide.$;

public abstract class CreateBasePage extends BasePage {
    protected static final String CREATE_URL = "/admin/createObjectMenu.html?projectId=%s&showMode=%s";
    public static final String REPO_URL = "https://github.com/pressannykey/teamcity-testing-framework";

    protected final SelenideElement urlInput = $("#url");
    protected final SelenideElement submitButton = $(Selectors.byAttribute("value", "Proceed"));
    protected final SelenideElement buildTypeNameInput = $("#buildTypeName");
    protected final SelenideElement connectionSuccessfulMessage = $(".connectionSuccessful");

    protected void baseCreateForm(String url) {
        urlInput.val(url);
        submitButton.click();
        connectionSuccessfulMessage.should(appear, BASE_WAITING);
    }
}
