# CampusPass — System Architecture Specification

---

### 1. Architectural Style & Design Principles
CampusPass follows a **Layered Clean Architecture** adhering to SOLID principles and 12-Factor App methodology.

```
       HTTP / JSON Clients (Web Browser, Gate Mobile Scanner)
                                  │
                                  ▼
                     [ API Gateway / Filters ]
          (CORS Filter -> JWT Authentication Filter -> Rate Limiting)
                                  │
                                  ▼
                         [ Controller Layer ]
       (@RestController: Request Validation, DTO mapping, HTTP Status Codes)
                                  │
                                  ▼
                          [ Service Layer ]
     (@Service: Business Invariants, Transaction Boundaries, State Engine)
                                  │
                                  ▼
                        [ Repository Layer ]
          (Spring Data JPA: Custom JPQL, Optimistic Locking, Pagination)
                                  │
                                  ▼
                         [ Database Layer ]
                    (MySQL 8.x: InnoDB, Foreign Keys, Indexes)
```

---

### 2. Strict Architectural Boundaries
1. **Zero Direct Entity Exposure**: Controllers must NEVER accept or return JPA `@Entity` objects. All API contracts communicate strictly via dedicated Data Transfer Objects (`dto/request` and `dto/response`).
2. **Standard API Envelope**: Every endpoint response (success or failure) conforms to a standard JSON envelope:
   ```json
   {
     "success": true,
     "message": "Operation completed successfully",
     "data": { ... },
     "timestamp": "2026-09-23T23:30:00Z"
   }
   ```
3. **Transactional Isolation**: All mutations across the Service layer are guarded by `@Transactional(rollbackFor = Exception.class)` to prevent partial state writes.
4. **Decoupled Verification Engine**: Gate QR verification is isolated into a dedicated validation pipeline that does not modify state during read-only inspection.
5. **Background Process Isolation**: Schedulers run in separate asynchronous threads to prevent blocking HTTP request handlers.
