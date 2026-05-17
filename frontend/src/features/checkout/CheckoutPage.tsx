import { useMutation, useQuery } from '@tanstack/react-query'
import { useNavigate, useParams } from 'react-router-dom'
import { api } from '../../api/client'
import { Badge, Button, Card } from '../../components/ui'

export function CheckoutPage() {
  const { sessionId = '' } = useParams()
  const navigate = useNavigate()
  const sessionsQuery = useQuery({ queryKey: ['sessions'], queryFn: () => api.getSessions({}) })
  const session = sessionsQuery.data?.find((item) => item.id === sessionId)

  const checkoutMutation = useMutation({
    mutationFn: () => api.createSessionCheckout(sessionId),
    onSuccess: (result) => {
      window.location.assign(result.url)
    },
  })

  if (!session) {
    return <div className="mx-auto max-w-[720px] px-4 py-16 text-text-muted">Loading checkout...</div>
  }

  return (
    <div className="mx-auto max-w-[720px] px-4 py-16">
      <Card className="overflow-hidden p-0">
        <img src={session.imageUrl} alt={session.classTitle} className="h-64 w-full object-cover" />
        <div className="space-y-5 p-8">
          <Badge tone="info">Secure Stripe checkout</Badge>
          <h1 className="text-4xl font-black tracking-tight text-text-primary">{session.classTitle}</h1>
          <p className="text-text-muted">{session.studioName} · {session.locationName}</p>
          <div className="text-3xl font-extrabold text-text-primary">€{session.priceEur}</div>
          <div className="flex gap-3">
            <Button className="flex-1" onClick={() => checkoutMutation.mutate()}>
              Continue to payment
            </Button>
            <Button variant="secondary" className="flex-1" onClick={() => navigate(`/studios/${session.studioSlug}`)}>
              Back to studio
            </Button>
          </div>
        </div>
      </Card>
    </div>
  )
}
