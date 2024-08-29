package com.example.instagramclone.instagramclone.service;

import java.util.Properties;

import org.springframework.stereotype.Service;
import jakarta.mail.Message;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.eclipse.angus.mail.smtp.SMTPSendFailedException;

@Service
public class EmailService {

    private final String FROM = "lucizodi@gmail.com"; // Replace with your verified email
    static final String SMTP_USERNAME = "AKIAYS2NXDRJGA7F2XIL"; // Replace with your SES SMTP username
    static final String SMTP_PASSWORD = "BMiIUKAtaWoyjKnfKjuC/SDlUpmZEPu9SOLaRWDS6W3P"; // Replace with your SES SMTP
                                                                                        // password
    static final String HOST = "email-smtp.us-east-2.amazonaws.com"; // SES SMTP host
    static final int PORT = 587; // SES SMTP port (typically 587 for TLS, 465 for SSL)

    // Updated SUBJECT
    static final String SUBJECT = "Your OTP for instaClone via Amazon SES";

    // Placeholder BODY template with OTP
    static final String BODY_TEMPLATE = String.join(
            System.getProperty("line.separator"),
            "<h1>Here's your OTP</h1>",
            "<p>Your OTP code is: {otp}</p>",
            "<p>If you did not request this code, please ignore this email.</p>");

    public void sendEmail(String toAddress, String subject, String bodyText) throws Exception {
        toAddress = toAddress.trim();
        System.out.println("to address = " + toAddress);

        try {
            Properties props = System.getProperties();
            props.put("mail.transport.protocol", "smtp");
            props.put("mail.smtp.port", PORT);
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.auth", "true");

            Session session = Session.getDefaultInstance(props);

            MimeMessage msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(FROM, "LUCY"));
            msg.setRecipient(Message.RecipientType.TO, new InternetAddress(toAddress));
            msg.setSubject(subject);
            msg.setContent(bodyText, "text/html");

            Transport transport = session.getTransport();

            try {
                System.out.println("Sending...");

                transport.connect(HOST, SMTP_USERNAME, SMTP_PASSWORD);
                transport.sendMessage(msg, msg.getAllRecipients());
                System.out.println("Email sent!");
            } catch (Exception ex) {
                System.out.println("The email was not sent. because " + ex.getMessage());
                throw new Exception("Email is not verified");
            } finally {
                transport.close();
            }
        } catch (Exception e) {
            throw e;
        }
    }

    public void sendOtp(String email, String otp) throws Exception {
        try {
            String bodyWithOtp = BODY_TEMPLATE.replace("{otp}", otp);
            sendEmail(email, SUBJECT, bodyWithOtp);
        } catch (Exception e) {
            throw e;
        }
    }
}
