import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.mail.*;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import email.EmailSender;

public class EmailTests {

    private EmailSender emailSender;

    @BeforeEach
    public void setUp() {
        emailSender = EmailSender.getInstance();
        emailSender.setCredentials("test@example.com", "password");
    }

    @Test
    public void testSingletonInstance() {
        EmailSender instance1 = EmailSender.getInstance();
        EmailSender instance2 = EmailSender.getInstance();
        assertSame(instance1, instance2);
    }

    @Test
    public void testSetCredentials() {
        emailSender.setCredentials("new@example.com", "newpassword");
        assertTrue(emailSender.areCredentialsSet());
    }

    @Test
    public void testSendEmail() throws MessagingException, IOException {
        // Mock the Transport class
        Transport transport = mock(Transport.class);
        Session session = Session.getInstance(new Properties());
        MimeMessage message = new MimeMessage(session);

        // Use Mockito to mock the static method Transport.send
        Mockito.mockStatic(Transport.class).when(() -> Transport.send(any(Message.class))).thenAnswer(invocation -> {
            Message msg = invocation.getArgument(0);
            assertEquals("test@example.com", msg.getFrom()[0].toString());
            assertEquals("recipient@example.com", msg.getAllRecipients()[0].toString());
            assertEquals("Test Subject", msg.getSubject());
            return null;
        });

        List<String> recipients = Collections.singletonList("recipient@example.com");
        String subject = "Test Subject";
        String messageBody = "Test Message";
        List<File> attachments = Collections.emptyList();

        emailSender.sendEmail(recipients, subject, messageBody, attachments);

        // Verify that Transport.send was called
        Mockito.verify(Transport.class, times(1));
        Transport.send(any(Message.class));
    }
}