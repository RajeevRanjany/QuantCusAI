export default function Card({ children, className = '' }) {
  return (
    <div className={`rounded-xl border border-gray-200 bg-white shadow-sm ${className}`}>
      {children}
    </div>
  )
}

Card.Header = function CardHeader({ children, className = '' }) {
  return (
    <div className={`border-b border-gray-100 px-6 py-4 ${className}`}>{children}</div>
  )
}

Card.Body = function CardBody({ children, className = '' }) {
  return <div className={`px-6 py-4 ${className}`}>{children}</div>
}
