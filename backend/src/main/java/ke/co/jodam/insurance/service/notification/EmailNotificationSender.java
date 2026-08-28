package ke.co.jodam.insurance.service.notification;

import ke.co.jodam.insurance.entity.Notification;
import ke.co.jodam.insurance.entity.NotificationChannel;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailNotificationSender
        implements NotificationSender {

    private final JavaMailSender mailSender;

    public EmailNotificationSender(
            JavaMailSender mailSender
    ) {
        this.mailSender = mailSender;
    }

    @Override
    public boolean supports(
            Notification notification
    ) {
        return notification != null
                && notification.getChannel()
                == NotificationChannel.EMAIL;
    }

    @Override
    public void send(
            Notification notification
    ) {

        SimpleMailMessage email =
                new SimpleMailMessage();

        email.setTo(
                notification.getRecipient()
        );

        if (notification.getSubject() != null
                && !notification.getSubject().isBlank()) {

            email.setSubject(
                    notification.getSubject()
            );

        } else {

            email.setSubject(
                    "Jodam Insurance Notification"
            );
        }

        email.setText(
                notification.getMessage()
        );

        mailSender.send(email);
    }
}