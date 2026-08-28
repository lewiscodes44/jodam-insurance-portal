import { useState, type ReactNode } from 'react'
import { Link, useLocation, useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import { Logo } from './Logo'
import { Icon, type IconName } from './Icon'

export function StaffShell({ children }: { children: ReactNode }) {
  const {username,role,signOut}=useAuth(); const location=useLocation(); const navigate=useNavigate(); const [open,setOpen]=useState(false)
  const links=role==='ADMIN' ? [
    ['/staff','Dashboard','briefcase'],['/staff/inquiries','Inquiries','receipt'],['/staff/claims','Claims','claim'],['/staff/policies','Policies','shield'],['/staff/staff','Staff','users']
  ] : [['/staff','Dashboard','briefcase'],['/staff/inquiries','My inquiries','receipt'],['/staff/claims','My claims','claim']]
  function logout(){signOut();navigate('/staff/login',{replace:true})}
  return <div className="staff-shell"><aside className={`staff-sidebar ${open?'staff-sidebar--open':''}`}><div className="staff-sidebar__top"><Logo compact/><button className="icon-button staff-sidebar__close" onClick={()=>setOpen(false)} aria-label="Close menu"><Icon name="menu"/></button></div><div className="staff-role"><span>Jodam Operations</span><strong>{role==='ADMIN'?'Administrator':'Insurance Agent'}</strong></div><nav className="staff-nav">{links.map(([to,label,icon])=><Link key={to} to={to} className={location.pathname===to?'active':''} onClick={()=>setOpen(false)}><Icon name={icon as IconName}/><span>{label}</span></Link>)}</nav><div className="staff-sidebar__bottom"><div className="user-chip"><span className="avatar">{(username??'J')[0].toUpperCase()}</span><span>{username}</span></div><button className="sidebar-logout" onClick={logout}><Icon name="logout"/> Sign out</button></div></aside><div className="staff-main"><header className="staff-topbar"><button className="icon-button mobile-menu" onClick={()=>setOpen(true)}><Icon name="menu"/></button><div className="topbar-spacer"/><div className="staff-topbar__identity"><span className="avatar avatar--small">{(username??'J')[0].toUpperCase()}</span><span>{username}</span><b>{role}</b></div></header><main className="staff-content">{children}</main></div></div>
}
