# Universal SaaS Platform - Integrations Module

Backend integrations module for the Universal SaaS Platform.

## Package

`com.universalsaas.platform.integrations`

## Requirements

- Java 17
- Maven 3.8+
- MySQL 8+

## Quick start

1. Create MySQL database `universal_saas` (or update `application.properties`).
2. Set `integration.encryption.secret` to a strong value (min 16 chars recommended).
3. Run:

```bash
mvn spring-boot:run
```

4. APIs are open in **Temporary Super Admin Mode** (no JWT). Default `tenant_id = 1` via `TenantContextService`.

## Core APIs

| Method | Endpoint |
|--------|----------|
| GET | `/api/integrations` |
| GET | `/api/integrations/{code}` |
| PATCH | `/api/integrations/{code}/toggle` |
| POST | `/api/integrations/{code}/configure` |
| POST | `/api/integrations/{code}/test` |
| POST | `/api/integrations/{code}/disconnect` |
| GET | `/api/integrations/{code}/logs` |
| GET | `/api/integrations/{code}/sync-history` |

Provider-specific routes under `/api/integrations/google`, `/meta`, `/whatsapp`, `/zapier`, `/webhooks`, `/zoom`, `/cashfree`, `/api-keys`.

## Frontend

See `frontend-api/integrationApi.ts` for optional TypeScript client methods.

## Production SQL

Reference migration: `src/main/resources/schema-integrations.sql`

## Future wiring

- `TenantContextService.getCurrentTenantId()` — JWT tenant resolution
- `LeadIntegrationAdapter` — CRM LeadService
- `PaymentIntegrationAdapter` — Payment module
- `IntegrationPermissions` — Spring Security `@PreAuthorize`
