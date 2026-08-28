export function Logo({ compact = false }: { compact?: boolean }) {
  return (
    <a className={`brand ${compact ? 'brand--compact' : ''}`} href="/">
      <img src="/assets/jodam-logo.png" alt="Jodam Insurance Agency" />
    </a>
  )
}
