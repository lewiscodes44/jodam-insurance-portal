package ke.co.jodam.insurance.service.notification;

import ke.co.jodam.insurance.entity.Notification;
import ke.co.jodam.insurance.entity.NotificationStatus;
import ke.co.jodam.insurance.repository.NotificationRepository;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@ConditionalOnProperty(
        prefix = "notification.dispatch",
        name = "enabled",
        havingValue = "true"
)
public class NotificationDispatcher {

    private final NotificationRepository notificationRepository;

    private final List<NotificationSender>
            notificationSenders;

    public NotificationDispatcher(
            NotificationRepository notificationRepository,
            List<NotificationSender> notificationSenders
    ) {
        this.notificationRepository =
                notificationRepository;

        this.notificationSenders =
                notificationSenders;
    }

    @Scheduled(
            fixedDelayString =
                    "${notification.dispatch.fixed-delay:30000}"
    )
    @Transactional
    public void dispatchPendingNotifications() {

        List<Notification> pendingNotifications =
                notificationRepository
                        .findByStatusOrderByIdDesc(
                                NotificationStatus.PENDING
                        );

        for (Notification notification
                : pendingNotifications) {

            dispatchNotification(notification);
        }
    }

    private void dispatchNotification(
            Notification notification
    ) {

        Optional<NotificationSender> sender =
                notificationSenders
                        .stream()
                        .filter(
                                candidate ->
                                        candidate.supports(
                                                notification
                                        )
                        )
                        .findFirst();

        if (sender.isEmpty()) {

            System.out.println(
                    "NOTIFICATION DISPATCH SKIPPED"
            );

            System.out.println(
                    "Notification ID: "
                            + notification.getId()
            );

            System.out.println(
                    "Reason: No sender configured for channel "
                            + notification.getChannel()
            );

            return;
        }

        try {

            sender.get().send(notification);

            notification.setStatus(
                    NotificationStatus.SENT
            );

            notification.setSentAt(
                    LocalDateTime.now()
            );

            notification.setFailureReason(
                    null
            );

            notificationRepository.save(
                    notification
            );

            System.out.println(
                    "NOTIFICATION SENT"
            );

            System.out.println(
                    "Notification ID: "
                            + notification.getId()
            );

            System.out.println(
                    "Channel: "
                            + notification.getChannel()
            );

            System.out.println(
                    "Recipient: "
                            + notification.getRecipient()
            );

        } catch (Exception exception) {

            notification.setStatus(
                    NotificationStatus.FAILED
            );

            notification.setFailureReason(
                    exception.getMessage()
            );

            notificationRepository.save(
                    notification
            );

            System.out.println(
                    "NOTIFICATION FAILED"
            );

            System.out.println(
                    "Notification ID: "
                            + notification.getId()
            );

            System.out.println(
                    "Reason: "
                            + exception.getMessage()
            );
        }
    }
}