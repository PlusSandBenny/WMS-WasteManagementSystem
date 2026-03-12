import { useCallback, useEffect, useState } from 'react'
import { apiFetch } from '../lib/api.js'

export default function Issues() {
  const [myIssues, setMyIssues] = useState([])
  const [selected, setSelected] = useState(null)
  const [messages, setMessages] = useState([])
  const [issueType, setIssueType] = useState('MISSED_COLLECTION')
  const [description, setDescription] = useState('')
  const [photo, setPhoto] = useState(null)
  const [message, setMessage] = useState('')
  const [err, setErr] = useState('')

  const loadIssues = useCallback(async () => {
    setErr('')
    try {
      const res = await apiFetch('/api/issues/me')
      setMyIssues(res)
      if (res.length && !selected) setSelected(res[0])
    } catch (ex) {
      setErr(ex.message || 'Failed to load issues')
    }
  }, [selected])

  const loadMessages = useCallback(async (issueId) => {
    try {
      const res = await apiFetch(`/api/issues/${issueId}/messages`)
      setMessages(res)
    } catch (ex) {
      setErr(ex.message || 'Failed to load messages')
    }
  }, [])

  useEffect(() => {
    loadIssues()
  }, [loadIssues])

  useEffect(() => {
    if (!selected?.id) return
    loadMessages(selected.id)
  }, [selected?.id, loadMessages])

  async function submitIssue(e) {
    e.preventDefault()
    setErr('')
    try {
      const fd = new FormData()
      fd.append(
        'data',
        new Blob([JSON.stringify({ issueType, description })], {
          type: 'application/json',
        }),
      )
      if (photo) fd.append('photo', photo)
      const res = await fetch('/api/issues', {
        method: 'POST',
        body: fd,
        headers: { Accept: 'application/json', Authorization: authHeader() },
      })
      if (!res.ok) {
        const t = await res.text()
        throw new Error(t || 'Failed to submit')
      }
      await loadIssues()
      setDescription('')
      setPhoto(null)
    } catch (ex) {
      setErr(ex.message || 'Failed to submit issue')
    }
  }

  async function sendMessage(e) {
    e.preventDefault()
    if (!selected?.id) return
    setErr('')
    try {
      await apiFetch(`/api/issues/${selected.id}/messages`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ message }),
      })
      setMessage('')
      await loadMessages(selected.id)
    } catch (ex) {
      setErr(ex.message || 'Failed to send message')
    }
  }

  return (
    <section className="page">
      <div className="page-head">
        <h1>Issues</h1>
        <p className="muted">Report missed bins, upload photos, and chat.</p>
      </div>

      {err ? <div className="error">{err}</div> : null}

      <div className="grid2">
        <div className="card">
          <div className="kicker">Report</div>
          <form className="stack" onSubmit={submitIssue}>
            <label className="field">
              <span>Issue Type</span>
              <select value={issueType} onChange={(e) => setIssueType(e.target.value)}>
                <option value="MISSED_COLLECTION">Missed collection</option>
                <option value="DAMAGED_BIN">Damaged bin</option>
                <option value="OVERFLOWING_BIN">Overflowing bin</option>
                <option value="OTHER">Other</option>
              </select>
            </label>
            <label className="field">
              <span>Description</span>
              <textarea value={description} onChange={(e) => setDescription(e.target.value)} rows={4} />
            </label>
            <label className="field">
              <span>Photo (optional)</span>
              <input type="file" accept="image/*" onChange={(e) => setPhoto(e.target.files?.[0] || null)} />
            </label>
            <button className="btn primary">Submit issue</button>
          </form>
        </div>

        <div className="card">
          <div className="kicker">My Issues</div>
          <div className="issue-list">
            {myIssues.length ? (
              myIssues.map((i) => (
                <button
                  key={i.id}
                  className={`issue-item ${selected?.id === i.id ? 'active' : ''}`}
                  onClick={() => setSelected(i)}
                >
                  <div className="issue-title">{i.issueType}</div>
                  <div className="muted sm">{i.status}</div>
                </button>
              ))
            ) : (
              <div className="muted">No issues yet.</div>
            )}
          </div>

          {selected ? (
            <>
              <div className="divider" role="presentation"></div>
              <div className="chat">
                <div className="chat-head">
                  <div className="big">Conversation</div>
                  {selected.photoUrl ? (
                    <a className="btn ghost" href={selected.photoUrl} target="_blank" rel="noreferrer">
                      View photo
                    </a>
                  ) : null}
                </div>
                <div className="chat-body">
                  {messages.map((m) => (
                    <div key={m.id} className="chat-msg">
                      <div className="muted sm">{m.sender?.email || m.sender?.phone || 'User'}</div>
                      <div>{m.message}</div>
                    </div>
                  ))}
                  {!messages.length ? <div className="muted">No messages yet.</div> : null}
                </div>
                <form className="chat-send" onSubmit={sendMessage}>
                  <input value={message} onChange={(e) => setMessage(e.target.value)} placeholder="Write a message…" />
                  <button className="btn">Send</button>
                </form>
              </div>
            </>
          ) : null}
        </div>
      </div>
    </section>
  )
}

function authHeader() {
  const token = localStorage.getItem('wms.accessToken')
  return token ? `Bearer ${token}` : ''
}
