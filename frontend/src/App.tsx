import { ErrorBoundary } from './features/errors/ErrorBoundary'
import { AppRouter } from './router'

export function App() {
  return (
    <ErrorBoundary>
      <AppRouter />
    </ErrorBoundary>
  )
}
