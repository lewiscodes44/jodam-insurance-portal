import { FormEvent, useEffect, useMemo, useState } from "react";
import { Link, useParams } from "react-router-dom";
import { AppShell } from "../components/AppShell";
import { StatusPill } from "../components/StatusPill";
import {
  cancelPolicy,
  getMyInquiry,
  getPolicy,
  initiatePayment,
  markNotificationsReadForReference,
  renewPolicy,
  type Inquiry,
  type Payment,
  type Policy,
  queryPayment,
} from "../lib/api";
import { vehicleTitle } from "../lib/vehicle";

export function PolicyDetailPage() {
  const { id } = useParams();
  const [policy, setPolicy] = useState<Policy | null>(null);
  const [inquiry, setInquiry] = useState<Inquiry | null>(null);
  const [phone, setPhone] = useState("");
  const [payment, setPayment] = useState<Payment | null>(null);
  const [newEndDate, setNewEndDate] = useState("");
  const [reason, setReason] = useState("");
  const [busy, setBusy] = useState("");
  const [error, setError] = useState("");
  useEffect(() => {
    if (id)
      getPolicy(Number(id))
        .then((result) => {
          setPolicy(result);
          void markNotificationsReadForReference(result.policyNumber);
          return getMyInquiry(result.inquiryId);
        })
        .then(setInquiry)
        .catch((e) => setError(e.message));
  }, [id]);
  const minRenewal = useMemo(() => {
    if (!policy) return "";
    const d = new Date(policy.endDate + "T00:00:00");
    d.setDate(d.getDate() + 1);
    return d.toISOString().slice(0, 10);
  }, [policy]);
  async function pay(e: FormEvent) {
    e.preventDefault();
    if (!policy) return;
    setBusy("pay");
    setError("");
    try {
      const result = await initiatePayment(policy.id, phone.trim());
      setPayment(result);
    } catch (err) {
      setError(
        err instanceof Error ? err.message : "Unable to initiate payment",
      );
    } finally {
      setBusy("");
    }
  }
  async function refreshPayment() {
    if (!payment) return;
    setBusy("query");
    setError("");
    try {
      const result = await queryPayment(payment.id);
      setPayment(result);
      if (result.status === "COMPLETED") setPolicy(await getPolicy(Number(id)));
    } catch (err) {
      setError(err instanceof Error ? err.message : "Unable to check payment");
    } finally {
      setBusy("");
    }
  }
  async function renew(e: FormEvent) {
    e.preventDefault();
    if (!policy) return;
    setBusy("renew");
    setError("");
    try {
      const result = await renewPolicy(policy.id, newEndDate);
      window.location.href = `/app/policies/${result.id}`;
    } catch (err) {
      setError(err instanceof Error ? err.message : "Unable to renew policy");
    } finally {
      setBusy("");
    }
  }
  async function cancel(e: FormEvent) {
    e.preventDefault();
    if (!policy) return;
    setBusy("cancel");
    setError("");
    try {
      await cancelPolicy(policy.id, reason);
      window.location.href = "/app/policies";
    } catch (err) {
      setError(err instanceof Error ? err.message : "Unable to cancel policy");
    } finally {
      setBusy("");
    }
  }
  const vehicle = vehicleTitle(inquiry ?? undefined, policy?.insuranceType);
  return (
    <AppShell>
      <div className="page-heading">
        <div>
          <span className="eyebrow eyebrow--red">{vehicle.cover} policy</span>
          <h1>
            {policy
              ? `${vehicle.registration} · ${vehicle.makeModel}`
              : "Policy"}
          </h1>
          <p>
            {policy
              ? `Policy ${policy.policyNumber} · Manage your motor cover and the actions available for its current status.`
              : "Loading your policy."}
          </p>
        </div>
        {policy && <StatusPill value={policy.status} />}
      </div>
      {error && <div className="form-error">{error}</div>}
      {!policy ? (
        <div className="loading-state">Loading policy…</div>
      ) : (
        <div className="detail-grid">
          <div className="detail-stack">
            <div className="panel">
              <div className="detail-facts detail-facts--wide">
                <div>
                  <span>Premium</span>
                  <strong>
                    KES {Number(policy.premiumAmount).toLocaleString()}
                  </strong>
                </div>
                <div>
                  <span>Cover</span>
                  <strong>{policy.coverageDetails || "Not specified"}</strong>
                </div>
                <div>
                  <span>Start date</span>
                  <strong>{policy.startDate}</strong>
                </div>
                <div>
                  <span>End date</span>
                  <strong>{policy.endDate}</strong>
                </div>
                <div>
                  <span>Agent</span>
                  <strong>{policy.agentUsername}</strong>
                </div>
                <div>
                  <span>Insurance</span>
                  <strong>{policy.insuranceType}</strong>
                </div>
                <div>
                  <span>Insurer</span>
                  <strong>{policy.insurer || "Not specified"}</strong>
                </div>
                <div>
                  <span>Product</span>
                  <strong>{policy.product || "Not specified"}</strong>
                </div>
                <div>
                  <span>Certificate number</span>
                  <strong>{policy.certificateNumber || "Not specified"}</strong>
                </div>
                <div>
                  <span>Certificate class</span>
                  <strong>{policy.certificateClass || "Not specified"}</strong>
                </div>
                <div>
                  <span>Valuation reference</span>
                  <strong>
                    {policy.valuationReference || "Not specified"}
                  </strong>
                </div>
                <div>
                  <span>Documents checked</span>
                  <strong>
                    {policy.documentsVerified
                      ? "Verified by staff"
                      : "Not recorded"}
                  </strong>
                </div>
              </div>
            </div>
            {policy.policyTerms && (
              <div className="panel">
                <span className="eyebrow">Policy schedule terms</span>
                <p className="detail-copy">{policy.policyTerms}</p>
              </div>
            )}
          </div>
          <div className="detail-stack">
            {policy.status === "PENDING_PAYMENT" && (
              <div className="panel">
                <span className="eyebrow">Payment</span>
                <h2>Activate your policy</h2>
                <p className="detail-copy">
                  Pay the quoted premium through M-Pesa. The amount is fixed by
                  the policy.
                </p>
                <form className="form-stack compact" onSubmit={pay}>
                  <label>
                    M-Pesa phone number
                    <input
                      value={phone}
                      onChange={(e) => setPhone(e.target.value)}
                      placeholder="0712345678"
                      required
                      pattern="^(?:254|0)7\d{8}$"
                    />
                  </label>
                  <button
                    className="button button--primary"
                    disabled={busy === "pay"}
                  >
                    {busy === "pay" ? "Sending prompt…" : "Send M-Pesa prompt"}
                  </button>
                </form>
                {payment && (
                  <div className="payment-state">
                    <StatusPill value={payment.status} />
                    <span>
                      Payment #{payment.id} · KES{" "}
                      {Number(payment.amount).toLocaleString()}
                    </span>
                    <button
                      className="button button--secondary"
                      onClick={refreshPayment}
                      disabled={busy === "query"}
                    >
                      {busy === "query" ? "Checking…" : "Check payment status"}
                    </button>
                  </div>
                )}
              </div>
            )}
            {["ACTIVE", "PENDING_PAYMENT"].includes(policy.status) && (
              <div className="panel">
                <span className="eyebrow">Renewal</span>
                <h2>Extend this policy</h2>
                <form className="form-stack compact" onSubmit={renew}>
                  <label>
                    New end date
                    <input
                      type="date"
                      value={newEndDate}
                      min={minRenewal}
                      onChange={(e) => setNewEndDate(e.target.value)}
                      required
                    />
                  </label>
                  <button
                    className="button button--secondary"
                    disabled={busy === "renew"}
                  >
                    {busy === "renew" ? "Renewing…" : "Renew policy"}
                  </button>
                </form>
              </div>
            )}
            {["ACTIVE", "PENDING_PAYMENT"].includes(policy.status) && (
              <div className="panel">
                <span className="eyebrow">Need to cancel?</span>
                <h2>Cancel policy</h2>
                <form className="form-stack compact" onSubmit={cancel}>
                  <label>
                    Reason
                    <textarea
                      value={reason}
                      onChange={(e) => setReason(e.target.value)}
                      rows={4}
                      required
                    />
                  </label>
                  <button
                    className="button button--danger"
                    disabled={busy === "cancel"}
                  >
                    {busy === "cancel" ? "Cancelling…" : "Cancel policy"}
                  </button>
                </form>
              </div>
            )}
            {policy.status === "ACTIVE" && (
              <div className="panel">
                <span className="eyebrow">Claims</span>
                <h2>Report a motor incident</h2>
                <p className="detail-copy">
                  Submit a claim against this active policy and track its
                  progress from your dashboard.
                </p>
                <Link
                  className="button button--primary"
                  to={`/app/claims/new?policyId=${policy.id}`}
                >
                  Submit a claim
                </Link>
              </div>
            )}
          </div>
        </div>
      )}
    </AppShell>
  );
}
