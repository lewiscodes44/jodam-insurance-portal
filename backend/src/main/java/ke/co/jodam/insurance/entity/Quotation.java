package ke.co.jodam.insurance.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "quotations")
public class Quotation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "inquiry_id", nullable = false, unique = true)
    private InsuranceInquiry inquiry;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "agent_id", nullable = false)
    private User agent;

    @Column(name = "insurer", nullable = false, length = 150)
    private String insurer;

    @Column(name = "product", nullable = false, length = 150)
    private String product;

    @Column(name = "basic_premium", nullable = false, precision = 12, scale = 2)
    private BigDecimal basicPremium;

    @Column(name = "training_levy", nullable = false, precision = 12, scale = 2)
    private BigDecimal trainingLevy = BigDecimal.ZERO;

    @Column(name = "phcf_levy", nullable = false, precision = 12, scale = 2)
    private BigDecimal phcfLevy = BigDecimal.ZERO;

    @Column(name = "stamp_duty", nullable = false, precision = 12, scale = 2)
    private BigDecimal stampDuty = BigDecimal.ZERO;

    @Column(name = "other_charges", nullable = false, precision = 12, scale = 2)
    private BigDecimal otherCharges = BigDecimal.ZERO;

    @Column(name = "total_payable", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalPayable;

    /** Legacy field retained because policy/reporting code still uses it. */
    @Column(name = "premium_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal premiumAmount;

    @Column(name = "coverage_details", length = 5000)
    private String coverageDetails;

    @Column(name = "quote_reference", nullable = false, unique = true, length = 50)
    private String quoteReference;

    @Column(name = "valid_until", nullable = false)
    private LocalDate validUntil;

    @Column(name = "proposed_start_date")
    private LocalDate proposedStartDate;

    @Column(name = "proposed_end_date")
    private LocalDate proposedEndDate;

    @Column(name = "excess", length = 1000)
    private String excess;

    @Column(name = "special_terms", length = 5000)
    private String specialTerms;

    @Column(name = "agent_notes", length = 5000)
    private String agentNotes;

    @Column(name = "customer_review_message", length = 3000)
    private String customerReviewMessage;

    @Column(name = "review_requested_at")
    private LocalDateTime reviewRequestedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private QuotationStatus status = QuotationStatus.DRAFT;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "sent_at")
    private LocalDateTime sentAt;

    public Quotation() {
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
        normalizeAmounts();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        normalizeAmounts();
    }

    public void calculateTotalPayable() {
        basicPremium = safeAmount(basicPremium);
        trainingLevy = safeAmount(trainingLevy);
        phcfLevy = safeAmount(phcfLevy);
        stampDuty = safeAmount(stampDuty);
        otherCharges = safeAmount(otherCharges);

        totalPayable = basicPremium
                .add(trainingLevy)
                .add(phcfLevy)
                .add(stampDuty)
                .add(otherCharges)
                .setScale(2, RoundingMode.HALF_UP);
        premiumAmount = totalPayable;
    }

    private void normalizeAmounts() {
        if (totalPayable == null) {
            calculateTotalPayable();
            return;
        }

        basicPremium = safeAmount(basicPremium);
        trainingLevy = safeAmount(trainingLevy);
        phcfLevy = safeAmount(phcfLevy);
        stampDuty = safeAmount(stampDuty);
        otherCharges = safeAmount(otherCharges);
        totalPayable = totalPayable.setScale(2, RoundingMode.HALF_UP);
        premiumAmount = totalPayable;
    }

    private BigDecimal safeAmount(BigDecimal amount) {
        return amount == null
                ? BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP)
                : amount.setScale(2, RoundingMode.HALF_UP);
    }

    public Long getId() { return id; }
    public InsuranceInquiry getInquiry() { return inquiry; }
    public void setInquiry(InsuranceInquiry inquiry) { this.inquiry = inquiry; }
    public User getAgent() { return agent; }
    public void setAgent(User agent) { this.agent = agent; }
    public String getInsurer() { return insurer; }
    public void setInsurer(String insurer) { this.insurer = insurer; }
    public String getProduct() { return product; }
    public void setProduct(String product) { this.product = product; }
    public BigDecimal getBasicPremium() { return basicPremium; }
    public void setBasicPremium(BigDecimal basicPremium) { this.basicPremium = basicPremium; }
    public BigDecimal getTrainingLevy() { return trainingLevy; }
    public void setTrainingLevy(BigDecimal trainingLevy) { this.trainingLevy = trainingLevy; }
    public BigDecimal getPhcfLevy() { return phcfLevy; }
    public void setPhcfLevy(BigDecimal phcfLevy) { this.phcfLevy = phcfLevy; }
    public BigDecimal getStampDuty() { return stampDuty; }
    public void setStampDuty(BigDecimal stampDuty) { this.stampDuty = stampDuty; }
    public BigDecimal getOtherCharges() { return otherCharges; }
    public void setOtherCharges(BigDecimal otherCharges) { this.otherCharges = otherCharges; }
    public BigDecimal getTotalPayable() { return totalPayable; }
    public void setTotalPayable(BigDecimal totalPayable) { this.totalPayable = totalPayable; }
    public BigDecimal getPremiumAmount() { return premiumAmount; }
    public void setPremiumAmount(BigDecimal premiumAmount) { this.premiumAmount = premiumAmount; }
    public String getCoverageDetails() { return coverageDetails; }
    public void setCoverageDetails(String coverageDetails) { this.coverageDetails = coverageDetails; }
    public String getQuoteReference() { return quoteReference; }
    public void setQuoteReference(String quoteReference) { this.quoteReference = quoteReference; }
    public LocalDate getValidUntil() { return validUntil; }
    public void setValidUntil(LocalDate validUntil) { this.validUntil = validUntil; }
    public LocalDate getProposedStartDate() { return proposedStartDate; }
    public void setProposedStartDate(LocalDate proposedStartDate) { this.proposedStartDate = proposedStartDate; }
    public LocalDate getProposedEndDate() { return proposedEndDate; }
    public void setProposedEndDate(LocalDate proposedEndDate) { this.proposedEndDate = proposedEndDate; }
    public String getExcess() { return excess; }
    public void setExcess(String excess) { this.excess = excess; }
    public String getSpecialTerms() { return specialTerms; }
    public void setSpecialTerms(String specialTerms) { this.specialTerms = specialTerms; }
    public String getAgentNotes() { return agentNotes; }
    public void setAgentNotes(String agentNotes) { this.agentNotes = agentNotes; }
    public String getCustomerReviewMessage() { return customerReviewMessage; }
    public void setCustomerReviewMessage(String value) { customerReviewMessage = value; }
    public LocalDateTime getReviewRequestedAt() { return reviewRequestedAt; }
    public void setReviewRequestedAt(LocalDateTime value) { reviewRequestedAt = value; }
    public QuotationStatus getStatus() { return status; }
    public void setStatus(QuotationStatus status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public LocalDateTime getSentAt() { return sentAt; }
    public void setSentAt(LocalDateTime sentAt) { this.sentAt = sentAt; }
}
