package server;

import db.DB;
import use.Control;

import java.io.IOException;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.nio.channels.*;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Главный класс на сервере
 */
public class Server {


    public static int readINTport(Integer refer, Scanner scr){
        System.out.println("Введите номер порта");
        while(true) {
            try {
                refer = Integer.valueOf(scr.nextLine());
                if(refer>1024 && refer<65535)
                    break;
                else throw new NumberFormatException(" ");
            } catch (NumberFormatException ex) {
                System.out.println("Ошибка, попробуте ещё раз ");
            }catch (NoSuchElementException | NullPointerException ex){
                System.exit(-1);
            }
        }return refer;
    }


    public static int readINTthreads(Integer refer){
        while(true) {
            try {
                if(refer>0 && refer<=10)
                    break;
                else throw new NumberFormatException("Неверный формат");
            } catch (NumberFormatException ex) {
                System.out.println("Ошибка, попробуйте ещё раз");
            }catch ( NoSuchElementException | NullPointerException ex){
                System.exit(-1);
            }
        }
        return refer;
    }


    public static ExecutorService executor = null;


    public static void main(String[] args) {
        try {
            System.setOut(new PrintStream(System.out, true, "cp866"));
        }
        catch (java.io.UnsupportedEncodingException ex) {
            System.out.println(ex.getMessage());
        }

        int port=-1000, threads=-100;
        Scanner scr = new Scanner(System.in);
        ServerSocketChannel ssc =null;
        Selector sc =null;

        Control collection = new Control(new ConcurrentSkipListSet<>(),new DB("localhost",5432,"LAB"));
        Map<Integer,SocketChannel> readQueueSet = new ConcurrentHashMap<>();
        Set<User> users = new ConcurrentSkipListSet<>();
        Execution.setu = users;

        boolean [] closeApp = {false};
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if(executor!=null) {
                executor.shutdown();
                try {
                    Thread.sleep(2000);
                    collection.getDb().connection.close();
                } catch (InterruptedException | SQLException ex) {
                    System.out.println(ex.getMessage());
                }

                closeApp[0] = true;
            }

        }));

        try {
            port = readINTport(port, scr);
            threads = readINTthreads(8);
        }catch (NullPointerException ex){
            System.exit(-1);
        }


        try{
            executor = Executors.newFixedThreadPool(threads);
            ssc = ServerSocketChannel.open();
            ssc.socket().bind(new InetSocketAddress(port));
            sc = Selector.open();
            ssc.configureBlocking(false);
            SelectionKey key = ssc.register(sc, SelectionKey.OP_ACCEPT);
            System.out.println("Сервер готов к работе ");
        }catch(IOException ex){
            System.out.println(ex.getMessage());
            System.exit(-1);
        }


        try {
            while (true) {

                int count = sc.select();

                if(count == 0){
                    continue;
                }
                try {
                    Iterator it = sc.selectedKeys().iterator();
                    while (it.hasNext()) {
                        SelectionKey sKey = (SelectionKey) it.next();

                        if (sKey.isAcceptable()) {
                            try {
                                SocketChannel socketClient = ssc.accept();
                                socketClient.configureBlocking(false);
                                socketClient.register(sc, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
                                System.out.println("Клиент подключился");
                            } catch (IOException ex) {
                                System.out.println("Невозможно подключится к серверу");
                                ex.printStackTrace();
                                sKey.cancel();
                            }
                            it.remove();
                        }
                        if (sKey.isReadable()) {
                            if (sKey.channel() instanceof SocketChannel) {
                                SocketChannel ReadChannel = (SocketChannel) sKey.channel();
                                if (!readQueueSet.containsKey(ReadChannel.hashCode())) {
                                    readQueueSet.put(ReadChannel.hashCode(), ReadChannel);
                                    System.out.println("Чтение");
                                    executor.submit(new ReadClient(ReadChannel, readQueueSet, executor, collection, sKey));

                                    it.remove();
                                }
                            }
                        }

                    }
                }catch (CancelledKeyException ex){
                    System.out.println(ex.getMessage());
                }
            }
        }catch (IOException ex){
            System.out.println("Ошибка проверки ");
            ex.printStackTrace();
        }


    }
}