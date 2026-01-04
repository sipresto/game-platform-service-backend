package be.kdg.ipj3.notifications.application;

import be.kdg.ipj3.notifications.shared.EmailSendException;
import be.kdg.ipj3.notifications.shared.NotificationType;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Locale;

@Service
public class EmailService {
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${notification.email.from:no-reply@gameplatform.com}")
    private String fromEmail;

    @Value("${notification.email.sender-name:VibeGames}")
    private String senderName;

    public EmailService(JavaMailSender mailSender, TemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }

    /**
     * Send an HTML email using Thymeleaf template
     */
    public void sendHtmlEmail(String to, String subject, String templateName, Context context) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            String htmlContent = templateEngine.process(templateName, context);

            helper.setFrom(fromEmail, senderName);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            mailSender.send(mimeMessage);
        } catch (Exception e) {
            logger.error("Failed to send HTML email to: {}", to, e);
            throw new EmailSendException("Failed to send HTML email to " + to, e);
        }
    }

    /**
     * Send a notification email
     */
    public void sendNotificationEmail(String to, String username, NotificationType type, String notificationMessage) {
        try {
            Context context = new Context(Locale.getDefault());
            context.setVariable("username", username);
            context.setVariable("notificationType", type);
            context.setVariable("message", notificationMessage);
            context.setVariable("appName", senderName);

            String subject = getEmailSubject(type);
            String templateName = getTemplateName(type);

            sendHtmlEmail(to, subject, templateName, context);
            logger.info("Notification email sent to {} for type {}", to, type);
        } catch (Exception e) {
            logger.error("Failed to send notification email to: {}", to, e);
            throw new EmailSendException("Failed to send notification email", e);
        }
    }

    private String getEmailSubject(NotificationType type) {
        return switch (type) {
            case FRIEND_REQUEST -> "New Friend Request on " + senderName;
            case ACHIEVEMENT -> "Achievement Unlocked on " + senderName;
            case YOUR_TURN -> "It's Your Turn! - " + senderName;
            case CHAT_MESSAGE -> "New Chat Message on " + senderName;
        };
    }

    private String getTemplateName(NotificationType type) {
        return switch (type) {
            case FRIEND_REQUEST -> "email/friend-request";
            case ACHIEVEMENT -> "email/achievement";
            case YOUR_TURN -> "email/your-turn";
            case CHAT_MESSAGE -> "email/chat-message";
        };
    }
}