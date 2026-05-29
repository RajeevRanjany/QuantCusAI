import { scoreBg, scoreColor, formatScore } from '../../utils/formatters'

export default function QualityScoreMeter({ score }) {
  const pct = Math.min(Math.max(score ?? 0, 0), 100)
  return (
    <div>
      <div className="mb-1 flex items-center justify-between">
        <span className="text-xs font-medium text-gray-500">Quality Score</span>
        <span className={`text-sm font-bold ${scoreColor(pct)}`}>{formatScore(pct)}</span>
      </div>
      <div className="h-2 w-full overflow-hidden rounded-full bg-gray-200">
        <div
          className={`h-2 rounded-full transition-all duration-500 ${scoreBg(pct)}`}
          style={{ width: `${pct}%` }}
        />
      </div>
    </div>
  )
}
