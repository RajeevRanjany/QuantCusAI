import Button from '../ui/Button'

const severityStyle = {
  ERROR: 'border-l-red-500 bg-red-50',
  WARNING: 'border-l-amber-400 bg-amber-50',
  INFO: 'border-l-blue-400 bg-blue-50',
}

const severityLabel = {
  ERROR: { cls: 'text-red-700 bg-red-100', text: 'Error' },
  WARNING: { cls: 'text-amber-700 bg-amber-100', text: 'Warning' },
  INFO: { cls: 'text-blue-700 bg-blue-100', text: 'Info' },
}

export default function AlertRow({ alert, onResolve, resolving }) {
  const { cls, text } = severityLabel[alert.severity] ?? severityLabel.INFO
  return (
    <div
      className={`flex items-start justify-between gap-4 rounded-lg border-l-4 p-4 ${severityStyle[alert.severity] ?? ''} ${alert.resolved ? 'opacity-50' : ''}`}
    >
      <div className="min-w-0 flex-1">
        <div className="mb-1 flex flex-wrap items-center gap-2">
          <span className={`rounded-full px-2 py-0.5 text-xs font-medium ${cls}`}>{text}</span>
          <span className="text-xs font-medium text-gray-700">{alert.alertType?.replace(/_/g, ' ')}</span>
          {alert.fieldName && (
            <span className="rounded bg-gray-100 px-1.5 py-0.5 font-mono text-xs text-gray-500">
              {alert.fieldName}
            </span>
          )}
          {alert.resolved && (
            <span className="rounded-full bg-emerald-100 px-2 py-0.5 text-xs font-medium text-emerald-700">
              Resolved
            </span>
          )}
        </div>
        <p className="text-sm text-gray-700">{alert.message}</p>
      </div>
      {!alert.resolved && onResolve && (
        <Button
          variant="secondary"
          size="sm"
          loading={resolving}
          onClick={() => onResolve(alert.id)}
        >
          Resolve
        </Button>
      )}
    </div>
  )
}
