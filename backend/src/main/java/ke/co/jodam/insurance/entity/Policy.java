package ke.co.jodam.insurance.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "policies",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_policy_number",
                        columnNames = "policy_number"
                )
        }
)
public class Policy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            name = "policy_number",
            nullable = false,
            unique = true,
            length = 50
    )
    private String policyNumber;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "inquiry_id",
            nullable = false,
            unique = true
    )
    private InsuranceInquiry inquiry;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "quotation_id",
            nullable = false,
            unique = true
    )
    private Quotation quotation;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "customer_id",
            nullable = false
    )
    private User customer;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "agent_id",
            nullable = false
    )
    private User agent;

    @Column(
            name = "insurance_type",
            nullable = false,
            length = 100
    )
    private String insuranceType;

    @Column(
            name = "premium_amount",
            nullable = false,
            precision = 12,
            scale = 2
    )
    private BigDecimal premiumAmount;

    @Column(
            name = "coverage_details",
            length = 5000
    )
    private String coverageDetails;


    @Column(name = "insurer", length = 150)
    private String insurer;
    @Column(name = "product", length = 150)
    private String product;
    @Column(name = "certificate_number", length = 80)
    private String certificateNumber;
    @Column(name = "certificate_class", length = 40)
    private String certificateClass;
    @Column(name = "valuation_reference", length = 100)
    private String valuationReference;
    @Column(name = "valuation_date")
    private LocalDate valuationDate;
    @Column(name = "documents_verified", nullable = false)
    private boolean documentsVerified;
    @Column(name = "policy_terms", length = 5000)
    private String policyTerms;    @Column(
            name = "start_date",
            nullable = false
    )
    private LocalDate startDate;

    @Column(
            name = "end_date",
            nullable = false
    )
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "status",
            nullable = false,
            length = 30
    )
    private PolicyStatus status;

    @Column(
            name = "cancellation_reason",
            length = 1000
    )
    private String cancellationReason;

    @Column(
            name = "cancelled_at"
    )
    private LocalDateTime cancelledAt;

    @Column(
            name = "created_at",
            nullable = false
    )
    private LocalDateTime createdAt;

    @Column(
            name = "updated_at",
            nullable = false
    )
    private LocalDateTime updatedAt;

    public Policy() {
    }

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();

        if (createdAt == null) {
            createdAt = now;
        }

        if (updatedAt == null) {
            updatedAt = now;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public String getPolicyNumber() {
        return policyNumber;
    }

    public void setPolicyNumber(String policyNumber) {
        this.policyNumber = policyNumber;
    }

    public InsuranceInquiry getInquiry() {
        return inquiry;
    }

    public void setInquiry(InsuranceInquiry inquiry) {
        this.inquiry = inquiry;
    }

    public Quotation getQuotation() {
        return quotation;
    }

    public void setQuotation(Quotation quotation) {
        this.quotation = quotation;
    }

    public User getCustomer() {
        return customer;
    }

    public void setCustomer(User customer) {
        this.customer = customer;
    }

    public User getAgent() {
        return agent;
    }

    public void setAgent(User agent) {
        this.agent = agent;
    }

    public String getInsuranceType() {
        return insuranceType;
    }

    public void setInsuranceType(String insuranceType) {
        this.insuranceType = insuranceType;
    }

    public BigDecimal getPremiumAmount() {
        return premiumAmount;
    }

    public void setPremiumAmount(BigDecimal premiumAmount) {
        this.premiumAmount = premiumAmount;
    }

    public String getCoverageDetails() {
        return coverageDetails;
    }

    public void setCoverageDetails(String coverageDetails) {
        this.coverageDetails = coverageDetails;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public PolicyStatus getStatus() {
        return status;
    }

    public void setStatus(PolicyStatus status) {
        this.status = status;
    }

    public String getCancellationReason() {
        return cancellationReason;
    }

    public void setCancellationReason(String cancellationReason) {
        this.cancellationReason = cancellationReason;
    }

    public LocalDateTime getCancelledAt() {
        return cancelledAt;
    }

    public void setCancelledAt(LocalDateTime cancelledAt) {
        this.cancelledAt = cancelledAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public String getInsurer(){return insurer;} public void setInsurer(String v){insurer=v;}
    public String getProduct(){return product;} public void setProduct(String v){product=v;}
    public String getCertificateNumber(){return certificateNumber;} public void setCertificateNumber(String v){certificateNumber=v;}
    public String getCertificateClass(){return certificateClass;} public void setCertificateClass(String v){certificateClass=v;}
    public String getValuationReference(){return valuationReference;} public void setValuationReference(String v){valuationReference=v;}
    public LocalDate getValuationDate(){return valuationDate;} public void setValuationDate(LocalDate v){valuationDate=v;}
    public boolean isDocumentsVerified(){return documentsVerified;} public void setDocumentsVerified(boolean v){documentsVerified=v;}
    public String getPolicyTerms(){return policyTerms;} public void setPolicyTerms(String v){policyTerms=v;}}