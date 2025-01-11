package org.example;

public class User {
    private String username;
    private String password; // In a real application, passwords should be hashed

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
