const API_BASE = import.meta.env.VITE_API_BASE || ''

const ACCESS_KEY = 'wms.accessToken'
const REFRESH_KEY = 'wms.refreshToken'

export function getAccessToken() {
  return localStorage.getItem(ACCESS_KEY)
}

export function setTokens({ accessToken, refreshToken }) {
  if (accessToken) localStorage.setItem(ACCESS_KEY, accessToken)
  if (refreshToken) localStorage.setItem(REFRESH_KEY, refreshToken)
}

export function clearTokens() {
  localStorage.removeItem(ACCESS_KEY)
  localStorage.removeItem(REFRESH_KEY)
}

export async function apiFetch(path, init = {}) {
  const headers = new Headers(init.headers || {})
  headers.set('Accept', 'application/json')

  const token = getAccessToken()
  if (token) headers.set('Authorization', `Bearer ${token}`)

  const res = await fetch(`${API_BASE}${path}`, { ...init, headers })
  if (res.status === 204) return null

  const contentType = res.headers.get('content-type') || ''
  const isJson = contentType.includes('application/json')
  const body = isJson ? await res.json().catch(() => null) : await res.text()

  if (!res.ok) {
    const message =
      (body && body.message) || (typeof body === 'string' ? body : 'Request failed')
    const err = new Error(message)
    err.status = res.status
    err.body = body
    throw err
  }
  return body
}

