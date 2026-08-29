import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { StaffShell } from "../components/StaffShell";
import { StatusPill } from "../components/StatusPill";
import {
  createQuotation,
  getMyInquiry,
  getQuotationForInquiry,
  issuePolicy,
  sendQuotation,
  type Inquiry,
  type Quotation,
} from "../lib/api";

const today = () => new Date().toISOString().slice(0, 10);
const documents = [
  "Logbook / import documents",
  "National ID",
  "KRA PIN",
  "Driving licence",
  "Current valuation report",
];
const benefits = [
  "Windscreen and window glass",
  "Passenger legal liability",
  "Excess protector",
  "Loss of use",
  "Personal accident",
  "Roadside assistance",
];
const standardTerms =
  "This policy is issued subject to the accepted quotation, the insured vehicle details and the applicable policy wording. The vehicle must be used only for the declared purpose. Any change of ownership, use, material vehicle details or material risk must be disclosed promptly. Cover, excess and optional benefits are as shown on this schedule.";
function valueOf(value: unknown) {
  return value === undefined || value === null || value === ""
    ? "Not provided"
    : String(value);
}

export function StaffInquiryDetailPage() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [item, setItem] = useState<Inquiry | null>(null);
  const [quote, setQuote] = useState<Quotation | null>(null);
  const [q, setQ] = useState({
    insurer: "Jodam Insurance",
    product: "Comprehensive Motor",
    basicPremium: "",
    trainingLevy: "0",
    phcfLevy: "0",
    stampDuty: "0",
    otherCharges: "0",
    validUntil: "",
    proposedStartDate: "",
    proposedEndDate: "",
    excess: "KES 5,000 or 2.5% of each claim, whichever is higher",
    coverageDetails:
      "Accidental loss or damage to the insured vehicle, third-party liability and the selected optional benefits.",
    specialTerms:
      "Subject to underwriting acceptance, a current valuation and the declared use of the vehicle.",
    agentNotes: "",
    valuationReference: "",
    docs: [] as string[],
    benefits: [] as string[],
  });
  const [p, setP] = useState({
    startDate: today(),
    durationMonths: 12,
    certificateClass: "Private Motor",
    valuationReference: "",
    valuationDate: "",
    documentsVerified: false,
    policyTerms: standardTerms,
  });
  const [busy, setBusy] = useState(false);
  const [error, setError] = useState("");
  useEffect(() => {
    if (!id) return;
    getMyInquiry(Number(id))
      .then(setItem)
      .catch((e) => setError(e.message));
    getQuotationForInquiry(Number(id))
      .then(setQuote)
      .catch(() => {});
  }, [id]);
  useEffect(() => {
    if (!quote || quote.status !== "REVIEW_REQUESTED") return;
    setQ((current) => ({
      ...current,
      insurer: quote.insurer,
      product: quote.product,
      basicPremium: String(quote.basicPremium),
      trainingLevy: String(quote.trainingLevy ?? 0),
      phcfLevy: String(quote.phcfLevy ?? 0),
      stampDuty: String(quote.stampDuty ?? 0),
      otherCharges: String(quote.otherCharges ?? 0),
      validUntil: quote.validUntil,
      proposedStartDate: quote.proposedStartDate ?? "",
      proposedEndDate: quote.proposedEndDate ?? "",
      excess: quote.excess ?? "",
      coverageDetails: quote.coverageDetails ?? "",
      specialTerms: quote.specialTerms ?? "",
      agentNotes: quote.agentNotes ?? "",
    }));
  }, [quote]);
  if (!item)
    return (
      <StaffShell>
        {error ? (
          <div className="form-error">{error}</div>
        ) : (
          <div className="loading-state">Loading inquiry…</div>
        )}
      </StaffShell>
    );
  const data = item.applicationData ?? {};
  const canQuote = ["NEW", "ASSIGNED", "UNDER_REVIEW"].includes(item.status);
  const setField = (field: string, value: string) =>
    setQ((current) => ({ ...current, [field]: value }));
  const toggle = (field: "docs" | "benefits", label: string) =>
    setQ((current) => ({
      ...current,
      [field]: current[field].includes(label)
        ? current[field].filter((x) => x !== label)
        : [...current[field], label],
    }));
  async function sendQuote() {
    if (!id || !q.basicPremium || !q.validUntil) return;
    setBusy(true);
    setError("");
    try {
      const notes =
        `${q.agentNotes}\nStaff verification — documents: ${q.docs.join(", ") || "none recorded"}; valuation reference: ${q.valuationReference || "not recorded"}; optional benefits: ${q.benefits.join(", ") || "none selected"}.`.trim();
      const draft = await createQuotation(Number(id), {
        insurer: q.insurer,
        product: q.product,
        basicPremium: Number(q.basicPremium),
        trainingLevy: Number(q.trainingLevy || 0),
        phcfLevy: Number(q.phcfLevy || 0),
        stampDuty: Number(q.stampDuty || 0),
        otherCharges: Number(q.otherCharges || 0),
        validUntil: q.validUntil,
        proposedStartDate: q.proposedStartDate || undefined,
        proposedEndDate: q.proposedEndDate || undefined,
        excess: q.excess,
        coverageDetails: q.coverageDetails,
        specialTerms: q.specialTerms,
        agentNotes: notes,
      });
      const sent = await sendQuotation(draft.id);
      setQuote(sent);
      setItem({ ...item, status: "QUOTATION_SENT" } as Inquiry);
      navigate("/staff/inquiries");
    } catch (e) {
      setError(e instanceof Error ? e.message : "Unable to send quotation");
    } finally {
      setBusy(false);
    }
  }
  async function issue() {
    if (!quote || !p.documentsVerified) return;
    setBusy(true);
    setError("");
    try {
      await issuePolicy(quote.id, p);
      navigate("/staff/policies");
    } catch (e) {
      setError(e instanceof Error ? e.message : "Unable to issue policy");
    } finally {
      setBusy(false);
    }
  }
  return (
    <StaffShell>
      <div className="page-heading">
        <div>
          <span className="eyebrow eyebrow--red">Motor application</span>
          <h1>
            {valueOf(data.registrationNumber)} · {valueOf(data.make)} {valueOf(data.model)}
          </h1>
          <p>
            {item.insuranceType} · {item.customerFullName || item.customerUsername}
          </p>
        </div>
        <StatusPill value={item.status} />
      </div>
      {error && <div className="form-error">{error}</div>}
      <div className="detail-grid">
        <div className="detail-stack">
          <div className="panel">
            <span className="eyebrow">Customer data</span>
            <h2>Customer details</h2>
            <div className="detail-facts detail-facts--wide">
              <Fact
                label="Customer name"
                value={item.customerFullName || item.customerUsername}
              />
              <Fact label="KRA PIN" value={data.pin} />
              <Fact label="Profession" value={data.profession} />
              <Fact label="Phone number" value={item.customerPhoneNumber} />
              <Fact label="Email" value={item.customerEmail} />
            </div>
          </div>
          <div className="panel">
            <span className="eyebrow">Vehicle data</span>
            <h2>Vehicle and cover</h2>
            <div className="detail-facts detail-facts--wide">
              <Fact
                label="Vehicle licence plate"
                value={data.registrationNumber}
              />
              <Fact
                label="Make and model"
                value={`${valueOf(data.make)} ${valueOf(data.model)}`}
              />
              <Fact
                label="Year of manufacture"
                value={data.yearOfManufacture}
              />
              <Fact label="Year purchased" value={data.datePurchased} />
              <Fact
                label="Current value"
                value={
                  data.estimatedValue ? `KES ${data.estimatedValue}` : undefined
                }
              />
              <Fact label="Type of cover" value={item.insuranceType} />
              <Fact label="Vehicle type" value={data.vehicleType} />
              <Fact label="Body type" value={data.bodyType} />
              <Fact label="Engine capacity" value={data.engineCapacity} />
              <Fact label="Chassis number" value={data.chassisNumber} />
              <Fact label="Engine number" value={data.engineNumber} />
              <Fact label="Vehicle location" value={data.vehicleLocation} />
            </div>
          </div>
          <div className="panel">
            <span className="eyebrow">Risk details</span>
            <h2>Usage & history</h2>
            <Detail label="Usage" value={(data.usage ?? []).join(", ")} />
            <Detail label="Previous insurer" value={data.previousInsurer} />
            <Detail
              label="Accident history"
              value={(data.accidentHistory ?? [])
                .map((a) => `${a.date || "Date unknown"} · ${a.details || ""}`)
                .join("\n")}
            />
          </div>
        </div>
        <aside className="detail-stack">
          {quote?.status === "REVIEW_REQUESTED" && (
            <QuotationStatusCard quote={quote} />
          )}
          {canQuote && (
            <div className="panel">
              <span className="eyebrow">Quotation</span>
              <h2>Prepare and send quotation</h2>
              <p className="panel-copy">
                Record underwriting checks, price and verified documents.
                Sending creates a customer notification and email.
              </p>
              <div className="form-stack">
                <label>
                  Insurer
                  <input
                    value={q.insurer}
                    onChange={(e) => setField("insurer", e.target.value)}
                    required
                  />
                </label>
                <label>
                  Product
                  <input
                    value={q.product}
                    onChange={(e) => setField("product", e.target.value)}
                    required
                  />
                </label>
                <label>
                  Basic premium (KES)
                  <input
                    type="number"
                    min="1"
                    step="0.01"
                    value={q.basicPremium}
                    onChange={(e) => setField("basicPremium", e.target.value)}
                    required
                  />
                </label>
                <div className="detail-facts">
                  <label>
                    Training levy (KES)
                    <input
                      type="number"
                      min="0"
                      step="0.01"
                      value={q.trainingLevy}
                      onChange={(e) => setField("trainingLevy", e.target.value)}
                    />
                  </label>
                  <label>
                    PHCF levy (KES)
                    <input
                      type="number"
                      min="0"
                      step="0.01"
                      value={q.phcfLevy}
                      onChange={(e) => setField("phcfLevy", e.target.value)}
                    />
                  </label>
                  <label>
                    Stamp duty (KES)
                    <input
                      type="number"
                      min="0"
                      step="0.01"
                      value={q.stampDuty}
                      onChange={(e) => setField("stampDuty", e.target.value)}
                    />
                  </label>
                  <label>
                    Other charges (KES)
                    <input
                      type="number"
                      min="0"
                      step="0.01"
                      value={q.otherCharges}
                      onChange={(e) => setField("otherCharges", e.target.value)}
                    />
                  </label>
                </div>
                <label>
                  Valid until
                  <input
                    type="date"
                    min={today()}
                    value={q.validUntil}
                    onChange={(e) => setField("validUntil", e.target.value)}
                    required
                  />
                </label>
                <div className="detail-facts">
                  <label>
                    Proposed start
                    <input
                      type="date"
                      value={q.proposedStartDate}
                      onChange={(e) =>
                        setField("proposedStartDate", e.target.value)
                      }
                    />
                  </label>
                  <label>
                    Proposed end
                    <input
                      type="date"
                      value={q.proposedEndDate}
                      onChange={(e) =>
                        setField("proposedEndDate", e.target.value)
                      }
                    />
                  </label>
                </div>
                <label>
                  Excess
                  <textarea
                    rows={2}
                    value={q.excess}
                    onChange={(e) => setField("excess", e.target.value)}
                  />
                </label>
                <label>
                  Coverage details
                  <textarea
                    rows={4}
                    value={q.coverageDetails}
                    onChange={(e) =>
                      setField("coverageDetails", e.target.value)
                    }
                  />
                </label>
                <fieldset>
                  <legend>Optional benefits included</legend>
                  {benefits.map((label) => (
                    <label key={label}>
                      <input
                        type="checkbox"
                        checked={q.benefits.includes(label)}
                        onChange={() => toggle("benefits", label)}
                      />{" "}
                      {label}
                    </label>
                  ))}
                </fieldset>
                <fieldset>
                  <legend>Documents verified by staff</legend>
                  {documents.map((label) => (
                    <label key={label}>
                      <input
                        type="checkbox"
                        checked={q.docs.includes(label)}
                        onChange={() => toggle("docs", label)}
                      />{" "}
                      {label}
                    </label>
                  ))}
                </fieldset>
                <label>
                  Valuation reference
                  <input
                    value={q.valuationReference}
                    onChange={(e) =>
                      setField("valuationReference", e.target.value)
                    }
                    placeholder="e.g. VAL-2026-001"
                  />
                </label>
                <label>
                  Special terms
                  <textarea
                    rows={3}
                    value={q.specialTerms}
                    onChange={(e) => setField("specialTerms", e.target.value)}
                  />
                </label>
                <label>
                  Internal underwriting notes
                  <textarea
                    rows={3}
                    value={q.agentNotes}
                    onChange={(e) => setField("agentNotes", e.target.value)}
                  />
                </label>
                <button
                  className="button button--primary button--large"
                  disabled={busy || !q.basicPremium || !q.validUntil}
                  onClick={sendQuote}
                >
                  {busy ? "Sending…" : "Send quotation"}
                </button>
              </div>
            </div>
          )}
          {item.status === "CUSTOMER_ACCEPTED" && quote && (
            <div className="panel">
              <span className="eyebrow">Accepted quotation</span>
              <h2>Issue policy schedule</h2>
              <p className="panel-copy">
                The policy is sent to the customer and becomes active after
                successful M-Pesa payment.
              </p>
              <div className="form-stack">
                <label>
                  Start date
                  <input
                    type="date"
                    min={today()}
                    value={p.startDate}
                    onChange={(e) => setP({ ...p, startDate: e.target.value })}
                  />
                </label>
                <label>
                  Duration
                  <select
                    value={p.durationMonths}
                    onChange={(e) =>
                      setP({ ...p, durationMonths: Number(e.target.value) })
                    }
                  >
                    {[1, 3, 6, 12, 24, 36].map((months) => (
                      <option key={months} value={months}>
                        {months} {months === 1 ? "month" : "months"}
                      </option>
                    ))}
                  </select>
                </label>
                <div className="info-strip">
                  The certificate number will be generated automatically when
                  this policy is issued.
                </div>
                <label>
                  Certificate class
                  <select
                    value={p.certificateClass}
                    onChange={(e) =>
                      setP({ ...p, certificateClass: e.target.value })
                    }
                  >
                    <option>Private Motor</option>
                    <option>Commercial Vehicle</option>
                    <option>PSV</option>
                    <option>Motorcycle</option>
                  </select>
                </label>
                <label>
                  Valuation reference
                  <input
                    value={p.valuationReference}
                    onChange={(e) =>
                      setP({ ...p, valuationReference: e.target.value })
                    }
                    placeholder="Valuation report reference"
                  />
                </label>
                <label>
                  Valuation date
                  <input
                    type="date"
                    value={p.valuationDate}
                    onChange={(e) =>
                      setP({ ...p, valuationDate: e.target.value })
                    }
                  />
                </label>
                <label>
                  <input
                    type="checkbox"
                    checked={p.documentsVerified}
                    onChange={(e) =>
                      setP({ ...p, documentsVerified: e.target.checked })
                    }
                  />{" "}
                  I have verified the required identity, ownership, driving and
                  valuation records.
                </label>
                <label>
                  Policy schedule terms
                  <textarea
                    rows={5}
                    value={p.policyTerms}
                    onChange={(e) =>
                      setP({ ...p, policyTerms: e.target.value })
                    }
                  />
                </label>
                <button
                  className="button button--primary button--large"
                  disabled={
                    busy || !p.documentsVerified
                  }
                  onClick={issue}
                >
                  {busy ? "Issuing…" : "Issue policy to customer"}
                </button>
              </div>
            </div>
          )}
          {quote && quote.status !== "REVIEW_REQUESTED" && (
            <div className="panel">
              <span className="eyebrow">Quotation status</span>
              <h2>{quote.status.replaceAll("_", " ")}</h2>
              <p className="detail-copy">
                {quote.quoteReference} · KES{" "}
                {Number(quote.totalPayable).toLocaleString()}
              </p>
              {quote.customerReviewMessage && (
                <div className="detail-block">
                  <span>Customer review request</span>
                  <strong>{quote.customerReviewMessage}</strong>
                </div>
              )}
            </div>
          )}
          <div className="panel">
            <span className="eyebrow">Application status</span>
            <h2>{item.status.replaceAll("_", " ")}</h2>
            <p className="detail-copy">
              Submitted {new Date(item.createdAt).toLocaleString()}.
            </p>
          </div>
        </aside>
      </div>
    </StaffShell>
  );
}
function Fact({ label, value }: { label: string; value: unknown }) {
  return (
    <div>
      <span>{label}</span>
      <strong>{valueOf(value)}</strong>
    </div>
  );
}
function Detail({ label, value }: { label: string; value: unknown }) {
  return (
    <div className="detail-block">
      <span>{label}</span>
      <strong>{valueOf(value)}</strong>
    </div>
  );
}

function QuotationStatusCard({ quote }: { quote: Quotation }) {
  return (
    <div className="panel">
      <span className="eyebrow">Quotation status</span>
      <h2>{quote.status.replaceAll("_", " ")}</h2>
      <p className="detail-copy">
        {quote.quoteReference} · KES {Number(quote.totalPayable).toLocaleString()}
      </p>
      {quote.customerReviewMessage && (
        <div className="detail-block">
          <span>Customer review request</span>
          <strong>{quote.customerReviewMessage}</strong>
        </div>
      )}
    </div>
  );
}
