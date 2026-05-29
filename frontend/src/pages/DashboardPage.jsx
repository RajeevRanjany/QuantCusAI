import { useState } from 'react'
import { useSearchParams, useNavigate } from 'react-router-dom'
import useFetch from '../hooks/useFetch'
import { getDashboard } from '../api/dashboardApi'
import Card from '../components/ui/Card'
import Spinner from '../components/ui/Spinner'
import StatusBadge from '../components/ui/StatusBadge'
import Button from '../components/ui/Button'
import { formatDate, formatScore, scoreColor } from '../utils/formatters'

function StatCard({ label, value, sub, accent }) {
  return (
    <Card>
      <Card.Body>
        <p className="text-xs font-medium uppercase tracking-wide text-gray-400">{label}</p>
        <p className={`mt-1 text-3xl font-bold ${accent ?? 'text-gray-900'}`}>{value}</p>
        {sub && <p className="mt-0.5 text-xs text-gray-400">{sub}</p>}
      </Card.Body>
    </Card>
  )
}

export default function DashboardPage() {
  const [searchParams] = useSearchParams()
  const navigate = useNavigate()
  const jobId = searchParams.get('jobId') ?? ''
  const [inputVal, setInputVal] = useState('')

  const { data, loading, error, refetch } = useFetch(
    jobId ? () => getDashboard(jobId) : null,
    [jobId],
  )

  if (!jobId) {
    return (
      <div className="mx-auto max-w-md pt-20 text-center">
        <h1 className="mb-2 text-xl font-bold text-gray-900">Dashboard</h1>
        <p className="mb-6 text-sm text-gray-500">Enter a Job ID to view the quality summary.</p>
        <div className="flex gap-2">
          <input
            value={inputVal}
            onChange={(e) => setInputVal(e.target.value)}
            placeholder="Paste Job ID (UUID)..."
            className="flex-1 rounded-lg border border-gray-300 px-3 py-2 text-sm focus:border-blue-500 focus:outline-none focus:ring-1 focus:ring-blue-500"
          />
          <Button
            onClick={() => navigate('/dashboard?jobId=' + inputVal.trim())}
            disabled={!inputVal.trim()}
          >
            View
          </Button>
        </div>
        <p className="mt-4 text-sm text-gray-400">
          Or pick a job from the{' '}
          <button onClick={() => navigate('/jobs')} className="text-blue-600 underline">Jobs page</button>.
        </p>
      </div>
    )
  }

  if (loading) return <div className="flex justify-center py-20"><Spinner size="lg" /></div>

  if (error) return (
    <div className="rounded-lg border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-700">{error}</div>
  )

  const { job, summary } = data ?? {}

  return (
    <div className="space-y-8">
      <div className="flex flex-wrap items-start justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Dashboard</h1>
          <p className="mt-1 font-mono text-xs text-gray-400">{jobId}</p>
        </div>
        <div className="flex gap-2">
          <Button variant="secondary" onClick={() => navigate('/jobs')}>All Jobs</Button>
          <Button variant="secondary" onClick={refetch}>Refresh</Button>
          <Button onClick={() => navigate(`/products?jobId=${jobId}`)}>View Products</Button>
        </div>
      </div>

      {job && (
        <Card>
          <Card.Header>
            <div className="flex items-center justify-between">
              <span className="text-sm font-semibold text-gray-800">Job Status</span>
              <StatusBadge status={job.status} />
            </div>
          </Card.Header>
          <Card.Body>
            <div className="grid grid-cols-2 gap-4 text-sm sm:grid-cols-4">
              <div>
                <p className="text-xs text-gray-400">Type</p>
                <p className="font-medium">{job.jobType}</p>
              </div>
              <div>
                <p className="text-xs text-gray-400">Started</p>
                <p className="font-medium">{formatDate(job.startedAt)}</p>
              </div>
              <div>
                <p className="text-xs text-gray-400">Completed</p>
                <p className="font-medium">{formatDate(job.completedAt)}</p>
              </div>
              <div>
                <p className="text-xs text-gray-400">Products</p>
                <p className="font-medium">{job.productCount}</p>
              </div>
            </div>
            {job.errorMessage && (
              <p className="mt-3 rounded bg-red-50 px-3 py-2 text-xs text-red-600">{job.errorMessage}</p>
            )}
          </Card.Body>
        </Card>
      )}

      {summary && (
        <>
          <div className="grid grid-cols-2 gap-4 sm:grid-cols-3 lg:grid-cols-4">
            <StatCard label="Total Products" value={summary.totalProducts} />
            <StatCard
              label="Avg Quality Score"
              value={formatScore(summary.avgQualityScore)}
              accent={scoreColor(summary.avgQualityScore)}
            />
            <StatCard label="Total Alerts" value={summary.totalAlerts} />
            <StatCard label="Errors" value={summary.errorCount} accent="text-red-600" />
            <StatCard label="Warnings" value={summary.warningCount} accent="text-amber-500" />
            <StatCard label="Duplicates" value={summary.duplicateCount} accent="text-purple-600" />
            <StatCard
              label="Cheaper than all"
              value={summary.priceBelowAllCompetitors}
              sub="your price is lowest"
              accent="text-emerald-600"
            />
            <StatCard
              label="Pricier than all"
              value={summary.priceAboveAllCompetitors}
              sub="your price is highest"
              accent="text-red-600"
            />
          </div>

          <div className="flex flex-wrap gap-3">
            <Button onClick={() => navigate(`/products?jobId=${jobId}`)}>
              Browse Products
            </Button>
            <Button variant="secondary" onClick={() => navigate(`/alerts?jobId=${jobId}`)}>
              View Alerts
            </Button>
          </div>
        </>
      )}
    </div>
  )
}
