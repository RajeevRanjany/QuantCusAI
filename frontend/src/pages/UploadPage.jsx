import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import FileDropzone from '../components/upload/FileDropzone'
import Button from '../components/ui/Button'
import Card from '../components/ui/Card'
import { uploadVideo, uploadCsv } from '../api/uploadApi'

export default function UploadPage() {
  const navigate = useNavigate()
  const [mode, setMode] = useState('video')
  const [file, setFile] = useState(null)
  const [enhanceTitle, setEnhanceTitle] = useState(true)
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState(null)

  const handleSubmit = async () => {
    if (!file) return
    setLoading(true)
    setError(null)
    try {
      const res = mode === 'video'
        ? await uploadVideo(file, enhanceTitle)
        : await uploadCsv(file, enhanceTitle)
      const jobId = res.data.data.id
      navigate(`/jobs?highlight=${jobId}`)
    } catch (err) {
      setError(err.message)
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="mx-auto max-w-2xl">
      <div className="mb-8">
        <h1 className="text-2xl font-bold text-gray-900">Upload Products</h1>
        <p className="mt-1 text-sm text-gray-500">
          Upload a product video or CSV to start the extraction pipeline.
        </p>
      </div>

      <Card>
        <Card.Body className="space-y-6">
          <div className="flex rounded-lg border border-gray-200 p-1">
            {['video', 'csv'].map((m) => (
              <button
                key={m}
                onClick={() => { setMode(m); setFile(null) }}
                className={`flex-1 rounded-md py-2 text-sm font-medium capitalize transition-colors ${
                  mode === m
                    ? 'bg-blue-600 text-white shadow-sm'
                    : 'text-gray-500 hover:text-gray-700'
                }`}
              >
                {m === 'video' ? 'Video Upload' : 'CSV Upload'}
              </button>
            ))}
          </div>

          {mode === 'video' ? (
            <FileDropzone
              accept="video/*"
              label="Drop a product video here"
              hint="MP4, MOV, AVI up to 100 MB"
              onFile={setFile}
              file={file}
            />
          ) : (
            <FileDropzone
              accept=".csv,text/csv"
              label="Drop your product CSV here"
              hint="CSV with standard 13-column schema"
              onFile={setFile}
              file={file}
            />
          )}

          <label className="flex cursor-pointer items-center gap-3">
            <div className="relative">
              <input
                type="checkbox"
                className="sr-only"
                checked={enhanceTitle}
                onChange={(e) => setEnhanceTitle(e.target.checked)}
              />
              <div
                className={`h-5 w-9 rounded-full transition-colors ${
                  enhanceTitle ? 'bg-blue-600' : 'bg-gray-300'
                }`}
              />
              <div
                className={`absolute top-0.5 h-4 w-4 rounded-full bg-white shadow transition-transform ${
                  enhanceTitle ? 'translate-x-4' : 'translate-x-0.5'
                }`}
              />
            </div>
            <div>
              <p className="text-sm font-medium text-gray-700">Enhance product titles</p>
              <p className="text-xs text-gray-400">Automatically improve titles using AI enrichment</p>
            </div>
          </label>

          {error && (
            <div className="rounded-lg border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-700">
              {error}
            </div>
          )}

          <Button
            className="w-full"
            size="lg"
            disabled={!file}
            loading={loading}
            onClick={handleSubmit}
          >
            Start Processing
          </Button>
        </Card.Body>
      </Card>
    </div>
  )
}
