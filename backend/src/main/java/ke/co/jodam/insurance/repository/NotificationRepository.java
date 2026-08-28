package ke.co.jodam.insurance.repository;

import ke.co.jodam.insurance.entity.Notification;
import ke.co.jodam.insurance.entity.NotificationStatus;
import ke.co.jodam.insurance.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository
        extends JpaRepository<Notification, Long> {

    List<Notification>
    findByUserOrderByIdDesc(
            User user
    );

    List<Notification>
    findByStatusOrderByIdDesc(
            NotificationStatus status
    );

    List<Notification>
    findAllByOrderByIdDesc();
}