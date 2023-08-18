package VIsaRobot;

import com.twocaptcha.TwoCaptcha;
import com.twocaptcha.captcha.HCaptcha;
import io.github.bonigarcia.wdm.WebDriverManager;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.FileInputStream;
import java.time.Duration;

public class Recaptcha {
    WebDriver driver;
    WebDriverWait wait;
    String email = "";
    String password = "";
    FileInputStream file;
    HSSFWorkbook workbook;
    HSSFSheet sheet;

    @BeforeMethod
    void launchBrowser() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-extensions");
        options.addArguments("--disable-gpu");

    }

    @Test
    void loginRecaptcha() throws Exception {
        wait = new WebDriverWait(driver, Duration.ofSeconds(240));

        // Read login credentials from Excel sheet
        try {
            file = new FileInputStream("C:\\Users\\MoshoodAhmed\\IdeaProjects\\Visa\\src\\main\\java\\VIsaRobot\\Book1.xls");
            workbook = new HSSFWorkbook(file);
            sheet = workbook.getSheetAt(0);
            for (Row row : sheet) {
                if (row.getRowNum() == 0) {
                    continue;
                }
                email = row.getCell(0).getStringCellValue();
                password = row.getCell(1).getStringCellValue();
            }
            file.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        driver.get("https://visa.vfsglobal.com/tur/tr/pol/login");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[@id='onetrust-accept-btn-handler']")));
        WebElement cookies0 = driver.findElement(By.xpath("//button[@id='onetrust-accept-btn-handler']"));
        cookies0.click();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(3));

        // Find username and password fields
        WebElement emailField = driver.findElement(By.xpath("//div[@class='mat-form-field-infix ng-tns-c60-0']/input"));
        WebElement passwordField = driver.findElement(By.xpath("//div[@class='mat-form-field-infix ng-tns-c60-1']/input"));
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(3));
        emailField.sendKeys(email);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(3));
        passwordField.sendKeys(password);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(20));

         /*
          solving reCaptcha using @2captha service
          @params data-siteKey --> 6LfDUY8bAAAAAPU5MWGT_w0x5M-8RdzC29SClOfI
          @params API-key --> 0182a721dd70f1400cdb5218e02970ce
          */


        TwoCaptcha service = new TwoCaptcha("0182a721dd70f1400cdb5218e02970ce");
        HCaptcha captcha = new HCaptcha();
        captcha.setSiteKey("6LfDUY8bAAAAAPU5MWGT_w0x5M-8RdzC29SClOfI");
        captcha.setUrl("https://visa.vfsglobal.com/tur/tr/pol/login");
        String response = null;

            Thread.sleep(5000);

        try {
            service.solve(captcha);
//            Thread.sleep(5000);
            response = captcha.getCode();
            System.out.println("Captcha solved: " + response);

        } catch (Exception e) {
            System.out.println("Error occurred: " + e.getMessage());

        }

//        if (response == null) {
//
//            System.out.println("Captcha is not available for injection!");
//            return;
//        }
        // Parsing solved captcha response if found after thread sleep.
        Thread.sleep(50000);

        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("document.getElementById('g-recaptcha-response').innerHTML='" + response + "';");
        Thread.sleep(200);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(20));

// find iframe widget.
        WebElement iframe = driver.findElement(By.xpath("//iframe[contains(@src,'recaptcha')]"));
        driver.switchTo().frame(iframe);

        Thread.sleep(200);

// finding the captcha button
        WebElement captchaButton = driver.findElement(By.xpath("//div[@class='recaptcha-checkbox-checkmark']"));

        Thread.sleep(200);

// clicks the captcha button
        js.executeScript("arguments[0].click();", captchaButton);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(20));

// switch back to default content  and submits the form
        driver.switchTo().defaultContent();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(20));

// find the submit button
        WebElement submitButton = driver.findElement(By.xpath("//form/button"));
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(20));

// clicks the submit button and complete login
        try {

            js.executeScript("arguments[0].click();", submitButton);
            System.out.println("i clicked successfully!");

        } catch (Exception e) {
            System.out.println("mosh: button get problem oooh!!!! " + e.getMessage());
        }

    }

    @Test
    void teardown() {
//        driver.close();
    }
}
