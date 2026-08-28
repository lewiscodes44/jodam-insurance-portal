package ke.co.jodam.insurance.repository;

import ke.co.jodam.insurance.entity.Policy;
import ke.co.jodam.insurance.entity.PolicyStatus;
import ke.co.jodam.insurance.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PolicyRepository
        extends JpaRepository<Policy, Long> {

    Optional<Policy> findByPolicyNumber(
            String policyNumber
    );

    Optional<Policy> findByInquiryId(
            Long inquiryId
    );

    Optional<Policy> findByQuotationId(
            Long quotationId
    );

    List<Policy> findByCustomerOrderByCreatedAtDesc(
            User customer
    );

    List<Policy> findByAgentOrderByCreatedAtDesc(
            User agent
    );

    List<Policy> findByStatusOrderByCreatedAtDesc(
            PolicyStatus status
    );

    List<Policy> findAllByOrderByCreatedAtDesc();

    long countByStatus(
            PolicyStatus status
    );
}