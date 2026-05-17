import { Link, useNavigate } from 'react-router-dom'
import { ArrowRight, Search } from 'lucide-react'
import { useQuery } from '@tanstack/react-query'
import { api } from '../../api/client'
import { Button, Card, Input, SectionHeading, Badge } from '../../components/ui'
import { StudioCard } from '../../components/marketing'
import { useAuth } from '../../auth/AuthProvider'
import { useState } from 'react'

export function LandingPage() {
  const { login, register, authenticated } = useAuth()
  const navigate = useNavigate()
  const [search, setSearch] = useState('')
  const landingQuery = useQuery({ queryKey: ['landing'], queryFn: api.getLanding })

  return (
    <div className="relative overflow-hidden">
      <section className="mx-auto grid max-w-[1200px] gap-10 px-4 py-10 md:px-8 lg:grid-cols-[1.1fr_0.9fr] lg:items-center lg:py-16">
        <div className="animate-fadeUp">
          <Badge tone="info" className="mb-5">Book drop-in classes across your neighborhood</Badge>
          <h1 className="max-w-2xl text-5xl font-black tracking-tight text-text-primary md:text-6xl">Find your flow with one class pass.</h1>
          <p className="mt-6 max-w-xl text-lg leading-8 text-text-muted">Discover yoga, HIIT, pilates, cycling, and climbing sessions at independent studios. Fast booking, premium studios, and a frictionless checkout experience.</p>

          <div className="mt-8 flex flex-col gap-3 sm:flex-row">
            <div className="relative flex-1">
              <Search className="pointer-events-none absolute left-4 top-1/2 h-4 w-4 -translate-y-1/2 text-slate-400" />
              <Input value={search} onChange={(event) => setSearch(event.target.value)} placeholder="Search studios, classes, neighborhoods" className="pl-11" />
            </div>
            <Button size="lg" onClick={() => navigate(`/discover?search=${encodeURIComponent(search)}`)}>
              Find Classes
            </Button>
          </div>

          <div className="mt-8 flex flex-wrap gap-3">
            <Button variant="secondary" onClick={() => navigate('/discover')}>Browse Classes</Button>
            {!authenticated ? <Button onClick={login}>Login</Button> : <Button onClick={() => navigate('/dashboard')}>My Dashboard</Button>}
            {!authenticated ? <Button variant="ghost" onClick={register}>Register</Button> : null}
          </div>
        </div>

        <Card className="relative overflow-hidden border-primary-100 bg-white/90 p-0 shadow-lg">
          <div className="absolute inset-0 bg-gradient-to-br from-primary-50 via-white to-emerald-50" />
          <div className="relative grid gap-0 md:grid-cols-[1.1fr_0.9fr]">
            <div className="relative min-h-[360px] overflow-hidden rounded-l-2xl">
              <img src="https://images.unsplash.com/photo-1518611012118-696072aa579a?auto=format&fit=crop&w=1400&q=80" alt="Studio atmosphere" className="h-full w-full object-cover" />
              <div className="absolute inset-0 bg-gradient-to-t from-slate-900/35 via-transparent to-transparent" />
            </div>
            <div className="flex flex-col justify-between p-6 md:p-8">
              <div>
                <p className="text-xs font-bold uppercase tracking-[0.25em] text-primary-600">Featured studio</p>
                <h2 className="mt-4 text-3xl font-bold tracking-tight text-text-primary">Zenith Studio</h2>
                <p className="mt-3 text-sm leading-6 text-text-muted">Sunlit yoga and mobility sessions built for recovery, focus, and a premium neighborhood feel.</p>
              </div>
              <div className="mt-8 space-y-3 rounded-2xl bg-white/85 p-4 shadow-sm backdrop-blur">
                <div className="text-sm font-semibold text-slate-500">Next session</div>
                <div className="text-lg font-bold text-text-primary">Tomorrow, 8:00 AM</div>
                <div className="flex gap-3 pt-2">
                  <Button className="flex-1" onClick={() => navigate('/studios/zenith-studio')}>View Studio</Button>
                  <Button variant="secondary" className="flex-1" onClick={() => navigate('/checkout/40000000-0000-0000-0000-000000000001')}>Book Now</Button>
                </div>
              </div>
            </div>
          </div>
        </Card>
      </section>

      <section className="mx-auto max-w-[1200px] px-4 py-10 md:px-8">
        <SectionHeading eyebrow="Why PulseFit" title="One place for nearby classes" description="Search sessions by neighborhood, discipline, price, or time, then book without juggling DMs or spreadsheets." />
        <div className="mt-8 grid gap-5 md:grid-cols-3">
          {[
            ['Search fast', 'Explore nearby studios and filter by time, discipline, and price.'],
            ['Book instantly', 'Stripe Checkout handles one-off payments and class-pack subscriptions.'],
            ['Manage everything', 'Track bookings, credits, receipts, and cancellations from one account.'],
          ].map(([title, text]) => (
            <Card key={title} className="p-6">
              <div className="text-xl font-bold text-text-primary">{title}</div>
              <p className="mt-3 text-sm leading-6 text-text-muted">{text}</p>
            </Card>
          ))}
        </div>
      </section>

      <section className="mx-auto max-w-[1200px] px-4 py-10 md:px-8">
        <SectionHeading eyebrow="Featured studios" title="A few neighborhood favorites" action={<Link to="/discover" className="text-sm font-semibold text-primary-600">View all <ArrowRight className="inline h-4 w-4" /></Link>} />
        <div className="mt-8 grid gap-6 lg:grid-cols-3">
          {landingQuery.data?.featuredStudios.map((studio) => <StudioCard key={studio.id} studio={studio} />)}
        </div>
      </section>

      <section className="mx-auto max-w-[1200px] px-4 py-10 md:px-8">
        <SectionHeading eyebrow="Memberships" title="The 10-class pack for regulars" description="Buy a monthly 10-class pack subscription, spend credits as you book, and manage it all from billing settings." />
        <div className="mt-8 grid gap-6 lg:grid-cols-2">
          <Card className="p-7">
            <div className="text-sm font-semibold uppercase tracking-[0.22em] text-primary-600">Class pack</div>
            <div className="mt-4 text-4xl font-black text-text-primary">€79<span className="align-top text-base font-semibold text-slate-500"> / month</span></div>
            <p className="mt-4 text-sm leading-6 text-text-muted">10 credits, monthly renewal, and receipt history in your billing dashboard. Cancel anytime before renewal.</p>
            <Button className="mt-6" onClick={() => navigate('/billing')}>See Membership Details</Button>
          </Card>
          <div className="grid gap-4 sm:grid-cols-2">
            {landingQuery.data?.latestReviews.map((review) => (
              <Card key={review.id} className="p-5">
                <div className="flex items-center justify-between">
                  <div className="text-sm font-semibold text-text-primary">{review.reviewerName}</div>
                  <Badge tone="success">{review.rating}/5</Badge>
                </div>
                <p className="mt-4 text-sm leading-6 text-text-muted">{review.comment}</p>
              </Card>
            ))}
          </div>
        </div>
      </section>
    </div>
  )
}
