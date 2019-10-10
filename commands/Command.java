package commands;


import server.User;
import classes.Car;

import java.io.Serializable;

/**
 * Класс описывающий общий формат для команды
 */
public class Command implements Serializable {

    protected String command;
    protected Car car;
    protected String firstParametr;
    protected String secondParametr;
    protected User user=null;


    public Command(String cmd){
        this(cmd,new Car());
    }

    public Command(String command, String firstParametr, String secondParametr) {
        this(command,new Car(),firstParametr,secondParametr);
    }

    public Command(String cmd, Car json){
        this.command = cmd;
        this.car = json;

    }

    public Command(String command, Car car, String firstParametr, String secondParametr) {
        this.command = command;
        this.car = car;
        this.firstParametr = firstParametr;
        this.secondParametr = secondParametr;
    }


    public Car getCar(){
        return car;
    }

    public User getUser() {
        return user;
    }

    public Command setUser(User user) {
        this.user = user;
        return this;
    }

    public String getFirstParametr() {
        return firstParametr;
    }

    public String getSecondParametr() {
        return secondParametr;
    }

    public void setParameters(String[] array){
        this.firstParametr = array[0];
        this.secondParametr = array[1];
    }


    @Override
    public String toString() {
        return command;
    }




}