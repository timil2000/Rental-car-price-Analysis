package org.example;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static WebDriver driver;  // WebDriver instance

    // This is the main method, the entry point of our program.
    public static void main(String[] args) {
        shouldScrapping();
    }

    static void shouldScrapping(){
        // Prompt the user for input
        Scanner scanner = new Scanner(System.in);
        String userInput = "yes";

        // Check user input and act accordingly
        if ("yes".equals(userInput)) {
            // Set up ChromeDriver
            WebDriverManager.chromedriver().setup();
            driver = new ChromeDriver();

            try {
                // Call the method to perform scraping and save data
                scrapeAndSaveData();
            } finally {
                // Ensure the driver is closed
                if (driver != null) {
                    driver.quit();
                }
            }
        }
    }

    // This method scrapes data and saves it to an Excel file.
    private static void scrapeAndSaveData() {
        // Path where the Excel file will be saved
        String excelFilePath = "rental_car_data.xlsx";

        try {
            // Defining the header for the Excel file
            String[] header = {"Car Name", "Car Image URL", "Car Rating", "Car Price"};

            // Creating a new Excel workbook and sheet
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Car Data");

            // Creating the header row in the Excel sheet
            Row headerRow = sheet.createRow(0);
            // Populating the header row with the defined header
            for (int i = 0; i < header.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(header[i]);
            }

            // Calling a method to scrape car data and populate the sheet
            scrapeCarData(sheet);

            // Saving the workbook to the specified file path
            try (FileOutputStream outputStream = new FileOutputStream(new File(excelFilePath))) {
                workbook.write(outputStream);
            }
        } catch (IOException e) {
            e.printStackTrace();  // Printing stack trace if an IO exception occurs
        }
    }

    // This method scrapes car data from a rental website and populates it into the provided Excel sheet.
    private static void scrapeCarData(Sheet sheet) {
        try {
            driver.manage().window().maximize();  // Maximize the browser window
            driver.get("https://getaround.com/");  // Navigate to the target website
            //System.out.println("Website opened.");

            // Handle the cookie consent modal
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
            wait.until(ExpectedConditions.presenceOfElementLocated(By.className("js_cookie-consent-modal__agreement"))).click();

            // Interact with the search input field and date picker
            WebElement input = driver.findElement(By.id("order_address"));
            input.click();
            input.sendKeys("Hollywood, Los Angeles");
            wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("/html/body/div[1]/div[1]/div/div/div[1]/div/form/div/div[1]/div[1]/div[1]/div/div/div[1]/ul/li[2]"))).click();

            WebElement div = driver.findElement(By.xpath("/html/body/div[1]/div[1]/div/div/div[1]/div/form/div/div[1]/div[2]/div/div/div[1]"));
            div.click();
            Actions action2 = new Actions(driver);
            WebElement divdate = driver.findElement(By.xpath("/html/body/div[1]/div[1]/div/div/div[1]/div/form/div/div[1]/div[2]/div/div/div[4]/div/div/div/div/div/div/div[2]"));
            action2.scrollToElement(divdate).perform();
            WebElement datepicker = driver.findElement(By.xpath("/html/body/div[1]/div[1]/div/div/div[1]/div/form/div/div[1]/div[2]/div/div/div[4]/div/div/div/div/div/div/div[2]/div[1]/div[2]/div[2]/div[2]/div"));
            datepicker.click();

            wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("/html/body/div[1]/div[1]/div/div/div[1]/div/form/div/div[1]/div[2]/div/div/div[1]/div[3]/div/div/div/div/div/div/div[16]"))).click();
            wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("/html/body/div[1]/div[1]/div/div/div[1]/div/form/div/div[1]/div[2]/div/div/div[3]"))).click();
            WebElement divdate2 = driver.findElement(By.xpath("/html/body/div[1]/div[1]/div/div/div[1]/div/form/div/div[1]/div[2]/div/div/div[4]/div/div/div/div/div/div/div[2]"));
            action2.scrollToElement(divdate2).perform();
            WebElement dropdatepicker = driver.findElement(By.xpath("/html/body/div[1]/div[1]/div/div/div[1]/div/form/div/div[1]/div[2]/div/div/div[4]/div/div/div/div/div/div/div[2]/div[1]/div[1]/div[2]/div[3]/div[2]"));
            dropdatepicker.click();

            wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("/html/body/div[1]/div[1]/div/div/div[1]/div/form/div/div[1]/div[2]/div/div/div[3]/div[3]/div/div/div/div/div/div/div[18]"))).click();
            wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("/html/body/div[1]/div[1]/div/div/div[1]/div/form/div/div[2]/button"))).click();
            Thread.sleep(2000);

            // Scroll to footer and click "Load More" if available
            Actions action = new Actions(driver);
            WebElement footer = driver.findElement(By.className("corporate_footer__container"));
            action.scrollToElement(footer).perform();
            boolean loadMoreVisible = true;

            while (loadMoreVisible) {
                try {
                    WebElement loadmore = driver.findElement(By.className("search-results__load-more-button"));
                    if (loadmore.isDisplayed()) {
                        action.scrollToElement(footer).perform();
                        Thread.sleep(2000);
                        loadmore.click();
                        Thread.sleep(2000);
                        action.scrollToElement(footer).perform();
                    } else {
                        loadMoreVisible = false;
                    }
                } catch (NoSuchElementException e) {
                    loadMoreVisible = false;
                }
//                System.out.println("Load more clicked");
            }

            // Find and write car data to Excel
            List<WebElement> carImg = driver.findElements(By.className("car_card__header"));
            List<WebElement> carName = driver.findElements(By.className("car_card__title"));
            List<WebElement> carRating = driver.findElements(By.className("cobalt-rating__label"));
            List<WebElement> carPrice = driver.findElements(By.className("car_card__pricing-value"));

            for (int i = 0; i < carImg.size(); i++) {
                String name = carName.get(i).getText();
                String imgURL = carImg.get(i).getAttribute("data-background-image");
                String rating = carRating.get(i).getText();
                String price = carPrice.get(i).getText();

                Row row = sheet.createRow(sheet.getLastRowNum() + 1);
                row.createCell(0).setCellValue(name);
                row.createCell(1).setCellValue(imgURL);
                row.createCell(2).setCellValue(rating);
                row.createCell(3).setCellValue(price);
            }

        } catch (Exception e) {
            e.printStackTrace();  // Print stack trace if an exception occurs
        }
    }
}
