import { useEffect, useState, type ReactNode } from 'react'
import { Link, useLocation } from 'react-router-dom'
import { Logo } from './Logo'
import { Icon } from './Icon'
import { useAuth } from '../context/AuthContext'
import { getMyNotifications } from '../lib/api'

export function AppShell({ children }: { children: ReactNode }) {
  const { username, signOut } = useAuth()
  const location = useLocation()
  const [open, setOpen] = useState(false)
  const [unread,setUnread]=useState(0)
  useEffect(()=>{getMyNotifications().then(items=>setUnread(items.filter(n=>!n.readAt).length)).catch(()=>{})},[location.pathname])
  const links = [
    { to: '/app', label: 'Overview' },
    { to: '/app/policies', label: 'My policies' },
    { to: '/app/quotations', label: 'Quotations' },
    { to: '/app/claims', label: 'Claims' },
    { to: '/app/notifications', label: 'Notifications' },
  ]

  return (
    <div className="app-shell">
      <aside className={`sidebar ${open ? 'sidebar--open' : ''}`}>
        <div className="sidebar__top"><Logo compact /><button className="icon-button sidebar__close" onClick={() => setOpen(false)}><Icon name="menu" /></button></div>
        <nav className="sidebar-nav">
          {links.map(link => <Link key={link.to} className={location.pathname === link.to ? 'active' : ''} to={link.to} onClick={() => setOpen(false)}>{link.label}{link.to==='/app/notifications'&&unread>0&&<span className="nav-badge">{unread}</span>}</Link>)}
        </nav>
        <div className="sidebar__bottom"><div className="user-chip"><span className="avatar">{(username ?? 'J')[0].toUpperCase()}</span><span>{username ?? 'Customer'}</span></div><button className="sidebar-logout" onClick={signOut}><Icon name="logout" /> Sign out</button></div>
      </aside>
      <div className="app-main"><header className="app-topbar"><button className="icon-button mobile-menu" onClick={() => setOpen(true)}><Icon name="menu" /></button><div className="topbar-spacer" /><div className="topbar-user"><span className="avatar avatar--small">{(username ?? 'J')[0].toUpperCase()}</span><span>{username ?? 'Customer'}</span></div></header><main className="app-content">{children}</main></div>
    </div>
  )
}
