import { BrowserRouter, Navigate, Route, Routes } from 'react-router-dom'
import { HomePage } from './pages/HomePage'
import { ResultsPage } from './pages/ResultsPage'

export default function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<HomePage />} />
        <Route path="/results/:id" element={<ResultsPage />} />
        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
    </BrowserRouter>
  )
}
