import { Component, type ReactNode } from 'react'
import { Button } from '../../components/ui'

type Props = { children: ReactNode }
type State = { hasError: boolean }

export class ErrorBoundary extends Component<Props, State> {
  state: State = { hasError: false }

  static getDerivedStateFromError() {
    return { hasError: true }
  }

  render() {
    if (this.state.hasError) {
      return (
        <div className="mx-auto flex min-h-screen max-w-2xl flex-col justify-center px-4 text-center">
          <div className="text-6xl font-black text-primary-600">PulseFit</div>
          <h1 className="mt-8 text-4xl font-bold tracking-tight text-text-primary">Something went off pace.</h1>
          <p className="mt-4 text-text-muted">Refresh the page or head back to the landing page to continue browsing classes.</p>
          <div className="mt-8 flex justify-center gap-3">
            <Button onClick={() => window.location.reload()}>Refresh</Button>
            <Button variant="secondary" asChild><a href="/">Go Home</a></Button>
          </div>
        </div>
      )
    }

    return this.props.children
  }
}
