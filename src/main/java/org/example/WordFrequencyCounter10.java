package org.example;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class WordFrequencyCounter10 {

    // Main method to run the program
    public static void main(String[] args) {
        String filePath = "cars1.csv";  // Path to the CSV file
        int topN = 10;  // Number of top frequent words to display
        List<Map.Entry<String, Integer>> topWords = processFile(filePath, topN);  // Process the file and get top words

        // Print the top N most frequent words
        System.out.println("Top " + topN + " most frequent words:");
        for (Map.Entry<String, Integer> entry : topWords) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }

    // Method to process the file and get the top N frequent words
    public static List<Map.Entry<String, Integer>> processFile(String filePath, int topN) {
        String text = readTextFromFile(filePath);  // Read the text from the file
        List<String> words = tokenize(text);  // Tokenize the text into words
        Map<String, Integer> wordCount = countWordFrequencies(words);  // Count word frequencies
        List<Map.Entry<String, Integer>> topWords = sortWordFrequencies(wordCount, topN);  // Sort and get top N words

        return topWords;  // Return the top N words
    }

    // Method to read text from a file
    public static String readTextFromFile(String filePath) {
        StringBuilder text = new StringBuilder();  // StringBuilder to accumulate the text
        try {
            // Check if the file has a .csv extension
            if (filePath.endsWith(".csv")) {
                List<String> lines = Files.readAllLines(Paths.get(filePath));  // Read all lines from the file
                for (String line : lines) {
                    text.append(line).append(" ");  // Append each line to the text, followed by a space
                }
            }
        } catch (IOException e) {
            e.printStackTrace();  // Print the stack trace if an IOException occurs
        }
        return text.toString();  // Convert StringBuilder to String and return
    }

    // Method to tokenize the text into a list of words
    public static List<String> tokenize(String text) {
        // Remove all non-alphanumeric characters and convert to lowercase
        text = text.replaceAll("[^a-zA-Z0-9\\s]", "").toLowerCase();
        return Arrays.asList(text.split("\\s+"));  // Split the text by whitespace and return as a list
    }

    // Method to count the frequencies of words in the list
    public static Map<String, Integer> countWordFrequencies(List<String> words) {
        Map<String, Integer> wordCount = new HashMap<>();  // Map to store word counts
        for (String word : words) {
            // Increment the count for the word in the map
            wordCount.put(word, wordCount.getOrDefault(word, 0) + 1);
        }
        return wordCount;  // Return the map of word counts
    }

    // Method to sort the word frequencies and get the top N words
    public static List<Map.Entry<String, Integer>> sortWordFrequencies(Map<String, Integer> wordCount, int topN) {
        // Priority queue (max heap) to sort the words by frequency in descending order
        PriorityQueue<Map.Entry<String, Integer>> maxHeap = new PriorityQueue<>(
                (e1, e2) -> e2.getValue().compareTo(e1.getValue()));

        maxHeap.addAll(wordCount.entrySet());  // Add all word count entries to the heap

        List<Map.Entry<String, Integer>> sortedEntries = new ArrayList<>();  // List to store the sorted entries
        for (int i = 0; i < topN && !maxHeap.isEmpty(); i++) {
            sortedEntries.add(maxHeap.poll());  // Poll the top entry from the heap and add to the list
        }

        return sortedEntries;  // Return the list of top N words
    }
}
