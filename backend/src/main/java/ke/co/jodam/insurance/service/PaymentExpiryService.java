package ke.co.jodam.insurance.service;

import ke.co.jodam.insurance.entity.Payment;
import ke.co.jodam.insurance.entity.PaymentStatus;
import ke.co.jodam.insurance.repository.PaymentRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class PaymentExpiryService {
    private static final int TIMEOUT_SECONDS = 120;
    private final PaymentRepository paymentRepository;

    public PaymentExpiryService(PaymentRepository paymentRepository) { this.paymentRepository = paymentRepository; }

    @Scheduled(fixedDelay = 30000)
    @Transactional
    public void expireStalePayments() {
        LocalDateTime cutoff = LocalDateTime.now().minusSeconds(TIMEOUT_SECONDS);
        paymentRepository.findByStatus(PaymentStatus.PENDING).stream()
                .filter(payment -> payment.getCreatedAt().isBefore(cutoff))
                .forEach(this::fail);
        paymentRepository.findByStatus(PaymentStatus.PROCESSING).stream()
                .filter(payment -> payment.getUpdatedAt().isBefore(cutoff))
                .forEach(this::fail);
    }

    private void fail(Payment payment) { payment.setStatus(PaymentStatus.FAILED); }
}
