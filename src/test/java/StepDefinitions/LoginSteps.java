package StepDefinitions;

import static StepDefinitions.Hook.driver;
import Utilities.ExcelReader;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class LoginSteps {

    private List<Object[]> testData;

    @Given("Read login data from Excel")
    public void read_data_from_excel() throws Exception {
        testData = ExcelReader.readExcelData("TestData/DataFile.xlsx", "Login_Data"); // Đọc dữ liệu từ file Excel với tên sheet
    }

    @When("Execute login tests with each data row")
    public void run_login_tests() {
        List<String> errors = new ArrayList<>();

        for (Object[] row : testData) {
            String username = row[0].toString();
            String password = row[1].toString();
            String expectedResult = row[2].toString();

            try {
                String actualResult = performLoginAndGetAlertMessage(username, password);

                if (expectedResult.trim().equalsIgnoreCase(actualResult.trim())) {
                    System.out.println("Passed with username: " + username + ", " + password);
                } else {
                    System.out.println("Failed with username: " + username + ", " + password);
                    System.out.println("   Expected: '" + expectedResult + "'");
                    System.out.println("   Actual  : '" + actualResult + "'");
                    errors.add("Username: " + username + " | Expected: '" + expectedResult + "' but got: '" + actualResult + "'");
                }

            } catch (Exception e) {
                System.out.println("Error with username: " + username);
                e.printStackTrace();
                errors.add("Username: " + username + " | Exception: " + e.getMessage());
            }
        }

        if (!errors.isEmpty()) {
            System.out.println("\nSummary of failed test cases:");
            for (String error : errors) {
                System.out.println(error);
            }
            throw new AssertionError("Some test cases failed. See details above.");
        }
    }

    @Then("Should see the expected login results")
    public void verify_results() {
        System.out.println("Finished checking all test cases.");
    }

    private String performLoginAndGetAlertMessage(String username, String password) throws InterruptedException {
        driver.get("http://127.0.0.1:5500/log%20in/log%20in.html");

        driver.findElement(By.id("username")).clear();
        driver.findElement(By.id("username")).sendKeys(username);
        Thread.sleep(300);

        driver.findElement(By.id("password")).clear();
        driver.findElement(By.id("password")).sendKeys(password);
        Thread.sleep(300);

        driver.findElement(By.className("login-btn")).click();
        Thread.sleep(1200);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(3));
        wait.until(ExpectedConditions.alertIsPresent());
        Alert alert = driver.switchTo().alert();
        String alertText = alert.getText();
        alert.accept();

        return alertText;
    }
}
