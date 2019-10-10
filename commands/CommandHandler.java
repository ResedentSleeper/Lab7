package commands;

import server.User;
import classes.Car;
import org.json.JSONException;
import org.json.JSONObject;

import javax.swing.*;
import java.io.*;
import java.util.Scanner;


public class CommandHandler {

    protected String command;
    protected JSONObject JSONObject;
    protected BufferedReader in;
    protected BufferedOutputStream out;
    protected int tmpchar;
    protected boolean finish = false;
    protected Command cmd = null;
    protected boolean UnknownCMD = false;
    protected User user = null;


    public CommandHandler(BufferedReader in, BufferedOutputStream out) {
        this.in = in;
        this.out = out;
    }

    public Command getCmd() {
        if (!UnknownCMD)
            return cmd;
        else return new Command("Неизвестная команда");
    }

    public void readCommand() throws IOException {
        System.out.print(">>>");
        int c = skipSpaces();
        command = "";
        while (c != '{' && c != '\n' && c != ' ' && c != '\r' && c != -1) {
            command += (char) c;
            c = in.read();
        }
        tmpchar = c;
        try {
            callFunctions();
        } catch (JSONException ex) {
            System.out.println(ex.getMessage());
        }
        if (tmpchar == -1)
            System.exit(-1);
    }


    public void readJSONObject() throws IOException, JSONException {
        int skobochiki = 0, ch;
        String JSON = "";
        if (tmpchar == ' ' || tmpchar == '\n' || tmpchar == '\r' || tmpchar == '\t') {
            ch = skipSpaces();
        } else
            ch = tmpchar;
        for (; ; ) {

            if (ch == '{') {
                skobochiki++;
            } else if (ch == '}') skobochiki--;
            else if (ch == -1) {
                throw new JSONException("Неверный формат объекта");
            }
            JSON += (char) ch;
            ch = in.read();
            if (skobochiki == 0) {
                JSONObject = new JSONObject(JSON);
                return;
            }
        }
    }


    public int skipSpaces() throws IOException {
        int c = in.read();

        while (c == ' ' || c == '\t' || c == '\n' || c == '\r')
            c = in.read();
        return c;
    }


    public static Command readResponse(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        Command cmd = (Command) ois.readObject();
        return cmd;
    }

    public String[] readTWOparametrs() throws IOException {
        switch (command) {
            case "login": {
                String login, pass;
                System.out.print("login:");
                login = readENTER();
                System.out.print("password:");
                pass = readENTER();
                String[] mas = {login, pass};
                return mas;
            }
            case "signin": {
                String login, email;
                System.out.print("login:");
                login = readENTER();
                System.out.print("email:");
                email = readENTER();
                String[] mas = {login, email};
                return mas;
            }
        }
        return null;
    }

    public String readENTER() throws IOException {
        String tmp = "";
        Scanner scanner = new Scanner(System.in);
        tmp = scanner.nextLine();
        tmp.replace(" ", "");
        return tmp;
    }

    public void help() {
        System.out.println("Доступные команды:" +
                "\n~ add {element} - добавляет элемент в коллекцию в формате json\n" +
                "\n~ remove {name} - удаляет элемент по его значению" +
                "\n~ clear - перечитывает коллекцию из файла" +
                "\n~ show - выводит все элементы коллекции в строковом представлении" +
                "\n~ help - выводит список доступных команд" +
                "\n~ login - вход в систему" +
                "\n~ signin - регистрация пользователя" +
                "\n~ logout - выход из системы" +
                "\n~ info - выводит в стандартный поток вывода информацию о коллекции.\n(Тип, кол-во машинок, дата, кол-во пользователей)");
    }


    public void callFunctions() throws IOException, JSONException {
        if (command.equals("help") || command.equals("info") || command.equals("show") || command.equals("clear") || command.equals("logout")) {
            cmd = new Command(command, new Car());
            UnknownCMD = false;
        } else if (command.equals("add") || command.equals("remove")) {
            readJSONObject();
            cmd = new Command(command, new Car(JSONObject));
            UnknownCMD = false;
        } else if (command.equals("login") || command.equals("signin")) {
            cmd = new Command(command, new Car());
            String[] parameters = readTWOparametrs();
            cmd.setParameters(parameters);
            UnknownCMD = false;
        } else if (command.equals("D") || command.equals("C") || command.equals("shutdown")) {
            cmd = new Command("shutdown", new Car());
        } else {
            UnknownCMD = true;
            System.out.println("Неизвестная команда");
        }
    }
}
