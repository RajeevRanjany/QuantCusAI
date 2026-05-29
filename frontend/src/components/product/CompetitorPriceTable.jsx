import { formatCurrency } from '../../utils/formatters'

export default function CompetitorPriceTable({ prices, sellerPrice }) {
  if (!prices || prices.length === 0) {
    return <p className="py-4 text-center text-sm text-gray-400">No competitor data available.</p>
  }

  return (
    <div className="overflow-x-auto">
      <table className="w-full text-sm">
        <thead>
          <tr className="border-b border-gray-100 text-left text-xs font-medium uppercase tracking-wide text-gray-500">
            <th className="pb-2 pr-4">Platform</th>
            <th className="pb-2 pr-4">Price</th>
            <th className="pb-2 pr-4">vs Yours</th>
            <th className="pb-2">Title</th>
          </tr>
        </thead>
        <tbody className="divide-y divide-gray-50">
          {prices.map((c) => {
            const delta = c.priceDelta ?? (sellerPrice != null ? c.price - sellerPrice : null)
            return (
              <tr key={c.id} className="py-2">
                <td className="py-2 pr-4 font-medium text-gray-800">{c.platform}</td>
                <td className="py-2 pr-4 font-semibold">{formatCurrency(c.price, c.currency)}</td>
                <td className="py-2 pr-4">
                  {delta != null && (
                    <span
                      className={`inline-flex items-center rounded-full px-2 py-0.5 text-xs font-medium ${
                        delta > 0
                          ? 'bg-emerald-100 text-emerald-700'
                          : delta < 0
                          ? 'bg-red-100 text-red-700'
                          : 'bg-gray-100 text-gray-600'
                      }`}
                    >
                      {delta > 0 ? '+' : ''}{formatCurrency(delta, c.currency)}
                    </span>
                  )}
                </td>
                <td className="max-w-xs truncate py-2 text-gray-500">{c.competitorTitle}</td>
              </tr>
            )
          })}
        </tbody>
      </table>
    </div>
  )
}
