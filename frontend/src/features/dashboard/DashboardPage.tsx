import { useQuery } from '@tanstack/react-query'
import { api } from '../../api/client'
import { Badge, Button, Card, KpiCard, SectionHeading } from '../../components/ui'
import { Link, useNavigate } from 'react-router-dom'

export function DashboardPage() {
  const navigate = useNavigate()
  const query = useQuery({ queryKey: ['dashboard'], queryFn: api.getDashboard })
  const dashboard = query.data

  if (!dashboard) {
    return <div className="mx-auto max-w-[1200px] px-4 py-10 text-text-muted">Loading your dashboard...</div>
  }

  return (
    <div className="mx-auto max-w-[1200px] px-4 py-10 md:px-8">
      <div className="grid gap-6 lg:grid-cols-[1.2fr_0.8fr]">
        <Card className="overflow-hidden p-0">
          <div className="grid min-h-[340px] md:grid-cols-[0.8fr_1.2fr]">
            <img src="https://images.unsplash.com/photo-1518611012118-696072aa579a?auto=format&fit=crop&w=1200&q=80" alt="Next class" className="h-full w-full object-cover" />
            <div className="p-8">
              <Badge tone="success">Next Class</Badge>
              <h1 className="mt-4 text-4xl font-black tracking-tight text-text-primary">Welcome back, {dashboard.profile.fullName.split(' ')[0]}.</h1>
              <p className="mt-3 text-text-muted">{dashboard.nextClass ? `You’re booked for ${dashboard.nextClass.classTitle} at ${dashboard.nextClass.studioName}` : 'Ready to sweat? Browse studios nearby.'}</p>
              {dashboard.nextClass ? (
                <div className="mt-6 space-y-3 text-sm text-slate-600">
                  <div>{new Intl.DateTimeFormat('en-GB', { weekday: 'long', hour: 'numeric', minute: '2-digit' }).format(new Date(dashboard.nextClass.startsAt))}</div>
                  <div>{dashboard.nextClass.locationName}</div>
                  <div>{dashboard.nextClass.discipline}</div>
                </div>
              ) : null}
              <div className="mt-8 flex gap-3">
                <Button onClick={() => navigate('/discover')}>Find a class</Button>
                <Button variant="secondary" onClick={() => navigate('/billing')}>Manage membership</Button>
              </div>
            </div>
          </div>
        </Card>

        <div className="grid gap-4">
          <KpiCard label="Classes attended" value={String(dashboard.classesAttended)} delta="+3 this week" tone="success" />
          <KpiCard label="Credits remaining" value={String(dashboard.creditPack.creditsBalance)} delta={dashboard.creditPack.status} />
          <KpiCard label="Amount spent" value={`€${dashboard.amountSpentEur}`} delta="Lifetime" tone="info" />
        </div>
      </div>

      <div className="mt-10 grid gap-6 lg:grid-cols-[1.1fr_0.9fr]">
        <Card className="p-0">
          <div className="border-b border-slate-200 p-6">
            <SectionHeading title="Upcoming bookings" description="Your next sessions and recent history are pulled directly from the backend." />
          </div>
          <div className="space-y-4 p-6">
            {dashboard.upcomingBookings.map((booking) => (
              <div key={booking.id} className="flex items-center justify-between rounded-2xl border border-slate-200 p-4">
                <div>
                  <div className="font-semibold text-text-primary">{booking.classTitle}</div>
                  <div className="text-sm text-text-muted">{booking.studioName} · {booking.discipline}</div>
                </div>
                <div className="text-right text-sm text-slate-600">
                  <div>{new Intl.DateTimeFormat('en-GB', { weekday: 'short', hour: 'numeric', minute: '2-digit' }).format(new Date(booking.startsAt))}</div>
                  <div>{booking.status}</div>
                </div>
              </div>
            ))}
            {!dashboard.upcomingBookings.length ? <div className="rounded-2xl bg-slate-50 p-6 text-sm text-text-muted">Ready to sweat? Browse studios nearby.</div> : null}
          </div>
        </Card>
        <Card className="p-0">
          <div className="border-b border-slate-200 p-6">
            <SectionHeading title="Recommended for you" description="Featured studios and nearby favorites are tailored from the current catalog." />
          </div>
          <div className="space-y-4 p-6">
            {dashboard.recommendedStudios.map((studio) => (
              <Link to={`/studios/${studio.slug}`} key={studio.id} className="flex gap-4 rounded-2xl border border-slate-200 p-4 transition hover:border-primary-300 hover:bg-primary-50">
                <img src={studio.imageUrl} alt={studio.name} className="h-20 w-20 rounded-xl object-cover" />
                <div>
                  <div className="text-xs font-semibold uppercase tracking-[0.2em] text-primary-600">{studio.discipline}</div>
                  <div className="mt-1 text-lg font-bold text-text-primary">{studio.name}</div>
                  <div className="text-sm text-text-muted">{studio.description}</div>
                </div>
              </Link>
            ))}
          </div>
        </Card>
      </div>
    </div>
  )
}
