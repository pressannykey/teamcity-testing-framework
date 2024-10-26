package com.example.teamcity.api;

import com.example.teamcity.api.models.BuildType;
import com.example.teamcity.api.models.Project;
import com.example.teamcity.api.models.Roles;
import com.example.teamcity.api.requests.CheckedRequests;
import com.example.teamcity.api.requests.unchecked.UncheckedBase;
import com.example.teamcity.api.spec.Specifications;
import org.hamcrest.Matchers;
import org.testng.annotations.Test;

import java.util.Arrays;

import static com.example.teamcity.api.enums.Endpoint.*;
import static com.example.teamcity.api.generators.TestDataGenerator.generate;
import static org.apache.http.HttpStatus.*;

@Test(groups = {"Regression"})
public class BuildTypeTest extends BaseApiTest {
    @Test(description = "User should be able to create build type", groups = {"Positive", "CRUD"})
    public void userCreatesBuildTypeTest() {
        // Create user
        superUserCheckRequests.getRequest(USERS).create(testData.getUser());

        var userCheckRequests = new CheckedRequests(Specifications.authSpec(testData.getUser()));
        // Create project
        userCheckRequests.<Project>getRequest(PROJECTS).create(testData.getProject());

        // Create buildType
        userCheckRequests.getRequest(BUILD_TYPES).create(testData.getBuildType());
        var createdBuildType = userCheckRequests.<BuildType>getRequest(BUILD_TYPES).read(testData.getBuildType().getId());

        // Assert buildType name is expected
        softy.assertEquals(testData.getBuildType().getName(),
                createdBuildType.getName(),
                "Build type name is not correct");
    }

    @Test(description = "User should not be able to create two build types with the same id", groups = {"Negative", "CRUD"})
    public void userCreatesTwoBuildTypesWithTheSameIdTest() {
        // Generate test data for buildType
        var buildTypeWithSameId = generate(Arrays.asList(testData.getProject()), BuildType.class, testData.getBuildType().getId());

        // Create user
        superUserCheckRequests.getRequest(USERS).create(testData.getUser());

        var userCheckRequests = new CheckedRequests(Specifications.authSpec(testData.getUser()));
        // Create project
        userCheckRequests.<Project>getRequest(PROJECTS).create(testData.getProject());

        // Create buildType
        userCheckRequests.getRequest(BUILD_TYPES).create(testData.getBuildType());

        // Try to create second buildType
        new UncheckedBase(Specifications.authSpec(testData.getUser()), BUILD_TYPES)
                .create(buildTypeWithSameId)
                .then().assertThat().statusCode(SC_BAD_REQUEST)
                .body(Matchers.containsString("The build configuration / template ID \"%s\" is already used by another configuration or template".formatted(testData.getBuildType().getId())));
    }

    @Test(description = "Project admin should be able to create build type for their project", groups = {"Positive", "Roles"})
    public void projectAdminCreatesBuildTypeTest() {
        // Create project
        superUserCheckRequests.<Project>getRequest(PROJECTS).create(testData.getProject());

        // Generate data for user with role Project admin
        testData.getUser().setRoles(generate(Roles.class,
                "PROJECT_ADMIN", "p:" + testData.getProject().getId()));

        // Create user with generated data
        superUserCheckRequests.getRequest(USERS).create(testData.getUser());

        new UncheckedBase(Specifications.authSpec(testData.getUser()), BUILD_TYPES)
                .create(testData.getBuildType())
                .then().assertThat().statusCode(SC_OK);
    }

    @Test(description = "Project admin should not be able to create build type for not their project", groups = {"Negative", "Roles"})
    public void projectAdminCreatesBuildTypeForAnotherUserProjectTest() {
        // Create 2 projects
        var projectOne = superUserCheckRequests.<Project>getRequest(PROJECTS).create(testData.getProject());
        var projectTwo = superUserCheckRequests.<Project>getRequest(PROJECTS).create(generate(Project.class));

        // Generate data for user with role Project admin for projectOne
        testData.getUser().setRoles(generate(Roles.class,
                "PROJECT_ADMIN", "p:" + projectOne.getId()));

        // Create user with generated data
        superUserCheckRequests.getRequest(USERS).create(testData.getUser());

        // Generate data for buildType with projectTwo
        var buildTypeForProjectTwo = generate(Arrays.asList(projectTwo), BuildType.class);

        // Try to create generated buildType
        new UncheckedBase(Specifications.authSpec(testData.getUser()), BUILD_TYPES)
                .create(buildTypeForProjectTwo)
                .then().assertThat().statusCode(SC_FORBIDDEN)
                .body(Matchers.containsString("You do not have enough permissions to edit project with id: %s".formatted(projectTwo.getId())));
    }
}
