import { render, screen, fireEvent } from '@testing-library/react'
import { MemoryRouter, Route, Routes } from 'react-router-dom'
import { QueryClientProvider } from '@tanstack/react-query'
import { describe, expect, it, vi } from 'vitest'
import { queryClient } from '../../api/queryClient'
import { LandingPage } from './LandingPage'

vi.mock('../../auth/AuthProvider', () => ({
  useAuth: () => ({
    authenticated: false,
    login: vi.fn(),
    register: vi.fn(),
  }),
}))

vi.mock('../../api/client', () => ({
  api: {
    getLanding: async () => ({
      featuredStudios: [
        {
          id: 'studio-1',
          name: 'Zenith Studio',
          slug: 'zenith-studio',
          neighborhood: 'Shoreditch',
          discipline: 'Yoga',
          description: 'Sunlit yoga sessions in Shoreditch.',
          priceFromEur: '18.00',
          rating: '4.9',
          reviewCount: 188,
          featured: true,
          imageUrl: 'https://images.unsplash.com/photo-1506126613408-eca07ce68773?auto=format&fit=crop&w=1200&q=80',
          bannerUrl: 'https://images.unsplash.com/photo-1506126613408-eca07ce68773?auto=format&fit=crop&w=1600&q=80',
          ownerName: 'Maya Lin',
          cancellationPolicyHours: 12,
        },
      ],
      upcomingSessions: [],
      latestReviews: [],
    }),
  },
}))

describe('LandingPage', () => {
  it('navigates to discovery from the hero CTA', async () => {
    render(
      <QueryClientProvider client={queryClient}>
        <MemoryRouter initialEntries={['/']}>
          <Routes>
            <Route path="/" element={<LandingPage />} />
            <Route path="/discover" element={<div data-testid="discover-page">Discovery</div>} />
          </Routes>
        </MemoryRouter>
      </QueryClientProvider>,
    )

    expect(await screen.findByText('Zenith Studio')).toBeInTheDocument()
    fireEvent.click(screen.getByRole('button', { name: /find classes/i }))
    expect(await screen.findByTestId('discover-page')).toBeInTheDocument()
  })
})
