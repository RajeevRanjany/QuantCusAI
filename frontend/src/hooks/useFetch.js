import { useState, useEffect, useCallback } from 'react'

export default function useFetch(fetchFn, deps = []) {
  const [data, setData] = useState(null)
  const [loading, setLoading] = useState(fetchFn != null)
  const [error, setError] = useState(null)

  const execute = useCallback(async () => {
    if (!fetchFn) {
      setLoading(false)
      return
    }
    setLoading(true)
    setError(null)
    try {
      const res = await fetchFn()
      setData(res.data.data)
    } catch (err) {
      setError(err.message)
    } finally {
      setLoading(false)
    }
  }, deps)

  useEffect(() => {
    execute()
  }, [execute])

  return { data, loading, error, refetch: execute }
}
