export function StatusPill({ value }: { value: string }) {
  const normalized = value.toLowerCase().replaceAll('_', '-').replaceAll(' ', '-')
  return <span className={`status status--${normalized}`}>{value.replaceAll('_', ' ')}</span>
}
