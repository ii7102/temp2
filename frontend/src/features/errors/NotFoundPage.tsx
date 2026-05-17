import { Link } from 'react-router-dom'
import { Button, Card } from '../../components/ui'

export function NotFoundPage() {
  return (
    <div className="mx-auto flex min-h-[70vh] max-w-5xl items-center px-4 py-14">
      <Card className="grid gap-6 p-8 md:grid-cols-[1fr_1fr] md:items-center">
        <div>
          <div className="text-7xl font-black text-primary-600">404</div>
          <h1 className="mt-4 text-4xl font-bold tracking-tight text-text-primary">Looks like you took a wrong turn.</h1>
          <p className="mt-4 text-text-muted">We couldn’t find that page, but the next class is probably still waiting.</p>
          <div className="mt-6 flex gap-3">
            <Button asChild><Link to="/discover">Find a class</Link></Button>
            <Button variant="secondary" asChild><Link to="/">Go Home</Link></Button>
          </div>
        </div>
        <img src="https://images.unsplash.com/photo-1544367567-0f2fcb009e0b?auto=format&fit=crop&w=1200&q=80" alt="Yoga" className="h-full rounded-3xl object-cover" />
      </Card>
    </div>
  )
}
