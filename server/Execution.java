package server;


import classes.Car;
import classes.Place;
import commands.Command;
import db.DB;
import use.Control;

import javax.jws.soap.SOAPBinding;
import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;
import java.nio.channels.SelectionKey;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;
import java.security.MessageDigest;

public class Execution {
    public static Set<User> setu;
    private DB db;

    public static Command execute(Control set, Command cmd, SelectionKey key) {

        switch (cmd.toString()) {

            case "add": {
                if (Execution.checkUser(set, cmd)) {
                    return (new Command(set.add(cmd.getCar()))).setUser(cmd.getUser());
                }
                return (new Command("Ошибка при проверке пользователя"));
            }

            case "remove": {
                if (Execution.checkUser(set, cmd)) {
                    return (new Command(set.remove(cmd.getCar(), cmd.getUser()))).setUser(cmd.getUser());
                }
                return (new Command("Ошибка при проверке пользователя"));
            }

            case "show": {
                if (Execution.checkUser(set, cmd)) {
                    return (new Command(set.show())).setUser(cmd.getUser());
                }
                return (new Command("Ошибка при проверке пользователя"));
            }

            case "info": {
                if (Execution.checkUser(set, cmd)) {
                    return (new Command(set.info())).setUser(cmd.getUser());
                }
                return (new Command("Ошибка при проверке пользователя"));
            }

            case "clear": {
                if (Execution.checkUser(set, cmd)) {
                    return (new Command(set.clear(cmd.getUser()))).setUser(cmd.getUser());
                }
                return (new Command("Ошибка при проверке пользователя"));
            }

            case "shutdown": {
                if (Execution.checkUser(set, cmd)) {
                    key.cancel();
                    if (cmd.getUser() != null)
                        setu.remove(cmd.getUser());
                    return new Command("...");
                }
                return (new Command("Ошибка при проверке пользователя"));
            }

            case "login": {
                User user;
                try {
                    System.out.print("User:" + cmd.getFirstParametr());
                    System.out.print(" password:" + cmd.getSecondParametr());
                    user = new User(cmd.getFirstParametr(), cmd.getSecondParametr());
                    Connection con = set.getDb().connection;
                    PreparedStatement statement = con.prepareStatement("SELECT login, password FROM users WHERE login=? AND password=? ");
                    statement.setString(1, cmd.getFirstParametr());
                    MessageDigest mes = MessageDigest.getInstance("MD2");
                    try {
                        String hashpass = new String(mes.digest(user.getPass().getBytes("UTF-8")));
                        statement.setString(2, hashpass);
                    } catch (UnsupportedEncodingException ex) {
                    }
                    if (statement.executeQuery().next()) {
                        setu.add(user);
                        statement.close();
                        Command response = new Command("Вы в системе");
                        response.setUser(user);
                        return response;
                    } else {
                        return new Command("Неверный логин и/или пароль");
                    }
                } catch (SQLException ex) {
                    return new Command(ex.getMessage());
                } catch (NoSuchAlgorithmException ex) {
                    return new Command(ex.getMessage());
                }
            }

            case "signin": {
                PreparedStatement statement = null;
                try {
                    MessageDigest mes = MessageDigest.getInstance("MD2");
                    Connection con = set.getDb().connection;
                    statement = con.prepareStatement("SELECT login FROM users WHERE login=?");
                    statement.setString(1, cmd.getFirstParametr());
                    if (!statement.executeQuery().next()) {
                        statement.close();
                        User user = new User(cmd.getFirstParametr(), cmd.getSecondParametr(), "default");
                        String hashpass = new String(mes.digest(user.getPass().getBytes()));

                        Mail mail = new Mail(MailService.MAIL_RU, "bot_botov_2000@bk.ru", "z[L=zR$6f8+Ac5p");
                        String massage = "Ваш пароль:  " + user.getPass() + " ";
                        mail.send("Пароль для подключения к машинкам", massage, user.getEmail());

                        PreparedStatement statement1 = con.prepareStatement("INSERT INTO users (login,password,email) VALUES(?,?,?)");
                        statement1.setString(1, user.getLogin());
                        statement1.setString(2, hashpass);
                        statement1.setString(3, user.getEmail());
                        statement1.executeUpdate();
                        statement1.close();
                        return new Command("Пароль был отправлен на вашу почту");
                    } else {
                        statement.close();
                        return new Command("Этот аккаунт уже используется");
                    }
                } catch (SQLException ex) {
                    return new Command(ex.getMessage()).setUser(null);
                } catch (NoSuchAlgorithmException ex) {
                    return new Command(ex.getMessage());
                } catch (Exception e) {
                    System.err.println("Ошибка при регистрации");
                }
            }

            case "logout": {
                if (Execution.checkUser(set, cmd)) {
                    if (cmd.getUser() != null)
                        setu.remove(cmd.getUser());
                    return new Command("Вы вышли из системы");
                }
                return (new Command("Ошибка при проверке пользователя"));
            }
            default: {
                return new Command("Неизвестная команда");
            }
        }
    }


    public static boolean checkUser(Control set, Command cmd) {
        User user = cmd.getUser();
        try {
            Connection con = set.getDb().connection;
            PreparedStatement statement = con.prepareStatement("SELECT login, password FROM users WHERE login=? AND password=? ");
            statement.setString(1, user.getLogin());
            MessageDigest mes = MessageDigest.getInstance("MD2");
            try {
                String hashpass = new String(mes.digest(user.getPass().getBytes("UTF-8")));
                statement.setString(2, hashpass);
            } catch (UnsupportedEncodingException ex) {
            }
            if (statement.executeQuery().next()) {
                statement.close();
                return true;
            } else {
                return false;
            }
        } catch (SQLException ex) {
            return false;
        } catch (NoSuchAlgorithmException ex) {
            return false;
        }
    }
}