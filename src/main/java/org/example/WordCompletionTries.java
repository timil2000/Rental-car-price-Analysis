package org.example;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WordCompletionTries {

    // Every node in the Trie is represented by the TrieNode class.
    static class TrieNode {
        Map<Character, TrieNode> children; // Map for storing offspring nodes
        boolean isEndOfWord; // Symbol to indicate the end of a word

        // Initialize the constructor TrieNode
        public TrieNode() {
            this.children = new HashMap<>(); // Set up the children's map initially.
            this.isEndOfWord = false; // Set the end of word flag to its initial value.
        }
    }

    // The Trie data structure is represented by the Trie class.
    static class Trie {
        private TrieNode root; // the Trie's root node

        // Initialize the constructor Attempt using the root node
        public Trie() {
            this.root = new TrieNode(); // Set up the Trie's root node initially.
        }

        // Technique for adding a word to the Trie
        public void insert(String word) {
            TrieNode current = root; // Commence at the base
            word = word.toUpperCase(); // Change to capital letters due to case insensitivity

            // Go over every character in the phrase.
            for (int i = 0; i < word.length(); i++) {
                char ch = word.charAt(i); // Obtain the current persona.
                TrieNode node = current.children.get(ch); // Verify whether the character is a child node.

                // Make a new node for the character if it does not already exist.
                if (node == null) {
                    node = new TrieNode();
                    current.children.put(ch, node); // Insert a new node in the kids' map
                }
                current = node; // Proceed to the following node.
            }
            current.isEndOfWord = true; // Declare the final node to be the word's conclusion.
        }

        // How to look up every word in the Trie that begins with a specific prefix
        public List<String> searchPrefix(String prefix) {
            List<String> results = new ArrayList<>(); // List to store matching words
            TrieNode current = root; // Start from the root

            prefix = prefix.toUpperCase(); // Prefix should be changed to uppercase for case insensitivity.

            // Go over every character in the prefix.
            for (int i = 0; i < prefix.length(); i++) {
                char ch = prefix.charAt(i); // Obtain the current persona.
                TrieNode node = current.children.get(ch); // Verify whether the character is a child node.

                // Return empty results if the character does not exist (no words match the prefix).
                if (node == null) {
                    return results;
                }
                current = node; // Proceed to the following node.
            }

            // Presently, current is pointing to the node that represents the prefix's final character.
            findAllWords(current, prefix, results); // Look up every word that begins with this node.
            return results; // Provide the list of words that match.
        }

        // Recursive approach to locate all words starting from a given node with an aid
        private void findAllWords(TrieNode node, String prefix, List<String> results) {
            // Include the node in the results list if it indicates the end of a term.
            if (node.isEndOfWord) {
                results.add(prefix);
            }
            // Go through each child node recursively
            for (char ch : node.children.keySet()) {
                findAllWords(node.children.get(ch), prefix + ch, results);
            }
        }
    }

    // Method to load data from the CSV file into the Trie
    private static Trie loadTrieFromCSV(String csvFile) {
        Trie trie = new Trie(); // Initialize a new Trie
        String line = "";
        String cvsSplitBy = ",";

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            // Read each line from the CSV file
            while ((line = br.readLine()) != null) {
                String[] data = line.split(cvsSplitBy); // Split the line by commas
                if (data.length > 0) {
                    String carModel = data[0].trim(); // Obtain the model of the car (assuming it is the first column).
                    trie.insert(carModel); // Put the automobile model into the Trie
                }
            }
        } catch (IOException e) {
            e.printStackTrace(); // For any IO exceptions, print the stack trace.
        }

        return trie;
    }

    // Method to get the list of words with a given prefix
    public static List<String> getWordsWithPrefix(String prefix) {
        String csvFile = "cars1.csv";
        Trie trie = loadTrieFromCSV(csvFile); // Load data into the Trie
        return trie.searchPrefix(prefix); // Get the list of words with the given prefix
    }

    // The primary way to use the word completion program
    public static void main(String[] args) {
        // Set up a console reader to accept input from users.
        BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));

        while (true) {
            try {
                System.out.print("Enter prefix (type 'exit' to quit): ");
                String prefix = consoleReader.readLine().trim(); // Check for prefixes in user input.

                // 'exit' is typed by the user to quit the condition
                if (prefix.equalsIgnoreCase("exit")) {
                    break;
                }

                // Get the list of words with the given prefix
                List<String> results = getWordsWithPrefix(prefix);

                // Display the results
                if (results.isEmpty()) {
                    System.out.println("No words found with prefix '" + prefix + "'");
                } else {
                    System.out.println("Words found with prefix '" + prefix + "':");
                    for (String result : results) {
                        System.out.println(result); // Print each word found
                    }
                }
            } catch (IOException e) {
                e.printStackTrace(); // For any IO exceptions, print the stack trace.
            }
        }
    }
}
