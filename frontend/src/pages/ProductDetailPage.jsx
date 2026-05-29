import { useState } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import useFetch from '../hooks/useFetch'
import { getProduct, enhanceTitle } from '../api/productsApi'
import { refreshCompetitorPrices } from '../api/competitorApi'
import Card from '../components/ui/Card'
import Badge from '../components/ui/Badge'
import Button from '../components/ui/Button'
import Spinner from '../components/ui/Spinner'
import QualityScoreMeter from '../components/product/QualityScoreMeter'
import CompetitorPriceTable from '../components/product/CompetitorPriceTable'
import AlertRow from '../components/alert/AlertRow'
import { formatCurrency, formatDate } from '../utils/formatters'

function Field({ label, value }) {
  if (!value && value !== 0) return null
  return (
    <div>
      <p className="text-xs font-medium uppercase tracking-wide text-gray-400">{label}</p>
      <p className="mt-0.5 text-sm text-gray-800">{value}</p>
    </div>
  )
}

export default function ProductDetailPage() {
  const { productId } = useParams()
  const navigate = useNavigate()
  const [enhancing, setEnhancing] = useState(false)
  const [refreshing, setRefreshing] = useState(false)

  const { data: product, loading, error, refetch } = useFetch(
    () => getProduct(productId),
    [productId],
  )

  const handleEnhance = async () => {
    setEnhancing(true)
    try {
      await enhanceTitle(productId)
      await refetch()
    } finally {
      setEnhancing(false)
    }
  }

  const handleRefreshPrices = async () => {
    setRefreshing(true)
    try {
      await refreshCompetitorPrices(productId)
      await refetch()
    } finally {
      setRefreshing(false)
    }
  }

  if (loading) return <div className="flex justify-center py-20"><Spinner size="lg" /></div>
  if (error) return <div className="rounded-lg border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-700">{error}</div>
  if (!product) return null

  const alerts = product.alerts ?? []
  const prices = product.competitorPrices ?? []

  return (
    <div className="space-y-6">
      <div className="flex flex-wrap items-start justify-between gap-4">
        <div>
          <button
            onClick={() => navigate(-1)}
            className="mb-2 flex items-center gap-1 text-sm text-gray-400 hover:text-gray-600"
          >
            <svg className="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 19l-7-7 7-7" />
            </svg>
            Back
          </button>
          <h1 className="text-xl font-bold text-gray-900">{product.productTitle ?? product.enhancedTitle ?? '—'}</h1>
          <p className="mt-0.5 font-mono text-xs text-gray-400">{product.skuId}</p>
        </div>
        <div className="flex flex-wrap gap-2">
          {!product.enhancedTitle && (
            <Button variant="secondary" size="sm" loading={enhancing} onClick={handleEnhance}>
              Enhance Title
            </Button>
          )}
          <Button variant="secondary" size="sm" loading={refreshing} onClick={handleRefreshPrices}>
            Refresh Prices
          </Button>
        </div>
      </div>

      <div className="grid gap-6 lg:grid-cols-3">
        <div className="space-y-6 lg:col-span-2">
          <Card>
            <Card.Header>
              <span className="text-sm font-semibold text-gray-800">Product Details</span>
            </Card.Header>
            <Card.Body>
              {product.enhancedTitle && product.enhancedTitle !== product.productTitle && (
                <div className="mb-4 rounded-lg border border-blue-100 bg-blue-50 p-3">
                  <p className="text-xs font-medium text-blue-500">Enhanced Title</p>
                  <p className="mt-0.5 text-sm font-medium text-blue-800">{product.enhancedTitle}</p>
                </div>
              )}
              <div className="grid grid-cols-2 gap-4 sm:grid-cols-3">
                <Field label="Brand" value={product.brand} />
                <Field label="Category" value={product.category} />
                <Field label="Availability" value={product.availability} />
                <Field label="Color" value={product.color} />
                <Field label="Size" value={product.size} />
                <Field label="Material" value={product.material} />
                <Field label="Extraction Source" value={product.extractionSource} />
              </div>
              {product.description && (
                <div className="mt-4">
                  <p className="text-xs font-medium uppercase tracking-wide text-gray-400">Description</p>
                  <p className="mt-1 text-sm leading-relaxed text-gray-700">{product.description}</p>
                </div>
              )}
              {product.imageUrl && (
                <div className="mt-4">
                  <p className="text-xs font-medium uppercase tracking-wide text-gray-400">Image URL</p>
                  <a
                    href={product.imageUrl}
                    target="_blank"
                    rel="noreferrer"
                    className="mt-0.5 block truncate text-sm text-blue-600 hover:underline"
                  >
                    {product.imageUrl}
                  </a>
                </div>
              )}
            </Card.Body>
          </Card>

          <Card>
            <Card.Header>
              <span className="text-sm font-semibold text-gray-800">Competitor Prices</span>
            </Card.Header>
            <Card.Body>
              <CompetitorPriceTable prices={prices} sellerPrice={product.price} />
            </Card.Body>
          </Card>

          {alerts.length > 0 && (
            <Card>
              <Card.Header>
                <span className="text-sm font-semibold text-gray-800">
                  Alerts ({alerts.length})
                </span>
              </Card.Header>
              <Card.Body className="space-y-3">
                {alerts.map((a) => (
                  <AlertRow key={a.id} alert={a} />
                ))}
              </Card.Body>
            </Card>
          )}
        </div>

        <div className="space-y-6">
          <Card>
            <Card.Body className="space-y-4">
              <QualityScoreMeter score={product.qualityScore} />
              <div className="flex flex-wrap gap-2">
                {product.duplicate && <Badge variant="red">Duplicate</Badge>}
                {product.availability && (
                  <Badge variant={product.availability === 'In Stock' ? 'green' : 'amber'}>
                    {product.availability}
                  </Badge>
                )}
              </div>
            </Card.Body>
          </Card>

          <Card>
            <Card.Header>
              <span className="text-sm font-semibold text-gray-800">Pricing</span>
            </Card.Header>
            <Card.Body className="space-y-2">
              <div>
                <p className="text-xs text-gray-400">Your Price</p>
                <p className="text-2xl font-bold text-gray-900">{formatCurrency(product.price)}</p>
              </div>
              {product.mrp && (
                <div>
                  <p className="text-xs text-gray-400">MRP</p>
                  <p className="text-sm text-gray-500 line-through">{formatCurrency(product.mrp)}</p>
                </div>
              )}
              {product.price && product.mrp && product.mrp > 0 && (
                <div>
                  <p className="text-xs text-gray-400">Discount</p>
                  <p className="text-sm font-medium text-emerald-600">
                    {(((product.mrp - product.price) / product.mrp) * 100).toFixed(1)}% off
                  </p>
                </div>
              )}
            </Card.Body>
          </Card>

          {product.productUrl && (
            <Card>
              <Card.Body>
                <p className="mb-1 text-xs font-medium uppercase tracking-wide text-gray-400">Product URL</p>
                <a
                  href={product.productUrl}
                  target="_blank"
                  rel="noreferrer"
                  className="break-all text-xs text-blue-600 hover:underline"
                >
                  {product.productUrl}
                </a>
              </Card.Body>
            </Card>
          )}
        </div>
      </div>
    </div>
  )
}
