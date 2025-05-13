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

public class RegisterSteps {
    private List<Object[]> testData;

    @Given("Read register data from Excel")
    public void read_data_from_excel() throws Exception {
        testData = ExcelReader.readExcelData("TestData/DataFile.xlsx", "Register_Data");
    }

    @When("Execute register tests with each data row")
    public void run_register_tests() {
        List<String> errors = new ArrayList<>();

        for (Object[] row : testData) {
            String username = row[0].toString();
            String email = row[1].toString();
            String password = row[2].toString();
            String phone = row[3].toString();
            String expectedResult = row[4].toString();

            try {
                String actualResult = performRegisterAndGetAlertMessage(username, email, password, phone);

                if (expectedResult.trim().equalsIgnoreCase(actualResult.trim())) {
                    System.out.println("Passed with username: " + username + "," + password);
                } else {
                    System.out.println("Failed with username: " + username + "," + password);
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

    @Then("Should see the expected register results")
    public void verify_results() {
        System.out.println("Finished checking all test cases.");
    }

    private String performRegisterAndGetAlertMessage(String username, String email, String password, String phone) throws InterruptedException {
        driver.get("http://127.0.0.1:5500/log%20in/register.html");

        // Nhập thông tin vào các trường
        driver.findElement(By.id("username")).clear();
        driver.findElement(By.id("username")).sendKeys(username);

        driver.findElement(By.id("email")).clear();
        driver.findElement(By.id("email")).sendKeys(email);

        driver.findElement(By.id("password")).clear();
        driver.findElement(By.id("password")).sendKeys(password);

        // Nếu ô số điện thoại trống thì không nhập gì vào ô này
        if (phone.trim().isEmpty()) {
            driver.findElement(By.id("phone")).clear();  // Xóa nếu ô trống
        } else {
            driver.findElement(By.id("phone")).sendKeys(phone);
        }

        driver.findElement(By.xpath("//button[@class='login-btn' and text()='Đăng ký']")).click();

        Thread.sleep(1000);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(3));
        wait.until(ExpectedConditions.alertIsPresent());
        Alert alert = driver.switchTo().alert();
        String alertText = alert.getText();
        alert.accept();

        return alertText;
    }
}
