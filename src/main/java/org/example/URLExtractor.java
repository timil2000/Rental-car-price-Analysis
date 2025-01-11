package org.example;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.ArrayList;
import java.util.List;

public class URLExtractor {

    // Method to extract URLs from a given text
    // This method takes a String as input and returns a list of URLs found within the text
    public static List<String> extractURLs(String text) {
        // List to store the found URLs
        // We use an ArrayList to store the URLs because it allows dynamic resizing and provides fast access
        List<String> urls = new ArrayList<>();

        // Regular expression to match URLs
        // This regex is designed to match both HTTP and HTTPS URLs, as well as URLs starting with "www."
        String urlPattern = "http[s]?://(?:[a-zA-Z]|[0-9]|[$-_@.&+]|[!*\\(\\),]|(?:%[0-9a-fA-F][0-9a-fA-F]))+|"
                + "www\\.(?:[a-zA-Z]|[0-9]|[$-_@.&+]|[!*\\(\\),]|(?:%[0-9a-fA-F][0-9a-fA-F]))+";

        // Compile the regular expression into a pattern
        // This step is necessary because it converts the string representation of the regex into a Pattern object
        Pattern pattern = Pattern.compile(urlPattern);

        // Create a matcher to find matches of the pattern in the text
        // The matcher will scan the text and try to find substrings that match the regex
        Matcher matcher = pattern.matcher(text);

        // Loop through all matches found in the text
        // The find() method of the Matcher class returns true if a substring matching the pattern is found
        while (matcher.find()) {
            // Add each found URL to the list
            // The group() method of the Matcher class returns the matched substring
            urls.add(matcher.group());
        }

        // Return the list of URLs
        // After extracting all the URLs, we return the list containing them
        return urls;
    }

    public static void main(String[] args) {
        // Path to the CSV file
        // This variable holds the path to the CSV file that we want to read
        String csvFile = "cars1.csv";

        // Try-with-resources statement to ensure the CSVReader is closed after use
        // The try-with-resources statement automatically closes the CSVReader after the try block is executed
        try (CSVReader reader = new CSVReader(new FileReader(csvFile))) {
            String[] nextLine;

            // Read each line of the CSV file
            // The readNext() method reads the next line from the CSV file and returns it as an array of strings
            while ((nextLine = reader.readNext()) != null) {
                // Process each cell in the current line
                // We loop through each cell in the current line and extract URLs from it
                for (String cell : nextLine) {
                    // Extract URLs from the cell text
                    // We call the extractURLs method to find all URLs in the current cell text
                    List<String> extractedURLs = extractURLs(cell);

                    // Print each extracted URL
                    // We loop through the list of extracted URLs and print each one to the console
                    for (String url : extractedURLs) {
                        System.out.println(url);
                    }
                }
            }
        } catch (IOException | CsvValidationException e) {
            // Print the stack trace if an exception occurs
            // If an IOException or CsvValidationException occurs, we print the stack trace for debugging purposes
            e.printStackTrace();
        }
    }
}
