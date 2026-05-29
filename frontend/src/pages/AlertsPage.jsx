import { useState } from 'react'
import { useSearchParams, useNavigate } from 'react-router-dom'
import useFetch from '../hooks/useFetch'
import { listAlerts, resolveAlert } from '../api/alertsApi'
import AlertRow from '../components/alert/AlertRow'
import Spinner from '../components/ui/Spinner'
import EmptyState from '../components/ui/EmptyState'
import Button from '../components/ui/Button'

export default function AlertsPage() {
  const [searchParams] = useSearchParams()
  const navigate = useNavigate()
  const jobId = searchParams.get('jobId') ?? ''
  const [resolvingId, setResolvingId] = useState(null)
  const [severityFilter, setSeverityFilter] = useState('ALL')

  const { data: alerts, loading, error, refetch } = useFetch(
    jobId ? () => listAlerts(jobId) : null,
    [jobId],
  )

  const handleResolve = async (alertId) => {
    setResolvingId(alertId)
    try {
      await resolveAlert(alertId)
      await refetch()
    } finally {
      setResolvingId(null)
    }
  }

  if (!jobId) {
    return (
      <div className="py-20 text-center">
        <p className="text-gray-500">Select a job from the Dashboard to view its alerts.</p>
        <Button className="mt-4" variant="secondary" onClick={() => navigate('/jobs')}>
          Go to Jobs
        </Button>
      </div>
    )
  }

  if (loading) return <div className="flex justify-center py-20"><Spinner size="lg" /></div>
  if (error) return <div className="rounded-lg border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-700">{error}</div>

  const all = alerts ?? []
  const filtered =
    severityFilter === 'ALL' ? all : all.filter((a) => a.severity === severityFilter)

  const counts = {
    ALL: all.length,
    ERROR: all.filter((a) => a.severity === 'ERROR').length,
    WARNING: all.filter((a) => a.severity === 'WARNING').length,
    INFO: all.filter((a) => a.severity === 'INFO').length,
  }

  const tabs = [
    { key: 'ALL', label: 'All' },
    { key: 'ERROR', label: 'Errors' },
    { key: 'WARNING', label: 'Warnings' },
    { key: 'INFO', label: 'Info' },
  ]

  return (
    <div>
      <div className="mb-6 flex flex-wrap items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Alerts</h1>
          <p className="mt-0.5 font-mono text-xs text-gray-400">{jobId}</p>
        </div>
        <Button variant="secondary" onClick={refetch}>Refresh</Button>
      </div>

      <div className="mb-4 flex gap-2 border-b border-gray-200">
        {tabs.map(({ key, label }) => (
          <button
            key={key}
            onClick={() => setSeverityFilter(key)}
            className={`flex items-center gap-1.5 border-b-2 pb-2 text-sm font-medium transition-colors ${
              severityFilter === key
                ? 'border-blue-600 text-blue-600'
                : 'border-transparent text-gray-500 hover:text-gray-700'
            }`}
          >
            {label}
            <span className={`rounded-full px-1.5 py-0.5 text-xs ${
              severityFilter === key ? 'bg-blue-100 text-blue-700' : 'bg-gray-100 text-gray-500'
            }`}>
              {counts[key]}
            </span>
          </button>
        ))}
      </div>

      {filtered.length === 0 ? (
        <EmptyState title="No alerts" description="No alerts match the current filter." />
      ) : (
        <div className="space-y-2">
          {filtered.map((alert) => (
            <AlertRow
              key={alert.id}
              alert={alert}
              onResolve={handleResolve}
              resolving={resolvingId === alert.id}
            />
          ))}
        </div>
      )}
    </div>
  )
}
