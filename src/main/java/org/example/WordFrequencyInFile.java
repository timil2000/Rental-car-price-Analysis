package org.example;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public class WordFrequencyInFile {

    // Function to preprocess the pattern and prepare bad character heuristic array
    private static void badCharHeuristic(String pattern, int[] badChar) {
        int m = pattern.length();

        // Initialize all occurrences as -1
        for (int i = 0; i < 256; i++) {
            badChar[i] = -1;
        }

        // Fill the actual value of last occurrence of a character
        for (int i = 0; i < m; i++) {
            badChar[(int) pattern.charAt(i)] = i;
        }
    }

    /**
     * Boyer-Moore algorithm for pattern searching.
     *
     * @param text The text to search within.
     * @param pattern The pattern (word) to search for.
     * @return The number of occurrences of the pattern in the text.
     */
    static int boyerMooreSearch(String text, String pattern) {
        int m = pattern.length();
        int n = text.length();
        int[] badChar = new int[256]; // Array to store bad character heuristic

        // Fill the bad character array using preprocessing function
        badCharHeuristic(pattern, badChar);

        int s = 0;  // Shift of the pattern with respect to text
        int count = 0; // Count of occurrences

        // Perform pattern searching in text
        while (s <= (n - m)) {
            int j = m - 1;

            // Reduce index j of pattern while characters match
            while (j >= 0 && pattern.charAt(j) == text.charAt(s + j)) {
                j--;
            }

            // If pattern is found, increment occurrence count
            if (j < 0) {
                count++;
                // Shift pattern based on bad character heuristic
                s += (s + m < n) ? m - badChar[text.charAt(s + m)] : 1;
            } else {
                // Shift pattern based on bad character heuristic
                s += Math.max(1, j - badChar[text.charAt(s + j)]);
            }
        }

        return count;
    }

    static String readCSV(String filePath) {
        StringBuilder content = new StringBuilder(); // StringBuilder to store file content
        String line;

        // Read file line by line and append to content StringBuilder
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            while ((line = br.readLine()) != null) {
                content.append(line).append("\n");
            }
        } catch (IOException e) {
            System.err.println("Error reading the file: " + e.getMessage());
        }

        return content.toString().toLowerCase(); // Convert content to lower case
    }

    public static int countOccurrences(String word) {
        String filePath = "cars1.csv";
        String content = readCSV(filePath); // Read CSV file and get content as string

        // Handle empty file or read error
        if (content.isEmpty()) {
            System.out.println("The file is empty or could not be read.");
            return 0;
        }

        return boyerMooreSearch(content, word.toLowerCase()); // Convert word to lower case and count occurrences
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String filePath = "cars1.csv"; // File path of the CSV file

        boolean continueChecking = true;
        while (continueChecking) {
            // Prompt user to enter word to search in CSV file
            System.out.print("Enter the word to search: ");
            String word = scanner.nextLine();

            // Validate user input
            if (word == null || word.trim().isEmpty()) {
                System.out.println("Invalid input. Please enter a non-empty word.");
                continue;
            }

            // Count occurrences of word in content using Boyer-Moore algorithm
            int occurrences = countOccurrences(word);

            // Print number of occurrences found
            if (occurrences > 0) {
                System.out.println("The word \"" + word + "\" occurs " + occurrences + " times in the file.");
            } else {
                System.out.println("The word \"" + word + "\" was not found in the file.");
            }

            // Prompt user to check another word or exit
            boolean validResponse = false;
            while (!validResponse) {
                System.out.print("Do you want to check another word's count? (yes/no): ");
                String response = scanner.nextLine().trim().toLowerCase();

                try {
                    if (response.equals("yes")) {
                        validResponse = true;
                    } else if (response.equals("no")) {
                        validResponse = true;
                        continueChecking = false;
                    } else {
                        throw new IllegalArgumentException("Invalid input. Please enter 'yes' or 'no'.");
                    }
                } catch (IllegalArgumentException e) {
                    System.out.println(e.getMessage());
                }
            }
        }
        scanner.close();
    }
}
