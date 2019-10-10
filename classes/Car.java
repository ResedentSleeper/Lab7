package classes;


import org.json.JSONObject;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

/***
 * @author Alena_Raskovalova
 * P3102
 * 10.02.19
 * <p>Класс машина, с реализованным базовым функцианалом машины</p>
 */
public class Car implements Comparable, Serializable {

    /** Имя машины */
    private String name;
    /** Скорость машины */
    private Double speed = 0.0;
    /** Владелец машины */
    private String login ;
    /** Место, в котором находится машина */
    private Place place;
    /**Время*/
    private LocalDateTime time;



    /**
     * Конструктор - создание нового объекта
     * @see Car#Car(String)
     */
    public Car() {
        this.name="Без названия";
        this.speed = 0.0;
        this.place= new Place (0,0);
        this.time = LocalDateTime.now();
    }

    /**
     * Конструктор - создание нового объекта с одним параметром
     * @param name - имя
     * @see Car#Car()
     */
    public Car(String name) {
        this.name = name;
        this.place= new Place(0,0);
        this.time = LocalDateTime.now();
    }

    public Car(String name, double speed) {
        this.name = name;
        this.place= new Place(0,0);
        this.time = LocalDateTime.now();
    }


    public Car(String name, double speed, Place place) {
        this.name = name;
        this.speed=speed;
        this.place=place;
        this.time = LocalDateTime.now();
    }

    public Car(String name, double speed,Place place, String login) {
        this.name = name;
        this.speed=speed;
        this.login=login;
        this.place=place;
        this.time = LocalDateTime.now();
    }

    public Car(String name, double speed,Place place, String login, String time) {
        this.name = name;
        this.speed=speed;
        this.login=login;
        this.place=place;
        this.time = LocalDateTime.parse(time);
    }


    public Car(JSONObject object){
        this();
        for(String str: object.keySet()){
            switch(str){
                case "name":{
                    name = object.getString("name");
                    break;
                }
                case "speed":{
                    speed = object.getDouble("speed");
                    break;
                }
                case "login":{
                    login =  object.getString("login");
                    break;
                }
                case "place":{
                    place = new Place(object.getJSONObject("place"));
                    break;
                }
            }
        }
        this.time = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "Car{ " +
                "name " + name  +
                ", speed " + speed +
                ", place " + place +
                ", time " + time +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        Car car = (Car) o;
        if (this.name == car.name) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public int compareTo (Object o){
        Car car = (Car)o;
        int result = this.name.compareTo(car.name);
        return result;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }


    /**
     * Функция получения значения поля {@link Car#name}
     * @return возвращает название машины
     */
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Функция получения значения поля {@link Car#speed}
     * @return возвращает скорость машины
     */
    public Double getSpeed() {
        return speed;
    }
    public void setSpeed(double speed) {
        this.speed = speed;
    }

    /**
     * Функция получения значения поля {@link Car#place}
     * @return возвращает местоположение машины
     */
    public Place getPlace(){
        return place;
    }
    public void setPlace(Place place){
        this.place=place;
    }

    public LocalDateTime getTime() {
        return time;
    }
    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    public String getlogin() {
        return login;
    }
    public void setlogin(String login) {
        this.login = login;
    }

}
