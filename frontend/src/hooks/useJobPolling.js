import { useState, useEffect, useRef } from 'react'
import { getJob } from '../api/jobsApi'

const TERMINAL_STATUSES = ['COMPLETED', 'FAILED']
const POLL_INTERVAL = 3000

export default function useJobPolling(jobId) {
  const [job, setJob] = useState(null)
  const [error, setError] = useState(null)
  const timerRef = useRef(null)

  useEffect(() => {
    if (!jobId) return

    const poll = async () => {
      try {
        const res = await getJob(jobId)
        const fetched = res.data.data
        setJob(fetched)
        if (!TERMINAL_STATUSES.includes(fetched.status)) {
          timerRef.current = setTimeout(poll, POLL_INTERVAL)
        }
      } catch (err) {
        setError(err.message)
      }
    }

    poll()

    return () => clearTimeout(timerRef.current)
  }, [jobId])

  return { job, error }
}
