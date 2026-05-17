import { useMemo, useState } from 'react'
import { useQuery } from '@tanstack/react-query'
import { Search } from 'lucide-react'
import { useNavigate, useSearchParams } from 'react-router-dom'
import { api } from '../../api/client'
import { Button, Card, Input, SectionHeading } from '../../components/ui'
import { SessionCard } from '../../components/marketing'

export function DiscoveryPage() {
  const [searchParams, setSearchParams] = useSearchParams()
  const navigate = useNavigate()
  const [search, setSearch] = useState(searchParams.get('search') ?? '')
  const [neighborhood, setNeighborhood] = useState(searchParams.get('neighborhood') ?? '')
  const [discipline, setDiscipline] = useState(searchParams.get('discipline') ?? '')
  const [maxPrice, setMaxPrice] = useState(searchParams.get('maxPrice') ?? '')

  const query = useQuery({
    queryKey: ['sessions', search, neighborhood, discipline, maxPrice],
    queryFn: () => api.getSessions({ search, neighborhood, discipline, maxPrice }),
  })

  const sessions = useMemo(() => query.data ?? [], [query.data])

  function applyFilters() {
    setSearchParams({ search, neighborhood, discipline, maxPrice })
  }

  return (
    <div className="mx-auto max-w-[1200px] px-4 py-10 md:px-8">
      <SectionHeading eyebrow="Explore" title="Browse classes across the city" description="Filter by neighborhood, time, discipline, and price. Guest users can browse everything and jump into checkout once they sign in." />

      <Card className="mt-8 p-5">
        <div className="grid gap-4 lg:grid-cols-[1.3fr_0.8fr_0.8fr_0.5fr_auto]">
          <div className="relative">
            <Search className="pointer-events-none absolute left-4 top-1/2 h-4 w-4 -translate-y-1/2 text-slate-400" />
            <Input value={search} onChange={(event) => setSearch(event.target.value)} placeholder="Search sessions or studios" className="pl-11" />
          </div>
          <Input value={neighborhood} onChange={(event) => setNeighborhood(event.target.value)} placeholder="Neighborhood" />
          <Input value={discipline} onChange={(event) => setDiscipline(event.target.value)} placeholder="Discipline" />
          <Input value={maxPrice} onChange={(event) => setMaxPrice(event.target.value)} placeholder="Max €" />
          <Button onClick={applyFilters}>Search</Button>
        </div>
      </Card>

      <div className="mt-8 grid gap-5">
        {sessions.map((session) => <SessionCard key={session.id} session={session} onBook={() => navigate(`/checkout/${session.id}`)} />)}
      </div>
    </div>
  )
}
