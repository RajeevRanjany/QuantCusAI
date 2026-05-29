export const formatCurrency = (amount, currency = 'INR') => {
  if (amount == null) return '—'
  return new Intl.NumberFormat('en-IN', {
    style: 'currency',
    currency,
    maximumFractionDigits: 2,
  }).format(amount)
}

export const formatDate = (iso) => {
  if (!iso) return '—'
  return new Intl.DateTimeFormat('en-IN', {
    day: '2-digit',
    month: 'short',
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
  }).format(new Date(iso))
}

export const formatScore = (score) => {
  if (score == null) return '—'
  return `${Math.round(score)}/100`
}

export const scoreColor = (score) => {
  if (score >= 80) return 'text-emerald-600'
  if (score >= 50) return 'text-amber-500'
  return 'text-red-500'
}

export const scoreBg = (score) => {
  if (score >= 80) return 'bg-emerald-500'
  if (score >= 50) return 'bg-amber-400'
  return 'bg-red-500'
}

export const truncate = (str, max = 60) => {
  if (!str) return '—'
  return str.length > max ? `${str.slice(0, max)}…` : str
}
