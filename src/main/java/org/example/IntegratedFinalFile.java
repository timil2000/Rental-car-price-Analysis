package org.example;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.example.Main.shouldScrapping;
import static org.example.SearchQuery.getSearchCount;
import static org.example.SpellChecker.getSuggestionWord;
import static org.example.WordCompletionTries.getWordsWithPrefix;
import static org.example.WordFrequencyInFile.countOccurrences;

public class IntegratedFinalFile {
    private static Scanner scanner = new Scanner(System.in);
    private static org.example.UserManager userManager = new org.example.UserManager();
    private static final Pattern INVALID_INPUT_PATTERN = Pattern.compile("[^a-zA-Z0-9 ]"); // Regex to detect special characters
    private static final int MAX_LINK_LENGTH = 20;
    private static final Map<Integer, String> linkMap = new HashMap<>();

    public static void main(String[] args) throws InterruptedException {
        printWelcomeMessage();
        while (true) {
            System.out.print("Enter 'login' to log in, 'signup' to sign up, or 'exit' to quit: ");
            String action = getInput().trim().toLowerCase();

            if (action.equals("exit")) {
                break;
            }

            if (action.equals("signup")) {
                handleSignup();
            } else if (action.equals("login")) {
                handleLogin();
            } else {
                System.out.println("Invalid option. Please enter 'login', 'signup', or 'exit'.");
            }
        }

        scanner.close();
    }

    private static void printWelcomeMessage() {
        System.out.println("***************************************");
        System.out.println("*                                     *");
        System.out.println("*       Welcome to Lancer Cars!       *");
        System.out.println("*                                     *");
        System.out.println("***************************************");
        System.out.println("*                                     *");
        System.out.println("*   Your trusted car rental service   *");
        System.out.println("*                                     *");
        System.out.println("***************************************");
        System.out.println();
    }

    private static void handleSignup() {
        String username;
        String password;

        while (true) {
            System.out.print("Enter username (email): ");
            username = getInput().trim();
            if (!userManager.isEmailValid(username)) {
                System.out.println("The username must be a valid email address. Please try again.");
            } else {
                break;
            }
        }

        while (true) {
            System.out.print("Enter password: ");
            password = getInput().trim();
            if (!userManager.isPasswordStrong(password)) {
                System.out.println("Your password is not strong enough. Please follow these guidelines:");
                System.out.println("1. At least 8 characters long");
                System.out.println("2. Includes both upper and lower case letters");
                System.out.println("3. Includes at least one digit");
                System.out.println("4. Includes at least one special character (e.g., !@#$%^&*)");
            } else if (userManager.isPasswordUsed(password)) {
                System.out.println("This password has been used before. Please choose a different password.");
            } else {
                break;
            }
        }

        boolean success = userManager.signup(username, password);
        if (success) {
            System.out.println("Signup successful.");
        } else {
            System.out.println("Signup failed.");
        }
    }

    private static void handleLogin() throws InterruptedException {
        System.out.print("Enter username (email): ");
        String username = getInput().trim();
        System.out.print("Enter password: ");
        String password = getInput().trim();

        boolean success = userManager.login(username, password);
        if (success) {
            System.out.println("Login successful.");
            handleSearchAndSuggestions();
        } else {
            System.out.println("Login failed.");
        }
    }

    // Method to restrict pure numbers in input
    public static boolean isPureNumber(String input) {
        for (char c : input.toCharArray()) {
            if (!Character.isDigit(c)) {
                return false;
            }
        }
        return true;
    }

    private static void handleSearchAndSuggestions() throws InterruptedException {
        while (true) {
            System.out.print("Do you want to perform the web scraping? (yes/no): ");
            String n = scanner.nextLine().trim().toLowerCase();
            if (n.equals("yes")) {
                shouldScrapping();
                break;
            } else if (n.equals("no")) {
                System.out.println("Scraping Skipped");
                break;
            } else {
                System.out.println("Invalid option. Please enter 'yes' or 'no'.");
            }
        }
        while (true) {
            System.out.print("Search a car (or write exit): ");
            String query = getInput().trim().toLowerCase();

            if (query.equals("exit")) {
                break;
            }

            if (query.isEmpty() || containsSpecialCharacters(query) || isPureNumber(query)) {
                System.out.println("Invalid input. Please do not use special characters or pure numbers.");
                continue; // Prompt the user again
            }

            //AYUSH task Search frequency
            int frequency = getSearchCount(query); // Assuming this method is implemented elsewhere
            System.out.println("This query is searched for " + frequency + " times.");

            //SMIT task -word search in file
            int wordInFileFreq = countOccurrences(query);
            System.out.println("This word occurred " + wordInFileFreq + " times in CSV file.");

            // SANKET TASK - Word complition Provide word suggestions for all valid inputs
            List<String> results = getWordsWithPrefix(query);
            if (results.size() >= 1) {
                System.out.println("You can complete your word with the use of this \nWord Suggestions: ");
                for (String result : results) {
                    System.out.println(result);
                }

            }

            //TIMIL TASK
            // Perform spell check only if query has more than 3 letters
            if (query.length() > 3) {
                List<String> suggestions = getSuggestionWord(query); // Assuming this method is implemented elsewhere

                if (suggestions.isEmpty()) {
                    System.out.println("No suggestions available.");
                } else if (query.equals(suggestions.get(0))) {
                    System.out.println("Word is Correct");

                    //Gravin's task - page ranking
                    org.example.PageRanking pageRanking = new org.example.PageRanking(); // Adjust as necessary to fit your setup
                    List<org.example.Product> topProducts = pageRanking.getTopItems("cars1.csv", query);

                    System.out.println("Top 10 Products:");
                    printProductsTable(topProducts);


                    //SMIT task - rating based sorting
                    String choice = "";
                    while(true) {
                        System.out.println("\nPress 1 to sort by rating or 2 to view the full list of products:");
                        choice = getInput().trim();
                        if(choice.equals("1") || choice.equals("2")) {
                            break;
                        } else {
                            System.out.println("Invalid option. Try Again");
                        }
                    }
                    if (choice.equals("1")) {
                        List<Product> allProducts = pageRanking.readProductData("cars1.csv");
                        String finalQuery = query;
                        List<Product> filteredProducts = allProducts.stream()
                                .filter(product -> product.name.toLowerCase().contains(finalQuery))
                                .collect(Collectors.toList());

                        // Using Quick Sort to sort the products by rating
                        quickSort(filteredProducts, 0, filteredProducts.size() - 1);

                        System.out.println("Products sorted by rating:");
                        printProductsTable(filteredProducts);
                    } else if (choice.equals("2")) {
                        List<Product> allProducts = pageRanking.readProductData("cars1.csv");
                        String finalQuery1 = query;
                        List<Product> filteredProducts = allProducts.stream()
                                .filter(product -> product.name.toLowerCase().contains(finalQuery1))
                                .collect(Collectors.toList());
                        System.out.println("Full list of products:");
                        printProductsTable(filteredProducts);
                    } else {
                        System.out.println("Invalid option.");
                    }
                } else {
                    System.out.println("Spelling is Wrong. Did you mean " + suggestions.get(0) + "?");
                    // No need to display top 10 products if the word is incorrect
                }
            }
        }
    }

    private static void printProductsTable(List<Product> products) {
        System.out.println("+------------------------------------------------------------------------------+");
        for (Product product : products) {
            System.out.println("Name of the car: " + product.name);
            System.out.println("Image Link: " + product.ImgLink);
            System.out.println("Product Link: " + product.link);
            System.out.println("Price per day: " + product.price);
            System.out.println("Rating: " + product.rating);
            System.out.println("passengers limit: "+ product.carPassenger);
            System.out.println("Car Specification: "+ product.carSpecification);
            System.out.println("Car Transmission type: "+ product.carTransmissionType);
            System.out.println("+---------------------------------------------------------------------------+");
        }
    }

    //quick sort implementation
    private static void quickSort(List<Product> products, int low, int high) {
        if (low < high) {
            int pi = partition(products, low, high);

            quickSort(products, low, pi - 1);
            quickSort(products, pi + 1, high);
        }
    }

    private static int partition(List<Product> products, int low, int high) {
        Product pivot = products.get(high);
        double pivotRating = convertRatingToDouble(pivot.rating); // Convert pivot rating to double
        int i = (low - 1);
        for (int j = low; j < high; j++) {
            double currentRating = convertRatingToDouble(products.get(j).rating); // Convert current rating to double
            if (currentRating >= pivotRating) {
                i++;

                // Swap products[i] and products[j]
                Product temp = products.get(i);
                products.set(i, products.get(j));
                products.set(j, temp);
            }
        }

        // Swap products[i+1] and products[high] (or pivot)
        Product temp = products.get(i + 1);
        products.set(i + 1, products.get(high));
        products.set(high, temp);

        return i + 1;
    }

    /**
     * Convert a rating string to a double for comparison.
     * Assumes that the string can be parsed to a double. Handle cases where parsing might fail if necessary.
     */
    private static double convertRatingToDouble(String rating) {
        try {
            return Double.parseDouble(rating);
        } catch (NumberFormatException e) {
            // Handle the case where the rating string is not a valid number
            // For now, return a default value (e.g., 0.0) or throw an exception
            System.err.println("Invalid rating format: " + rating);
            return 0.0; // Or handle as appropriate
        }
    }

    private static boolean containsSpecialCharacters(String input) {
        return INVALID_INPUT_PATTERN.matcher(input).find();
    }

    private static String getInput() {
        try {
            return scanner.nextLine();
        } catch (NoSuchElementException e) {
            // Handle the case where no input is available
            System.out.println("Input error. Exiting..."+e);
            System.exit(1);
            return ""; // This line is unreachable but required for compilation
        }
    }
}
