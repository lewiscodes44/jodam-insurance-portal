package ke.co.jodam.insurance.controller;

import ke.co.jodam.insurance.dto.notification.NotificationResponse;
import ke.co.jodam.insurance.service.NotificationService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(
            NotificationService notificationService
    ) {
        this.notificationService = notificationService;
    }

    @GetMapping("/my")
    public ResponseEntity<List<NotificationResponse>>
    getMyNotifications(
            Authentication authentication
    ) {

        return ResponseEntity.ok(
                notificationService.getMyNotifications(
                        authentication.getName()
                )
        );
    }
    @PostMapping("/read-all")
    public ResponseEntity<Void> markRead(Authentication authentication) { notificationService.markMyNotificationsRead(authentication.getName()); return ResponseEntity.noContent().build(); }
    @PostMapping("/read-related")
    public ResponseEntity<Void> markRelatedRead(
            @RequestParam String reference,
            Authentication authentication
    ) {
        notificationService.markNotificationsReadForReference(authentication.getName(), reference);
        return ResponseEntity.noContent().build();
    }
    @PostMapping("/{notificationId}/read")
    public ResponseEntity<Void> markOneRead(@org.springframework.web.bind.annotation.PathVariable Long notificationId, Authentication authentication) { notificationService.markNotificationRead(notificationId, authentication.getName()); return ResponseEntity.noContent().build(); }
}
