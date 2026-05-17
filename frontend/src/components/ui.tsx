import { Children, cloneElement, forwardRef, isValidElement, type ButtonHTMLAttributes, type HTMLAttributes, type InputHTMLAttributes, type ReactElement, type ReactNode, type TextareaHTMLAttributes } from 'react'
import { cn } from '../lib/cn'

export function Badge({ className, children, tone = 'neutral' }: { className?: string; children: ReactNode; tone?: 'neutral' | 'success' | 'warning' | 'danger' | 'info' }) {
  const tones: Record<string, string> = {
    neutral: 'bg-slate-100 text-slate-700',
    success: 'bg-emerald-100 text-emerald-700',
    warning: 'bg-amber-100 text-amber-700',
    danger: 'bg-red-100 text-red-700',
    info: 'bg-indigo-100 text-indigo-700',
  }
  return <span className={cn('inline-flex items-center rounded-full px-3 py-1 text-xs font-semibold tracking-wide', tones[tone], className)}>{children}</span>
}

export const Button = forwardRef<HTMLButtonElement, ButtonHTMLAttributes<HTMLButtonElement> & { variant?: 'primary' | 'secondary' | 'ghost' | 'danger'; size?: 'sm' | 'md' | 'lg'; asChild?: boolean }>(
  function Button({ className, variant = 'primary', size = 'md', asChild, children, ...props }, ref) {
    const variants = {
      primary: 'bg-primary-600 text-white shadow-md hover:bg-primary-700',
      secondary: 'bg-white text-text-primary border border-slate-200 hover:border-primary-300 hover:bg-primary-50',
      ghost: 'bg-transparent text-text-primary hover:bg-slate-100',
      danger: 'bg-danger text-white hover:opacity-90',
    }
    const sizes = {
      sm: 'h-9 px-3 text-sm',
      md: 'h-11 px-5 text-sm font-semibold',
      lg: 'h-12 px-6 text-base font-semibold',
    }
    const sharedClassName = cn('inline-flex items-center justify-center gap-2 rounded-xl transition focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-primary-400 focus-visible:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-50', variants[variant], sizes[size], className)

    if (asChild && isValidElement(Children.only(children))) {
      const child = Children.only(children) as ReactElement<{ className?: string }>
      return cloneElement(child, {
        className: cn(sharedClassName, child.props.className),
      })
    }

    return <button ref={ref} className={sharedClassName} {...props}>{children}</button>
  },
)

export const Input = forwardRef<HTMLInputElement, InputHTMLAttributes<HTMLInputElement>>(function Input({ className, ...props }, ref) {
  return <input ref={ref} className={cn('h-12 w-full rounded-xl border border-slate-200 bg-white px-4 text-sm text-text-primary shadow-sm outline-none transition placeholder:text-slate-400 focus:border-primary-400 focus:ring-2 focus:ring-primary-100', className)} {...props} />
})

export function Textarea(props: TextareaHTMLAttributes<HTMLTextAreaElement>) {
  return <textarea className={cn('min-h-28 w-full rounded-xl border border-slate-200 bg-white px-4 py-3 text-sm text-text-primary shadow-sm outline-none transition placeholder:text-slate-400 focus:border-primary-400 focus:ring-2 focus:ring-primary-100', props.className)} {...props} />
}

export function Card({ className, children, ...props }: HTMLAttributes<HTMLDivElement>) {
  return <div className={cn('rounded-2xl border border-slate-200 bg-white shadow-sm transition hover:shadow-md', className)} {...props}>{children}</div>
}

export function SectionHeading({ eyebrow, title, description, action, className }: { eyebrow?: string; title: string; description?: string; action?: ReactNode; className?: string }) {
  return (
    <div className={cn('flex items-start justify-between gap-4', className)}>
      <div>
        {eyebrow ? <p className="text-xs font-semibold uppercase tracking-[0.25em] text-primary-600">{eyebrow}</p> : null}
        <h2 className="mt-2 text-2xl font-bold tracking-tight text-text-primary md:text-3xl">{title}</h2>
        {description ? <p className="mt-2 max-w-2xl text-sm leading-6 text-text-muted md:text-base">{description}</p> : null}
      </div>
      {action}
    </div>
  )
}

export function KpiCard({ label, value, delta, tone = 'info' }: { label: string; value: string; delta?: string; tone?: 'info' | 'success' | 'warning' | 'danger' }) {
  const toneStyles = {
    info: 'bg-indigo-50 text-primary-700',
    success: 'bg-emerald-50 text-emerald-700',
    warning: 'bg-amber-50 text-amber-700',
    danger: 'bg-red-50 text-red-700',
  }
  return (
    <Card className="p-5">
      <div className="text-xs font-semibold uppercase tracking-[0.22em] text-slate-500">{label}</div>
      <div className="mt-4 flex items-end justify-between gap-3">
        <div className="text-4xl font-extrabold tracking-tight text-text-primary">{value}</div>
        {delta ? <Badge className={toneStyles[tone]}>{delta}</Badge> : null}
      </div>
    </Card>
  )
}

export function Avatar({ src, name, size = 'md' }: { src?: string; name: string; size?: 'sm' | 'md' | 'lg' }) {
  const sizes = { sm: 'h-8 w-8 text-xs', md: 'h-10 w-10 text-sm', lg: 'h-14 w-14 text-base' }
  const initials = name.split(' ').map((part) => part[0]).join('').slice(0, 2).toUpperCase()
  return src ? (
    <img src={src} alt={name} className={cn('rounded-full object-cover', sizes[size])} />
  ) : (
    <div className={cn('flex items-center justify-center rounded-full bg-primary-100 font-semibold text-primary-700', sizes[size])}>{initials}</div>
  )
}
