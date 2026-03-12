import { useEffect, useState } from 'react'
import { apiFetch } from '../lib/api.js'

export default function Vehicles() {
  const [items, setItems] = useState([])
  const [licensePlate, setLicensePlate] = useState('KJA-123AA')
  const [vehicleType, setVehicleType] = useState('Compactor')
  const [capacity, setCapacity] = useState(10)
  const [status, setStatus] = useState('ACTIVE')
  const [err, setErr] = useState('')

  async function load() {
    setErr('')
    try {
      setItems(await apiFetch('/api/vehicles'))
    } catch (ex) {
      setErr(ex.message || 'Failed to load vehicles')
    }
  }

  useEffect(() => {
    load()
  }, [])

  async function create(e) {
    e.preventDefault()
    setErr('')
    try {
      await apiFetch('/api/vehicles', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ licensePlate, vehicleType, capacity, status }),
      })
      await load()
    } catch (ex) {
      setErr(ex.message || 'Failed to create vehicle')
    }
  }

  return (
    <section className="page">
      <div className="page-head">
        <h1>Fleet</h1>
        <p className="muted">Vehicles and maintenance state.</p>
      </div>

      {err ? <div className="error">{err}</div> : null}

      <div className="grid2">
        <div className="card">
          <div className="kicker">Add Vehicle</div>
          <form className="stack" onSubmit={create}>
            <label className="field">
              <span>License Plate</span>
              <input value={licensePlate} onChange={(e) => setLicensePlate(e.target.value)} />
            </label>
            <label className="field">
              <span>Type</span>
              <input value={vehicleType} onChange={(e) => setVehicleType(e.target.value)} />
            </label>
            <label className="field">
              <span>Capacity</span>
              <input type="number" value={capacity} onChange={(e) => setCapacity(Number(e.target.value))} />
            </label>
            <label className="field">
              <span>Status</span>
              <select value={status} onChange={(e) => setStatus(e.target.value)}>
                <option value="ACTIVE">Active</option>
                <option value="MAINTENANCE">Maintenance</option>
                <option value="OUT_OF_SERVICE">Out of service</option>
              </select>
            </label>
            <button className="btn primary">Create</button>
          </form>
        </div>

        <div className="card">
          <div className="kicker">Vehicles</div>
          <div className="table">
            <div className="thead">
              <div>Plate</div>
              <div>Type</div>
              <div>Status</div>
              <div>Capacity</div>
            </div>
            {items.map((v) => (
              <div className="trow" key={v.id}>
                <div className="mono">{v.licensePlate}</div>
                <div>{v.vehicleType || '—'}</div>
                <div>
                  <span className="pill">{v.status}</span>
                </div>
                <div>{v.capacity ?? '—'}</div>
              </div>
            ))}
          </div>
        </div>
      </div>
    </section>
  )
}

