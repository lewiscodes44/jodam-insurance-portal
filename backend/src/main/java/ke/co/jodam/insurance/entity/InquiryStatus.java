package ke.co.jodam.insurance.entity;

/**
 * Lifecycle status of a customer insurance inquiry.
 *
 * Legacy values remain so existing database records can still be read while
 * the application transitions to the explicit processing workflow.
 */
public enum InquiryStatus {

    NEW,
    ASSIGNED,
    UNDER_REVIEW,
    QUOTATION_DRAFT,
    QUOTATION_SENT,
    CUSTOMER_ACCEPTED,
    CUSTOMER_DECLINED,
    POLICY_PENDING_PAYMENT,

    /** @deprecated legacy status retained for compatibility. */
    @Deprecated
    QUOTED,

    /** @deprecated legacy status retained for compatibility. */
    @Deprecated
    ACCEPTED,

    /** @deprecated legacy status retained for compatibility. */
    @Deprecated
    REJECTED,

    /** @deprecated legacy status retained for compatibility. */
    @Deprecated
    CONVERTED
}
