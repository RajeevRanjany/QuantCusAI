import { useState } from 'react'
import { useSearchParams, useNavigate } from 'react-router-dom'
import useFetch from '../hooks/useFetch'
import { listProducts } from '../api/productsApi'
import QualityScoreMeter from '../components/product/QualityScoreMeter'
import Badge from '../components/ui/Badge'
import Spinner from '../components/ui/Spinner'
import EmptyState from '../components/ui/EmptyState'
import Button from '../components/ui/Button'
import { formatCurrency, truncate } from '../utils/formatters'

export default function ProductListPage() {
  const [searchParams] = useSearchParams()
  const navigate = useNavigate()
  const jobId = searchParams.get('jobId') ?? ''
  const [search, setSearch] = useState('')

  const { data: products, loading, error } = useFetch(
    jobId ? () => listProducts(jobId) : null,
    [jobId],
  )

  if (!jobId) {
    return (
      <div className="py-20 text-center">
        <p className="text-gray-500">Select a job from the Dashboard to view its products.</p>
        <Button className="mt-4" variant="secondary" onClick={() => navigate('/jobs')}>Go to Jobs</Button>
      </div>
    )
  }

  if (loading) return <div className="flex justify-center py-20"><Spinner size="lg" /></div>
  if (error) return <div className="rounded-lg border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-700">{error}</div>

  const filtered = (products ?? []).filter(
    (p) =>
      !search ||
      p.productTitle?.toLowerCase().includes(search.toLowerCase()) ||
      p.skuId?.toLowerCase().includes(search.toLowerCase()),
  )

  return (
    <div>
      <div className="mb-6 flex flex-wrap items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Products</h1>
          <p className="mt-0.5 font-mono text-xs text-gray-400">{jobId}</p>
        </div>
        <div className="flex items-center gap-2">
          <input
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            placeholder="Search by title or SKU..."
            className="w-64 rounded-lg border border-gray-300 px-3 py-2 text-sm focus:border-blue-500 focus:outline-none focus:ring-1 focus:ring-blue-500"
          />
          <Button variant="secondary" onClick={() => navigate(`/dashboard?jobId=${jobId}`)}>
            Dashboard
          </Button>
        </div>
      </div>

      {filtered.length === 0 ? (
        <EmptyState title="No products found" description="Try a different search term." />
      ) : (
        <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
          {filtered.map((product) => (
            <div
              key={product.id}
              onClick={() => navigate(`/products/${product.id}`)}
              className="group cursor-pointer rounded-xl border border-gray-200 bg-white p-4 shadow-sm transition-all hover:border-blue-300 hover:shadow-md"
            >
              <div className="mb-3 flex items-start justify-between gap-2">
                <div className="min-w-0 flex-1">
                  <p className="text-sm font-semibold text-gray-900 group-hover:text-blue-700">
                    {truncate(product.productTitle ?? product.enhancedTitle, 50)}
                  </p>
                  <p className="mt-0.5 font-mono text-xs text-gray-400">{product.skuId}</p>
                </div>
                {product.duplicate && (
                  <Badge variant="red">Duplicate</Badge>
                )}
              </div>

              <div className="mb-3 flex flex-wrap gap-1.5">
                {product.brand && <Badge variant="blue">{product.brand}</Badge>}
                {product.category && <Badge>{product.category}</Badge>}
                {product.availability && (
                  <Badge variant={product.availability === 'In Stock' ? 'green' : 'amber'}>
                    {product.availability}
                  </Badge>
                )}
              </div>

              <QualityScoreMeter score={product.qualityScore} />

              <div className="mt-3 flex items-center justify-between">
                <div>
                  <span className="text-base font-bold text-gray-900">
                    {formatCurrency(product.price)}
                  </span>
                  {product.mrp && product.mrp !== product.price && (
                    <span className="ml-2 text-xs text-gray-400 line-through">
                      {formatCurrency(product.mrp)}
                    </span>
                  )}
                </div>
                {product.extractionSource && (
                  <span className="text-xs text-gray-400">{product.extractionSource}</span>
                )}
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  )
}
