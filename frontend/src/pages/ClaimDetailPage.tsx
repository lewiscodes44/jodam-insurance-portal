import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import { AppShell } from "../components/AppShell";
import { StatusPill } from "../components/StatusPill";
import {
  getClaim,
  markNotificationsReadForReference,
  type Claim,
} from "../lib/api";

const steps = [
  ["SUBMITTED", "Incident reported", "We have received your incident report."],
  [
    "TRIAGE",
    "Review and documents",
    "We validate the policy and confirm any evidence required.",
  ],
  [
    "UNDER_ASSESSMENT",
    "Assessment",
    "An assessor may inspect the damage or review estimates.",
  ],
  [
    "APPROVED",
    "Decision",
    "We record the approved, partial or declined outcome.",
  ],
  [
    "SETTLED",
    "Settlement and closure",
    "We arrange settlement and close the claim when complete.",
  ],
];
const order = [
  "SUBMITTED",
  "TRIAGE",
  "AWAITING_CUSTOMER",
  "UNDER_REVIEW",
  "UNDER_ASSESSMENT",
  "AWAITING_APPROVAL",
  "APPROVED",
  "PARTIALLY_APPROVED",
  "REJECTED",
  "SETTLEMENT_IN_PROGRESS",
  "SETTLED",
  "CLOSED",
  "WITHDRAWN",
];
function stepState(status: string, index: number) {
  if (["REJECTED", "WITHDRAWN"].includes(status))
    return index === 3 ? "current" : index < 3 ? "done" : "upcoming";
  const position = order.indexOf(status);
  const thresholds = [0, 1, 4, 6, 10];
  return position >= thresholds[index]
    ? "done"
    : position === thresholds[index] - 1
      ? "current"
      : "upcoming";
}
function nextStep(status: string) {
  return (
    {
      SUBMITTED:
        "Your claim has been received. A claims officer will confirm the documents or information needed next.",
      TRIAGE:
        "We are validating the incident and policy details. Keep any photographs, police abstract and repair estimate ready.",
      AWAITING_CUSTOMER:
        "Your claims officer has requested more information. Check your notifications and provide it as soon as possible.",
      UNDER_REVIEW:
        "Your evidence is being reviewed. We may contact you for clarification.",
      UNDER_ASSESSMENT:
        "Assessment is under way. Do not authorise repairs unless your claims officer confirms it is appropriate.",
      AWAITING_APPROVAL:
        "Assessment is complete and awaiting a final decision.",
      APPROVED: "Your claim is approved. Settlement arrangements will follow.",
      PARTIALLY_APPROVED:
        "Part of the claimed amount has been approved. Review the decision note below.",
      REJECTED:
        "Review the decision note below. Contact Jodam if you need clarification on the outcome.",
      SETTLEMENT_IN_PROGRESS:
        "Settlement is being arranged. We will notify you once it is complete.",
      SETTLED: "Settlement is complete. Keep this record for your files.",
      CLOSED: "This claim is closed.",
      WITHDRAWN: "This claim was withdrawn.",
    }[status] ?? "Your claim is being processed."
  );
}

export function ClaimDetailPage() {
  const { id } = useParams();
  const [claim, setClaim] = useState<Claim | null>(null);
  const [error, setError] = useState("");
  useEffect(() => {
    if (id)
      getClaim(Number(id))
        .then((result) => {
          setClaim(result);
          void markNotificationsReadForReference(result.claimNumber);
        })
        .catch((e) => setError(e.message));
  }, [id]);
  return (
    <AppShell>
      <div className="page-heading">
        <div>
          <span className="eyebrow eyebrow--red">
            Motor claim · {claim?.claimNumber || `#${id}`}
          </span>
          <h1>Claim progress</h1>
          <p>
            See where your claim is in the review, assessment and settlement
            journey.
          </p>
        </div>
        {claim && <StatusPill value={claim.status} />}
      </div>
      {error && <div className="form-error">{error}</div>}
      {claim && (
        <>
          <div className="info-strip claim-next-step">
            <strong>What happens next</strong>
            <span>{nextStep(claim.status)}</span>
          </div>
          <div className="detail-grid">
            <div className="detail-stack">
              <div className="panel">
                <span className="eyebrow">Claim journey</span>
                <h2>From incident to settlement</h2>
                <div className="journey-list">
                  {steps.map(([key, title, copy], index) => (
                    <div
                      className={`journey-step ${stepState(claim.status, index)}`}
                      key={key}
                    >
                      <span>
                        {stepState(claim.status, index) === "done"
                          ? "✓"
                          : String(index + 1).padStart(2, "0")}
                      </span>
                      <div>
                        <strong>{title}</strong>
                        <small>{copy}</small>
                      </div>
                    </div>
                  ))}
                </div>
              </div>
              <div className="panel">
                <span className="eyebrow">Incident report</span>
                <h2>What you reported</h2>
                <p className="detail-copy claim-narrative">
                  {claim.description}
                </p>
              </div>
            </div>
            <aside className="detail-stack">
              <div className="panel">
                <span className="eyebrow">Claim details</span>
                <div className="detail-facts">
                  <div>
                    <span>Policy</span>
                    <strong>{claim.policyNumber}</strong>
                  </div>
                  <div>
                    <span>Incident date</span>
                    <strong>
                      {new Date(claim.incidentDate).toLocaleDateString()}
                    </strong>
                  </div>
                  <div>
                    <span>Estimated loss</span>
                    <strong>
                      KES {Number(claim.claimedAmount).toLocaleString()}
                    </strong>
                  </div>
                  <div>
                    <span>Approved amount</span>
                    <strong>
                      {claim.approvedAmount == null
                        ? "—"
                        : `KES ${Number(claim.approvedAmount).toLocaleString()}`}
                    </strong>
                  </div>
                </div>
              </div>
              <div className="panel">
                <span className="eyebrow">Decision</span>
                <h2>{claim.status.replaceAll("_", " ")}</h2>
                <p className="detail-copy">
                  {claim.decisionReason ||
                    "No decision note has been recorded yet."}
                </p>
                {claim.reviewedAt && (
                  <div className="info-strip">
                    Reviewed {new Date(claim.reviewedAt).toLocaleString()}.
                  </div>
                )}
                {claim.settledAt && (
                  <div className="info-strip">
                    Settled {new Date(claim.settledAt).toLocaleString()}.
                  </div>
                )}
                {claim.closedAt && (
                  <div className="info-strip">
                    Closed {new Date(claim.closedAt).toLocaleString()}.
                  </div>
                )}
              </div>
            </aside>
          </div>
        </>
      )}
    </AppShell>
  );
}
