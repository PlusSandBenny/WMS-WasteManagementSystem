import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { apiFetch } from '../lib/api.js'
import { useAuth } from '../lib/auth.jsx'

const STATE_OPTIONS = ['Lagos', 'FCT', 'Kano', 'Rivers', 'Oyo', 'Kaduna']
const LGA_OPTIONS = {
  Lagos: ['Ikeja', 'Surulere', 'Eti-Osa', 'Alimosho'],
  FCT: ['Abuja Municipal', 'Gwagwalada', 'Kuje'],
  Kano: ['Nassarawa', 'Tarauni', 'Fagge'],
  Rivers: ['Port Harcourt', 'Obio/Akpor'],
  Oyo: ['Ibadan North', 'Ibadan South-West'],
  Kaduna: ['Kaduna North', 'Kaduna South'],
}

export default function Address() {
  const auth = useAuth()
  const navigate = useNavigate()

  const [state, setState] = useState('Lagos')
  const [lga, setLga] = useState('Ikeja')
  const [street, setStreet] = useState('Allen Avenue')
  const [houseNumber, setHouseNumber] = useState('12B')
  const [landmark, setLandmark] = useState('Near XYZ Pharmacy')
  const [busy, setBusy] = useState(false)
  const [err, setErr] = useState('')

  useEffect(() => {
    if (auth.me?.address) {
      const a = auth.me.address
      setState(a.state || 'Lagos')
      setLga(a.lga || 'Ikeja')
      setStreet(a.street || '')
      setHouseNumber(a.houseNumber || '')
      setLandmark(a.landmark || '')
    }
  }, [auth.me])

  useEffect(() => {
    const lg = LGA_OPTIONS[state] || []
    if (!lg.includes(lga)) setLga(lg[0] || '')
  }, [state, lga])

  async function onSave(e) {
    e.preventDefault()
    setErr('')
    setBusy(true)
    try {
      await apiFetch('/api/me/address', {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ state, lga, street, houseNumber, landmark }),
      })
      await auth.refreshMe()
      navigate('/home')
    } catch (ex) {
      setErr(ex.message || 'Failed to save address')
    } finally {
      setBusy(false)
    }
  }

  return (
    <section className="page">
      <div className="page-head">
        <h1>Verify Address</h1>
        <p className="muted">State → LGA → Street + House Number + Landmark.</p>
      </div>

      <form className="card grid" onSubmit={onSave}>
        <label className="field">
          <span>State</span>
          <select value={state} onChange={(e) => setState(e.target.value)}>
            {STATE_OPTIONS.map((s) => (
              <option key={s} value={s}>
                {s}
              </option>
            ))}
          </select>
        </label>

        <label className="field">
          <span>LGA</span>
          <select value={lga} onChange={(e) => setLga(e.target.value)}>
            {(LGA_OPTIONS[state] || []).map((x) => (
              <option key={x} value={x}>
                {x}
              </option>
            ))}
          </select>
        </label>

        <label className="field span2">
          <span>Street</span>
          <input value={street} onChange={(e) => setStreet(e.target.value)} />
        </label>

        <label className="field">
          <span>House Number</span>
          <input
            value={houseNumber}
            onChange={(e) => setHouseNumber(e.target.value)}
          />
        </label>

        <label className="field span2">
          <span>Landmark (optional)</span>
          <input value={landmark} onChange={(e) => setLandmark(e.target.value)} />
        </label>

        <div className="actions span2">
          <button className="btn primary" disabled={busy}>
            {busy ? 'Saving…' : 'Save & Continue'}
          </button>
          {err ? <div className="error">{err}</div> : null}
        </div>
      </form>
    </section>
  )
}

