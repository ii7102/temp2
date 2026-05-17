import { useEffect, useState } from 'react'
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { api } from '../../api/client'
import { Badge, Button, Card, Input, SectionHeading } from '../../components/ui'

export function AccountPage() {
  const queryClient = useQueryClient()
  const profileQuery = useQuery({ queryKey: ['profile'], queryFn: api.getMeProfile })
  const [fullName, setFullName] = useState('')
  const [email, setEmail] = useState('')
  const [city, setCity] = useState('')
  const [neighborhood, setNeighborhood] = useState('')
  const [marketingOptIn, setMarketingOptIn] = useState(false)

  useEffect(() => {
    if (profileQuery.data) {
      setFullName(profileQuery.data.fullName)
      setEmail(profileQuery.data.email)
      setCity(profileQuery.data.city ?? '')
      setNeighborhood(profileQuery.data.neighborhood ?? '')
      setMarketingOptIn(profileQuery.data.marketingOptIn)
    }
  }, [profileQuery.data])

  const saveMutation = useMutation({
    mutationFn: () => api.updateMeProfile({ fullName, email, city, neighborhood, marketingOptIn }),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['profile'] }),
  })

  if (!profileQuery.data) {
    return <div className="mx-auto max-w-[920px] px-4 py-10 text-text-muted">Loading profile...</div>
  }

  return (
    <div className="mx-auto max-w-[920px] px-4 py-10 md:px-8">
      <SectionHeading eyebrow="Account" title="Profile settings" description="Keep your name and email in sync with Keycloak. Marketing emails stay opt-in only for GDPR friendliness." />
      <div className="mt-8 grid gap-6 lg:grid-cols-[0.7fr_1.3fr]">
        <Card className="p-7">
          <div className="text-lg font-bold text-text-primary">Membership</div>
          <Badge tone={profileQuery.data.hasSubscription ? 'success' : 'warning'} className="mt-4">{profileQuery.data.hasSubscription ? 'Active class pack' : 'No active pack'}</Badge>
          <p className="mt-4 text-sm leading-6 text-text-muted">Your saved payment method and subscription status are managed through Stripe and synced into the backend.</p>
        </Card>
        <Card className="space-y-4 p-7">
          <div>
            <label className="text-sm font-semibold text-text-primary">Full name</label>
            <Input value={fullName} onChange={(event) => setFullName(event.target.value)} className="mt-2" />
          </div>
          <div>
            <label className="text-sm font-semibold text-text-primary">Email</label>
            <Input value={email} onChange={(event) => setEmail(event.target.value)} className="mt-2" />
          </div>
          <div className="grid gap-4 sm:grid-cols-2">
            <div>
              <label className="text-sm font-semibold text-text-primary">City</label>
              <Input value={city} onChange={(event) => setCity(event.target.value)} className="mt-2" />
            </div>
            <div>
              <label className="text-sm font-semibold text-text-primary">Neighborhood</label>
              <Input value={neighborhood} onChange={(event) => setNeighborhood(event.target.value)} className="mt-2" />
            </div>
          </div>
          <label className="flex items-center gap-3 rounded-xl border border-slate-200 p-4 text-sm text-text-muted">
            <input type="checkbox" checked={marketingOptIn} onChange={(event) => setMarketingOptIn(event.target.checked)} className="h-4 w-4 rounded border-slate-300 text-primary-600" />
            I want to receive product emails and local class recommendations.
          </label>
          <Button onClick={() => saveMutation.mutate()}>Save changes</Button>
        </Card>
      </div>
    </div>
  )
}
