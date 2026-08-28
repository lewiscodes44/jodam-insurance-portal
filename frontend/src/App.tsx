import type { ReactElement } from 'react'
import { BrowserRouter, Navigate, Route, Routes } from 'react-router-dom'
import { AuthProvider, useAuth, type UserRole } from './context/AuthContext'
import { HomePage } from './pages/HomePage'
import { LoginPage } from './pages/LoginPage'
import { RegisterPage } from './pages/RegisterPage'
import { CustomerDashboardPage } from './pages/CustomerDashboardPage'
import { EnquiriesPage, NewEnquiryPage } from './pages/EnquiriesPage'
import { EnquiryDetailPage } from './pages/EnquiryDetailPage'
import { QuotationsPage } from './pages/QuotationsPage'
import { QuotationDetailPage } from './pages/QuotationDetailPage'
import { IssuePolicyPage } from './pages/IssuePolicyPage'
import { PoliciesPage } from './pages/PoliciesPage'
import { PolicyDetailPage } from './pages/PolicyDetailPage'
import { ClaimsPage, NewClaimPage } from './pages/ClaimsPage'
import { ClaimDetailPage } from './pages/ClaimDetailPage'
import { NotificationsPage } from './pages/NotificationsPage'
import { StaffLoginPage } from './pages/StaffLoginPage'
import { StaffDashboardPage } from './pages/StaffDashboardPage'
import { StaffInquiriesPage } from './pages/StaffInquiriesPage'
import { StaffInquiryDetailPage } from './pages/StaffInquiryDetailPage'
import { StaffClaimsPage } from './pages/StaffClaimsPage'
import { StaffPoliciesPage } from './pages/StaffPoliciesPage'
import { StaffManagementPage } from './pages/StaffManagementPage'

function RequireAuth({children}:{children:ReactElement}){const {token}=useAuth();return token?children:<Navigate to="/login" replace/>}
function RequireCustomer({children}:{children:ReactElement}){const {token,role}=useAuth();return token&&role==='CUSTOMER'?children:<Navigate to={token?'/staff':'/login'} replace/>}
function RequireStaff({children,allowed}:{children:ReactElement;allowed:UserRole[]}){const {token,role}=useAuth();return token&&role&&allowed.includes(role)?children:<Navigate to={token?'/staff/login':'/staff/login'} replace/>}

export function App(){return <BrowserRouter><AuthProvider><Routes>
<Route path="/" element={<HomePage/>}/><Route path="/login" element={<LoginPage/>}/><Route path="/register" element={<RegisterPage/>}/>
<Route path="/app" element={<RequireCustomer><CustomerDashboardPage/></RequireCustomer>}/><Route path="/app/enquiries" element={<RequireCustomer><EnquiriesPage/></RequireCustomer>}/><Route path="/app/enquiries/new" element={<RequireCustomer><NewEnquiryPage/></RequireCustomer>}/><Route path="/app/enquiries/:id" element={<RequireCustomer><EnquiryDetailPage/></RequireCustomer>}/><Route path="/app/quotations" element={<RequireCustomer><QuotationsPage/></RequireCustomer>}/><Route path="/app/quotations/:id" element={<RequireCustomer><QuotationDetailPage/></RequireCustomer>}/><Route path="/app/quotations/:id/issue" element={<RequireCustomer><IssuePolicyPage/></RequireCustomer>}/><Route path="/app/policies" element={<RequireCustomer><PoliciesPage/></RequireCustomer>}/><Route path="/app/policies/:id" element={<RequireCustomer><PolicyDetailPage/></RequireCustomer>}/><Route path="/app/claims" element={<RequireCustomer><ClaimsPage/></RequireCustomer>}/><Route path="/app/claims/new" element={<RequireCustomer><NewClaimPage/></RequireCustomer>}/><Route path="/app/claims/:id" element={<RequireCustomer><ClaimDetailPage/></RequireCustomer>}/><Route path="/app/notifications" element={<RequireCustomer><NotificationsPage/></RequireCustomer>}/>
<Route path="/staff/login" element={<StaffLoginPage/>}/><Route path="/staff" element={<RequireStaff allowed={['ADMIN','AGENT']}><StaffDashboardPage/></RequireStaff>}/><Route path="/staff/inquiries" element={<RequireStaff allowed={['ADMIN','AGENT']}><StaffInquiriesPage/></RequireStaff>}/><Route path="/staff/inquiries/:id" element={<RequireStaff allowed={['ADMIN','AGENT']}><StaffInquiryDetailPage/></RequireStaff>}/><Route path="/staff/claims" element={<RequireStaff allowed={['ADMIN','AGENT']}><StaffClaimsPage/></RequireStaff>}/><Route path="/staff/policies" element={<RequireStaff allowed={['ADMIN','AGENT']}><StaffPoliciesPage/></RequireStaff>}/><Route path="/staff/staff" element={<RequireStaff allowed={['ADMIN']}><StaffManagementPage/></RequireStaff>}/>
<Route path="*" element={<Navigate to="/" replace/>}/>
</Routes></AuthProvider></BrowserRouter>}
