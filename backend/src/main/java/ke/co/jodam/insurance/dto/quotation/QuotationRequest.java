package ke.co.jodam.insurance.dto.quotation;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public class QuotationRequest {

    @NotBlank(message = "Insurer is required")
    @Size(max = 150, message = "Insurer must not exceed 150 characters")
    private String insurer;

    @NotBlank(message = "Product is required")
    @Size(max = 150, message = "Product must not exceed 150 characters")
    private String product;

    @NotNull(message = "Basic premium is required")
    @DecimalMin(value = "0.01", message = "Basic premium must be greater than zero")
    private BigDecimal basicPremium;

    @DecimalMin(value = "0.00", message = "Training levy cannot be negative")
    private BigDecimal trainingLevy;

    @DecimalMin(value = "0.00", message = "PHCF levy cannot be negative")
    private BigDecimal phcfLevy;

    @DecimalMin(value = "0.00", message = "Stamp duty cannot be negative")
    private BigDecimal stampDuty;

    @DecimalMin(value = "0.00", message = "Other charges cannot be negative")
    private BigDecimal otherCharges;

    @NotNull(message = "Valid until date is required")
    private LocalDate validUntil;

    private LocalDate proposedStartDate;

    private LocalDate proposedEndDate;

    @Size(max = 1000, message = "Excess must not exceed 1000 characters")
    private String excess;

    @Size(max = 5000, message = "Special terms must not exceed 5000 characters")
    private String specialTerms;

    @Size(max = 5000, message = "Agent notes must not exceed 5000 characters")
    private String agentNotes;

    @Size(max = 5000, message = "Coverage details must not exceed 5000 characters")
    private String coverageDetails;

    public QuotationRequest() {
    }

    public String getInsurer() {
        return insurer;
    }

    public void setInsurer(String insurer) {
        this.insurer = insurer;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public BigDecimal getBasicPremium() {
        return basicPremium;
    }

    public void setBasicPremium(BigDecimal basicPremium) {
        this.basicPremium = basicPremium;
    }

    public BigDecimal getTrainingLevy() {
        return trainingLevy;
    }

    public void setTrainingLevy(BigDecimal trainingLevy) {
        this.trainingLevy = trainingLevy;
    }

    public BigDecimal getPhcfLevy() {
        return phcfLevy;
    }

    public void setPhcfLevy(BigDecimal phcfLevy) {
        this.phcfLevy = phcfLevy;
    }

    public BigDecimal getStampDuty() {
        return stampDuty;
    }

    public void setStampDuty(BigDecimal stampDuty) {
        this.stampDuty = stampDuty;
    }

    public BigDecimal getOtherCharges() {
        return otherCharges;
    }

    public void setOtherCharges(BigDecimal otherCharges) {
        this.otherCharges = otherCharges;
    }

    public LocalDate getValidUntil() {
        return validUntil;
    }

    public void setValidUntil(LocalDate validUntil) {
        this.validUntil = validUntil;
    }

    public LocalDate getProposedStartDate() {
        return proposedStartDate;
    }

    public void setProposedStartDate(LocalDate proposedStartDate) {
        this.proposedStartDate = proposedStartDate;
    }

    public LocalDate getProposedEndDate() {
        return proposedEndDate;
    }

    public void setProposedEndDate(LocalDate proposedEndDate) {
        this.proposedEndDate = proposedEndDate;
    }

    public String getExcess() {
        return excess;
    }

    public void setExcess(String excess) {
        this.excess = excess;
    }

    public String getSpecialTerms() {
        return specialTerms;
    }

    public void setSpecialTerms(String specialTerms) {
        this.specialTerms = specialTerms;
    }

    public String getAgentNotes() {
        return agentNotes;
    }

    public void setAgentNotes(String agentNotes) {
        this.agentNotes = agentNotes;
    }

    public String getCoverageDetails() {
        return coverageDetails;
    }

    public void setCoverageDetails(String coverageDetails) {
        this.coverageDetails = coverageDetails;
    }
}