import java.sql.*;

public class AuthService {

    public User authenticate(String username, String password) throws SQLException {
        String sql = "SELECT id, username, password, email, phone, telegram_chat_id, is_admin " +
                "FROM users WHERE username = ?";

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next() && resultSet.getString("password").equals(password)) {
                return new User(
                        resultSet.getInt("id"),
                        resultSet.getString("username"),
                        resultSet.getString("password"),
                        resultSet.getString("email"),
                        resultSet.getString("phone"),
                        resultSet.getString("telegram_chat_id"),
                        resultSet.getBoolean("is_admin")
                );
            }
        }

        return null;
    }

    public boolean register(String username, String password,
                            String email, String phone, String telegramChatId,
                            boolean isAdmin, User creator) throws SQLException {

        if (creator != null && isAdmin && !creator.isAdmin()) {
            return false;
        }

        String sql = "INSERT INTO users (username, password, email, phone, telegram_chat_id, is_admin) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            preparedStatement.setString(3, email);
            preparedStatement.setString(4, phone);
            preparedStatement.setString(5, telegramChatId);
            preparedStatement.setBoolean(6, isAdmin);

            return preparedStatement.executeUpdate() > 0;
        }
    }

    public int getUserId(String username) throws SQLException {
        String sql = "SELECT id FROM users WHERE username = ?";

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();

            return resultSet.next() ? resultSet.getInt("id") : -1;
        }
    }
}