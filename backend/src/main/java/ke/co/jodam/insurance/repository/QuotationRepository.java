package ke.co.jodam.insurance.repository;

import ke.co.jodam.insurance.entity.InsuranceInquiry;
import ke.co.jodam.insurance.entity.Quotation;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface QuotationRepository
        extends JpaRepository<Quotation, Long> {

    Optional<Quotation> findByInquiry(
            InsuranceInquiry inquiry
    );

    List<Quotation> findByInquiryCustomerId(
            Long customerId
    );
}