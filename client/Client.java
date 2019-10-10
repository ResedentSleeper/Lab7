package client;

import classes.Car;
import commands.Command;
import commands.CommandHandler;
import server.Server;
import server.User;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

/**
 * Главный класс на клиенте
 */
public class Client {


    public static void main(String[] args) {
        try {
            System.setOut(new PrintStream(System.out, true, "cp866"));
        }
        catch (java.io.UnsupportedEncodingException ex) {
            System.out.println(ex.getMessage());
        }

        Socket socket = null;
        int port = 0;
        Scanner scanner = new Scanner(System.in);
        boolean[] isAddShutDown ={false};
        boolean[] closeApp = {false};
        Command again = null;
        Command response=null;
        Stat status = Stat.LOGOUT;
        User user = null;

        while(true) {

            port = Server.readINTport(port, scanner);
            while (true) {
                try {
                    socket = new Socket("localhost", port);

                } catch (UnknownHostException ex) {
                    System.out.println(ex.getMessage());
                    break;
                } catch (IOException ex) {
                    System.out.println(ex.getMessage());
                    break;
                }


                final Socket shutdown = socket;
                final User forSD=user;
                if (!isAddShutDown[0]) {
                    isAddShutDown[0]=true;
                    Runtime.getRuntime().addShutdownHook(new Thread() {
                        @Override
                        public void run() {

                            try {
                                Send.send(new Command("shutdown", new Car()), shutdown,forSD);
                                closeApp[0] = true;
                                try {
                                    System.out.println("Закрытие...");
                                    shutdown.close();
                                } catch (IOException ex) {
                                    System.out.println("Ошибка при закрытии");
                                    ex.printStackTrace();
                                }
                            } catch (IOException ex) {
                                System.out.println(ex.getMessage());
                            }catch(ClassNotFoundException ex){
                                System.out.println(ex.getMessage());
                            }
                        }
                    });
                }

                System.out.println("Здравствуйте,\n" +
                        "Для работы с машинками войдите с систему, используя команду login\n" +
                        "Или пройдите регистрацию, используя команду signin");
                CommandHandler cmdh = new CommandHandler(new BufferedReader(new InputStreamReader(System.in)), new BufferedOutputStream(System.out));
                try {
                    socket.setSoTimeout(15000);

                    while (true) {
                        if (again != null) {
                            if (!again.toString().equals("help") &&
                                    (status == Stat.LOGIN && (!again.toString().equals("login") && !again.toString().equals("signin")) || (status == Stat.LOGOUT && (again.toString().equals("login") || again.toString().equals("signin"))))) {
                                response = Send.send(again, socket, user);
                                if (status == Stat.LOGOUT && again.toString().equals("login") && response.toString().equals("Вы в системе")) {
                                    System.out.println(response.getUser());
                                    status = Stat.LOGIN;
                                    user = response.getUser();

                                } else if (status == Stat.LOGIN && again.toString().equals("logout")) {
                                    user = null;
                                    status = Stat.LOGOUT;
                                }
                                System.out.println(response.toString());
                            } else if (status == Stat.LOGIN && (again.toString().equals("login") || again.toString().equals("signin"))) {
                                System.out.println("Вы уже в системе");
                            } else if (again.toString().equals("help")) {
                                cmdh.help();
                            } else if (again.toString().equals("Неизвестная команда")) {
                                System.out.println("Неизвестная команда");
                            } else {
                                System.out.println("Залогинтесь чтобы отправлять команды");
                            }
                        }
                        cmdh.readCommand();
                        again = cmdh.getCmd();
                    }
                } catch (IOException exx) {
                    System.out.println("Сервер недоступен: " + exx.getMessage());
                    System.out.println("Переподключение...");

                } catch (NullPointerException e){
                    System.out.println("Было отправленно нулевое значение");
                    System.exit(-1);
                }catch (ClassNotFoundException ex) {
                    System.out.println(ex.getMessage());
                }
            }

        }
    }
}