package com.herokuapp.theinternet.loginpagetests;

import com.herokuapp.theinternet.base.BaseTest;
import com.herokuapp.theinternet.base.CsvDataProviders;
import com.herokuapp.theinternet.base.Retry;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.LoginPage;

import java.util.Map;

public class NegativeLogInTests extends BaseTest {

	@Test(dataProvider = "csvReader", dataProviderClass = CsvDataProviders.class, retryAnalyzer = Retry.class)
	public void negativeLogInTest(Map<String, String> testData) {
		String no = testData.get("no");
		String username  = testData.get("username");
		String password = testData.get("password");
		String expectedErrorMessage = testData.get("expectedMessage");
		String description = testData.get("description");
		
		log.info("Starting negativeLogInTest #" + no + " for " + description);

        LoginPage loginPage = new LoginPage(driverInstance, log);
        log.info("Driver Instance is: " + driverInstance);
        loginPage.openPage();

		// execute negative login
		loginPage.negativeLogIn(username, password);

		// wait for error message
		loginPage.waitForErrorMessage();
		String message = loginPage.getErrorMessageText();

		// Verification
		Assert.assertTrue(message.contains(expectedErrorMessage), "Message doesn't contain expected text.");
	}
}
