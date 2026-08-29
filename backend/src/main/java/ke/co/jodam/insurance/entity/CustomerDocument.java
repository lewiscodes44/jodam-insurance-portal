package ke.co.jodam.insurance.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "customer_documents")
public class CustomerDocument {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "customer_id") private User customer;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "inquiry_id") private InsuranceInquiry inquiry;
    @Column(name = "document_type", nullable = false, length = 40) private String documentType;
    @Column(name = "original_filename", nullable = false) private String originalFilename;
    @Column(name = "content_type", nullable = false) private String contentType;
    @Column(nullable = false, columnDefinition = "bytea") private byte[] content;
    @Column(name = "uploaded_at", nullable = false) private LocalDateTime uploadedAt = LocalDateTime.now();
    public Long getId(){return id;} public User getCustomer(){return customer;} public void setCustomer(User v){customer=v;} public InsuranceInquiry getInquiry(){return inquiry;} public void setInquiry(InsuranceInquiry v){inquiry=v;} public String getDocumentType(){return documentType;} public void setDocumentType(String v){documentType=v;} public String getOriginalFilename(){return originalFilename;} public void setOriginalFilename(String v){originalFilename=v;} public String getContentType(){return contentType;} public void setContentType(String v){contentType=v;} public byte[] getContent(){return content;} public void setContent(byte[] v){content=v;} public LocalDateTime getUploadedAt(){return uploadedAt;}
}
