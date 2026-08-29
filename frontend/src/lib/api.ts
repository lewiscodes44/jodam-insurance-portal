const API_BASE = import.meta.env.VITE_API_BASE_URL ?? ''

export async function apiRequest<T>(path: string, options: RequestInit = {}): Promise<T> {
  const token = localStorage.getItem('jodam.token')
  const headers = new Headers(options.headers)
  if (!headers.has('Content-Type') && options.body && !(options.body instanceof FormData)) headers.set('Content-Type', 'application/json')
  if (token) headers.set('Authorization', `Bearer ${token}`)
  const response = await fetch(`${API_BASE}${path}`, { ...options, headers })
  if (!response.ok) {
    let message = `Request failed (${response.status})`
    try { const body = await response.json(); message = body.message ?? body.error ?? message } catch { /* noop */ }
    if (response.status === 401) { localStorage.removeItem('jodam.token'); localStorage.removeItem('jodam.username'); localStorage.removeItem('jodam.role') }
    throw new Error(message)
  }
  if (response.status === 204) return undefined as T
  return response.json() as Promise<T>
}

export type AuthResponse = { message: string; username: string; token: string; role?: string }
export function login(username: string, password: string) { return apiRequest<AuthResponse>('/api/auth/login', { method: 'POST', body: JSON.stringify({ username, password }) }) }
export function register(payload: { username: string; email: string; password: string; firstName: string; lastName: string; phoneNumber: string }) { return apiRequest<AuthResponse>('/api/auth/register', { method: 'POST', body: JSON.stringify(payload) }) }

export type Policy = { id:number; policyNumber:string; inquiryId:number; quotationId:number; customerUsername:string; agentUsername:string; insuranceType:string; premiumAmount:number; coverageDetails:string; startDate:string; endDate:string; status:string; insurer?:string; product?:string; certificateNumber?:string; certificateClass?:string; valuationReference?:string; valuationDate?:string; documentsVerified?:boolean; policyTerms?:string; createdAt?:string; updatedAt?:string }
export type Quotation = { id:number; inquiryId:number; customerUsername:string; agentUsername:string; insurer:string; product:string; basicPremium:number; trainingLevy?:number; phcfLevy?:number; stampDuty?:number; otherCharges?:number; totalPayable:number; premiumAmount:number; coverageDetails:string; specialTerms?:string; excess?:string; agentNotes?:string; customerReviewMessage?:string; reviewRequestedAt?:string; validUntil:string; proposedStartDate?:string; proposedEndDate?:string; status:string; quoteReference:string; createdAt:string }
export type InquiryApplication = {
  pin?: string; profession?: string; insuranceStartDate?: string; vehicleType?: string; registrationNumber?: string; make?: string; model?: string;
  bodyType?: string; yearOfManufacture?: string; chassisNumber?: string; engineNumber?: string; engineCapacity?: string; seatingCapacity?: string;
  estimatedValue?: string; windscreenValue?: string; accessoriesValue?: string; datePurchased?: string; antiTheftDevices?: string;
  leftHandDrive?: string; localDealer?: string; modified?: string; modificationDetails?: string; reconditioned?: string; importOrigin?: string;
  financierInterest?: string; financierDetails?: string; usage?: string[]; vehicleLocation?: string; hireOrReward?: string;
  drivers?: { name:string; age:string; occupation:string; licenceDate:string; experience:string; provisional:string }[];
  previousInsurance?: string; previousInsurer?: string; accidentHistory?: { date:string; cost:string; details:string }[];
  insurerDeclined?: string; insurerPremiumIncrease?: string; noClaimDiscount?: string; additionalCovers?: Record<string, boolean>; additionalNotes?: string; declarationsAccepted?: boolean;
}
export type Inquiry = { id:number; insuranceType:string; description:string; applicationData?:InquiryApplication; status:string; customerUsername:string; assignedAgentUsername?:string; createdAt:string; updatedAt:string }
export type Notification = { id:number; channel:string; recipient:string; subject?:string; message:string; status:string; readAt?:string; createdAt:string; updatedAt:string }
export type Claim = { id:number; claimNumber:string; policyId:number; policyNumber:string; customerUsername:string; assignedAgentUsername?:string; incidentDate:string; description:string; claimedAmount:number; status:string; decisionReason?:string; approvedAmount?:number; reviewedAt?:string; settledAt?:string; closedAt?:string; createdAt:string; updatedAt:string }
export type Payment = { id:number; policyId:number; policyNumber:string; amount:number; phoneNumber:string; transactionReference?:string; checkoutRequestId?:string; status:string; createdAt:string; updatedAt:string }
export type Dashboard = { totalInquiries:number; newInquiries:number; assignedInquiries:number; quotedInquiries:number; acceptedInquiries:number; rejectedInquiries:number; convertedInquiries:number; totalPolicies:number; pendingPaymentPolicies:number; activePolicies:number; expiredPolicies:number; cancelledPolicies:number; totalPayments:number; pendingPayments:number; processingPayments:number; completedPayments:number; failedPayments:number; cancelledPayments:number; totalCompletedPaymentAmount:number }
export type StaffSummary = { id:number; username:string; firstName:string; lastName:string; email:string; phoneNumber?:string; role:string; active:boolean }
export type CustomerDocument = { id:number; documentType:string; filename:string; contentType:string; inquiryId?:number; uploadedAt:string }

export const getMyPolicies = () => apiRequest<Policy[]>('/api/policies/my')
export const getAllPolicies = () => apiRequest<Policy[]>('/api/policies/all')
export const getPolicy = (id:number) => apiRequest<Policy>(`/api/policies/${id}`)
export const getMyQuotations = () => apiRequest<Quotation[]>('/api/quotations/my')
export const getMyInquiries = () => apiRequest<Inquiry[]>('/api/inquiries/my')
export const getNewInquiries = () => apiRequest<Inquiry[]>('/api/inquiries/new')
export const getAllInquiries = () => apiRequest<Inquiry[]>('/api/inquiries/all')
export const getAssignedInquiries = () => apiRequest<Inquiry[]>('/api/inquiries/assigned')
export const getMyInquiry = (id:number) => apiRequest<Inquiry>(`/api/inquiries/${id}`)
export const createInquiry = (payload:{insuranceType:string; description:string; applicationData?:InquiryApplication}) => apiRequest<Inquiry>('/api/inquiries', { method:'POST', body:JSON.stringify(payload) })
export const assignInquiry = (id:number, agentUsername:string) => apiRequest<Inquiry>(`/api/inquiries/${id}/assign`, { method:'POST', body:JSON.stringify({ agentUsername }) })
export type CreateQuotationPayload = { insurer:string; product:string; basicPremium:number; validUntil:string; trainingLevy?:number; phcfLevy?:number; stampDuty?:number; otherCharges?:number; proposedStartDate?:string; proposedEndDate?:string; excess?:string; coverageDetails?:string; specialTerms?:string; agentNotes?:string }
export const createQuotation = (inquiryId:number, payload:CreateQuotationPayload) => apiRequest<Quotation>(`/api/quotations/inquiry/${inquiryId}`, { method:'POST', body:JSON.stringify(payload) })
export const updateQuotation = (id:number, payload:CreateQuotationPayload) => apiRequest<Quotation>(`/api/quotations/${id}`, { method:'PUT', body:JSON.stringify(payload) })
export const sendQuotation = (id:number) => apiRequest<Quotation>(`/api/quotations/${id}/send`, { method:'POST' })
export const getQuotationForInquiry = (inquiryId:number) => apiRequest<Quotation>(`/api/quotations/inquiry/${inquiryId}`)
export const acceptQuotation = (id:number) => apiRequest<Quotation>(`/api/quotations/${id}/accept`, { method:'POST' })
export const rejectQuotation = (id:number) => apiRequest<Quotation>(`/api/quotations/${id}/reject`, { method:'POST' })
export const requestQuotationReview = (id:number, message:string) => apiRequest<Quotation>(`/api/quotations/${id}/request-review`, { method:'POST', body:JSON.stringify({message}) })
export type IssuePolicyPayload = { startDate:string; durationMonths:number; certificateNumber:string; certificateClass:string; valuationReference?:string; valuationDate?:string; documentsVerified:boolean; policyTerms?:string }
export const issuePolicy = (quotationId:number, payload:IssuePolicyPayload) => apiRequest<Policy>(`/api/policies/quotation/${quotationId}`, { method:'POST', body:JSON.stringify(payload) })
export const renewPolicy = (policyId:number, newEndDate:string) => apiRequest<Policy>(`/api/policies/${policyId}/renew`, { method:'POST', body:JSON.stringify({newEndDate}) })
export const cancelPolicy = (policyId:number, reason:string) => apiRequest<Policy>(`/api/policies/${policyId}/cancel`, { method:'POST', body:JSON.stringify({reason}) })
export const initiatePayment = (policyId:number, phoneNumber:string) => apiRequest<Payment>(`/api/payments/policy/${policyId}`, { method:'POST', body:JSON.stringify({phoneNumber}) })
export const queryPayment = (paymentId:number) => apiRequest<Payment>(`/api/payments/${paymentId}/query`, { method:'POST' })
export const getMyNotifications = () => apiRequest<Notification[]>('/api/notifications/my')
export const markNotificationsRead = () => apiRequest<void>('/api/notifications/read-all', {method:'POST'})
export const markNotificationRead = (id:number) => apiRequest<void>(`/api/notifications/${id}/read`, {method:'POST'})
export const getMyClaims = () => apiRequest<Claim[]>('/api/claims/my')
export const getAllClaims = () => apiRequest<Claim[]>('/api/claims/all')
export const getAssignedClaims = () => apiRequest<Claim[]>('/api/claims/assigned')
export const getClaim = (id:number) => apiRequest<Claim>(`/api/claims/${id}`)
export const submitClaim = (policyId:number, payload:{incidentDate:string; description:string; claimedAmount:number}) => apiRequest<Claim>(`/api/claims/policy/${policyId}`, { method:'POST', body:JSON.stringify(payload) })
export const reviewClaim = (id:number) => apiRequest<Claim>(`/api/claims/${id}/review`, { method:'POST' })
export const approveClaim = (id:number, payload:{approvedAmount:number; decisionReason:string}) => apiRequest<Claim>(`/api/claims/${id}/approve`, { method:'POST', body:JSON.stringify(payload) })
export const rejectClaim = (id:number, decisionReason:string) => apiRequest<Claim>(`/api/claims/${id}/reject`, { method:'POST', body:JSON.stringify({decisionReason}) })
export const settleClaim = (id:number) => apiRequest<Claim>(`/api/claims/${id}/settle`, { method:'POST' })
export const closeClaim = (id:number) => apiRequest<Claim>(`/api/claims/${id}/close`, { method:'POST' })
export const getAdminDashboard = () => apiRequest<Dashboard>('/api/admin/reports/dashboard')
export const getInquiryReport = () => apiRequest<Inquiry[]>('/api/admin/reports/inquiries')
export const getPaymentReport = () => apiRequest<Payment[]>('/api/admin/reports/payments')
export const getAgents = () => apiRequest<StaffSummary[]>('/api/staff/agents')
export const getProfileDocuments = () => apiRequest<CustomerDocument[]>('/api/documents/profile')
export const getCustomerProfileDocuments = (username:string) => apiRequest<CustomerDocument[]>(`/api/documents/customers/${username}`)
export const getInquiryDocuments = (inquiryId:number) => apiRequest<CustomerDocument[]>(`/api/documents/inquiries/${inquiryId}`)
export const uploadProfileDocument = (type:string, file:File) => { const body=new FormData(); body.append('file',file); return apiRequest<CustomerDocument>(`/api/documents/profile/${type}`,{method:'POST',body}) }
export const uploadInquiryDocument = (inquiryId:number,type:string,file:File) => { const body=new FormData(); body.append('file',file); return apiRequest<CustomerDocument>(`/api/documents/inquiries/${inquiryId}/${type}`,{method:'POST',body}) }
