import type { Inquiry } from './api'

export function vehicleTitle(inquiry: Inquiry | undefined, cover?: string) {
  const data = inquiry?.applicationData
  const registration = data?.registrationNumber?.trim() || 'Registration pending'
  const makeModel = [data?.make, data?.model].filter(Boolean).join(' ') || 'Vehicle details pending'
  return { registration, makeModel, cover: inquiry?.insuranceType || cover || 'Motor cover' }
}

export function inquiryById(inquiries: Inquiry[], inquiryId: number) {
  return inquiries.find(inquiry => inquiry.id === inquiryId)
}
