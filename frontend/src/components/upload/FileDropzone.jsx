import { useRef, useState } from 'react'

export default function FileDropzone({ accept, label, hint, onFile, file }) {
  const inputRef = useRef(null)
  const [dragging, setDragging] = useState(false)

  const handleDrop = (e) => {
    e.preventDefault()
    setDragging(false)
    const dropped = e.dataTransfer.files[0]
    if (dropped) onFile(dropped)
  }

  return (
    <div
      onDragOver={(e) => { e.preventDefault(); setDragging(true) }}
      onDragLeave={() => setDragging(false)}
      onDrop={handleDrop}
      onClick={() => inputRef.current.click()}
      className={`cursor-pointer rounded-xl border-2 border-dashed p-10 text-center transition-colors ${
        dragging
          ? 'border-blue-400 bg-blue-50'
          : file
          ? 'border-emerald-400 bg-emerald-50'
          : 'border-gray-300 bg-gray-50 hover:border-blue-300 hover:bg-blue-50'
      }`}
    >
      <input
        ref={inputRef}
        type="file"
        accept={accept}
        className="hidden"
        onChange={(e) => e.target.files[0] && onFile(e.target.files[0])}
      />
      <div className="mb-3 flex justify-center">
        {file ? (
          <svg className="h-10 w-10 text-emerald-500" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={1.5} d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
          </svg>
        ) : (
          <svg className="h-10 w-10 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={1.5} d="M7 16a4 4 0 01-.88-7.903A5 5 0 1115.9 6L16 6a5 5 0 011 9.9M15 13l-3-3m0 0l-3 3m3-3v12" />
          </svg>
        )}
      </div>
      {file ? (
        <>
          <p className="text-sm font-medium text-emerald-700">{file.name}</p>
          <p className="mt-1 text-xs text-emerald-600">{(file.size / 1024 / 1024).toFixed(2)} MB</p>
        </>
      ) : (
        <>
          <p className="text-sm font-medium text-gray-700">{label}</p>
          <p className="mt-1 text-xs text-gray-400">{hint}</p>
        </>
      )}
    </div>
  )
}
