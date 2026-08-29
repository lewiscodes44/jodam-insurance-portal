package ke.co.jodam.insurance.repository;
import ke.co.jodam.insurance.entity.CustomerDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface CustomerDocumentRepository extends JpaRepository<CustomerDocument, Long> {
    List<CustomerDocument> findByCustomerIdAndInquiryIsNullOrderByUploadedAtDesc(Long customerId);
    List<CustomerDocument> findByInquiryIdOrderByUploadedAtDesc(Long inquiryId);
    Optional<CustomerDocument> findFirstByCustomerIdAndInquiryIsNullAndDocumentTypeOrderByUploadedAtDesc(Long customerId, String documentType);
    Optional<CustomerDocument> findFirstByInquiryIdAndDocumentTypeOrderByUploadedAtDesc(Long inquiryId, String documentType);
}
