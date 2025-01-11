package org.example;

import java.sql.*;

public class UserManager {
    private static final String DB_URL = "jdbc:sqlite:users.db"; // Path to your SQLite database
    private static final String USERNAME_EXIST_MSG = "Username already exists.";
    private static final String EMAIL_INVALID_MSG = "Invalid email address.";
    private static final String PASSWORD_WEAK_MSG = "Password does not meet strength requirements.";
    private static final String PASSWORD_USED_MSG = "Password has been used before.";

    private Connection connection;

    public UserManager() {
        try {
            // Initialize the database connection
            connection = DriverManager.getConnection(DB_URL);
            // Create users table if it does not exist
            try (Statement stmt = connection.createStatement()) {
                String createTableSQL = "CREATE TABLE IF NOT EXISTS users (" +
                        "username TEXT PRIMARY KEY, " +
                        "password TEXT NOT NULL)";
                stmt.execute(createTableSQL);
            }
        } catch (SQLException e) {
            System.err.println("Error connecting to database: " + e.getMessage());
        }
    }

    public boolean isEmailValid(String email) {
        // Simple validation for email format
        return email != null && email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$");
    }

    public boolean isPasswordStrong(String password) {
        // Simple validation for password strength
        return password != null && password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*]).{8,}$");
    }

    public boolean isPasswordUsed(String password) {
        String sql = "SELECT COUNT(*) FROM users WHERE password = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, password);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("SQL error: " + e.getMessage());
        }
        return false;
    }

    public boolean isUsernameExists(String username) {
        String sql = "SELECT COUNT(*) FROM users WHERE username = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("SQL error: " + e.getMessage());
        }
        return false;
    }

    public boolean signup(String username, String password) {
        if (!isEmailValid(username)) {
            System.out.println(EMAIL_INVALID_MSG);
            return false;
        }

        if (isUsernameExists(username)) {
            System.out.println(USERNAME_EXIST_MSG);
            return false;
        }

        if (!isPasswordStrong(password)) {
            System.out.println(PASSWORD_WEAK_MSG);
            return false;
        }

        if (isPasswordUsed(password)) {
            return false;
        }//            System.out.println(PASSWORD_USED_MSG);


        String sql = "INSERT INTO users (username, password) VALUES (?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("SQL error: " + e.getMessage());
        }
        return false;
    }

    public boolean login(String username, String password) {
        String sql = "SELECT password FROM users WHERE username = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String storedPassword = rs.getString("password");
                return storedPassword.equals(password);
            }
        } catch (SQLException e) {
            System.err.println("SQL error: " + e.getMessage());
        }
        return false;
    }
}
