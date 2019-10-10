package server;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.Authenticator;

/**
 * Класс для отправки сообщений
 */
public class Mail {

    private MailService service;
    private String login;
    private String password;


    public Mail(MailService service, String login, String password) {
        this.service = service;
        this.login = login;        this.password = password;
    }


    public void send(String theme, String message, String toEmail) {
        System.out.println("Отправка сообщения...");
        java.util.Properties props = new java.util.Properties();
        props.put("mail.smtp.host", service.getHost());
        props.put("mail.smtp.port", service.getPort());
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.socketFactory.port", service.getPort());
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");

        Session session = Session.getDefaultInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(login, password);
            }
        });


        String to = toEmail;
        String from = login;
        String subject = theme;
        Message msg = new MimeMessage(session);
        try {
            msg.setFrom(new InternetAddress(from));
            msg.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
            msg.setSubject(subject);
            msg.setText(message);

            Transport.send(msg);
            System.out.println("Отправлено!");
        } catch (AuthenticationFailedException e) {
            System.err.println("Ошибка, некорректный логин/пароль");
        } catch (javax.mail.MessagingException e) {
            System.err.println("При отправке сообщения произошла ошибка");
            e.printStackTrace();
        }
    }
}
