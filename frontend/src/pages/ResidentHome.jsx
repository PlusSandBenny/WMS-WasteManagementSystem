import { useEffect, useMemo, useState } from 'react'
import { Client } from '@stomp/stompjs'
import { apiFetch } from '../lib/api.js'
import { useAuth } from '../lib/auth.jsx'

function formatNgn(amount) {
  if (amount == null) return '—'
  const n = Number(amount)
  if (Number.isNaN(n)) return String(amount)
  return `NGN ${n.toLocaleString('en-NG')}`
}

export default function ResidentHome() {
  const auth = useAuth()
  const [home, setHome] = useState(null)
  const [err, setErr] = useState('')

  const addressId = auth.me?.address?.id

  async function load() {
    setErr('')
    try {
      const res = await apiFetch('/api/me/home')
      setHome(res)
    } catch (ex) {
      setErr(ex.message || 'Failed to load')
    }
  }

  useEffect(() => {
    load()
  }, [])

  useEffect(() => {
    if (!addressId) return
    const wsUrl = `${location.protocol === 'https:' ? 'wss' : 'ws'}://${location.host}/ws`
    const client = new Client({ brokerURL: wsUrl, reconnectDelay: 2000 })

    client.onConnect = () => {
      client.subscribe(`/topic/resident/status/${addressId}`, () => load())
    }
    client.activate()
    return () => client.deactivate()
  }, [addressId])

  const hasAddress = !!auth.me?.address
  const invoice = home?.invoice

  const todayLine = useMemo(() => {
    if (!home?.todayPickups?.length) return 'Not scheduled today'
    return home.todayPickups
      .map((p) => {
        const time = p.actualCollectionTimeIso ? ` at ${p.actualCollectionTimeIso}` : ''
        return `${p.binType}: ${p.status}${time}`
      })
      .join(' | ')
  }, [home])

  async function payNow() {
    if (!invoice?.invoiceId) return
    const intent = await apiFetch('/api/payments/intent', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        invoiceId: invoice.invoiceId,
        method: 'CARD',
        provider: 'stub',
      }),
    })
    await apiFetch('/api/payments/webhook/stub', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        providerReference: intent.providerReference,
        transactionId: intent.providerReference,
      }),
    })
    await load()
  }

  return (
    <section className="page">
      <div className="page-head">
        <h1>Home</h1>
        <p className="muted">{home?.addressDisplay || '—'}</p>
      </div>

      {!hasAddress ? (
        <div className="card">
          <div className="callout warn">
            Address verification is required before schedule and billing.
          </div>
        </div>
      ) : null}

      {err ? <div className="error">{err}</div> : null}

      <div className="grid2">
        <div className="card">
          <div className="kicker">Today</div>
          <div className="big">{todayLine}</div>
          <div className="muted sm">Real-time updates arrive automatically.</div>
        </div>

        <div className="card">
          <div className="kicker">Next Pickup</div>
          {home?.nextPickup ? (
            <>
              <div className="big">
                {home.nextPickup.binType} on {home.nextPickup.date} ({home.nextPickup.time})
              </div>
              {home.nextPickup.shiftedByHoliday ? (
                <div className="pill">Shifted by holiday</div>
              ) : null}
            </>
          ) : (
            <div className="big">No upcoming pickup found</div>
          )}
        </div>

        <div className="card span2">
          <div className="kicker">Billing</div>
          {invoice ? (
            <div className="bill-row">
              <div>
                <div className="big">
                  {invoice.yearMonth} · {invoice.status}
                </div>
                <div className="muted sm">
                  Owing: {formatNgn(invoice.amountOwing)} · Due: {invoice.dueDate}
                </div>
              </div>
              <div className="bill-actions">
                <button
                  className="btn"
                  onClick={payNow}
                  disabled={invoice.status === 'PAID'}
                >
                  Pay now (stub)
                </button>
                <a
                  className="btn ghost"
                  href={`/api/invoices/${invoice.invoiceId}/pdf`}
                  target="_blank"
                  rel="noreferrer"
                >
                  View PDF
                </a>
              </div>
            </div>
          ) : (
            <div className="muted">
              No invoice yet for this month (finance can run monthly job).
            </div>
          )}
        </div>
      </div>
    </section>
  )
}
