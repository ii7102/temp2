import { useParams, useNavigate } from 'react-router-dom'
import { useQuery } from '@tanstack/react-query'
import { Clock3, MapPin, Star } from 'lucide-react'
import { api } from '../../api/client'
import { Badge, Button, Card, SectionHeading } from '../../components/ui'

export function StudioPage() {
  const { slug = '' } = useParams()
  const navigate = useNavigate()
  const query = useQuery({ queryKey: ['studio', slug], queryFn: () => api.getStudio(slug) })

  const studio = query.data?.studio
  if (!studio) return <div className="mx-auto max-w-[1200px] px-4 py-10 text-text-muted">Loading studio...</div>

  return (
    <div className="mx-auto max-w-[1200px] px-4 py-10 md:px-8">
      <div className="overflow-hidden rounded-[24px] bg-white shadow-lg">
        <div className="grid lg:grid-cols-[1.2fr_0.8fr]">
          <div className="relative min-h-[380px]">
            <img src={studio.bannerUrl} alt={studio.name} className="h-full w-full object-cover" />
            <div className="absolute inset-0 bg-gradient-to-tr from-slate-950/35 via-transparent to-transparent" />
          </div>
          <div className="p-8">
            <Badge tone="info">Approved studio</Badge>
            <h1 className="mt-4 text-4xl font-black tracking-tight text-text-primary">{studio.name}</h1>
            <p className="mt-4 text-base leading-7 text-text-muted">{studio.description}</p>
            <div className="mt-6 flex flex-wrap gap-3 text-sm text-slate-600">
              <span className="inline-flex items-center gap-2"><MapPin className="h-4 w-4" /> {studio.neighborhood}</span>
              <span className="inline-flex items-center gap-2"><Clock3 className="h-4 w-4" /> {studio.cancellationPolicyHours}h cancellation</span>
              <span className="inline-flex items-center gap-2"><Star className="h-4 w-4" /> {studio.rating} rating</span>
            </div>
            <div className="mt-8 flex gap-3">
              <Button onClick={() => navigate('/discover')}>Browse Sessions</Button>
              <Button variant="secondary" onClick={() => navigate('/billing')}>Memberships</Button>
            </div>
          </div>
        </div>
      </div>

      <SectionHeading eyebrow="Schedule" title="Upcoming sessions" description="Book a spot, pay with Stripe Checkout, or use your class-pack credits once you’re logged in." action={<Button variant="secondary" onClick={() => navigate('/discover')}>View All Classes</Button>} />
      <div className="mt-8 grid gap-4">
        {query.data?.upcomingSessions.map((session) => (
          <Card key={session.id} className="overflow-hidden">
            <div className="grid gap-4 md:grid-cols-[160px_1fr_auto] md:items-center">
              <img src={session.imageUrl} alt={session.classTitle} className="h-40 w-full object-cover" />
              <div>
                <div className="text-xs font-semibold uppercase tracking-[0.2em] text-primary-600">{session.discipline}</div>
                <div className="mt-2 text-2xl font-bold text-text-primary">{session.classTitle}</div>
                <div className="mt-2 text-sm text-text-muted">{session.locationName}</div>
                <div className="mt-3 text-sm text-slate-600">{new Intl.DateTimeFormat('en-GB', { weekday: 'short', hour: 'numeric', minute: '2-digit' }).format(new Date(session.startsAt))} · €{session.priceEur}</div>
              </div>
              <div className="p-4 md:p-6">
                <Button onClick={() => navigate(`/checkout/${session.id}`)}>Book Now</Button>
              </div>
            </div>
          </Card>
        ))}
      </div>

      <div className="mt-12 grid gap-4 md:grid-cols-2">
        <div>
          <SectionHeading eyebrow="Instructors" title="Meet the teachers" />
          <div className="mt-4 grid gap-4">
            {query.data?.instructors.map((instructor) => (
              <Card key={instructor.id} className="flex gap-4 p-4">
                <img src={instructor.avatarUrl} alt={instructor.fullName} className="h-16 w-16 rounded-full object-cover" />
                <div>
                  <div className="font-semibold text-text-primary">{instructor.fullName}</div>
                  <div className="text-sm text-text-muted">{instructor.specialties}</div>
                </div>
              </Card>
            ))}
          </div>
        </div>
        <div>
          <SectionHeading eyebrow="Reviews" title="What people are saying" />
          <div className="mt-4 grid gap-4">
            {query.data?.reviews.map((review) => (
              <Card key={review.id} className="p-4">
                <div className="flex items-center justify-between">
                  <div className="font-semibold text-text-primary">{review.reviewerName}</div>
                  <Badge tone="success">{review.rating}/5</Badge>
                </div>
                <p className="mt-3 text-sm leading-6 text-text-muted">{review.comment}</p>
              </Card>
            ))}
          </div>
        </div>
      </div>
    </div>
  )
}
