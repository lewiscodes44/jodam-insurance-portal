import { useEffect, useState } from "react";
import { Link, useNavigate, useParams } from "react-router-dom";
import { AppShell } from "../components/AppShell";
import { Icon } from "../components/Icon";
import {
  getMyNotifications,
  getMyPolicies,
  getMyQuotations,
  markNotificationRead,
  type Notification,
  type Policy,
  type Quotation,
} from "../lib/api";

function useUpdates() {
  const [items, setItems] = useState<Notification[]>([]);
  const [quotes, setQuotes] = useState<Quotation[]>([]);
  const [policies, setPolicies] = useState<Policy[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  useEffect(() => {
    Promise.all([getMyNotifications(), getMyQuotations(), getMyPolicies()])
      .then(([n, q, p]) => {
        setItems(n);
        setQuotes(q);
        setPolicies(p);
      })
      .catch((e) =>
        setError(
          e instanceof Error ? e.message : "Unable to load notifications",
        ),
      )
      .finally(() => setLoading(false));
  }, []);
  return { items, quotes, policies, loading, error, setItems };
}
function destination(n: Notification, quotes: Quotation[], policies: Policy[]) {
  const quote = quotes.find((q) => n.message.includes(q.quoteReference));
  if (quote) return `/app/quotations/${quote.id}`;
  const policy = policies.find((p) => n.message.includes(p.policyNumber));
  if (policy) return `/app/policies/${policy.id}`;
  return `/app/notifications/${n.id}`;
}
function liveMessage(n: Notification, quotes: Quotation[], policies: Policy[]) {
  const policy = policies.find((p) => n.message.includes(p.policyNumber));
  if (policy)
    return n.message.replace(
      /Amount due: KES [\d,.]+/i,
      `Amount due: KES ${Number(policy.premiumAmount).toLocaleString()}`,
    );
  const quote = quotes.find((q) => n.message.includes(q.quoteReference));
  if (quote)
    return n.message.replace(
      /Total payable: KES [\d,.]+/i,
      `Total payable: KES ${Number(quote.totalPayable).toLocaleString()}`,
    );
  return n.message;
}

export function NotificationsPage() {
  const { items, quotes, policies, loading, error, setItems } = useUpdates();
  const unread = items.filter((n) => !n.readAt);
  const read = items.filter((n) => n.readAt);
  async function open(n: Notification) {
    if (!n.readAt) {
      await markNotificationRead(n.id);
      setItems((current) =>
        current.map((item) =>
          item.id === n.id
            ? { ...item, readAt: new Date().toISOString() }
            : item,
        ),
      );
    }
  }
  const section = (title: string, records: Notification[]) => (
    <section className="notification-section">
      <div className="panel__header">
        <div>
          <span className="eyebrow">{title}</span>
          <h2>
            {title === "Unread" ? "Needs your attention" : "Previously viewed"}
          </h2>
        </div>
        <span>{records.length}</span>
      </div>
      <div className="stack-list">
        {records.map((n) => (
          <Link
            className={`list-card ${!n.readAt ? "notification-card--unread" : ""}`}
            key={n.id}
            to={destination(n, quotes, policies)}
            onClick={() => open(n)}
          >
            <div>
              <h2>{n.subject || "Jodam Insurance update"}</h2>
              <p>{liveMessage(n, quotes, policies)}</p>
              <small>{new Date(n.createdAt).toLocaleString()}</small>
            </div>
          </Link>
        ))}
      </div>
    </section>
  );
  return (
    <AppShell>
      <div className="page-heading">
        <div>
          <span className="eyebrow eyebrow--red">Updates</span>
          <h1>Notifications</h1>
          <p>
            Open a notification to view the related item. Unread updates stay at
            the top.
          </p>
        </div>
      </div>
      {error && <div className="form-error">{error}</div>}
      {loading ? (
        <div className="loading-state">Loading notifications…</div>
      ) : (
        <div className="notification-groups">
          {unread.length > 0 && section("Unread", unread)}
          {read.length > 0 && section("Read", read)}
          {!items.length && (
            <div className="panel empty-state">You're all caught up.</div>
          )}
        </div>
      )}
    </AppShell>
  );
}

export function NotificationDetailPage() {
  const { id } = useParams();
  const navigate = useNavigate();
  const { items, quotes, policies, loading, error, setItems } = useUpdates();
  const notification = items.find((n) => n.id === Number(id));
  useEffect(() => {
    if (notification && !notification.readAt)
      markNotificationRead(notification.id).then(() =>
        setItems((current) =>
          current.map((n) =>
            n.id === notification.id
              ? { ...n, readAt: new Date().toISOString() }
              : n,
          ),
        ),
      );
  }, [notification, setItems]);
  return (
    <AppShell>
      {loading ? (
        <div className="loading-state">Loading notification…</div>
      ) : error ? (
        <div className="form-error">{error}</div>
      ) : !notification ? (
        <div className="empty-state">Notification not found.</div>
      ) : (
        <div className="panel notification-detail">
          <h1>{notification.subject || "Jodam Insurance update"}</h1>
          <p className="detail-copy">
            {liveMessage(notification, quotes, policies)}
          </p>
          <small>{new Date(notification.createdAt).toLocaleString()}</small>
          <button
            className="button button--secondary"
            onClick={() => navigate("/app/notifications")}
          >
            <Icon name="arrow" /> Back to notifications
          </button>
        </div>
      )}
    </AppShell>
  );
}
