package ke.co.jodam.insurance.service.notification;

import ke.co.jodam.insurance.entity.Notification;

public interface NotificationSender {

    boolean supports(Notification notification);

    void send(Notification notification);
}