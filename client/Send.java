package client;


import java.io.*;
import java.net.Socket;
import server.User;
import commands.Command;
import commands.CommandHandler;

/**
 *Класс, который подготавливает команду к отправке на сервер
 */
public class Send {

    public static Command send(Command cmd, Socket socket, User user) throws IOException,ClassNotFoundException {
        if ((socket != null)) {
            ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(socket.getOutputStream()));
            cmd.setUser(user);
            if(user!=null) {
                cmd.getCar().setlogin(user.getLogin());
                System.out.println(user.getLogin());
            }

            oos.writeObject(cmd);
            oos.flush();
            System.out.println("Команда " + cmd.toString() + " была отправлена");

            if(!cmd.toString().equals("shutdown")){
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                return CommandHandler.readResponse(ois);
            }

        }else {
            System.out.println("Сокет равен нулю");
            System.exit(-1);
        }
        return null;
    }

}
