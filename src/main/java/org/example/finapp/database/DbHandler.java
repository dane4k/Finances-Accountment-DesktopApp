package org.example.finapp.database;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.PieChart;
import org.example.finapp.models.TransactionItem;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DbHandler {
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/finances";
    private static final String DB_USER = "postgres";
    private static final String DB_PASSWORD = "admin";


    /**
     * создание подключения к БД
     * @return объект Connection
     * @throws SQLException исключение
     */
    public Connection getDbConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    /**
     * проверка на уникальность ника при регистрации
     * @param username никнейм
     * @return уникален или нет
     */
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

    /**
     * добавление категории в БД
     * @param category название категории
     * @param username имя пользователя
     * @throws SQLException исключение
     */
    public void addCategory(String category, String username) throws SQLException {
        String insert = "INSERT INTO categories (name, user_id) VALUES (?, ?)";

        try (Connection connection = getDbConnection()) {
            int userId = getUserIdByUsername(connection, username);

            if (userId != -1) {
                try (PreparedStatement insertStmt = connection.prepareStatement(insert)) {
                    insertStmt.setString(1, category);
                    insertStmt.setInt(2, userId);

                    insertStmt.executeUpdate();
                }
            } else {
                System.out.println("нет юзера " + username);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * получение id юзера по никнейму
     * @param connection объект Connection для подключения
     * @param username имя пользователя
     * @return id пользователя
     * @throws SQLException исключение
     */
    public int getUserIdByUsername(Connection connection, String username) throws SQLException {
        String getUserId = "SELECT id FROM users WHERE username = ?";
        try (PreparedStatement getUserIdStmt = connection.prepareStatement(getUserId)) {
            getUserIdStmt.setString(1, username);
            ResultSet resultSet = getUserIdStmt.executeQuery();

            if (resultSet.next()) {
                int userId = resultSet.getInt("id");
                return userId;
            } else {
                return -1;
            }
        }
    }

    /**
     * добавление пользователя в БД
     * @param username имя пользователя
     * @param password пароль
     */
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

    /**
     * проверка пароля через unhash
     * @param username имя пользователя
     * @param password пароль
     * @return совпадает ли пароль
     */
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

    /**
     * удаление категории из БД
     * @param category название категории
     * @param username имя пользователя во избежание удаления категории с user_id == null (общая категория)
     * @throws SQLException исключение
     */
    public void deleteCategory(String category, String username) throws SQLException {
        String delete = "DELETE FROM categories WHERE name = ? AND user_id = ? AND user_id IS NOT NULL";

        try (Connection connection = getDbConnection()) {
            int userId = getUserIdByUsername(connection, username);

            if (userId != -1) {
                try (PreparedStatement deleteStmt = connection.prepareStatement(delete)) {
                    deleteStmt.setString(1, category);
                    deleteStmt.setInt(2, userId);
                    int deletedRows = deleteStmt.executeUpdate();

                    if (deletedRows == 0) {
                        throw new SQLException("Категория не может быть удалена");
                    }
                }
            } else {
                System.out.println("нет юзера " + username);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * заполнение списка категориям прикрепленным к юзеру и общими
     * @param connection объект Connection для подключения
     * @param username никнейм
     * @param result список для заполнения
     * @return список категорий
     * @throws SQLException исключение
     */
    public List<String> fetchCategories(Connection connection, String username, List<String> result) throws SQLException {
        String getAllCategories = "SELECT name FROM categories WHERE user_id = ? OR user_id IS NULL";
        try {
            int userId = getUserIdByUsername(connection, username);
            if (userId != -1) {
                try (PreparedStatement getCategories = connection.prepareStatement(getAllCategories)) {
                    getCategories.setInt(1, userId);
                    ResultSet resultSet = getCategories.executeQuery();

                    while (resultSet.next()) {
                        String categoryName = resultSet.getString("name");
                        result.add(categoryName);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Добавление транзакции в БД
     * @param connection объект Connection для подключения
     * @param userId id пользователя
     * @param amount сумма транзакции
     * @param date дата транзакции
     * @param type тип транзакции (расход/доход)
     * @param categoryId id категории
     * @throws SQLException исключение
     */
    public void addTransaction(Connection connection, int userId, Double amount, LocalDate date, boolean type, int categoryId) throws SQLException {
        String insert = "INSERT INTO transactions (user_id, amount, date, income, category_id) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement prSt = connection.prepareStatement(insert)) {
            prSt.setInt(1, userId);
            prSt.setDouble(2, amount);
            prSt.setDate(3, Date.valueOf(date));
            prSt.setBoolean(4, type);
            prSt.setInt(5, categoryId);

            prSt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Получение id категории по названию
     * @param connection объект Connection для подключения
     * @param category название категории
     * @return id категории
     * @throws SQLException исключение
     */
    public int getCategoryIdByCategoryName(Connection connection, String category) throws SQLException {
        String getCategoryId = "SELECT id FROM categories WHERE name = ?";
        try (PreparedStatement getCategoryIdStmt = connection.prepareStatement(getCategoryId)) {
            getCategoryIdStmt.setString(1, category);
            ResultSet resultSet = getCategoryIdStmt.executeQuery();

            if (resultSet.next()) {
                return resultSet.getInt("id");
            } else {
                return -1;
            }
        }
    }


    /**
     * Заполнение списка транзакциями
     * @param connection объект Connection для подключения
     * @param username имя пользователя
     * @return список транзакций по юзернейму
     * @throws SQLException исключение
     */
    public List<TransactionItem> fetchTransactions(Connection connection, String username) throws SQLException {
        List<TransactionItem> transactions = new ArrayList<>();
        int userId;
        try {
            userId = getUserIdByUsername(connection, username);
        }
        catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

        String query = "SELECT t.id, t.amount, t.date, t.income, t.category_id, c.name AS category_name " +
                "FROM transactions t " +
                "LEFT JOIN categories c ON t.category_id = c.id " +
                "WHERE t.user_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                double amount = resultSet.getDouble("amount");
                LocalDate date = resultSet.getDate("date").toLocalDate();
                String type = resultSet.getBoolean("income") ? "Доход" : "Расход";
                int categoryId = resultSet.getInt("category_id");
                String categoryName = resultSet.getString("category_name");

                TransactionItem transaction = new TransactionItem(amount, date, type, categoryName);
                transactions.add(transaction);
            }
        } catch (SQLException e) {
            throw e;
        }

        return transactions;
    }

    /**
     * удаление транзакции из БД
     * @param connection объект Connection для подключения
     * @param username имя пользователя
     * @param transaction объект TransactionItem
     * @throws SQLException исключение
     */
    public void deleteTransaction(Connection connection, String username, TransactionItem transaction) throws SQLException {
        String deleteQuery = "DELETE FROM transactions WHERE user_id = ? AND amount = ? AND date = ? AND income = ?";

        int userId = getUserIdByUsername(connection, username);

        try (PreparedStatement statement = connection.prepareStatement(deleteQuery)) {
            statement.setInt(1, userId);
            statement.setDouble(2, transaction.getAmount());
            statement.setDate(3, Date.valueOf(transaction.getDate()));
            statement.setBoolean(4, transaction.getType().equals("Доход"));

            statement.executeUpdate();
        }
    }

    public ObservableList<PieChart.Data> getTransactionsData(Connection conn, boolean isIncome, int userID) throws SQLException {
        ObservableList<PieChart.Data> data = FXCollections.observableArrayList();

        String sql = "SELECT c.name AS category_name, SUM(t.amount) AS total_amount FROM transactions t JOIN categories c ON t.category_id = c.id " +
                "WHERE t.user_id = ? " +
                "  AND t.income = ? " +
                "  AND t.date >= ? " +
                "GROUP BY c.name";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, userID);
            pstmt.setBoolean(2, isIncome);
            pstmt.setDate(3, Date.valueOf(LocalDate.now().minusDays(30)));

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String categoryName = rs.getString("category_name");
                    double totalAmount = rs.getDouble("total_amount");
                    data.add(new PieChart.Data(categoryName, totalAmount));
                }
            }
        }

        return data;
    }


}
