import { useEffect, useState } from "react";
import { StaffShell } from "../components/StaffShell";
import { StatusPill } from "../components/StatusPill";
import {
  approveClaim,
  closeClaim,
  getAllClaims,
  getAssignedClaims,
  rejectClaim,
  reviewClaim,
  settleClaim,
  type Claim,
} from "../lib/api";
import { useAuth } from "../context/AuthContext";

function nextAction(claim: Claim) {
  return (
    {
      UNDER_REVIEW:
        "Claim is under review. Record an approval or rejection once the assessment is complete.",
      APPROVED: "Claim approved. Arrange settlement, then mark it settled.",
      REJECTED:
        "Claim rejected. The decision has been recorded; no further staff action is needed.",
      SETTLED: "Claim settled. Close the claim once all records are complete.",
      CLOSED: "Claim closed. No further action is required.",
    }[claim.status] ?? ""
  );
}

export function StaffClaimsPage() {
  const { role } = useAuth();
  const [items, setItems] = useState<Claim[]>([]);
  const [filter, setFilter] = useState("ALL");
  const [selected, setSelected] = useState<Claim | null>(null);
  const [reason, setReason] = useState("");
  const [approved, setApproved] = useState("");
  const [busy, setBusy] = useState(false);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");
  async function load() {
    try {
      setItems(await (role === "ADMIN" ? getAllClaims() : getAssignedClaims()));
    } catch (e) {
      setError(e instanceof Error ? e.message : "Unable to load claims");
    }
  }
  useEffect(() => {
    load();
  }, [role]);
  const visible =
    filter === "ALL" ? items : items.filter((c) => c.status === filter);
  async function act(fn: () => Promise<Claim>) {
    setBusy(true);
    setError("");
    setSuccess("");
    try {
      const updated = await fn();
      setItems((prev) => prev.map((x) => (x.id === updated.id ? updated : x)));
      setSelected(updated);
      setReason("");
      setSuccess(nextAction(updated));
    } catch (e) {
      setError(e instanceof Error ? e.message : "Claim action failed");
    } finally {
      setBusy(false);
    }
  }
  return (
    <StaffShell>
      <div className="page-heading">
        <div>
          <span className="eyebrow eyebrow--red">Claims</span>
          <h1>{role === "ADMIN" ? "Claims operations" : "My claims"}</h1>
          <p>
            Review claims, record decisions and move approved claims through
            settlement and closure.
          </p>
        </div>
      </div>
      {error && <div className="form-error">{error}</div>}
      {success && <div className="success-banner">{success}</div>}
      <div className="filter-row">
        {[
          "ALL",
          "SUBMITTED",
          "UNDER_REVIEW",
          "APPROVED",
          "REJECTED",
          "SETTLED",
          "CLOSED",
        ].map((v) => (
          <button
            key={v}
            className={`filter-button ${filter === v ? "active" : ""}`}
            onClick={() => setFilter(v)}
          >
            {v.replaceAll("_", " ")}
          </button>
        ))}
      </div>
      <div className="staff-claims-grid">
        <div className="panel table-panel">
          <div className="staff-table">
            <div className="staff-table__head">
              <span>Claim</span>
              <span>Customer</span>
              <span>Amount</span>
              <span>Status</span>
              <span></span>
            </div>
            {visible.map((c) => (
              <button
                className={`staff-table__row staff-table__row--button ${selected?.id === c.id ? "selected" : ""}`}
                key={c.id}
                onClick={() => {
                  setSelected(c);
                  setReason(c.decisionReason ?? "");
                  setApproved(c.approvedAmount ? String(c.approvedAmount) : "");
                  setSuccess("");
                }}
              >
                <span>
                  <strong>{c.claimNumber}</strong>
                  <small>{c.policyNumber}</small>
                </span>
                <span>{c.customerUsername}</span>
                <span>KES {Number(c.claimedAmount).toLocaleString()}</span>
                <span>
                  <StatusPill value={c.status} />
                </span>
                <span>Open →</span>
              </button>
            ))}
            {!visible.length && (
              <div className="empty-state table-empty">
                No claims match this filter.
              </div>
            )}
          </div>
        </div>
        <aside className="panel claim-action-panel">
          {selected ? (
            <>
              <div className="panel__header">
                <div>
                  <span className="eyebrow">Claim review</span>
                  <h2>{selected.claimNumber}</h2>
                </div>
                <StatusPill value={selected.status} />
              </div>
              <div className="detail-facts">
                <Fact label="Policy" value={selected.policyNumber} />
                <Fact label="Incident date" value={selected.incidentDate} />
                <Fact
                  label="Claimed amount"
                  value={`KES ${Number(selected.claimedAmount).toLocaleString()}`}
                />
                <Fact label="Customer" value={selected.customerUsername} />
              </div>
              <div className="detail-block">
                <span>Description</span>
                <strong>{selected.description}</strong>
              </div>
              <div className="form-stack">
                <label>
                  Decision reason
                  <textarea
                    rows={4}
                    value={reason}
                    onChange={(e) => setReason(e.target.value)}
                  />
                </label>
                {["SUBMITTED", "UNDER_REVIEW"].includes(selected.status) && (
                  <button
                    className="button button--secondary button--full"
                    disabled={busy}
                    onClick={() => act(() => reviewClaim(selected.id))}
                  >
                    {selected.status === "SUBMITTED"
                      ? "Move to under review"
                      : "Under review"}
                  </button>
                )}
                {selected.status === "UNDER_REVIEW" && (
                  <>
                    <label>
                      Approved amount (KES)
                      <input
                        type="number"
                        min="0.01"
                        step="0.01"
                        value={approved}
                        onChange={(e) => setApproved(e.target.value)}
                      />
                    </label>
                    <div className="action-row">
                      <button
                        className="button button--primary"
                        disabled={busy || !approved || !reason}
                        onClick={() =>
                          act(() =>
                            approveClaim(selected.id, {
                              approvedAmount: Number(approved),
                              decisionReason: reason,
                            }),
                          )
                        }
                      >
                        Approve
                      </button>
                      <button
                        className="button button--danger"
                        disabled={busy || !reason}
                        onClick={() =>
                          act(() => rejectClaim(selected.id, reason))
                        }
                      >
                        Reject
                      </button>
                    </div>
                  </>
                )}
                {selected.status === "APPROVED" && (
                  <button
                    className="button button--primary button--full"
                    disabled={busy}
                    onClick={() => act(() => settleClaim(selected.id))}
                  >
                    Mark settled
                  </button>
                )}
                {selected.status === "SETTLED" && (
                  <button
                    className="button button--primary button--full"
                    disabled={busy}
                    onClick={() => act(() => closeClaim(selected.id))}
                  >
                    Close claim
                  </button>
                )}
                {nextAction(selected) && (
                  <div className="info-strip claim-next-step">
                    {nextAction(selected)}
                  </div>
                )}
              </div>
            </>
          ) : (
            <div className="empty-state claim-empty">
              Select a claim to review it.
            </div>
          )}
        </aside>
      </div>
    </StaffShell>
  );
}
function Fact({ label, value }: { label: string; value: string }) {
  return (
    <div>
      <span>{label}</span>
      <strong>{value}</strong>
    </div>
  );
}
