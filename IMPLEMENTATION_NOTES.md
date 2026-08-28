# Jodam Insurance Portal — Frontend & Staff Implementation

Implemented from the current functional customer snapshot and the supplied APA/AMACO private motor proposal forms.

## Implemented

- Public header now has **Contact us** with desktop hover/focus dropdown for phone and email.
- Footer logo asset background was made transparent so the previous white rectangle no longer appears on the dark footer.
- Customer login now includes password visibility control.
- Landing-page hero card now presents the three concrete private motor cover options evidenced by the supplied proposal material: Comprehensive, Third Party Fire & Theft, and Third Party.
- Added a product overview section for motor insurance.
- Replaced the simple motor enquiry textarea with a six-step Jodam Motor Application covering cover choice, vehicle details, usage, drivers/history, additional cover, and review/declaration.
- Motor application data is stored as structured JSON text on the inquiry record via `application_data`, with Flyway migration `V5__add_motor_application_data.sql`.
- Customer enquiry detail now presents a customer-friendly progress journey and submitted vehicle details.
- Added a separate `/staff/login` entry point for internal operations.
- Added role-aware staff routing for ADMIN and AGENT accounts.
- Added staff dashboard, inquiry queue/detail, claim operations, admin policy view, and staff account creation screens.
- Added an ADMIN-only `/api/staff/agents` endpoint so new inquiries can be assigned from a staff picker.
- Authentication response now includes the user's primary role so the frontend can route staff/customer accounts appropriately.

## Validation in this environment

- All modified TypeScript/TSX source files passed TypeScript parser syntax checks and transpilation checks for TS/TSX source.
- A full Vite build could not be executed because npm dependencies were not present and this environment could not reach the npm registry.
- A full Maven compile could not be executed because the Maven wrapper could not download Maven from Maven Central in the offline environment.

The source changes are packaged in the project ZIP. Run the normal frontend `npm install && npm run build` and backend `./mvnw -DskipTests compile` in the development environment where the dependencies are available.

## Phase 1–3 backend workflow update

The backend now supports the explicit inquiry-processing and quotation-drafting lifecycle:

`NEW -> ASSIGNED -> UNDER_REVIEW -> QUOTATION_DRAFT -> QUOTATION_SENT -> CUSTOMER_ACCEPTED/CUSTOMER_DECLINED -> POLICY_PENDING_PAYMENT`

Legacy inquiry status values remain in the Java enum solely for compatibility with older code/data, while Flyway V6 maps existing records to their corresponding new stages.

Added staff operations:

- `POST /api/inquiries/{id}/start-processing`
- `POST /api/quotations/inquiry/{inquiryId}` — creates a quotation as DRAFT
- `PUT /api/quotations/{quotationId}` — edits a DRAFT
- `POST /api/quotations/{quotationId}/send` — explicitly sends the quotation
- `GET /api/quotations/inquiry/{inquiryId}` — retrieves a quotation for authorized staff/customer use

Administrators are accepted alongside agents for inquiry processing and quotation operations.

The quotation model now includes insurer/product, premium and statutory charge components, calculated total payable, quotation reference, quotation validity, proposed policy dates, excess, special terms, agent notes, quotation status, update timestamp and sent timestamp. The legacy `premium_amount` and `coverage_details` columns remain as compatibility fields for the existing policy/reporting layer.

Flyway migration: `V6__expand_inquiry_quotation_workflow.sql`.

Important: the existing frontend quotation form has not yet been migrated to these new quotation fields. Complete the frontend staff quotation workflow before testing quotation preparation through the old embedded form.
