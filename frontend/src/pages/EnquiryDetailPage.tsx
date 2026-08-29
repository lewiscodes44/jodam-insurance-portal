import { useEffect, useState } from "react";
import { Link, useParams } from "react-router-dom";
import { AppShell } from "../components/AppShell";
import { StatusPill } from "../components/StatusPill";
import {
  getMyInquiry,
  getMyPolicies,
  getQuotationForInquiry,
  type Inquiry,
  type Policy,
  type Quotation,
} from "../lib/api";
import { vehicleTitle } from "../lib/vehicle";

export function EnquiryDetailPage() {
  const { id } = useParams();
  const [item, setItem] = useState<Inquiry | null>(null);
  const [quotation, setQuotation] = useState<Quotation | null>(null);
  const [policy, setPolicy] = useState<Policy | null>(null);
  const [error, setError] = useState("");
  useEffect(() => {
    if (!id) return;
    const inquiryId = Number(id);
    getMyInquiry(inquiryId)
      .then(async (inquiry) => {
        setItem(inquiry);
        const [quoteResult, policiesResult] = await Promise.allSettled([
          getQuotationForInquiry(inquiryId),
          getMyPolicies(),
        ]);
        if (quoteResult.status === "fulfilled") setQuotation(quoteResult.value);
        if (policiesResult.status === "fulfilled")
          setPolicy(
            policiesResult.value.find(
              (entry) => entry.inquiryId === inquiryId,
            ) ?? null,
          );
      })
      .catch((e) => setError(e.message));
  }, [id]);
  const current = workflow(item, quotation, policy);
  const vehicle = vehicleTitle(item ?? undefined);
  return (
    <AppShell>
      <div className="page-heading">
        <div>
          <span className="eyebrow eyebrow--red">Motor inquiry</span>
          <h1>{vehicle.registration} · {vehicle.makeModel}</h1>
          <p>{vehicle.cover} · Track your inquiry from submission to an active motor policy.</p>
        </div>
        {item && (
          <StatusPill
            value={policy?.status ?? quotation?.status ?? item.status}
          />
        )}
      </div>
      {error && <div className="form-error">{error}</div>}
      {item && (
        <div className="detail-grid">
          <div className="detail-stack">
            <div className="panel">
              <div className="detail-header">
                <StatusPill
                  value={policy?.status ?? quotation?.status ?? item.status}
                />
                <span>{new Date(item.createdAt).toLocaleString()}</span>
              </div>
              <h2>{item.insuranceType}</h2>
              <p className="detail-copy">{current.copy}</p>
              <div className="journey-list">
                {journey.map((entry, index) => (
                  <div
                    className={`journey-step ${journeyState(item.status, quotation, policy, index)}`}
                    key={entry.label}
                  >
                    <span>
                      {journeyState(item.status, quotation, policy, index) ===
                      "done"
                        ? "✓"
                        : String(index + 1).padStart(2, "0")}
                    </span>
                    <div>
                      <strong>{entry.label}</strong>
                      <small>{entry.text}</small>
                    </div>
                  </div>
                ))}
              </div>
            </div>
            <div className="panel">
              <div className="panel__header">
                <div>
                  <span className="eyebrow">Submitted details</span>
                  <h2>Your application</h2>
                </div>
              </div>
              <div className="detail-facts detail-facts--wide">
                <Fact
                  label="Vehicle"
                  value={`${item.applicationData?.make ?? ""} ${item.applicationData?.model ?? ""}`.trim()}
                />
                <Fact
                  label="Registration"
                  value={item.applicationData?.registrationNumber}
                />
                <Fact
                  label="Year"
                  value={item.applicationData?.yearOfManufacture}
                />
                <Fact
                  label="Engine capacity"
                  value={item.applicationData?.engineCapacity}
                />
                <Fact
                  label="Estimated value"
                  value={
                    item.applicationData?.estimatedValue
                      ? `KES ${item.applicationData.estimatedValue}`
                      : undefined
                  }
                />
                <Fact
                  label="Main use"
                  value={(item.applicationData?.usage ?? []).join(", ")}
                />
                <Fact
                  label="Vehicle location"
                  value={item.applicationData?.vehicleLocation}
                />
                <Fact
                  label="Previous insurer"
                  value={item.applicationData?.previousInsurer}
                />
              </div>
            </div>
          </div>
          <aside className="detail-stack">
            <div className="panel">
              <span className="eyebrow">Current step</span>
              <h2>{current.title}</h2>
              <p className="detail-copy">{current.copy}</p>
              {current.to && (
                <Link
                  className="button button--primary button--full"
                  to={current.to}
                >
                  {current.action}
                </Link>
              )}
            </div>
            <div className="panel">
              <span className="eyebrow">Need help?</span>
              <h2>Contact Jodam</h2>
              <p className="detail-copy">
                Questions about this application can be handled directly by the
                Jodam team.
              </p>
              <a className="text-link" href="tel:+254713559966">
                +254 713 559 966
              </a>
              <br />
              <a className="text-link" href="mailto:twirextras@gmail.com">
                twirextras@gmail.com
              </a>
            </div>
          </aside>
        </div>
      )}
      {!item && !error && (
        <div className="loading-state">Loading application…</div>
      )}
    </AppShell>
  );
}

const journey = [
  {
    label: "Application received",
    text: "Your motor application has been submitted to Jodam.",
  },
  {
    label: "Application reviewed",
    text: "The team reviews the information and allocates it where needed.",
  },
  {
    label: "Quotation prepared",
    text: "An agent prepares your quotation based on the submitted risk information.",
  },
  {
    label: "Your decision",
    text: "Review and accept or decline the quotation from your portal.",
  },
  {
    label: "Policy & payment",
    text: "Issue the policy and complete payment through M-Pesa.",
  },
];
function journeyState(
  inquiryStatus: string,
  quotation: Quotation | null,
  policy: Policy | null,
  index: number,
) {
  const current = policy
    ? policy.status === "ACTIVE"
      ? 5
      : 4
    : quotation
      ? ["ACCEPTED"].includes(quotation.status)
        ? 4
        : 3
      : Math.max(
          ["NEW", "ASSIGNED", "QUOTED", "ACCEPTED", "CONVERTED"].indexOf(
            inquiryStatus,
          ) + 1,
          1,
        );
  if (index + 1 < current) return "done";
  if (index + 1 === current) return "current";
  return "upcoming";
}
function workflow(
  inquiry: Inquiry | null,
  quotation: Quotation | null,
  policy: Policy | null,
) {
  if (policy?.status === "PENDING_PAYMENT")
    return {
      title: "Policy ready for payment",
      copy: `Your policy ${policy.policyNumber} has been issued. Pay KES ${Number(policy.premiumAmount).toLocaleString()} by M-Pesa to activate it.`,
      action: "Pay and activate policy",
      to: `/app/policies/${policy.id}`,
    };
  if (policy?.status === "ACTIVE")
    return {
      title: "Policy active",
      copy: `Your policy ${policy.policyNumber} is active and your motor cover is in force.`,
      action: "View active policy",
      to: `/app/policies/${policy.id}`,
    };
  if (policy)
    return {
      title: "Manage your policy",
      copy: `Your application has been converted into policy ${policy.policyNumber}.`,
      action: "View policy",
      to: `/app/policies/${policy.id}`,
    };
  if (quotation?.status === "SENT")
    return {
      title: "Quotation ready",
      copy: "Your quotation is ready for review. Accept it or request changes from the quotation page.",
      action: "Review quotation",
      to: `/app/quotations/${quotation.id}`,
    };
  if (quotation?.status === "REVIEW_REQUESTED")
    return {
      title: "Quotation review in progress",
      copy: "Jodam is reviewing the changes you requested. You will be notified when the revised quotation is ready.",
      action: "View quotation",
      to: `/app/quotations/${quotation.id}`,
    };
  if (quotation?.status === "ACCEPTED")
    return {
      title: "Policy preparation",
      copy: "You accepted the quotation. Jodam is preparing your policy schedule for issue.",
      action: "View accepted quotation",
      to: `/app/quotations/${quotation.id}`,
    };
  if (inquiry?.status === "ASSIGNED")
    return {
      title: "Being reviewed",
      copy: "Jodam is reviewing the information you provided for this motor risk.",
    };
  return {
    title: "Application received",
    copy: "We have received your application and will notify you when a quotation is ready.",
  };
}
function Fact({ label, value }: { label: string; value: unknown }) {
  return (
    <div>
      <span>{label}</span>
      <strong>
        {value === undefined || value === null || value === ""
          ? "Not provided"
          : String(value)}
      </strong>
    </div>
  );
}
