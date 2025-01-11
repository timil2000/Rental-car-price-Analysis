package org.example; // Package declaration for the class file

import com.opencsv.CSVReader; // Importing package for reading CSV files
import com.opencsv.exceptions.CsvValidationException;

import java.io.FileReader; // Importing package for reading files
import java.io.IOException; // Importing package for handling IO exceptions
import java.util.*; // Importing package for various utilities

// Class for implementing AVL Tree
class AVLTree {
    // Node class represents each node in the AVL Tree
    class Node {
        String key; // Key stored in the node
        int height; // Height of the node in the tree
        int frequency; // Frequency count of the key in the tree
        Node left, right; // References to left and right child nodes

        // Constructor to initialize a node with a key
        Node(String d) {
            key = d.toLowerCase(); // Assign the key in lower case
            height = 1; // Initial height of a new node is 1
            frequency = 1; // Initial frequency of a new key is 1
        }
    }

    Node root; // Root of the AVL Tree

    // Method to obtain the height of a node
    int height(Node N) {
        if (N == null)
            return 0; // Return 0 for null nodes
        return N.height; // Return the height of the node
    }

    // Method to find the maximum of two integers
    int max(int a, int b) {
        return (a > b) ? a : b; // Return the larger of the two integers
    }

    // Method to perform a right rotation on a given node y
    Node rightRotate(Node y) {
        Node x = y.left; // Store y's left child as x
        Node T2 = x.right; // Store x's right child as T2
        // Perform rotation
        x.right = y; // Make y the right child of x
        y.left = T2; // Make T2 the left child of y
        // Update heights
        y.height = max(height(y.left), height(y.right)) + 1; // Update y's height
        x.height = max(height(x.left), height(x.right)) + 1; // Update x's height
        // Return the new root
        return x; // Return x as the new root of the rotated subtree
    }

    // Method to perform a left rotation on a given node x
    Node leftRotate(Node x) {
        Node y = x.right; // Store x's right child as y
        Node T2 = y.left; // Store y's left child as T2
        // Perform rotation
        y.left = x; // Make x the left child of y
        x.right = T2; // Make T2 the right child of x
        // Update heights
        x.height = max(height(x.left), height(x.right)) + 1; // Update x's height
        y.height = max(height(y.left), height(y.right)) + 1; // Update y's height
        // Return the new root
        return y; // Return y as the new root of the rotated subtree
    }

    // Method to obtain the balance factor of a node N
    int getBalance(Node N) {
        if (N == null)
            return 0; // Return 0 if node is null
        return height(N.left) - height(N.right); // Return balance factor of the node
    }

    // Method to insert a key into the AVL Tree
    Node insert(Node node, String key) {
        key = key.toLowerCase(); // Convert key to lower case before inserting
        // If the tree is empty, return a new node with the key
        if (node == null)
            return (new Node(key));

        // Otherwise, recur down the tree
        if (key.compareTo(node.key) < 0)
            node.left = insert(node.left, key); // Insert into the left subtree
        else if (key.compareTo(node.key) > 0)
            node.right = insert(node.right, key); // Insert into the right subtree
        else { // If key already exists in the tree, increment its frequency
            node.frequency++; // Increment frequency count
            return node; // Return the updated node
        }

        // Update the height of the current node
        node.height = 1 + max(height(node.left), height(node.right));

        // Get the balance factor of this node to check if it became unbalanced
        int balance = getBalance(node);

        // If the node becomes unbalanced, there are four possible cases:

        // Left Left Case: Perform a right rotation
        if (balance > 1 && key.compareTo(node.left.key) < 0)
            return rightRotate(node);

        // Right Right Case: Perform a left rotation
        if (balance < -1 && key.compareTo(node.right.key) > 0)
            return leftRotate(node);

        // Left Right Case: Perform left rotation on left child followed by right rotation on node
        if (balance > 1 && key.compareTo(node.left.key) > 0) {
            node.left = leftRotate(node.left);
            return rightRotate(node);
        }

        // Right Left Case: Perform right rotation on right child followed by left rotation on node
        if (balance < -1 && key.compareTo(node.right.key) < 0) {
            node.right = rightRotate(node.right);
            return leftRotate(node);
        }

        // Return the (unchanged) node pointer
        return node;
    }

    // Method to perform in-order traversal of the AVL Tree and populate a result map
    void inOrder(Node node, Map<String, Integer> result) {
        if (node != null) {
            // Recursively traverse the left subtree
            inOrder(node.left, result);
            // Store the key and its frequency in the result map
            result.put(node.key, node.frequency);
            // Recursively traverse the right subtree
            inOrder(node.right, result);
        }
    }

    // Public method to insert a key into the AVL Tree
    public void insert(String key) {
        root = insert(root, key); // Call the private insert method to insert the key
    }

    // Public method to get an in-order traversal result of the AVL Tree as a sorted map
    public Map<String, Integer> inOrder() {
        Map<String, Integer> result = new TreeMap<>(); // TreeMap to store keys in sorted order
        inOrder(root, result); // Perform in-order traversal starting from the root
        return result; // Return the sorted map of keys and frequencies
    }

    // Public method to find the frequency of a specific key in the AVL Tree
    public int findFrequency(String key) {
        Node node = root; // Start searching from the root of the AVL Tree
        key = key.toLowerCase(); // Convert key to lower case before searching
        // Traverse the tree to find the node with the given key
        while (node != null) {
            int cmp = key.compareTo(node.key); // Compare the key with the current node's key
            if (cmp < 0) {
                node = node.left; // Move to the left child if key is smaller
            } else if (cmp > 0) {
                node = node.right; // Move to the right child if key is larger
            } else { // If key is found, return its frequency
                return node.frequency; // Return the frequency of the key
            }
        }
        // If key is not found, return 0
        return 0; // Return 0 indicating key not found
    }
}

// Class to access the CSV file and perform operations based on user input
public class SearchQuery {
    private static AVLTree avlTree = new AVLTree(); // Instance of AVLTree for storing search queries
    private static List<String[]> data = new ArrayList<>(); // List to store data from the CSV file
    private static HashMap<String, Integer> searchCounts = new HashMap<>(); // HashMap to store search query counts

    static {
        // Attempt to find and access the CSV file from the specified location
        try (CSVReader reader = new CSVReader(new FileReader("cars1.csv"))) {
            String[] nextLine;
            reader.readNext(); // Skip the header row
            while ((nextLine = reader.readNext()) != null) {
                data.add(nextLine); // Add the row data to the list
                for (String value : nextLine) {
                    avlTree.insert(value.trim().toLowerCase()); // Insert data into the AVLTree in lower case for search tracking
                }
            }
        } catch (IOException | CsvValidationException e) {
            e.printStackTrace(); // Print stack trace if an IO exception occurs
        }
    }

    // Public method to get the search count for a given query
    public static int getSearchCount(String query) {
        query = query.toLowerCase().trim(); // Convert query to lower case and trim spaces
        boolean found = false; // Flag to track if any matching data is found in the CSV file
        // Iterate through each entry in the data list to find matches for the query
        for (String[] entry : data) {
            // Check if any cell in the current entry contains the search query (case-insensitive)
            String finalQuery = query;
            if (Arrays.stream(entry).anyMatch(s -> s.toLowerCase().contains(finalQuery))) {
                found = true; // Set found flag to true if match is found
                break; // Exit loop early since match is found
            }
        }

        // If matching data is found, update the AVLTree and searchCounts
        if (found) {
            avlTree.insert(query); // Insert the search query into the AVLTree for frequency tracking
            searchCounts.put(query, searchCounts.getOrDefault(query, 0) + 1); // Update search count for the query
            return avlTree.findFrequency(query); // Return the frequency count of the search query
        } else {
            return 0; // Return 0 if no matching data is found
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in); // Scanner object to allow user input
        // Continuous loop to handle search queries until user chooses to exit
        while (true) {
            System.out.println("Enter a search query (or type 'exit' to quit):");
            String query = scanner.nextLine().trim().toLowerCase(); // Read and trim user input, convert to lower case
            if (query.equalsIgnoreCase("exit")) {
                break; // Exit loop if user enters "exit"
            }
            if(!query.isEmpty()) {
                int frequency = getSearchCount(query); // Get search count for the query
                if (frequency > 0) {
                    // Print separator line and display search term and its frequency
                    System.out.println("-----------------------------");
                    System.out.println("Search Term: " + query); // Display the search term entered by the user
                    System.out.println("Count: " + frequency); // Display the frequency count of the search term
                    System.out.println("-----------------------------");

                    // Display previous search counts during the current session
                    System.out.println("Previous Searches:");
                    for (Map.Entry<String, Integer> entry : searchCounts.entrySet()) {
                        System.out.println(entry.getKey() + ": " + entry.getValue()); // Print previous search queries and their counts
                    }
                    System.out.println("-----------------------------");
                } else {
                    System.out.println("No data available"); // Print message if no matching data is found
                }
            } else {
                System.out.println("Enter Valid Search Query");
                System.out.println();
            }
        }
        scanner.close(); // Close the scanner object after terminating the loop
    }
}
