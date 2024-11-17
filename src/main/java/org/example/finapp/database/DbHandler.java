package org.example.finapp.database;


import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;

public class DbHandler {
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/finances";
    private static final String DB_USER = "postgres";
    private static final String DB_PASSWORD = "admin";

    public Connection getDbConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }


    public boolean isUsernameUnique(String username) {
        String select = "SELECT COUNT(*) FROM users WHERE username = ?";

        try (Connection connection = getDbConnection();
             PreparedStatement prSt = connection.prepareStatement(select)) {

            prSt.setString(1, username);
            try (ResultSet resultSet = prSt.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1) == 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public void addUser(String username, String password) {
        String insert = "INSERT INTO users (username, password) VALUES (?, ?)";

        String encryptedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

        try (Connection connection = getDbConnection();
             PreparedStatement prSt = connection.prepareStatement(insert)) {

            prSt.setString(1, username);
            prSt.setString(2, encryptedPassword);
            prSt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean checkPassword(String username, String password) {
        String select = "SELECT password FROM users WHERE username = ?";

        try (Connection connection = getDbConnection();
             PreparedStatement prSt = connection.prepareStatement(select)) {

            prSt.setString(1, username);

            try (ResultSet resultSet = prSt.executeQuery()) {
                if (resultSet.next()) {
                    String storedPassword = resultSet.getString("password");

                    return BCrypt.checkpw(password, storedPassword);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }


}
