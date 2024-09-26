package bo.edu.ucb.syntax_flavor_backend.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import java.nio.charset.StandardCharsets;

@Service
public class EmailService {

    Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private JavaMailSenderImpl emailSender;

    public void sendEmail(String to, String subject, String body) {
        try {
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message,
                    MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    StandardCharsets.UTF_8.name());

            helper.setTo(to);
            helper.setText(body, true);
            helper.setSubject(subject);
            helper.setFrom("sender-test@example.com");

            emailSender.send(message);
            logger.info("SMTP Server: " + emailSender.getHost() + ":" + emailSender.getPort());
            logger.info("Email sent successfully!");

        } catch (MessagingException e) {
            logger.error("Error while sending email", e);
        }
    }
    public void sendEmailWithAttachment(String to, String subject, String body, byte[] attachmentData, String attachmentName) {
        try {
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());

            helper.setTo(to);
            helper.setText(body, true);
            helper.setSubject(subject);
            helper.setFrom("sender-test@example.com");

            // Crear InputStreamSource con los datos del adjunto
            InputStreamSource attachmentSource = new ByteArrayResource(attachmentData);
            helper.addAttachment(attachmentName, attachmentSource);

            // Enviar el correo
            emailSender.send(message);
            logger.info("Email con adjunto enviado a: " + to);

        } catch (MessagingException e) {
            logger.error("Error al enviar el correo con adjunto", e);
        }
    }
}