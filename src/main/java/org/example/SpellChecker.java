package org.example;

import java.io.*;
import java.util.*;

public class SpellChecker {

    // Class to build vocabulary from CSV
    public static class VocabularyBuilder {
        public static Set<String> buildVocabulary(String csvFilePath) {
            Set<String> vocabulary = new HashSet<>();
            try (BufferedReader br = new BufferedReader(new FileReader(csvFilePath))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] values = line.split(",");
                    if (values.length > 0) {
                        String carTitle = values[0].trim();
                        if (!carTitle.isEmpty()) {
                            String[] words = carTitle.split("\\s+");
                            vocabulary.addAll(Arrays.asList(words));
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return vocabulary;
        }
    }

    // Class for Cuckoo Hash Table
    public static class CuckooHashTable {
        private static final int TABLE_SIZE = 11; // Example size, adjust as needed
        private String[] table;
        private int numKeys;

        public CuckooHashTable() {
            table = new String[TABLE_SIZE];
            numKeys = 0;
        }

        private int hash1(String key) {
            return Math.abs(key.hashCode()) % TABLE_SIZE;
        }

        private int hash2(String key) {
            return (Math.abs(key.hashCode()) / TABLE_SIZE) % TABLE_SIZE;
        }

        public boolean insert(String key) {
            if (numKeys >= TABLE_SIZE) {
                return false; // Table is full
            }
            int pos1 = hash1(key);
            if (table[pos1] == null) {
                table[pos1] = key;
                numKeys++;
                return true;
            }
            String temp = table[pos1];
            table[pos1] = key;
            int pos2 = hash2(temp);
            if (table[pos2] == null) {
                table[pos2] = temp;
                numKeys++;
                return true;
            }
            return false; // Collision resolution failed
        }

        public boolean contains(String key) {
            return table[hash1(key)] != null && table[hash1(key)].equals(key) ||
                    table[hash2(key)] != null && table[hash2(key)].equals(key);
        }
    }

    // Class for Edit Distance calculation (Levenshtein Distance)
    public static class EditDistance {
        public static int levenshteinDistance(String a, String b) {
            int[][] dp = new int[a.length() + 1][b.length() + 1];

            for (int i = 0; i <= a.length(); i++) {
                for (int j = 0; j <= b.length(); j++) {
                    if (i == 0) {
                        dp[i][j] = j;
                    } else if (j == 0) {
                        dp[i][j] = i;
                    } else {
                        dp[i][j] = Math.min(dp[i - 1][j - 1] + (a.charAt(i - 1) == b.charAt(j - 1) ? 0 : 1),
                                Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1));
                    }
                }
            }
            return dp[a.length()][b.length()];
        }
    }

    // Class for Merge Sort
    public static class MergeSort {
        public static void mergeSort(List<String> list, Map<String, Integer> editDistances) {
            if (list.size() > 1) {
                List<String> left = new ArrayList<>(list.subList(0, list.size() / 2));
                List<String> right = new ArrayList<>(list.subList(list.size() / 2, list.size()));

                mergeSort(left, editDistances);
                mergeSort(right, editDistances);

                merge(list, left, right, editDistances);
            }
        }

        private static void merge(List<String> list, List<String> left, List<String> right, Map<String, Integer> editDistances) {
            int i = 0, j = 0, k = 0;

            while (i < left.size() && j < right.size()) {
                if (editDistances.get(left.get(i)) <= editDistances.get(right.get(j))) {
                    list.set(k++, left.get(i++));
                } else {
                    list.set(k++, right.get(j++));
                }
            }

            while (i < left.size()) {
                list.set(k++, left.get(i++));
            }

            while (j < right.size()) {
                list.set(k++, right.get(j++));
            }
        }
    }

    public static List<String> getSuggestionWord(String query) {
        String csvFilePath = "cars1.csv"; // Replace with actual CSV file path
        Set<String> vocabulary = VocabularyBuilder.buildVocabulary(csvFilePath);

        CuckooHashTable hashTable = new CuckooHashTable();
        for (String word : vocabulary) {
            hashTable.insert(word);
        }


        List<String> suggestions = new ArrayList<>();

        Map<String, Integer> editDistances = new HashMap<>();
        for (String word : vocabulary) {
            word = word.toLowerCase();
            int distance = EditDistance.levenshteinDistance(query, word);
            editDistances.put(word, distance);
            suggestions.add(word);
        }

        MergeSort.mergeSort(suggestions, editDistances);

        return suggestions;
    }

    public static void main(String[] args) {
        String csvFilePath = "cars1.csv"; // Replace with actual CSV file path
        Set<String> vocabulary = VocabularyBuilder.buildVocabulary(csvFilePath);

        CuckooHashTable hashTable = new CuckooHashTable();
        for (String word : vocabulary) {
            hashTable.insert(word);
        }

        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter a word to check: ");
        String input = scanner.nextLine();

        if (hashTable.contains(input)) {
            System.out.println("Word is correct.");
        } else {
            System.out.println("Word is incorrect. Suggestions:");
            List<String> suggestions = new ArrayList<>();
            Map<String, Integer> editDistances = new HashMap<>();
            for (String word : vocabulary) {
                int distance = EditDistance.levenshteinDistance(input, word);
                editDistances.put(word, distance);
                suggestions.add(word);
            }

            MergeSort.mergeSort(suggestions, editDistances);
            for (String suggestion : suggestions) {
                System.out.println(suggestion + " (Edit Distance: " + editDistances.get(suggestion) + ")");
            }
        }
    }
}
