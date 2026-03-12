import { useEffect, useMemo, useState } from 'react'
import { Client } from '@stomp/stompjs'
import { apiFetch } from '../lib/api.js'

function formatNgn(amount) {
  const n = Number(amount)
  if (Number.isNaN(n)) return String(amount)
  return `NGN ${n.toLocaleString('en-NG')}`
}

export default function Finance() {
  const [lga, setLga] = useState('Ikeja')
  const [ym, setYm] = useState(() => {
    const d = new Date()
    return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}`
  })
  const [data, setData] = useState(null)
  const [err, setErr] = useState('')

  const [year, month] = ym.split('-').map((x) => Number(x))

  async function load() {
    setErr('')
    try {
      const res = await apiFetch(
        `/api/finance/unpaid?lga=${encodeURIComponent(lga)}&year=${year}&month=${month}`,
      )
      setData(res)
    } catch (ex) {
      setErr(ex.message || 'Failed to load')
    }
  }

  useEffect(() => {
    load()
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [lga, ym])

  useEffect(() => {
    const wsUrl = `${location.protocol === 'https:' ? 'wss' : 'ws'}://${location.host}/ws`
    const client = new Client({ brokerURL: wsUrl, reconnectDelay: 2000 })
    client.onConnect = () => {
      client.subscribe(`/topic/finance/unpaid/${sanitizeTopic(lga)}`, () => load())
    }
    client.activate()
    return () => client.deactivate()
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [lga])

  const rows = data?.rows || []

  const totals = useMemo(() => {
    return {
      owed: data?.totalOwedNgn ? formatNgn(data.totalOwedNgn) : '—',
      collected: data?.totalCollectedNgn ? formatNgn(data.totalCollectedNgn) : '—',
    }
  }, [data])

  return (
    <section className="page">
      <div className="page-head row">
        <div>
          <h1>Finance</h1>
          <p className="muted">Real-time unpaid dashboard per LGA.</p>
        </div>
        <div className="row gap">
          <label className="field inline">
            <span>LGA</span>
            <input value={lga} onChange={(e) => setLga(e.target.value)} />
          </label>
          <label className="field inline">
            <span>Month</span>
            <input value={ym} onChange={(e) => setYm(e.target.value)} placeholder="YYYY-MM" />
          </label>
        </div>
      </div>

      {err ? <div className="error">{err}</div> : null}

      <div className="grid2">
        <div className="card">
          <div className="kicker">Revenue</div>
          <div className="big">{totals.collected} collected</div>
          <div className="muted sm">{totals.owed} total owed (unpaid + partial)</div>
        </div>
        <div className="card">
          <div className="kicker">Unpaid</div>
          <div className="big">{rows.length} accounts</div>
          <div className="muted sm">Auto-updates on payment reconciliation.</div>
        </div>
      </div>

      <div className="card">
        <div className="table">
          <div className="thead">
            <div>Address</div>
            <div>Owing</div>
            <div>Overdue</div>
            <div>Status</div>
          </div>
          {rows.map((r) => (
            <div className="trow" key={r.invoiceId}>
              <div className="addr">{r.address}</div>
              <div>{formatNgn(r.owingNgn)}</div>
              <div>{r.daysOverdue}d</div>
              <div>
                <span className="pill">{r.status}</span>
              </div>
            </div>
          ))}
          {!rows.length ? <div className="muted">No unpaid accounts for this month.</div> : null}
        </div>
      </div>
    </section>
  )
}

function sanitizeTopic(s) {
  return String(s || '').replace(/[^a-zA-Z0-9_-]/g, '_')
}

