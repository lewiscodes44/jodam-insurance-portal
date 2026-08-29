import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { AppShell } from '../components/AppShell'
import { StatusPill } from '../components/StatusPill'
import { getMyNotifications, getMyQuotations, markNotificationsRead, type Notification, type Quotation } from '../lib/api'

export function NotificationsPage(){
  const [items,setItems]=useState<Notification[]>([]); const [quotes,setQuotes]=useState<Quotation[]>([]); const [loading,setLoading]=useState(true); const [error,setError]=useState('')
  useEffect(()=>{Promise.all([getMyNotifications(),getMyQuotations()]).then(([notifications,quotations])=>{setItems(notifications);setQuotes(quotations);return markNotificationsRead()}).catch(e=>setError(e instanceof Error?e.message:'Unable to load notifications')).finally(()=>setLoading(false))},[])
  const quoteFor=(notification:Notification)=>quotes.find(q=>notification.message.includes(q.quoteReference))
  return <AppShell><div className="page-heading"><div><span className="eyebrow eyebrow--red">Updates</span><h1>Notifications</h1><p>Payment confirmations, policy updates and important messages from Jodam.</p></div></div>{error&&<div className="form-error">{error}</div>}{loading?<div className="loading-state">Loading notifications…</div>:<div className="stack-list">{items.map(n=>{const quote=quoteFor(n);const card=<><div><span className="eyebrow">{n.channel}</span><h2>{n.subject||'Jodam Insurance update'}</h2><p>{n.message}</p><small>{new Date(n.createdAt).toLocaleString()} · {n.recipient}</small></div><StatusPill value={n.status}/></>;return quote?<Link className="list-card" key={n.id} to={`/app/quotations/${quote.id}`}>{card}</Link>:<article className="list-card list-card--static" key={n.id}>{card}</article>})}{!items.length&&<div className="panel empty-state">You're all caught up.</div>}</div>}</AppShell>
}
