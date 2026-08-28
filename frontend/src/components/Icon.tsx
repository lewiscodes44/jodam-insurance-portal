export type IconName = 'shield' | 'arrow' | 'check' | 'car' | 'receipt' | 'bell' | 'plus' | 'logout' | 'menu' | 'eye' | 'eye-off' | 'users' | 'claim' | 'briefcase' | 'chevron'
type Props = { name: IconName }

export function Icon({ name }: Props) {
  const common = { width: 18, height: 18, viewBox: '0 0 24 24', fill: 'none', stroke: 'currentColor', strokeWidth: 1.8, strokeLinecap: 'round' as const, strokeLinejoin: 'round' as const }
  switch (name) {
    case 'shield': return <svg {...common}><path d="M12 3 20 6v5c0 5.2-3.4 8.7-8 10-4.6-1.3-8-4.8-8-10V6l8-3Z"/><path d="m8.5 12 2.2 2.2 4.8-4.8"/></svg>
    case 'arrow': return <svg {...common}><path d="M5 12h14"/><path d="m13 6 6 6-6 6"/></svg>
    case 'check': return <svg {...common}><path d="m5 12 4 4L19 6"/></svg>
    case 'car': return <svg {...common}><path d="M5 16 6.8 9.8A2 2 0 0 1 8.7 8.3h6.6a2 2 0 0 1 1.9 1.5L19 16"/><path d="M4 16h16v4H4z"/><path d="M7 20v1M17 20v1M7 13h10"/></svg>
    case 'receipt': return <svg {...common}><path d="M6 3h12v18l-3-2-3 2-3-2-3 2V3Z"/><path d="M9 8h6M9 12h6M9 16h4"/></svg>
    case 'bell': return <svg {...common}><path d="M18 8a6 6 0 0 0-12 0c0 7-3 7-3 9h18c0-2-3-2-3-9"/><path d="M10 21h4"/></svg>
    case 'plus': return <svg {...common}><path d="M12 5v14M5 12h14"/></svg>
    case 'logout': return <svg {...common}><path d="M10 17l5-5-5-5"/><path d="M15 12H3"/><path d="M21 19V5a2 2 0 0 0-2-2h-5"/></svg>
    case 'menu': return <svg {...common}><path d="M4 7h16M4 12h16M4 17h16"/></svg>
    case 'eye': return <svg {...common}><path d="M2.5 12s3.2-5 9.5-5 9.5 5 9.5 5-3.2 5-9.5 5-9.5-5-9.5-5Z"/><circle cx="12" cy="12" r="2.5"/></svg>
    case 'eye-off': return <svg {...common}><path d="M3 3l18 18"/><path d="M10.6 10.6A2.5 2.5 0 0 0 14 14"/><path d="M6.7 6.7C4.1 8.2 2.5 10.3 2.5 12c0 0 3.2 5 9.5 5 1.5 0 2.8-.3 3.9-.8"/><path d="M17.3 17.3c2.6-1.5 4.2-3.6 4.2-5.3 0 0-3.2-5-9.5-5-1.2 0-2.3.2-3.3.6"/></svg>
    case 'users': return <svg {...common}><circle cx="9" cy="8" r="3"/><path d="M3 20c0-3.3 2.4-5 6-5s6 1.7 6 5"/><path d="M16 5.2a3 3 0 0 1 0 5.6M18 15.2c1.8.8 3 2.2 3 4.8"/></svg>
    case 'claim': return <svg {...common}><path d="M6 3h12v18l-3-2-3 2-3-2-3 2V3Z"/><path d="M9 8h6M9 12h6M9 16h3"/></svg>
    case 'briefcase': return <svg {...common}><rect x="3" y="7" width="18" height="13" rx="2"/><path d="M8 7V5a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2M3 12h18M10 12v2h4v-2"/></svg>
    case 'chevron': return <svg {...common}><path d="m7 10 5 5 5-5"/></svg>
  }
}
