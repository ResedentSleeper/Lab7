package server;

import commands.Command;
import use.Control;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

/**
 * Класс подготавливающий ответ на запрос клиента
 */
public  class AnswerClient implements Runnable {
    protected SocketChannel channel;
    protected Control set;
    protected Command command;
    public SelectionKey key;

    public AnswerClient(SocketChannel channel, Control set, Command command,SelectionKey key) {
        this.channel = channel;
        this.set = set;
        this.command = command;
        this.key = key;
    }

    public  void run(){
        Command response=Execution.execute(set,command,key);
        if(!command.toString().equals("shutdown")) {
            System.out.println("Загружаю...");
            try {
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                ObjectOutputStream ois = new ObjectOutputStream(bytes);
                ois.writeObject(response);
                channel.write(ByteBuffer.wrap(bytes.toByteArray()));
                System.out.println("Запрос был отправлен");

            } catch (IOException io) {
                System.out.println("Ошибка при отправке через канал " + channel + ":" + io.getMessage());
            }
        }
    }

}