import { Link } from 'react-router-dom'
import { Button, Card } from '../../components/ui'

export function ForbiddenPage() {
  return (
    <div className="mx-auto flex min-h-[70vh] max-w-4xl items-center px-4 py-14">
      <Card className="grid gap-6 p-8 md:grid-cols-[1fr_0.9fr] md:items-center">
        <div>
          <div className="text-6xl font-black text-primary-600">403</div>
          <h1 className="mt-4 text-4xl font-bold tracking-tight text-text-primary">You do not have access.</h1>
          <p className="mt-4 text-text-muted">This area is reserved for PulseFit admins.</p>
          <div className="mt-6 flex gap-3">
            <Button asChild><Link to="/">Go Home</Link></Button>
            <Button variant="secondary" asChild><Link to="/dashboard">User Dashboard</Link></Button>
          </div>
        </div>
        <img src="https://images.unsplash.com/photo-1517836357463-d25dfeac3438?auto=format&fit=crop&w=1200&q=80" alt="Admin" className="h-full rounded-2xl object-cover" />
      </Card>
    </div>
  )
}
