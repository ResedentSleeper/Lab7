package db;

import java.sql.*;

/**
 * Класс для установки соединения с Базой Данных
 */
public class DB {
    public Connection connection = null;
    private String url = "jdbc:postgresql://%s:%d/%s";


    public DB(String _host, int _port, String _dbName) {
        url = String.format(url, _host, _port, _dbName);
    }


    public void connect(String _login, String _password) throws DBException {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException | NullPointerException e) {
            throw new DBException("Не удалось найти org.postgresql.Driver.");
        }
        if (connection == null)
            try {
                connection = DriverManager.getConnection(url, _login, _password);
            } catch (SQLException e) {
                throw new DBException("Неверный логин или пароль " + e.getMessage());
            }
    }
}