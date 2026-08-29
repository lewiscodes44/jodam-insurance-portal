import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { AppShell } from '../components/AppShell'
import { Icon } from '../components/Icon'
import { StatusPill } from '../components/StatusPill'
import { getMyClaims, getMyInquiries, getMyNotifications, getMyPolicies, getMyQuotations, type Claim, type Inquiry, type Notification, type Policy, type Quotation } from '../lib/api'
import { inquiryById, vehicleTitle } from '../lib/vehicle'

export function CustomerDashboardPage() {
  const [policies, setPolicies] = useState<Policy[]>([])
  const [quotations, setQuotations] = useState<Quotation[]>([])
  const [inquiries, setInquiries] = useState<Inquiry[]>([])
  const [claims, setClaims] = useState<Claim[]>([])
  const [notifications, setNotifications] = useState<Notification[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  useEffect(() => {
    Promise.all([getMyPolicies(), getMyQuotations(), getMyInquiries(), getMyClaims(), getMyNotifications()])
      .then(([p, q, i, c, n]) => { setPolicies(p); setQuotations(q); setInquiries(i); setClaims(c); setNotifications(n) })
      .catch(err => setError(err instanceof Error ? err.message : 'Unable to load your portal'))
      .finally(() => setLoading(false))
  }, [])

  const activePolicies = policies.filter(p => p.status === 'ACTIVE')
  const pendingPayments = policies.filter(p => p.status === 'PENDING_PAYMENT')
  const activeQuotations = quotations.filter(q => !policies.some(p => p.quotationId === q.id))
  const currentUpdates = notifications.filter(n => !quotations.some(q => n.message.includes(q.quoteReference) && policies.some(p => p.quotationId === q.id)))
  const updateMessage = (message:string) => { const policy=policies.find(p=>message.includes(p.policyNumber)); return policy ? message.replace(/Amount due: KES [\d,.]+/i,`Amount due: KES ${Number(policy.premiumAmount).toLocaleString()}`) : message }
  const unread = notifications.filter(n => !n.readAt).length

  return <AppShell>
    <div className="page-heading"><div><span className="eyebrow eyebrow--red">Customer portal</span><h1>Your insurance, in one place.</h1><p>Track your cover, quotations, claims and payment activity.</p></div><Link className="button button--primary" to="/app/enquiries/new"><Icon name="plus" /> New motor enquiry</Link></div>
    {error && <div className="form-error">{error}</div>}
    {loading ? <div className="loading-state">Loading your portal…</div> : <>
      <section className="stat-grid"><Link className="stat-card" to="/app/policies"><span>Active policies</span><strong>{activePolicies.length}</strong><small>Currently in force</small></Link><Link className="stat-card" to="/app/quotations"><span>Quotations</span><strong>{activeQuotations.length}</strong><small>Available to review</small></Link><Link className="stat-card" to="/app/claims"><span>Open claims</span><strong>{claims.filter(c => !['CLOSED','REJECTED'].includes(c.status)).length}</strong><small>Across your policies</small></Link><Link className="stat-card" to="/app/notifications"><span>Notifications</span><strong>{unread}</strong><small>Need your attention</small></Link></section>
      {pendingPayments.length > 0 && <div className="notice-banner"><Icon name="receipt" /><div><strong>Payment required</strong><p>You have {pendingPayments.length} policy{pendingPayments.length > 1 ? 'ies' : ''} waiting for payment.</p></div><Link className="text-link" to={`/app/policies/${pendingPayments[0].id}`}>Review <Icon name="arrow" /></Link></div>}
      <section className="dashboard-grid"><div className="panel panel--wide"><div className="panel__header"><div><span className="eyebrow">Policies</span><h2>Your cover</h2></div><Link className="text-link" to="/app/policies">View all <Icon name="arrow" /></Link></div><div className="policy-list">{policies.slice(0,3).map(policy => { const vehicle=vehicleTitle(inquiryById(inquiries,policy.inquiryId),policy.insuranceType); return <Link className="policy-row" key={policy.id} to={`/app/policies/${policy.id}`}><div className="policy-icon"><Icon name="car" /></div><div className="policy-main"><strong>{vehicle.registration} · {vehicle.makeModel}</strong><span>{vehicle.cover} · {policy.policyNumber}</span></div><div className="policy-meta"><span>KES {Number(policy.premiumAmount).toLocaleString()}</span><StatusPill value={policy.status} /></div></Link> })}{policies.length===0 && <div className="empty-state empty-state--action"><p>No policies yet.</p><Link className="button button--secondary" to="/app/enquiries/new">Start a motor enquiry</Link></div>}</div></div>
      <div className="panel"><div className="panel__header"><div><span className="eyebrow">Recent activity</span><h2>Enquiries</h2></div><Link className="text-link" to="/app/enquiries">View all <Icon name="arrow" /></Link></div><div className="activity-list">{inquiries.slice(0,4).map(item => { const vehicle=vehicleTitle(item); return <Link className="activity-row" key={item.id} to={`/app/enquiries/${item.id}`}><div><strong>{vehicle.registration} · {vehicle.makeModel}</strong><span>{vehicle.cover}</span></div><StatusPill value={item.status} /></Link> })}{inquiries.length===0 && <p className="empty-state">No enquiries yet.</p>}</div></div></section>
      <section className="dashboard-grid dashboard-grid--lower"><div className="panel"><div className="panel__header"><div><span className="eyebrow">Quotations</span><h2>Ready to review</h2></div><Link className="text-link" to="/app/quotations">View all <Icon name="arrow" /></Link></div>{activeQuotations.slice(0,3).map(q => { const vehicle=vehicleTitle(inquiryById(inquiries,q.inquiryId)); return <div className="quote-card" key={q.id}><div><strong>{vehicle.registration} · {vehicle.makeModel}</strong><span>{vehicle.cover} · KES {Number(q.totalPayable??q.premiumAmount).toLocaleString()} · Valid until {q.validUntil}</span></div><Link className="button button--secondary" to={`/app/quotations/${q.id}`}>Review</Link></div> })}{activeQuotations.length===0 && <div className="empty-state empty-state--action"><p>No quotations need your decision.</p><Link className="text-link" to="/app/policies">View policies <Icon name="arrow" /></Link></div>}</div><div className="panel"><div className="panel__header"><div><span className="eyebrow">Notifications</span><h2>Latest updates</h2></div><Link className="text-link" to="/app/notifications">All <Icon name="arrow" /></Link></div>{currentUpdates.slice(0,3).map(n => <div className="notification-row" key={n.id}><div className="notification-dot" /><div><strong>{n.subject ?? 'Jodam update'}</strong><p>{updateMessage(n.message)}</p></div></div>)}{currentUpdates.length===0 && <p className="empty-state">You're all caught up.</p>}</div></section>
    </>}
  </AppShell>
}
