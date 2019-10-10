package use;

import server.User;
import db.DB;
import db.DBException;
import classes.Place;
import classes.Car;

import java.io.BufferedOutputStream;
import java.security.InvalidParameterException;
import java.sql.*;
import java.util.Date;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.stream.Collectors;


public class Control {
    protected ConcurrentSkipListSet<Car> set;
    protected Date date = new Date();
    protected BufferedOutputStream out;
    public boolean saving = false;
    private DB db;

    public Control(ConcurrentSkipListSet<Car> set,DB db) {
        try{
            this.set = set;
            this.db = db;
            db.connect("postgres", "HELPme");
            loadCollection();
        }catch(DBException ex){
            System.out.println(ex.getMessage());
            System.exit(-1);
        }

    }


    /**
     * очищает коллекцию
     */
    public String clear(User user){
        loadCollection();
        set.removeAll(set.stream().filter(c -> c.getlogin().equals(user.getLogin())).collect(Collectors.toList()));
        Connection con = db.connection;
        try {
            PreparedStatement statement = con.prepareStatement("DELETE FROM cars WHERE login=?");
            statement.setString(1, user.getLogin());
            statement.executeUpdate();
            statement.close();
        } catch (SQLException ex) {
            return ex.getMessage();
        }
        return "Из коллекции удалены все ваши машинки";
    }

    /**
     * выводит все элементы в консоль
     * @return элементы или фразу "Collection is empty"
     */
     public String show() {
         loadCollection();
         if (set.isEmpty()) return "Коллекция пуста";
         String[] tmp = {""};
         int[] k = {1};
         set.forEach(e -> { tmp[0] += e.toString() + " (Создатель: " + e.getlogin() + ")\n"; });
         return tmp[0];

    }


    /**
     * выводит информацию о коллекции
     * @return String
     */
    public String info() {
        loadCollection();
        String information = String.format("\nТип: " + set.getClass().getName() + "\nКол-во машинок: " + set.size() + "\nДата: " + date.toString()
                + "\nКол-во пользователей: " + countUsers());
        System.out.println(information);
        return information;
    }


    /**
     * добавляет элемент в коллекцию
     * @param object объект
     */
    public String add(Car object) {
        loadCollection();
        boolean a = set.add(object);
        Connection con = db.connection;
        if (a) {
            try {
                PreparedStatement statement = con.prepareStatement("INSERT INTO cars (name, speed, place, login, time ) VALUES(?, ?, ?, ?, ?)");
                set(statement, object, true);
                statement.executeUpdate();
                statement.close();
            } catch (SQLException ex) {
                return ex.getMessage();
            }
            return "Машина с названием " + object.getName() + " была добавлена";
        } else return "Не удалось добавить машину";
    }



    /**
     * удаляет элемент по значнию
     * @param object объкт
     * @throws InvalidParameterException если не найден объект
     */
    public String remove(Car object, User user) throws InvalidParameterException {
        boolean remove = false;
        if (object.getlogin().equals(user.getLogin()))
            remove = set.remove(object);
        String deleting = "DELETE FROM cars WHERE  name=? AND speed=? AND place=? AND  login=? ";
        if (remove) {
            try {
                Connection con = db.connection;
                PreparedStatement statement = con.prepareStatement("DELETE FROM cars WHERE name=? AND speed=? AND place =? AND login=?");
                set(statement, object, false);
                statement.executeUpdate();
                statement.close();

            } catch (SQLException ex) {
                return ex.getMessage();
            }
        }
        return "Пробую удалить машину";
    }




    public int countUsers() {
        Connection con = db.connection;
        try {
            PreparedStatement statement = con.prepareStatement("SELECT count(*) FROM users");
            ResultSet rset = statement.executeQuery();
            rset.next();
            return rset.getInt(1);
        } catch (SQLException ex) {
            return 0;
        }
    }


    public void loadCollection(){
        Connection con = db.connection;
        try {
            PreparedStatement statement = con.prepareStatement("SELECT * FROM cars");
            ResultSet objects = statement.executeQuery();
            while (objects.next()){
                set.add(new Car(
                        objects.getString("name"),
                        objects.getInt("speed"),
                        new Place((Integer[]) objects.getArray("place").getArray()),
                        objects.getString("login"),
                        objects.getString("time")
                ));
            }
        }catch(SQLException ex){
            System.out.println(ex.getMessage());
        }
    }

    public DB getDb() {
        return db;
    }

    private PreparedStatement set(PreparedStatement statement,Car object,boolean a)throws SQLException{
        int c=0;
        statement.setString(++c,object.getName());
        statement.setDouble(++c,object.getSpeed());

        Integer[] ina = {object.getPlace().getX(),object.getPlace().getY()};
        Array ar =db.connection.createArrayOf("INTEGER",ina);
        statement.setArray(++c,ar);

        statement.setString(++c,object.getlogin());
        System.out.println(object.getlogin());

        if(a) statement.setString(++c,object.getTime().toString());
        return statement;
    }

}