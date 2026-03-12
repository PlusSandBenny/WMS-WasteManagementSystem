import { useEffect, useMemo, useState } from 'react'
import { apiFetch } from '../lib/api.js'
import { useAuth } from '../lib/auth.jsx'

function monthLabel(ym) {
  const [y, m] = ym.split('-').map((x) => Number(x))
  const date = new Date(y, m - 1, 1)
  return date.toLocaleDateString('en-GB', { month: 'long', year: 'numeric' })
}

function dayNum(s) {
  return Number(s.slice(-2))
}

export default function Schedule() {
  const auth = useAuth()
  const [ym, setYm] = useState(() => {
    const d = new Date()
    return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}`
  })
  const [data, setData] = useState(null)
  const [err, setErr] = useState('')

  const hasAddress = !!auth.me?.address

  async function load(targetYm = ym) {
    setErr('')
    const [year, month] = targetYm.split('-').map((x) => Number(x))
    try {
      const res = await apiFetch(`/api/schedule/month?year=${year}&month=${month}`)
      setData(res)
    } catch (ex) {
      setErr(ex.message || 'Failed to load schedule')
    }
  }

  useEffect(() => {
    if (!hasAddress) return
    load()
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [ym, hasAddress])

  const byDay = useMemo(() => {
    const map = new Map()
    const pickups = data?.pickupsByDate || {}
    for (const [date, items] of Object.entries(pickups)) {
      map.set(dayNum(date), items)
    }
    return map
  }, [data])

  const gridDays = useMemo(() => {
    const [y, m] = ym.split('-').map((x) => Number(x))
    const first = new Date(y, m - 1, 1)
    const startOffset = (first.getDay() + 6) % 7 // monday=0
    const daysInMonth = new Date(y, m, 0).getDate()
    const cells = []
    for (let i = 0; i < startOffset; i++) cells.push(null)
    for (let d = 1; d <= daysInMonth; d++) cells.push(d)
    while (cells.length % 7 !== 0) cells.push(null)
    return cells
  }, [ym])

  function shiftMonth(delta) {
    const [y, m] = ym.split('-').map((x) => Number(x))
    const d = new Date(y, m - 1 + delta, 1)
    setYm(`${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}`)
  }

  if (!hasAddress) {
    return (
      <section className="page">
        <div className="page-head">
          <h1>Schedule</h1>
          <p className="muted">Address verification is required.</p>
        </div>
        <div className="card">
          <div className="callout warn">Set your address first.</div>
        </div>
      </section>
    )
  }

  return (
    <section className="page">
      <div className="page-head row">
        <div>
          <h1>Schedule</h1>
          <p className="muted">
            {auth.me?.address?.state} / {auth.me?.address?.lga}
          </p>
        </div>
        <div className="row gap">
          <button className="btn ghost" onClick={() => shiftMonth(-1)}>
            Prev
          </button>
          <button className="btn ghost" onClick={() => shiftMonth(1)}>
            Next
          </button>
        </div>
      </div>

      {err ? <div className="error">{err}</div> : null}

      <div className="card">
        <div className="calendar-head">
          <div className="calendar-title">{monthLabel(ym)}</div>
          <div className="legend">
            <span className="tag green">General</span>
            <span className="tag blue">Recycling</span>
            <span className="tag brown">Garden</span>
            <span className="tag black">Food</span>
          </div>
        </div>

        <div className="calendar">
          {['Mo', 'Tu', 'We', 'Th', 'Fr', 'Sa', 'Su'].map((d) => (
            <div className="dow" key={d}>
              {d}
            </div>
          ))}

          {gridDays.map((d, idx) => {
            if (!d) return <div className="cell empty" key={idx}></div>
            const items = byDay.get(d) || []
            return (
              <div className="cell" key={idx}>
                <div className="date">{d}</div>
                <div className="chips">
                  {items.map((p, i) => (
                    <div
                      className={`chip ${chipClass(p.binType)} ${
                        p.shiftedByHoliday ? 'shifted' : ''
                      }`}
                      key={i}
                      title={p.shiftedByHoliday ? 'Shifted by holiday' : ''}
                    >
                      {shortBin(p.binType)}
                    </div>
                  ))}
                </div>
              </div>
            )
          })}
        </div>

        {data?.notices?.length ? (
          <div className="notices">
            {data.notices.map((n, i) => (
              <div className="notice" key={i}>
                {n}
              </div>
            ))}
          </div>
        ) : null}
      </div>
    </section>
  )
}

function shortBin(binType) {
  if (binType === 'GENERAL_WASTE') return 'G'
  if (binType === 'RECYCLING') return 'R'
  if (binType === 'GARDEN') return 'Ga'
  if (binType === 'FOOD_WASTE') return 'F'
  return '?'
}

function chipClass(binType) {
  if (binType === 'GENERAL_WASTE') return 'green'
  if (binType === 'RECYCLING') return 'blue'
  if (binType === 'GARDEN') return 'brown'
  if (binType === 'FOOD_WASTE') return 'black'
  return 'gray'
}

