package ke.co.jodam.insurance.repository;

import ke.co.jodam.insurance.entity.InsuranceInquiry;
import ke.co.jodam.insurance.entity.InquiryStatus;
import ke.co.jodam.insurance.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InsuranceInquiryRepository
        extends JpaRepository<InsuranceInquiry, Long> {

    List<InsuranceInquiry> findByCustomer(User customer);

    List<InsuranceInquiry> findByAssignedAgent(User assignedAgent);

    List<InsuranceInquiry> findByStatus(InquiryStatus status);

    List<InsuranceInquiry> findAllByOrderByCreatedAtDesc();

    List<InsuranceInquiry>
    findByCustomerOrderByCreatedAtDesc(
            User customer
    );

    List<InsuranceInquiry>
    findByAssignedAgentOrderByCreatedAtDesc(
            User assignedAgent
    );

    List<InsuranceInquiry>
    findByStatusOrderByCreatedAtDesc(
            InquiryStatus status
    );

    long countByStatus(
            InquiryStatus status
    );
}