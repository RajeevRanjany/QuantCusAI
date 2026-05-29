import { NavLink } from 'react-router-dom'

const links = [
  { to: '/upload', label: 'Upload' },
  { to: '/jobs', label: 'Jobs' },
  { to: '/dashboard', label: 'Dashboard' },
  { to: '/products', label: 'Products' },
  { to: '/alerts', label: 'Alerts' },
]

export default function Navbar() {
  return (
    <header className="sticky top-0 z-40 border-b border-gray-200 bg-white">
      <div className="mx-auto flex max-w-7xl items-center justify-between px-4 py-3 sm:px-6 lg:px-8">
        <div className="flex items-center gap-2">
          <div className="flex h-8 w-8 items-center justify-center rounded-lg bg-blue-600">
            <span className="text-sm font-bold text-white">Q</span>
          </div>
          <span className="text-base font-semibold text-gray-900">Quantacus</span>
        </div>
        <nav className="flex items-center gap-1">
          {links.map(({ to, label }) => (
            <NavLink
              key={to}
              to={to}
              className={({ isActive }) =>
                `rounded-lg px-3 py-1.5 text-sm font-medium transition-colors ${
                  isActive
                    ? 'bg-blue-50 text-blue-700'
                    : 'text-gray-600 hover:bg-gray-100 hover:text-gray-900'
                }`
              }
            >
              {label}
            </NavLink>
          ))}
        </nav>
      </div>
    </header>
  )
}
