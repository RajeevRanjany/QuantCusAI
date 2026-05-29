import { useEffect, useState } from 'react'
import { useSearchParams, useNavigate } from 'react-router-dom'
import { listJobs } from '../api/jobsApi'
import StatusBadge from '../components/ui/StatusBadge'
import Spinner from '../components/ui/Spinner'
import Button from '../components/ui/Button'
import EmptyState from '../components/ui/EmptyState'
import { formatDate } from '../utils/formatters'

export default function JobsPage() {
  const [searchParams] = useSearchParams()
  const highlight = searchParams.get('highlight')
  const navigate = useNavigate()

  const [page, setPage] = useState(0)
  const [data, setData] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)

  const load = async (p) => {
    setLoading(true)
    setError(null)
    try {
      const res = await listJobs(p)
      setData(res.data.data)
    } catch (err) {
      setError(err.message)
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => { load(page) }, [page])

  const jobs = data?.content ?? []
  const totalPages = data?.totalPages ?? 0

  return (
    <div>
      <div className="mb-8 flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Processing Jobs</h1>
          <p className="mt-1 text-sm text-gray-500">Track your upload and extraction jobs.</p>
        </div>
        <Button variant="secondary" onClick={() => load(page)}>Refresh</Button>
      </div>

      {loading ? (
        <div className="flex justify-center py-20"><Spinner size="lg" /></div>
      ) : error ? (
        <div className="rounded-lg border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-700">{error}</div>
      ) : jobs.length === 0 ? (
        <EmptyState
          title="No jobs yet"
          description="Upload a product video or CSV to start processing."
          action={<Button onClick={() => navigate('/upload')}>Upload Now</Button>}
        />
      ) : (
        <div className="space-y-3">
          {jobs.map((job) => (
            <div
              key={job.id}
              onClick={() => navigate(`/dashboard?jobId=${job.id}`)}
              className={`flex cursor-pointer items-center justify-between rounded-xl border bg-white p-4 shadow-sm transition-all hover:shadow-md ${
                job.id === highlight ? 'border-blue-400 ring-2 ring-blue-100' : 'border-gray-200'
              }`}
            >
              <div className="min-w-0 flex-1">
                <div className="flex items-center gap-3">
                  <StatusBadge status={job.status} />
                  <span className="text-xs font-medium uppercase tracking-wide text-gray-400">
                    {job.jobType}
                  </span>
                </div>
                <p className="mt-1.5 truncate font-mono text-xs text-gray-500">{job.id}</p>
                <p className="mt-0.5 text-xs text-gray-400">{formatDate(job.createdAt)}</p>
              </div>
              <div className="ml-4 flex flex-col items-end gap-1 text-right">
                <span className="text-sm font-semibold text-gray-800">
                  {job.productCount ?? 0} products
                </span>
                {job.status === 'FAILED' && job.errorMessage && (
                  <span className="max-w-xs truncate text-xs text-red-500">{job.errorMessage}</span>
                )}
              </div>
            </div>
          ))}

          {totalPages > 1 && (
            <div className="flex items-center justify-center gap-3 pt-4">
              <Button
                variant="secondary"
                size="sm"
                disabled={page === 0}
                onClick={() => setPage((p) => p - 1)}
              >
                Previous
              </Button>
              <span className="text-sm text-gray-500">Page {page + 1} of {totalPages}</span>
              <Button
                variant="secondary"
                size="sm"
                disabled={page >= totalPages - 1}
                onClick={() => setPage((p) => p + 1)}
              >
                Next
              </Button>
            </div>
          )}
        </div>
      )}
    </div>
  )
}
