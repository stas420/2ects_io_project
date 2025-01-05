package email;

import javax.mail.*;
import javax.mail.internet.*;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

public class EmailSender {
    private String senderEmail;
    private String senderPassword;

    private static EmailSender instance;

    private EmailSender() {
        //prywatny konstruktor
    }
    public static EmailSender getInstance() {
        if (instance == null) {
            instance = new EmailSender();
        }
        return instance;
    }
    public void setCredentials(String senderEmail, String senderPassword) {
        this.senderEmail = senderEmail;
        this.senderPassword = senderPassword;
    }
    public boolean areCredentialsSet() {
        return senderEmail != null && senderPassword != null;
    }

    public void sendEmail(List<String> recipients, String subject, String message, List<File> attachments) throws MessagingException, IOException {
        if(!areCredentialsSet()) {
            throw new MessagingException("Credentials not set");
        }

        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");

        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(senderEmail, senderPassword);
            }
        });

        Message emailMessage = new MimeMessage(session);
        emailMessage.setFrom(new InternetAddress(senderEmail));

        for (String recipient : recipients) {
            emailMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
        }

        emailMessage.setSubject(subject);

        // Tworzenie treści wiadomości z załącznikami
        MimeBodyPart messageBodyPart = new MimeBodyPart();
        messageBodyPart.setText(message);

        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(messageBodyPart);

        for (File file : attachments) {
            MimeBodyPart attachmentPart = new MimeBodyPart();
            attachmentPart.attachFile(file);
            multipart.addBodyPart(attachmentPart);
        }

        emailMessage.setContent(multipart);

        // Wysyłanie wiadomości
        Transport.send(emailMessage);
    }
}
