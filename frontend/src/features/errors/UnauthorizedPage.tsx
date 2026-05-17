import { useAuth } from '../../auth/AuthProvider'
import { Button, Card } from '../../components/ui'

export function UnauthorizedPage() {
  const auth = useAuth()

  return (
    <div className="mx-auto flex min-h-[70vh] max-w-4xl items-center px-4 py-14">
      <Card className="grid gap-6 p-8 md:grid-cols-[1fr_0.9fr] md:items-center">
        <div>
          <div className="text-6xl font-black text-primary-600">401</div>
          <h1 className="mt-4 text-4xl font-bold tracking-tight text-text-primary">Sign in to keep moving.</h1>
          <p className="mt-4 text-text-muted">You can browse as a guest, but booking a session or buying a class pack requires a PulseFit account.</p>
          <div className="mt-6 flex gap-3">
            <Button onClick={auth.login}>Login</Button>
            <Button variant="secondary" onClick={auth.register}>Register</Button>
          </div>
        </div>
        <img src="https://images.unsplash.com/photo-1506126613408-eca07ce68773?auto=format&fit=crop&w=1200&q=80" alt="Fitness" className="h-full rounded-2xl object-cover" />
      </Card>
    </div>
  )
}
