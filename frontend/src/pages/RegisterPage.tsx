import { FormEvent, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { register } from '../lib/api'
import { Logo } from '../components/Logo'
import { Icon } from '../components/Icon'

export function RegisterPage() {
  const navigate = useNavigate()
  const [form, setForm] = useState({ username: '', email: '', password: '', firstName: '', lastName: '', phoneNumber: '' })
  const [error, setError] = useState('')
  const [busy, setBusy] = useState(false)
  const [showPassword, setShowPassword] = useState(false)

  const update = (key: keyof typeof form, value: string) => setForm(prev => ({ ...prev, [key]: value }))

  async function submit(event: FormEvent) {
    event.preventDefault(); setError(''); setBusy(true)
    try {
      const result = await register(form)
      if (result.token) {
        localStorage.setItem('jodam.token', result.token)
        localStorage.setItem('jodam.username', result.username)
        navigate('/app')
      } else {
        navigate('/login')
      }
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Unable to register')
    } finally { setBusy(false) }
  }

  return (
    <div className="auth-shell auth-shell--wide">
      <div className="auth-shell__brand"><Logo /></div>
      <div className="auth-card">
        <div className="auth-card__intro"><span className="eyebrow eyebrow--red">Get started</span><h1>Create your account.</h1><p>Start your motor insurance journey with Jodam.</p></div>
        {error && <div className="form-error">{error}</div>}
        <form onSubmit={submit} className="form-stack">
          <div className="form-grid"><label>First name<input value={form.firstName} onChange={e => update('firstName', e.target.value)} required /></label><label>Last name<input value={form.lastName} onChange={e => update('lastName', e.target.value)} required /></label></div>
          <label>Email<input value={form.email} onChange={e => update('email', e.target.value)} type="email" required /></label>
          <label>Phone number<input value={form.phoneNumber} onChange={e => update('phoneNumber', e.target.value)} placeholder="0711 000 000" required /></label>
          <div className="form-grid"><label>Username<input value={form.username} onChange={e => update('username', e.target.value)} required /></label><label>Password<span className="password-input"><input value={form.password} onChange={e => update('password', e.target.value)} type={showPassword ? 'text' : 'password'} autoComplete="new-password" required /><button type="button" className="password-toggle" onClick={() => setShowPassword(value => !value)} aria-label={showPassword ? 'Hide password' : 'Show password'} title={showPassword ? 'Hide password' : 'Show password'}><Icon name={showPassword ? 'eye-off' : 'eye'} /></button></span></label></div>
          <button className="button button--primary button--full" disabled={busy}>{busy ? 'Creating account…' : 'Create account'}</button>
        </form>
        <p className="auth-switch">Already registered? <a href="/login">Sign in</a></p>
      </div>
      <p className="auth-footnote">Insurance with a Difference</p>
    </div>
  )
}
