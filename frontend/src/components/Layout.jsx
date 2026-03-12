import { NavLink, Outlet, useNavigate } from 'react-router-dom'
import { useAuth } from '../lib/auth.jsx'

export default function Layout() {
  const auth = useAuth()
  const navigate = useNavigate()

  const role = auth.me?.role
  const isFinance = role === 'FINANCE_OFFICER' || role === 'SUPER_ADMIN'
  const isFleet = role === 'FLEET_MANAGER' || role === 'SUPER_ADMIN'
  const isRoute = role === 'ROUTE_SUPERVISOR' || role === 'SUPER_ADMIN'

  return (
    <div className="app-shell">
      <header className="topbar">
        <div className="brand">
          <div className="mark" aria-hidden="true">
            W
          </div>
          <div className="brand-text">
            <div className="brand-name">Nigeria Waste Services</div>
            <div className="brand-sub">Residential Waste Management</div>
          </div>
        </div>

        <div className="topbar-right">
          <button
            className="btn ghost"
            onClick={() => {
              auth.logout()
              navigate('/login')
            }}
          >
            Sign out
          </button>
        </div>
      </header>

      <div className="shell-body">
        <nav className="sidebar" aria-label="Primary navigation">
          <NavLink to="/home" className="nav">
            Home
          </NavLink>
          <NavLink to="/address" className="nav">
            Address
          </NavLink>
          <NavLink to="/schedule" className="nav">
            Schedule
          </NavLink>
          <NavLink to="/issues" className="nav">
            Issues
          </NavLink>

          {isFinance ? (
            <NavLink to="/finance" className="nav">
              Finance
            </NavLink>
          ) : null}
          {isFleet ? (
            <NavLink to="/vehicles" className="nav">
              Fleet
            </NavLink>
          ) : null}
          {isRoute ? (
            <NavLink to="/route" className="nav">
              Routes
            </NavLink>
          ) : null}

          <div className="nav-meta">
            <div className="meta-row">
              <span className="meta-k">Role</span>
              <span className="meta-v">{role || '...'}</span>
            </div>
          </div>
        </nav>

        <main className="content">
          <Outlet />
        </main>
      </div>
    </div>
  )
}

