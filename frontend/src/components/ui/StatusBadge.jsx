const statusMap = {
  PENDING: { label: 'Pending', cls: 'bg-gray-100 text-gray-600' },
  UPLOADING: { label: 'Uploading', cls: 'bg-blue-100 text-blue-700' },
  EXTRACTING: { label: 'Extracting', cls: 'bg-purple-100 text-purple-700' },
  EXTRACTION_INCOMPLETE: { label: 'Incomplete', cls: 'bg-amber-100 text-amber-700' },
  AWAITING_FALLBACK: { label: 'Awaiting Fallback', cls: 'bg-amber-100 text-amber-700' },
  VALIDATING: { label: 'Validating', cls: 'bg-blue-100 text-blue-700' },
  ENHANCING_TITLE: { label: 'Enhancing', cls: 'bg-purple-100 text-purple-700' },
  FETCHING_PRICES: { label: 'Fetching Prices', cls: 'bg-indigo-100 text-indigo-700' },
  COMPLETED: { label: 'Completed', cls: 'bg-emerald-100 text-emerald-700' },
  FAILED: { label: 'Failed', cls: 'bg-red-100 text-red-700' },
}

export default function StatusBadge({ status }) {
  const { label, cls } = statusMap[status] ?? { label: status, cls: 'bg-gray-100 text-gray-600' }
  return (
    <span className={`inline-flex items-center rounded-full px-2.5 py-0.5 text-xs font-medium ${cls}`}>
      {label}
    </span>
  )
}
