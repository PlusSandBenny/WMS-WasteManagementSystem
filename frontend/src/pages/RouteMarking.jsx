import { useState } from 'react'
import { apiFetch } from '../lib/api.js'

export default function RouteMarking() {
  const [addressId, setAddressId] = useState('1')
  const [scheduledDate, setScheduledDate] = useState(new Date().toISOString().slice(0, 10))
  const [binType, setBinType] = useState('GENERAL_WASTE')
  const [status, setStatus] = useState('PICKED_UP')
  const [notes, setNotes] = useState('')
  const [msg, setMsg] = useState('')
  const [err, setErr] = useState('')

  async function mark(e) {
    e.preventDefault()
    setErr('')
    setMsg('')
    try {
      await apiFetch(`/api/collections/${addressId}/mark`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          scheduledDate,
          binType,
          status,
          actualCollectionTimeIso: status === 'PICKED_UP' ? new Date().toISOString() : null,
          notes,
        }),
      })
      setMsg('Marked successfully')
    } catch (ex) {
      setErr(ex.message || 'Failed to mark')
    }
  }

  return (
    <section className="page">
      <div className="page-head">
        <h1>Routes</h1>
        <p className="muted">Supervisor marking tool (Phase 1 minimal UI).</p>
      </div>

      <div className="card">
        <div className="kicker">Mark Collection</div>
        <form className="grid" onSubmit={mark}>
          <label className="field">
            <span>Address ID</span>
            <input value={addressId} onChange={(e) => setAddressId(e.target.value)} />
          </label>
          <label className="field">
            <span>Date</span>
            <input value={scheduledDate} onChange={(e) => setScheduledDate(e.target.value)} />
          </label>
          <label className="field">
            <span>Bin Type</span>
            <select value={binType} onChange={(e) => setBinType(e.target.value)}>
              <option value="GENERAL_WASTE">General Waste</option>
              <option value="RECYCLING">Recycling</option>
              <option value="GARDEN">Garden</option>
              <option value="FOOD_WASTE">Food Waste</option>
            </select>
          </label>
          <label className="field">
            <span>Status</span>
            <select value={status} onChange={(e) => setStatus(e.target.value)}>
              <option value="PICKED_UP">Picked up</option>
              <option value="MISSED">Missed</option>
              <option value="REPORTED">Reported</option>
              <option value="SCHEDULED">Scheduled</option>
            </select>
          </label>
          <label className="field span2">
            <span>Notes</span>
            <input value={notes} onChange={(e) => setNotes(e.target.value)} placeholder="Optional" />
          </label>
          <div className="actions span2">
            <button className="btn primary">Save</button>
            {msg ? <div className="ok">{msg}</div> : null}
            {err ? <div className="error">{err}</div> : null}
          </div>
        </form>
      </div>
    </section>
  )
}

