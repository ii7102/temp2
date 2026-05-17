import type { ReactNode } from 'react'
import { Link } from 'react-router-dom'
import { ChevronRight, Heart, Menu, ShieldCheck, Ticket, UserCircle2, X } from 'lucide-react'
import { useState } from 'react'
import { useAuth } from '../auth/AuthProvider'
import { Button, Badge } from './ui'

const publicLinks = [
  { to: '/discover', label: 'Find Classes' },
  { to: '/studios/zenith-studio', label: 'Studios' },
  { to: '/billing', label: 'Memberships' },
  { to: '/dashboard', label: 'Community' },
]

const userLinks = [
  { to: '/discover', label: 'Explore' },
  { to: '/dashboard', label: 'My Bookings' },
  { to: '/billing', label: 'Billing' },
  { to: '/account', label: 'Profile' },
]

const adminLinks = [
  { to: '/admin', label: 'Metrics' },
  { to: '/discover', label: 'Studios' },
  { to: '/admin#users', label: 'Users' },
  { to: '/admin#logs', label: 'System Logs' },
]

export function SiteShell({ children }: { children: ReactNode }) {
  const auth = useAuth()
  const [menuOpen, setMenuOpen] = useState(false)
  const links = auth.authenticated ? (auth.roles.includes('admin') ? adminLinks : userLinks) : publicLinks

  return (
    <div className="min-h-screen">
      <header className="sticky top-0 z-30 border-b border-white/70 bg-white/85 backdrop-blur-xl">
        <div className="mx-auto flex max-w-[1200px] items-center justify-between gap-4 px-4 py-4 md:px-8">
          <Link to="/" className="flex items-center gap-3">
            <div className="flex h-11 w-11 items-center justify-center rounded-xl bg-primary-600 text-white shadow-md">
              <span className="text-lg font-black">P</span>
            </div>
            <div>
              <div className="text-2xl font-extrabold tracking-tight text-primary-600">PulseFit</div>
              <div className="text-[11px] font-semibold uppercase tracking-[0.25em] text-slate-500">Neighborhood fitness</div>
            </div>
          </Link>

          <nav className="hidden items-center gap-7 lg:flex">
            {links.map((link) => (
              <Link key={link.to} to={link.to} className="text-sm font-medium text-text-primary transition hover:text-primary-600">
                {link.label}
              </Link>
            ))}
            {auth.authenticated && auth.roles.includes('admin') ? <Badge tone="info">Admin</Badge> : null}
          </nav>

          <div className="hidden items-center gap-3 lg:flex">
            {!auth.authenticated ? (
              <>
                <Button variant="ghost" onClick={auth.login}>Sign In</Button>
                <Button onClick={auth.register}>Book Now</Button>
              </>
            ) : (
              <>
                <Button variant="ghost" onClick={auth.logout}>Logout</Button>
                <Button asChild>
                  <Link to={auth.roles.includes('admin') ? '/admin' : '/dashboard'} className="inline-flex items-center gap-2">
                    {auth.roles.includes('admin') ? <ShieldCheck className="h-4 w-4" /> : <UserCircle2 className="h-4 w-4" />}
                    {auth.name.split(' ')[0]}
                  </Link>
                </Button>
              </>
            )}
          </div>

          <button className="inline-flex h-11 w-11 items-center justify-center rounded-xl border border-slate-200 bg-white lg:hidden" onClick={() => setMenuOpen((value) => !value)}>
            {menuOpen ? <X className="h-5 w-5" /> : <Menu className="h-5 w-5" />}
          </button>
        </div>

        {menuOpen ? (
          <div className="border-t border-slate-200 bg-white px-4 py-4 lg:hidden">
            <div className="mx-auto flex max-w-[1200px] flex-col gap-3">
              {links.map((link) => (
                <Link key={link.to} to={link.to} className="rounded-xl px-3 py-2 text-sm font-medium text-text-primary hover:bg-slate-50" onClick={() => setMenuOpen(false)}>
                  {link.label}
                </Link>
              ))}
              {!auth.authenticated ? (
                <div className="flex gap-3 pt-2">
                  <Button variant="secondary" className="flex-1" onClick={auth.login}>Sign In</Button>
                  <Button className="flex-1" onClick={auth.register}>Register</Button>
                </div>
              ) : (
                <Button variant="secondary" onClick={auth.logout}>Logout</Button>
              )}
            </div>
          </div>
        ) : null}
      </header>

      <main>{children}</main>

      <footer className="border-t border-white/70 bg-[#e3e8ff]">
        <div className="mx-auto flex max-w-[1200px] flex-col gap-10 px-4 py-14 md:flex-row md:px-8">
          <div className="max-w-sm space-y-3">
            <div className="text-3xl font-extrabold text-primary-600">PulseFit</div>
            <p className="text-sm leading-6 text-slate-700">Book local classes with the speed of a modern marketplace and the polish of a premium studio brand.</p>
          </div>
          <div className="grid flex-1 gap-8 sm:grid-cols-3">
            <div>
              <div className="text-sm font-semibold uppercase tracking-[0.2em] text-slate-500">Browse</div>
              <div className="mt-4 space-y-3 text-sm text-slate-700">
                <div>Studios</div>
                <div>Classes</div>
                <div>Memberships</div>
              </div>
            </div>
            <div>
              <div className="text-sm font-semibold uppercase tracking-[0.2em] text-slate-500">Support</div>
              <div className="mt-4 space-y-3 text-sm text-slate-700">
                <div>Contact</div>
                <div>Privacy</div>
                <div>Terms</div>
              </div>
            </div>
            <div>
              <div className="text-sm font-semibold uppercase tracking-[0.2em] text-slate-500">App</div>
              <div className="mt-4 space-y-3 text-sm text-slate-700">
                <div className="flex items-center gap-2"><Ticket className="h-4 w-4 text-primary-600" /> Book in minutes</div>
                <div className="flex items-center gap-2"><Heart className="h-4 w-4 text-secondary" /> Save favorites</div>
                <div className="flex items-center gap-2"><ChevronRight className="h-4 w-4 text-primary-600" /> Manage credits</div>
              </div>
            </div>
          </div>
        </div>
      </footer>
    </div>
  )
}
