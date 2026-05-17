import { Link } from 'react-router-dom'
import { Badge, Button, Card } from './ui'
import type { PublicSessionSummary, PublicStudioSummary } from '../api/types'

export function StudioCard({ studio }: { studio: PublicStudioSummary }) {
  return (
    <Link to={`/studios/${studio.slug}`}>
      <Card className="group overflow-hidden">
        <div className="relative h-52 overflow-hidden">
          <img src={studio.imageUrl} alt={studio.name} className="h-full w-full object-cover transition duration-500 group-hover:scale-105" />
          {studio.featured ? <Badge className="absolute left-3 top-3 bg-accent/90 text-white">Featured</Badge> : null}
        </div>
        <div className="space-y-3 p-5">
          <div className="flex items-center justify-between text-sm text-primary-600">
            <span>{studio.discipline} · {studio.neighborhood}</span>
            <span className="font-semibold">€{studio.priceFromEur}</span>
          </div>
          <div className="text-xl font-bold text-text-primary">{studio.name}</div>
          <p className="line-clamp-3 text-sm leading-6 text-text-muted">{studio.description}</p>
        </div>
      </Card>
    </Link>
  )
}

export function SessionCard({ session, onBook }: { session: PublicSessionSummary; onBook?: () => void }) {
  return (
    <Card className="overflow-hidden">
      <div className="grid gap-4 md:grid-cols-[180px_1fr_auto] md:items-center">
        <img src={session.imageUrl} alt={session.classTitle} className="h-44 w-full object-cover md:h-full" />
        <div className="p-4 md:p-0">
          <div className="text-sm font-semibold uppercase tracking-[0.2em] text-primary-600">{session.discipline} · {session.studioName}</div>
          <div className="mt-2 text-2xl font-bold text-text-primary">{session.classTitle}</div>
          <p className="mt-2 text-sm leading-6 text-text-muted">{session.locationName}</p>
          <div className="mt-4 flex flex-wrap gap-3 text-sm text-slate-600">
            <span>{new Intl.DateTimeFormat('en-GB', { weekday: 'short', hour: 'numeric', minute: '2-digit' }).format(new Date(session.startsAt))}</span>
            <span>•</span>
            <span>{session.bookedCount}/{session.capacity} booked</span>
            <span>•</span>
            <span>€{session.priceEur}</span>
          </div>
        </div>
        <div className="p-4 md:p-5 md:text-right">
          {onBook ? <Button onClick={onBook}>Book Now</Button> : <Button variant="secondary" asChild><Link to={`/checkout/${session.id}`}>Book Now</Link></Button>}
        </div>
      </div>
    </Card>
  )
}
