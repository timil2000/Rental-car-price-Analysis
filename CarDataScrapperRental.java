
package carrental;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.util.*;
import java.util.NoSuchElementException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.support.ui.WebDriverWait;

public class CarDataScrapperRental {
    private WebDriver driver;
    private Actions actions;
    private WebDriverWait wait;

    public CarDataScrapperRental(WebDriver driver) {
        this.driver = driver;
        this.actions = new Actions(driver);
        // Create WebDriverWait with a Duration of 10 seconds
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    // this method scroll data till end of loading
    public void scrollAndClickShowMore() {
        boolean loadMoreVisible = true;
        List<Map<String, Object>> Cars = new ArrayList<>();


        while (loadMoreVisible) {
            try {
                WebElement showMoreButton = new WebDriverWait(driver, Duration.ofSeconds(10)).until(ExpectedConditions.visibilityOfElementLocated(By.id("paginationShowMoreBtn")));
                actions.scrollToElement(showMoreButton).perform();
                Thread.sleep(2000); // Add a delay to allow the button to be clickable
                showMoreButton.click();
                Thread.sleep(2000); // Add a delay to allow new data to load
            } catch (NoSuchElementException | TimeoutException e) {
                // No more "Show more" button found
                loadMoreVisible = false;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // Find all car elements
        List<WebElement> carElements = driver.findElements(By.className("offer-card-desktop"));
        int totalCars = 0;

//         Iterate over each car element
        for (WebElement car : carElements) {
            totalCars++; // Increment the total count
            // Extract and print car details
            WebElement carTitleElement = car.findElement(By.cssSelector(".offer-card-desktop .uitk-text"));
            WebElement carCompact = car.findElement(By.tagName("h3"));
            WebElement carImage = car.findElement(By.cssSelector(".uitk-image-media"));
            WebElement carPricePerDay = car.findElement(By.cssSelector(".per-day-price"));
            WebElement carTotalPrice = car.findElement(By.cssSelector(".total-price"));

            // Extract additional details
            WebElement carPassengerElement = car.findElement(By.xpath(".//span[contains(@class, 'uitk-spacing text-attribute')][1]"));
            WebElement carTransmissionElement = car.findElement(By.xpath(".//span[contains(@class, 'uitk-spacing text-attribute')][2]"));

            String carPassenger = carPassengerElement.getText();
            String carTransmission = carTransmissionElement.getText();

// With explicit wait:
//            WebElement anchorTag = wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".uitk-spacing.offer-reserve-button a")));
//            String carPageDirectionLink = anchorTag.getAttribute("href");

//            WebElement anchorTag = car.findElement(By.cssSelector("uitk-spacing offer-reserve-button uitk-spacing-margin-block-three"));
//
//            String carPageDirectionLink = anchorTag.getAttribute("href");
// Assuming 'car' is your WebDriver instance
//            WebDriverWait wait = new WebDriverWait(car, 10);
            WebElement ratingsElement = car.findElement(By.cssSelector("span.uitk-text.uitk-type-start.uitk-type-300.uitk-text-white-space-break-spaces.uitk-text-default-theme.uitk-spacing.text-attribute.uitk-spacing-padding-inlinestart-two"));

            String ratingsText = ratingsElement.getText().trim();
            Pattern pattern = Pattern.compile("(\\d+)%");
            Matcher matcher = pattern.matcher(ratingsText);
            double ratingOutOf5 = 0.0;
            if (matcher.find()) {
                String percentageStr = matcher.group(1);
                int percentage = Integer.parseInt(percentageStr);
                ratingOutOf5 = (percentage / 100.0) * 5.0;
            }
            WebElement anchorTag = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("a.uitk-spacing.offer-reserve-button.uitk-spacing-margin-block-three")));
//
//            String carPageDirectionLink = anchorTag.getAttribute("href");
//            System.out.println("Href: " + carPageDirectionLink);

            // Find the specific anchor tag within the current car element
            WebElement anchorTagPage = car.findElement(By.cssSelector("a.uitk-spacing.offer-reserve-button.uitk-spacing-margin-block-three"));
            String carPageDirectionLink = anchorTagPage.getAttribute("href");
//            System.out.println("Href: " + carPageDirectionLink);

            // Print to console
            // Extract car title text and process it to get only the first two words
            String carTitleText = carTitleElement.getText();
            String[] words = carTitleText.split(" ");
            String processedCarTitle = String.join(" ", Arrays.copyOfRange(words, 0, Math.min(words.length, 2)));


            // Print to console

            System.out.println("Title: " + processedCarTitle);
            System.out.println("Specification: " + carCompact.getText());
            System.out.println("Image URL: " + carImage.getAttribute("src"));
            System.out.println("Price per day: " + carPricePerDay.getText());
            System.out.println("Total price with Tax: " + carTotalPrice.getText());
            System.out.println("Passenger capacity: " + carPassenger);
            System.out.println("Transmission type: " + carTransmission);
            System.out.println("Rating: " + String.format("%.2f", ratingOutOf5));
            System.out.println("Car Page Direction Link: " + carPageDirectionLink);

            Map<String, Object> cardata = new HashMap<>();
            cardata.put("CarTitle", processedCarTitle.replace(",", ""));
            cardata.put("ImageURL", carImage.getAttribute("src").replace(",", ""));
            cardata.put("Price", carPricePerDay.getText().replace(",", ""));
            cardata.put("Rating", String.format("%.2f", ratingOutOf5));
            cardata.put("PageDirectionLink", carPageDirectionLink.replace(",", ""));

            // add extra
            cardata.put("carPassenger", carPassenger.replace(",", ""));
            cardata.put("carTransmission Type", carTransmission.replace(",", ""));
            cardata.put("carSpecification", carCompact.getText());

            Cars.add(cardata);

            System.out.println("--------------------------------");
        }
//         Print the total count
        System.out.println("Total cars: " + totalCars);

        // Save Cars Data into CSV
        String filePath = "cars_Calgary.csv";
        saveCarDataCsv(Cars, filePath);

        // Click on the last <li> tag's <div> class
        List<WebElement> liElements = driver.findElements(By.cssSelector("li.offer-card-desktop"));
        if (!liElements.isEmpty()) {
            WebElement lastLiElement = liElements.get(liElements.size() - 1);
            WebElement divElement = lastLiElement.findElement(By.cssSelector("div[data-testid='car-offer-card']"));
            divElement.click();
        }

        try {
            Thread.sleep(4000);  // 6000 milliseconds = 6 seconds
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

// Get the window handles before clicking the "Trips" button
        Set<String> windowHandlesBefore = driver.getWindowHandles();
        try {
            // Find the "Trips" button by its ID
            WebElement tripsButton = driver.findElement(By.id("itinerary"));

// Simulate keyboard action to open link in a new tab
            Actions newTab = new Actions(driver);
            newTab.keyDown(Keys.CONTROL).click(tripsButton).keyUp(Keys.CONTROL).build().perform();
            // Get the window handles after clicking the "Trips" button
            Set<String> windowHandlesAfter = driver.getWindowHandles();

// Find the new tab handle
            String newTabHandle = "";
            for (String handle : windowHandlesAfter) {
                if (!windowHandlesBefore.contains(handle)) {
                    newTabHandle = handle;
                    break;
                }
            }

// Switch to the new tab
            driver.switchTo().window(newTabHandle);
        } catch (NoSuchElementException e) {
            System.err.println("Trips button not found: " + e.getMessage());
        }

        try {
            Thread.sleep(6000);  // Wait for 6 seconds
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Close the browser
        driver.quit();
    }

    // this functionality store scraping data in csv
    public static void saveCarDataCsv(List<Map<String, Object>> cars, String filePath) {
        try (FileWriter writer = new FileWriter(filePath)) {
            // Write header
//            writer.append("CarName,CarSpecification,CarImage,CarPricePerDay,CarTotalPriceWithTax,CarPassengerCapacity,CarTransmissionType,Rating,PageDirectionLink\n");

//            writer.append("CarTitle,ImageURL,Price,Rating,PageDirectionLink\n");
            writer.append("CarTitle,ImageURL,Price,Rating,PageDirectionLink,carPassenger,carTransmission Type,carSpecification\n");

            // Write data
//            for (Map<String, Object> car : cars) {
//                writer.append(String.join(",",
//                        String.valueOf(car.get("CarTitle")),
//                        String.valueOf(car.get("ImageURL")),
//                        String.valueOf(car.get("Price")),
//                        String.valueOf(car.get("Rating")),
//                        String.valueOf(car.get("PageDirectionLink"))
//                ));
//                writer.append("\n");
//            }

            for (Map<String, Object> car : cars) {
                writer.append(String.join(",",
                        String.valueOf(car.get("CarTitle")),
                        String.valueOf(car.get("ImageURL")),
                        String.valueOf(car.get("Price")),
                        String.valueOf(car.get("Rating")),
                        String.valueOf(car.get("PageDirectionLink")),
                        String.valueOf(car.get("carPassenger")),
                        String.valueOf(car.get("carTransmission Type")),
                        String.valueOf(car.get("carSpecification"))

                ));
                writer.append("\n");
            }

//            for (Map<String, Object> car : cars) {
//                writer.append(String.join(",",
//                        String.valueOf(car.get("CarName")),
//                        String.valueOf(car.get("CarSpecification")),
//                        String.valueOf(car.get("CarImage")),
//                        String.valueOf(car.get("CarPricePerDay")),
//                        String.valueOf(car.get("CarTotalPriceWithTax")),
//                        String.valueOf(car.get("CarPassengerCapacity")),
//                        String.valueOf(car.get("CarTransmissionType")),
//                        String.valueOf(car.get("Rating")),
//                        String.valueOf(car.get("PageDirectionLink"))
//                ));
//                writer.append("\n");
//            }

            System.out.println(" Csv file generate succefully and stored scraping data>>");
        } catch (IOException e) {
            System.err.println(" Error happens to create csv file: " + e.getMessage());
        }
    }

    public static void saveAdditionalDataCsv(List<String> rentalCompanies, List<String> destinations, List<String> carTypes, String filePath) {
        try (FileWriter writer = new FileWriter(filePath, true)) { // Open the file in append mode

            // Leave 2 blank lines
            writer.append("\n\n");

            // Write header
            writer.append("Category,Name\n");

            // Write data for rental companies
            for (String company : rentalCompanies) {
                writer.append("Car Rental Companies,").append(company).append("\n");
            }

            // Leave 2 blank lines
            writer.append("\n\n");

            // Write data for destinations
            for (String destination : destinations) {
                writer.append("Popular Destinations,").append(destination).append("\n");
            }

            // Leave 2 blank lines
            writer.append("\n\n");

            // Write data for car types
            for (String carType : carTypes) {
                writer.append("Popular Car Type,").append(carType).append("\n");
            }
            // Leave 2 blank lines
            writer.append("\n\n");
            System.out.println("Additional data appended to the CSV file successfully.");
        } catch (IOException e) {
            System.err.println("Error occurred while appending additional data to the CSV file: " + e.getMessage());
        }
    }


    // main functionality to call diffrent methods.
    public static void main(String[] args) {
        // Set the path to the ChromeDriver executable
        System.setProperty("webdriver.chrome.driver", "C:\\Users\\sanke\\OneDrive\\Desktop\\chromedriver-win64\\chromedriver-win64\\chromedriver.exe");
        // Initialize a new instance of ChromeDriver
        WebDriver driver = new ChromeDriver();

        // Open the website
        driver.get("https://www.carrentals.com/");

        // Use WebDriverWait to wait for elements to be present and interactable
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        Actions action = new Actions(driver);
        WebElement footer = driver.findElement(By.className("footer"));
        action.scrollToElement(footer).perform();
        try {
            Thread.sleep(1000);  // 1000 milliseconds = 1 second
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // started comment here
        // find car rental compnies

        // Find all list items containing rental company names
//        List<WebElement> rentalItems = driver.findElements(By.cssSelector("ul.uitk-layout-grid li a.uitk-link"));
//        List<String> rentalCompanies = new ArrayList<>();
//        List<String> destinations = new ArrayList<>();
//        List<String> carTypes = new ArrayList<>();

        // Initialize the JavaScript Executor
//        JavascriptExecutor js = (JavascriptExecutor) driver;

        // Iterate through each list item and print the rental company name
//        for (WebElement item : rentalItems) {
//
//            //  String innerHTML = item.getAttribute("innerHTML");
//            //  System.out.println("Inner HTML: " + innerHTML);
//
//            // Extract the text using JavaScript if necessary
//            String companyName = (String) js.executeScript("return arguments[0].textContent;", item);
//            System.out.println("Rental Company: " + companyName);
//            rentalCompanies.add(companyName);
//        }

// find  top destination ride
//        try {
//            // Find all list items containing destination names
//            List<WebElement> destinationItems = driver.findElements(By.cssSelector("ul.uitk-layout-grid li a.uitk-link"));
//
//            // Initialize the JavaScript Executor
//            JavascriptExecutor js1 = (JavascriptExecutor) driver;
//
//            // Iterate through each list item and print the destination name
//            for (WebElement item : destinationItems) {
//                // Extract the text using JavaScript to ensure accuracy
//                String destinationName = (String) js1.executeScript("return arguments[0].textContent;", item);
//                System.out.println("Destination: " + destinationName);
//                destinations.add(destinationName);
//            }
//        } finally {
//            // Close the browser
//            //driver.quit();
//        }
//
//        // find top popular car types
//        try {
//            // Find all list items containing car types
//            List<WebElement> carTypeItems = driver.findElements(By.cssSelector("ul.uitk-layout-grid li a.uitk-link"));
//
//            // Initialize the JavaScript Executor
//            JavascriptExecutor js2 = (JavascriptExecutor) driver;
//
//            // Iterate through each list item and print the car type
//            for (WebElement item : carTypeItems) {
//                // Extract the text using JavaScript to ensure accuracy
//                String carTypeName = (String) js2.executeScript("return arguments[0].textContent;", item);
//                carTypes.add(carTypeName);
//                System.out.println("Car Type: " + carTypeName);
//            }
//        } finally {
//            //Close the browser
//        }

        // scrooll till header
        Actions action2 = new Actions(driver);
        WebElement header = driver.findElement(By.tagName("header"));
        action.scrollToElement(header).perform();
        try {
            Thread.sleep(1000);  // 1000 milliseconds = 1 second
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Locate the "Pick-up" button and click it
        WebElement pickUpButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[@aria-label='Pick-up']")));
        pickUpButton.click();

        // Locate the input field and enter "Windsor"
        WebElement pickUpInput = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("location-field-locn")));
//        pickUpInput.sendKeys("Winnipeg");
//pickUpInput.sendKeys("Windsor");
//pickUpInput.sendKeys("Windsor");
pickUpInput.sendKeys("Calgary ");
//pickUpInput.sendKeys("Northwest Territories ");
//pickUpInput.sendKeys("kitchener");
//pickUpInput.sendKeys("ottawa");
//pickUpInput.sendKeys("Toronto");


        // here upper pass key which you want to search in website for your destination point

        // Wait for the dropdown to appear and select the first item
        WebElement firstDropdownItem = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[@data-stid='location-field-locn-result-item-button']")));
        firstDropdownItem.click();

        // Locate the "Search" button and click it
        WebElement searchButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[@data-testid='submit-button']")));
        searchButton.click();

        // Wait for the results to load (update with the actual class name or identifier for the results)
        try {
            Thread.sleep(8000);   // 6000 milliseconds = 6 seconds
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // this is functionality we use to scrooll to footer
        Actions action1 = new Actions(driver);
        WebElement footer1 = driver.findElement(By.className("footer"));
        action1.scrollToElement(footer1).perform();
        try {
            Thread.sleep(1000);  // 1000 milliseconds = 1 second
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Create an instance of MyFirst class
        CarDataScrapperRental finalCallScroller = new CarDataScrapperRental(driver);
        // Call the scrollAndClickShowMore method
        finalCallScroller.scrollAndClickShowMore();

//        // Save additional data to CSV
//        String additionalDataFilePath = "cars.csv";
//        saveAdditionalDataCsv(rentalCompanies, destinations, carTypes, additionalDataFilePath);

    }
}


