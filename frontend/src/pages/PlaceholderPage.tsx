import { AppShell } from '../components/AppShell'

export function PlaceholderPage({ title, description }: { title: string; description: string }) {
  return <AppShell><div className="page-heading"><div><span className="eyebrow eyebrow--red">Motor insurance</span><h1>{title}</h1><p>{description}</p></div></div><div className="panel placeholder-panel"><strong>This area is next.</strong><p>The navigation and application shell are in place. We will wire this flow to the verified backend endpoints in the next implementation pass.</p></div></AppShell>
}
