import { useEffect, useState } from "react";
import { Link, useParams } from "react-router-dom";
import { AppShell } from "../components/AppShell";
import { Icon } from "../components/Icon";
import { StatusPill } from "../components/StatusPill";
import {
  acceptQuotation,
  getMyQuotations,
  markNotificationsReadForReference,
  requestQuotationReview,
  type Quotation,
} from "../lib/api";

export function QuotationDetailPage() {
  const { id } = useParams();
  const [q, setQ] = useState<Quotation | null>(null);
  const [message, setMessage] = useState("");
  const [busy, setBusy] = useState(false);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");
  useEffect(() => {
    getMyQuotations()
      .then((items) => setQ(items.find((x) => x.id === Number(id)) ?? null))
      .catch((e) => setError(e.message));
  }, [id]);
  useEffect(() => {
    if (q?.quoteReference)
      void markNotificationsReadForReference(q.quoteReference);
  }, [q?.quoteReference]);
  async function accept() {
    if (!q) return;
    setBusy(true);
    setError("");
    try {
      await acceptQuotation(q.id);
      window.location.href = `/app/quotations/${q.id}/issue`;
    } catch (e) {
      setError(e instanceof Error ? e.message : "Unable to accept quotation");
    } finally {
      setBusy(false);
    }
  }
  async function review() {
    if (!q || !message.trim()) return;
    setBusy(true);
    setError("");
    try {
      const result = await requestQuotationReview(q.id, message.trim());
      setQ(result);
      setSuccess(
        "Your review request has been sent to Jodam. A staff member will contact you.",
      );
      setMessage("");
    } catch (e) {
      setError(
        e instanceof Error ? e.message : "Unable to send review request",
      );
    } finally {
      setBusy(false);
    }
  }
  if (!q)
    return (
      <AppShell>
        <div className="loading-state">{error || "Loading quotation…"}</div>
      </AppShell>
    );
  const ready = q.status === "SENT";
  return (
    <AppShell>
      <div className="page-heading">
        <div>
          <span className="eyebrow eyebrow--red">
            Quotation {q.quoteReference}
          </span>
          <h1>
            KES {Number(q.totalPayable || q.premiumAmount).toLocaleString()}
          </h1>
          <p>
            Review the offered cover, then accept it or ask Jodam to revise it.
          </p>
        </div>
        <StatusPill value={q.status} />
      </div>
      {error && <div className="form-error">{error}</div>}
      {success && <div className="success-banner">{success}</div>}
      <div className="detail-grid">
        <div className="detail-stack">
          <div className="panel">
            <span className="eyebrow">Offer details</span>
            <h2>
              {q.insurer} · {q.product}
            </h2>
            <div className="detail-facts detail-facts--wide">
              <Fact
                label="Basic premium"
                value={`KES ${Number(q.basicPremium).toLocaleString()}`}
              />
              <Fact
                label="Total payable"
                value={`KES ${Number(q.totalPayable || q.premiumAmount).toLocaleString()}`}
              />
              <Fact label="Valid until" value={q.validUntil} />
              <Fact
                label="Proposed cover"
                value={
                  q.proposedStartDate && q.proposedEndDate
                    ? `${q.proposedStartDate} to ${q.proposedEndDate}`
                    : "To be confirmed on policy issue"
                }
              />
              <Fact label="Excess" value={q.excess} />
            </div>
            <Detail title="Coverage details" value={q.coverageDetails} />
            <Detail title="Special terms" value={q.specialTerms} />
          </div>
        </div>
        <aside className="detail-stack">
          {ready && (
            <div className="panel">
              <span className="eyebrow">Decision</span>
              <h2>Ready to proceed?</h2>
              <p className="detail-copy">
                Accept this quotation to have Jodam prepare your policy for
                payment.
              </p>
              <button
                className="button button--primary button--large"
                disabled={busy}
                onClick={accept}
              >
                {busy ? "Saving…" : "Accept quotation"} <Icon name="arrow" />
              </button>
            </div>
          )}
          {ready && (
            <div className="panel">
              <span className="eyebrow">Need changes?</span>
              <h2>Request a review</h2>
              <p className="detail-copy">
                Describe what you would like changed. Jodam will contact you and
                send an updated quotation after agreeing the terms.
              </p>
              <div className="form-stack">
                <label>
                  Message
                  <textarea
                    rows={4}
                    value={message}
                    onChange={(e) => setMessage(e.target.value)}
                    placeholder="For example: Please review the excess or add windscreen cover."
                  />
                </label>
                <button
                  className="button button--secondary"
                  disabled={busy || !message.trim()}
                  onClick={review}
                >
                  {busy ? "Sending…" : "Send review request"}
                </button>
              </div>
            </div>
          )}
          {q.status === "REVIEW_REQUESTED" && (
            <div className="panel">
              <span className="eyebrow">Review requested</span>
              <h2>Jodam is reviewing your terms</h2>
              <p className="detail-copy">
                {q.customerReviewMessage || "Your request has been received."}
              </p>
              <p className="detail-copy">
                When staff resend the quotation, you will receive a notification
                and can accept the revised offer here.
              </p>
            </div>
          )}
          <Link className="text-link" to="/app/quotations">
            Back to quotations
          </Link>
        </aside>
      </div>
    </AppShell>
  );
}
function Fact({ label, value }: { label: string; value?: string }) {
  return (
    <div>
      <span>{label}</span>
      <strong>{value || "Not specified"}</strong>
    </div>
  );
}
function Detail({ title, value }: { title: string; value?: string }) {
  return (
    <div className="detail-block">
      <span>{title}</span>
      <strong>{value || "Not specified"}</strong>
    </div>
  );
}
