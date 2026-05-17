import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { api } from '../../api/client'
import { Badge, Button, Card, SectionHeading } from '../../components/ui'

export function BillingPage() {
  const queryClient = useQueryClient()
  const billingQuery = useQuery({ queryKey: ['billing'], queryFn: api.getBilling })
  const portalMutation = useMutation({ mutationFn: api.createPortal, onSuccess: (result) => window.location.assign(result.url) })
  const subscribeMutation = useMutation({ mutationFn: api.createSubscriptionCheckout, onSuccess: (result) => window.location.assign(result.url) })
  const cancelMutation = useMutation({ mutationFn: api.cancelSubscription, onSuccess: () => queryClient.invalidateQueries({ queryKey: ['billing'] }) })

  if (!billingQuery.data) {
    return <div className="mx-auto max-w-[1000px] px-4 py-10 text-text-muted">Loading billing...</div>
  }

  const billing = billingQuery.data

  return (
    <div className="mx-auto max-w-[1000px] px-4 py-10 md:px-8">
      <SectionHeading eyebrow="Billing" title="Manage your class pack and payment method" description="Update your saved payment method with Stripe Customer Portal and review receipts here." />

      <div className="mt-8 grid gap-6 lg:grid-cols-[0.95fr_1.05fr]">
        <Card className="p-7">
          <Badge tone="info">10-class pack</Badge>
          <div className="mt-5 text-4xl font-black text-text-primary">€79<span className="text-base font-semibold text-slate-500"> / month</span></div>
          <p className="mt-4 text-sm leading-6 text-text-muted">{billing.subscription.creditsBalance} credits remaining. Cancel before renewal if you want to stop the next cycle.</p>
          <div className="mt-6 flex flex-wrap gap-3">
            <Button onClick={() => subscribeMutation.mutate()}>Buy class pack</Button>
            <Button variant="secondary" onClick={() => portalMutation.mutate()}>Update payment method</Button>
            <Button variant="danger" onClick={() => cancelMutation.mutate()}>Cancel subscription</Button>
          </div>
        </Card>

        <Card className="p-7">
          <div className="text-lg font-bold text-text-primary">Saved payment method</div>
          <p className="mt-2 text-sm text-text-muted">{billing.profile.savedPaymentMethodBrand ?? 'No card on file'} {billing.profile.savedPaymentMethodLast4 ? `•••• ${billing.profile.savedPaymentMethodLast4}` : ''}</p>
          <div className="mt-6 text-sm text-text-muted">Customer Portal URL is loaded from the backend and keeps secret keys server-side.</div>
          <Button className="mt-6" variant="secondary" onClick={() => portalMutation.mutate()}>Open customer portal</Button>
        </Card>
      </div>

      <SectionHeading eyebrow="Receipts" title="Recent payments" className="mt-12" />
      <div className="mt-8 grid gap-4">
        {billing.receipts.map((receipt) => (
          <Card key={receipt.id} className="flex items-center justify-between p-5">
            <div>
              <div className="font-semibold text-text-primary">{receipt.description}</div>
              <div className="text-sm text-text-muted">{new Intl.DateTimeFormat('en-GB').format(new Date(receipt.createdAt))}</div>
            </div>
            <div className="text-right">
              <div className="font-bold text-text-primary">€{receipt.amountEur}</div>
              <div className="text-sm text-slate-500">{receipt.status}</div>
            </div>
          </Card>
        ))}
      </div>
    </div>
  )
}
