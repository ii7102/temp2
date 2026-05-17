import { useMemo, useState } from 'react'
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { api } from '../../api/client'
import { Badge, Button, Card, Input, KpiCard, SectionHeading } from '../../components/ui'

export function AdminDashboardPage() {
  const queryClient = useQueryClient()
  const dashboardQuery = useQuery({ queryKey: ['admin-dashboard'], queryFn: api.getAdminDashboard })
  const [search, setSearch] = useState('')
  const usersQuery = useQuery({ queryKey: ['admin-users', search], queryFn: () => api.getAdminUsers(search) })
  const paymentsQuery = useQuery({ queryKey: ['admin-payments'], queryFn: api.getPayments })
  const roleMutation = useMutation({ mutationFn: ({ id, role }: { id: string; role: 'user' | 'admin' }) => api.updateAdminRole(id, role), onSuccess: () => queryClient.invalidateQueries({ queryKey: ['admin-users'] }) })
  const refundMutation = useMutation({ mutationFn: (id: string) => api.refundPayment(id), onSuccess: () => queryClient.invalidateQueries({ queryKey: ['admin-payments'] }) })

  const users = useMemo(() => usersQuery.data?.items ?? [], [usersQuery.data])

  if (!dashboardQuery.data) {
    return <div className="mx-auto max-w-[1200px] px-4 py-10 text-text-muted">Loading admin dashboard...</div>
  }

  const dashboard = dashboardQuery.data

  return (
    <div className="min-h-screen bg-admin-sidebar text-white">
      <div className="grid min-h-screen lg:grid-cols-[260px_1fr]">
        <aside className="border-r border-white/10 bg-admin-sidebar px-5 py-6">
          <div className="text-3xl font-black text-primary-400">PulseFit</div>
          <div className="mt-1 text-sm text-slate-400">Admin Terminal</div>
          <nav className="mt-10 space-y-2 text-sm font-semibold text-slate-300">
            <div className="rounded-xl bg-white/5 px-4 py-3 text-secondary">Metrics</div>
            <div className="px-4 py-3">Studios</div>
            <div className="px-4 py-3">Users</div>
            <div className="px-4 py-3">System Logs</div>
          </nav>
        </aside>

        <main className="bg-background text-text-primary">
          <div className="flex items-center justify-between border-b border-slate-200 bg-white px-6 py-5">
            <div className="text-3xl font-black text-primary-600">PulseFit Admin</div>
            <Input placeholder="Search studios, users..." className="max-w-xl" value={search} onChange={(event) => setSearch(event.target.value)} />
          </div>

          <div className="space-y-8 px-6 py-8">
            <SectionHeading eyebrow="Global Overview" title="Track platform metrics, studio approvals, and system health." />
            <div className="grid gap-4 md:grid-cols-2 xl:grid-cols-4">
              <KpiCard label="Total Revenue" value={`€${dashboard.metrics.totalRevenueEur}`} delta="+12% vs last month" tone="success" />
              <KpiCard label="Active Bookings" value={String(dashboard.metrics.activeBookings)} delta="+8% vs last month" tone="info" />
              <KpiCard label="Pending Approvals" value={String(dashboard.metrics.pendingApprovals)} delta="Action needed" tone="warning" />
              <KpiCard label="System Health" value={`${dashboard.metrics.systemHealth}%`} delta="Optimal" tone="success" />
            </div>

            <div className="grid gap-6 xl:grid-cols-[1.3fr_0.9fr]">
              <Card className="overflow-hidden">
                <div className="border-b border-slate-200 px-6 py-5">
                  <SectionHeading eyebrow="Approvals" title="Studio Approval Queue" description="Review pending studios before they go live." />
                </div>
                <div className="space-y-4 p-6">
                  {dashboard.approvalQueue.map((studio) => (
                    <div key={studio.id} className="flex items-center justify-between rounded-2xl border border-slate-200 p-4">
                      <div>
                        <div className="font-semibold text-text-primary">{studio.name}</div>
                        <div className="text-sm text-text-muted">{studio.category} · {studio.location}</div>
                      </div>
                      <Badge tone="warning">{studio.status}</Badge>
                    </div>
                  ))}
                  {!dashboard.approvalQueue.length ? <div className="text-sm text-text-muted">No pending approvals right now.</div> : null}
                </div>
              </Card>

              <Card className="overflow-hidden">
                <div className="border-b border-slate-200 px-6 py-5">
                  <SectionHeading eyebrow="System alerts" title="Recent events" />
                </div>
                <div className="space-y-4 p-6">
                  {dashboard.alerts.map((alert) => (
                    <div key={alert.title} className="rounded-2xl border border-slate-200 p-4">
                      <div className="font-semibold text-text-primary">{alert.title}</div>
                      <div className="mt-2 text-sm text-text-muted">{alert.message}</div>
                    </div>
                  ))}
                </div>
              </Card>
            </div>

            <Card className="overflow-hidden">
              <div className="border-b border-slate-200 px-6 py-5">
                <SectionHeading eyebrow="Users" title="Keycloak user directory" description="Search registered accounts and promote or demote between user and admin roles." />
              </div>
              <div className="space-y-4 p-6">
                {users.map((user) => (
                  <div key={user.id} className="flex flex-col gap-3 rounded-2xl border border-slate-200 p-4 md:flex-row md:items-center md:justify-between">
                    <div>
                      <div className="font-semibold text-text-primary">{user.fullName}</div>
                      <div className="text-sm text-text-muted">{user.email}</div>
                    </div>
                    <div className="flex flex-wrap items-center gap-3">
                      {user.roles.map((role) => <Badge key={role} tone={role === 'admin' ? 'info' : 'neutral'}>{role}</Badge>)}
                      <Button variant="secondary" size="sm" onClick={() => roleMutation.mutate({ id: user.id, role: 'user' })}>Set user</Button>
                      <Button size="sm" onClick={() => roleMutation.mutate({ id: user.id, role: 'admin' })}>Set admin</Button>
                    </div>
                  </div>
                ))}
              </div>
            </Card>

            <Card className="overflow-hidden">
              <div className="border-b border-slate-200 px-6 py-5">
                <SectionHeading eyebrow="Payments" title="Recent payments and refunds" />
              </div>
              <div className="space-y-4 p-6">
                {paymentsQuery.data?.map((payment) => (
                  <div key={payment.id} className="flex flex-col gap-3 rounded-2xl border border-slate-200 p-4 md:flex-row md:items-center md:justify-between">
                    <div>
                      <div className="font-semibold text-text-primary">{payment.reference}</div>
                      <div className="text-sm text-text-muted">{payment.customer} · {payment.type} · {payment.status}</div>
                    </div>
                    <div className="flex items-center gap-3">
                      <div className="font-bold text-text-primary">€{payment.amountEur}</div>
                      <Button variant="danger" size="sm" onClick={() => refundMutation.mutate(payment.id)}>Refund</Button>
                    </div>
                  </div>
                ))}
              </div>
            </Card>
          </div>
        </main>
      </div>
    </div>
  )
}
