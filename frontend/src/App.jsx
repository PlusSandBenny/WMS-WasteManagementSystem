import { BrowserRouter, Navigate, Route, Routes } from 'react-router-dom'
import { AuthProvider } from './lib/auth.jsx'
import Layout from './components/Layout.jsx'
import RequireAuth from './components/RequireAuth.jsx'
import Login from './pages/Login.jsx'
import Address from './pages/Address.jsx'
import ResidentHome from './pages/ResidentHome.jsx'
import Schedule from './pages/Schedule.jsx'
import Issues from './pages/Issues.jsx'
import Finance from './pages/Finance.jsx'
import Vehicles from './pages/Vehicles.jsx'
import RouteMarking from './pages/RouteMarking.jsx'

export default function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <Routes>
          <Route path="/login" element={<Login />} />
          <Route
            path="/"
            element={
              <RequireAuth>
                <Layout />
              </RequireAuth>
            }
          >
            <Route index element={<Navigate to="/home" replace />} />
            <Route path="home" element={<ResidentHome />} />
            <Route path="address" element={<Address />} />
            <Route path="schedule" element={<Schedule />} />
            <Route path="issues" element={<Issues />} />
            <Route path="finance" element={<Finance />} />
            <Route path="vehicles" element={<Vehicles />} />
            <Route path="route" element={<RouteMarking />} />
          </Route>
          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </AuthProvider>
    </BrowserRouter>
  )
}

