package ke.co.jodam.insurance.dto.notification;

import ke.co.jodam.insurance.entity.NotificationChannel;
import ke.co.jodam.insurance.entity.NotificationStatus;

import java.time.LocalDateTime;

public class NotificationResponse {

    private Long id;
    private NotificationChannel channel;
    private String recipient;
    private String subject;
    private String message;
    private NotificationStatus status;
    private String failureReason;
    private LocalDateTime sentAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public NotificationResponse() {
    }

    public NotificationResponse(
            Long id,
            NotificationChannel channel,
            String recipient,
            String subject,
            String message,
            NotificationStatus status,
            String failureReason,
            LocalDateTime sentAt,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        this.id = id;
        this.channel = channel;
        this.recipient = recipient;
        this.subject = subject;
        this.message = message;
        this.status = status;
        this.failureReason = failureReason;
        this.sentAt = sentAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public NotificationChannel getChannel() {
        return channel;
    }

    public String getRecipient() {
        return recipient;
    }

    public String getSubject() {
        return subject;
    }

    public String getMessage() {
        return message;
    }

    public NotificationStatus getStatus() {
        return status;
    }

    public String getFailureReason() {
        return failureReason;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}