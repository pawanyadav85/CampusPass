# CampusPass — System Requirements Specification (SRS)
## Digital Student Permission & Gate Movement Management System

---

### 1. Document Control & Versioning
- **Project Name**: CampusPass
- **Document Version**: 1.0.0
- **Status**: APPROVED
- **Target Platform**: Java 21 LTS / Spring Boot 3.4.x / MySQL 8.x / Modern Web Browsers

---

### 2. Business Invariants & Functional Rules

#### 2.1 Student Permission Lifecycle
1. **Natural Language Rationale**: A student MUST provide a written statement in their own words detailing why they need permission to leave campus. Drop-down single-select reasons alone are strictly prohibited.
2. **Temporal Validity**:
   - `leave_date` must not be in the past.
   - `leave_time` must precede `expected_return_date_time`.
   - The requested duration cannot exceed the system-configured maximum (default: 48 hours for regular passes).
3. **Emergency Flagging**:
   - Students may toggle `is_emergency = true`.
   - Emergency requests do NOT automatically approve the pass; they bypass ordinary batch queues and appear in a high-priority, highlighted inbox for HOD triage.
4. **Clarification Resubmission**:
   - If an HOD requests clarification, the application shifts to `CLARIFICATION_REQUIRED`.
   - The student may supply additional explanations and/or upload supporting documentation.
   - Upon submission, the state shifts to `RESUBMITTED` and alerts the HOD without generating duplicate records.

#### 2.2 Digital E-Pass & QR Verification
1. **Zero Personally Identifiable Information (PII) in QR**:
   - The QR code contains ONLY a secure, cryptographically random high-entropy token (`urn:campuspass:token:<UUID>`).
   - QR codes NEVER encode raw student names, roll numbers, or parent contact details.
2. **Single-Pass State Invariance**:
   - An approved pass starts in `ACTIVE` state.
   - It can be used for **exactly ONE exit** and **exactly ONE return**.
   - If the departure window lapses without an exit scan, the pass transitions to `EXPIRED`.

#### 2.3 Gate Movement & Overdue Logic
1. **Exit Verification**:
   - Security staff scans QR. The backend validates:
     - Pass exists and is `ACTIVE`.
     - Current time is within the allowable departure window.
     - Student has no other unresolved open movement.
   - Gate movement record created with `status = OUTSIDE` and timestamp logged.
2. **Grace Period & Overdue Detection**:
   - A college-wide configurable grace period (default: 15 minutes) is enforced.
   - An automated background scheduler inspects open movements every 60 seconds:
     - Condition: `movement_status = 'OUTSIDE'` AND `NOW() > (expected_return_time + INTERVAL grace_period MINUTE)`.
     - Action: Update `movement_status = 'OVERDUE'`, dispatch in-app alert to student and departmental HOD.
3. **Return Verification**:
   - Security staff scans QR upon arrival.
   - If returned within the allowed window: marked `RETURNED_ON_TIME`.
   - If returned past the grace period: marked `RETURNED_OVERDUE`.
   - In both cases, the E-Pass is finalized as `COMPLETED` and invalidated for future gate passes.

#### 2.4 Pass Extension Workflow
1. A student currently in `OUTSIDE` state may submit an extension request providing the requested new return time and reason.
2. HOD reviews the request. If approved, `expected_return_time` on the gate movement is updated, and the original expected return time is preserved in the audit log.

#### 2.5 Privacy Protection
- Security guards at gate terminals are presented ONLY with operational data:
  - Student Photo
  - Full Name
  - Roll Number & Department
  - Allowed Departure & Return Times
  - Pass Status (`VALID`, `EXPIRED`, `INVALID`, `ALREADY_RETURNED`)
- Guardian contact numbers and private medical notes are strictly omitted from gate API responses.

---

### 3. Non-Functional Invariants
- **Authentication**: Stateless HMAC-SHA256 JWT tokens with role claims.
- **Password Security**: BCrypt hashing with work factor 12.
- **Auditability**: All state transitions record actor ID, IP address, timestamp, and metadata into an immutable append-only `audit_logs` table.
- **File Security**: Uploaded files restricted to PDF, PNG, and JPEG; maximum file size 5MB; stored outside web root with randomized UUID names.
