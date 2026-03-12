import { createContext, useContext, useEffect, useMemo, useState } from 'react'
import { apiFetch, clearTokens, getAccessToken, setTokens } from './api.js'

const AuthContext = createContext(null)

export function AuthProvider({ children }) {
  const [me, setMe] = useState(null)
  const [ready, setReady] = useState(false)

  const isAuthenticated = !!getAccessToken()

  async function loadMe() {
    try {
      const data = await apiFetch('/api/me')
      setMe(data)
    } catch {
      clearTokens()
      setMe(null)
    } finally {
      setReady(true)
    }
  }

  useEffect(() => {
    loadMe()
  }, [])

  const value = useMemo(() => {
    return {
      ready,
      me,
      isAuthenticated,
      async loginWithOtp({ destination, otp }) {
        const data = await apiFetch('/api/auth/verify-otp', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ destination, otp }),
        })
        setTokens({ accessToken: data.accessToken, refreshToken: data.refreshToken })
        await loadMe()
        return data
      },
      async requestOtp({ destination }) {
        return apiFetch('/api/auth/request-otp', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ destination }),
        })
      },
      async refreshMe() {
        await loadMe()
      },
      logout() {
        clearTokens()
        setMe(null)
      },
    }
  }, [isAuthenticated, me, ready])

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}

export function useAuth() {
  const ctx = useContext(AuthContext)
  if (!ctx) throw new Error('AuthProvider missing')
  return ctx
}
