package server;

import java.io.Serializable;
import java.util.Random;

/**
 * Класс пользователь
 * Поля логин, пароль и почты
 */
public class User implements Serializable, Comparable<User> {
    private String login;
    private String email;
    private String pass;

    @Override
    public int compareTo(User o) {
        return login.compareTo(o.getLogin());
    }

    public User(String login, String email, String pass) {
        this.login = login;
        this.email = email;
        this.pass = pass;
        this.pass = generate_pas();
    }

    public User(String login, String pass) {
        if(login!=null && pass!=null){
            this.login = login;
            this.pass = pass;
        }
        else System.out.println("Для входа необходим и логин, и пароль");
    }

    public String getLogin() {
        return login;
    }

    public String getEmail() {
        return email;
    }

    public String getPass(){
        return pass;
    }

    public String generate_pas(){
        String CHARACTERS = "?!#$%&()*+,-.0123456789:;<=>ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        Random rand = new Random();
        StringBuilder pass = new StringBuilder();
        int sizeOFpass = rand.nextInt(4)+6;
        for(int i = 0; i<sizeOFpass;i++){
            pass.append(CHARACTERS.charAt(rand.nextInt(CHARACTERS.length())));
        }
        return (this.pass=pass.toString());
    }

    @Override
    public String toString() {
        return "User " + login;
    }
}