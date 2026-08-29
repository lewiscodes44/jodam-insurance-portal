package ke.co.jodam.insurance.service;

import ke.co.jodam.insurance.dto.notification.NotificationResponse;
import ke.co.jodam.insurance.entity.Notification;
import ke.co.jodam.insurance.entity.NotificationChannel;
import ke.co.jodam.insurance.entity.NotificationStatus;
import ke.co.jodam.insurance.entity.Payment;
import ke.co.jodam.insurance.entity.Policy;
import ke.co.jodam.insurance.entity.Quotation;
import ke.co.jodam.insurance.entity.Quotation;
import ke.co.jodam.insurance.entity.User;
import ke.co.jodam.insurance.repository.NotificationRepository;
import ke.co.jodam.insurance.repository.UserRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public NotificationService(
            NotificationRepository notificationRepository,
            UserRepository userRepository
    ) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public void createPaymentConfirmationNotifications(
            Payment payment
    ) {

        if (payment == null
                || payment.getPolicy() == null
                || payment.getPolicy().getCustomer() == null) {

            System.out.println(
                    "NOTIFICATION SKIPPED: Payment or customer information is missing"
            );

            return;
        }

        Policy policy = payment.getPolicy();
        User customer = policy.getCustomer();

        String message =
                buildPaymentConfirmationMessage(payment);

        if (customer.getPhoneNumber() != null
                && !customer.getPhoneNumber().isBlank()) {

            createNotification(
                    customer,
                    NotificationChannel.SMS,
                    customer.getPhoneNumber(),
                    null,
                    message
            );
        }

        if (customer.getEmail() != null
                && !customer.getEmail().isBlank()) {

            createNotification(
                    customer,
                    NotificationChannel.EMAIL,
                    customer.getEmail(),
                    "Payment Confirmation - "
                            + policy.getPolicyNumber(),
                    message
            );
        }
    }

    @Transactional
    public void createQuotationSentNotification(Quotation quotation) {
        if (quotation == null || quotation.getInquiry() == null || quotation.getInquiry().getCustomer() == null) {
            return;
        }
        User customer = quotation.getInquiry().getCustomer();
        if (customer.getEmail() == null || customer.getEmail().isBlank()) {
            return;
        }
        String message = "Your quotation " + quotation.getQuoteReference()
                + " is ready. Total payable: KES " + quotation.getTotalPayable()
                + ". Please sign in to review and accept or decline it before "
                + quotation.getValidUntil() + ".";
        createNotification(customer, NotificationChannel.EMAIL, customer.getEmail(),
                "Your Jodam quotation is ready", message);
    }
    @Transactional
    public void createQuotationReviewRequestNotification(Quotation quotation) {
        if (quotation == null || quotation.getAgent() == null || quotation.getAgent().getEmail() == null || quotation.getAgent().getEmail().isBlank()) return;
        createNotification(quotation.getAgent(), NotificationChannel.EMAIL, quotation.getAgent().getEmail(), "Customer requested quotation review", "The customer requested a review of quotation " + quotation.getQuoteReference() + ": " + quotation.getCustomerReviewMessage());
    }
    @Transactional
    public void createPolicyIssuedNotification(Policy policy) {
        if (policy == null || policy.getCustomer() == null) {
            return;
        }
        User customer = policy.getCustomer();
        if (customer.getEmail() == null || customer.getEmail().isBlank()) {
            return;
        }
        String message = "Your policy " + policy.getPolicyNumber()
                + " has been issued and is awaiting M-Pesa payment. Amount due: KES "
                + policy.getPremiumAmount() + ". Sign in to view the policy and pay.";
        createNotification(customer, NotificationChannel.EMAIL, customer.getEmail(),
                "Your Jodam policy is ready for payment", message);
    }
    @Transactional(readOnly = true)
    public List<NotificationResponse> getMyNotifications(
            String username
    ) {

        User user =
                userRepository
                        .findByUsername(username)
                        .orElseThrow(
                                () -> new IllegalStateException(
                                        "Authenticated user not found"
                                )
                        );

        return notificationRepository
                .findByUserOrderByIdDesc(user)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<NotificationResponse> getAllNotifications() {

        return notificationRepository
                .findAllByOrderByIdDesc()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private void createNotification(
            User user,
            NotificationChannel channel,
            String recipient,
            String subject,
            String message
    ) {

        Notification notification = new Notification();

        notification.setUser(user);
        notification.setChannel(channel);
        notification.setRecipient(recipient);
        notification.setSubject(subject);
        notification.setMessage(message);
        notification.setStatus(
                NotificationStatus.PENDING
        );

        notificationRepository.saveAndFlush(
                notification
        );

        System.out.println(
                "NOTIFICATION CREATED"
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

        System.out.println(
                "Status: "
                        + notification.getStatus()
        );
    }

    private String buildPaymentConfirmationMessage(
            Payment payment
    ) {

        Policy policy = payment.getPolicy();
        BigDecimal amount = payment.getAmount();

        String transactionReference =
                payment.getTransactionReference();

        return "Payment received successfully. "
                + "Policy "
                + policy.getPolicyNumber()
                + " is now active. "
                + "Amount: KES "
                + amount
                + ". "
                + "M-Pesa Receipt: "
                + (
                transactionReference == null
                        ? "Not available"
                        : transactionReference
        )
                + ".";
    }

    private NotificationResponse toResponse(
            Notification notification
    ) {

        return new NotificationResponse(
                notification.getId(),
                notification.getChannel(),
                notification.getRecipient(),
                notification.getSubject(),
                notification.getMessage(),
                notification.getStatus(),
                notification.getFailureReason(),
                notification.getSentAt(),
                notification.getCreatedAt(),
                notification.getUpdatedAt()
        );
    }
}
