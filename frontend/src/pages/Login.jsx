import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useAuth } from '../lib/auth.jsx'

export default function Login() {
  const auth = useAuth()
  const navigate = useNavigate()

  const [destination, setDestination] = useState('finance@demo.ng')
  const [otp, setOtp] = useState('')
  const [devOtp, setDevOtp] = useState('')
  const [busy, setBusy] = useState(false)
  const [err, setErr] = useState('')

  async function onRequestOtp(e) {
    e.preventDefault()
    setErr('')
    setBusy(true)
    try {
      const res = await auth.requestOtp({ destination })
      setDevOtp(res.devOtp || '')
    } catch (ex) {
      setErr(ex.message || 'Failed to request OTP')
    } finally {
      setBusy(false)
    }
  }

  async function onVerify(e) {
    e.preventDefault()
    setErr('')
    setBusy(true)
    try {
      const res = await auth.loginWithOtp({ destination, otp })
      if (!res.hasAddress) navigate('/address')
      else navigate('/home')
    } catch (ex) {
      setErr(ex.message || 'Failed to verify OTP')
    } finally {
      setBusy(false)
    }
  }

  return (
    <div className="auth-wrap">
      <div className="auth-card">
        <div className="auth-head">
          <div className="auth-title">Sign in</div>
          <div className="auth-sub">OTP access for residents and council staff.</div>
        </div>

        <form className="stack" onSubmit={onRequestOtp}>
          <label className="field">
            <span>Phone or Email</span>
            <input
              value={destination}
              onChange={(e) => setDestination(e.target.value)}
              placeholder="+2348012345678 or name@example.com"
              autoComplete="username"
            />
          </label>
          <button className="btn" disabled={busy || !destination}>
            {busy ? 'Sending…' : 'Send OTP'}
          </button>
          {devOtp ? (
            <div className="hint">
              Dev OTP: <code>{devOtp}</code>
            </div>
          ) : null}
        </form>

        <div className="divider" role="presentation"></div>

        <form className="stack" onSubmit={onVerify}>
          <label className="field">
            <span>OTP</span>
            <input
              value={otp}
              onChange={(e) => setOtp(e.target.value)}
              placeholder="6-digit code"
              inputMode="numeric"
              autoComplete="one-time-code"
            />
          </label>
          <button className="btn primary" disabled={busy || otp.length < 4}>
            {busy ? 'Verifying…' : 'Verify & Continue'}
          </button>
          {err ? <div className="error">{err}</div> : null}
        </form>

        <div className="auth-foot">
          Demo users: <code>finance@demo.ng</code>, <code>superadmin@demo.ng</code>, <code>resident@demo.ng</code>, <code>fleet@demo.ng</code>, <code>route@demo.ng</code>, <code>contractor@demo.ng</code>
        </div>
      </div>
    </div>
  )
}

