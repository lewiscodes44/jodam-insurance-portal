import { createContext, useContext, useMemo, useState, type ReactNode } from 'react'
import { login as loginRequest } from '../lib/api'

export type UserRole = 'CUSTOMER' | 'AGENT' | 'ADMIN'
export type AuthState = { token: string | null; username: string | null; role: UserRole | null }

type AuthContextValue = AuthState & {
  signIn: (username: string, password: string) => Promise<UserRole>
  signOut: () => void
}

const AuthContext = createContext<AuthContextValue | undefined>(undefined)

function readRole(): UserRole | null {
  const value = localStorage.getItem('jodam.role')
  return value === 'CUSTOMER' || value === 'AGENT' || value === 'ADMIN' ? value : null
}

export function AuthProvider({ children }: { children: ReactNode }) {
  const [token, setToken] = useState<string | null>(() => localStorage.getItem('jodam.token'))
  const [username, setUsername] = useState<string | null>(() => localStorage.getItem('jodam.username'))
  const [role, setRole] = useState<UserRole | null>(readRole)

  const value = useMemo<AuthContextValue>(() => ({
    token,
    username,
    role,
    async signIn(user: string, password: string) {
      const response = await loginRequest(user, password)
      const nextRole = (response.role ?? 'CUSTOMER') as UserRole
      localStorage.setItem('jodam.token', response.token)
      localStorage.setItem('jodam.username', response.username)
      localStorage.setItem('jodam.role', nextRole)
      setToken(response.token); setUsername(response.username); setRole(nextRole)
      return nextRole
    },
    signOut() {
      localStorage.removeItem('jodam.token'); localStorage.removeItem('jodam.username'); localStorage.removeItem('jodam.role')
      setToken(null); setUsername(null); setRole(null)
    },
  }), [token, username, role])

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}

export function useAuth() {
  const context = useContext(AuthContext)
  if (!context) throw new Error('useAuth must be used inside AuthProvider')
  return context
}
