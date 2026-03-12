import { Navigate } from 'react-router-dom'
import { useAuth } from '../lib/auth.jsx'

export default function RequireAuth({ children }) {
  const auth = useAuth()

  if (!auth.ready) return null
  if (!auth.isAuthenticated) return <Navigate to="/login" replace />

  return children
}

