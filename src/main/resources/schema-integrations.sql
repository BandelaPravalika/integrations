-- Universal SaaS Platform - Integrations Module Schema
-- Reference / production migration script. Hibernate entities are the primary model.

CREATE TABLE IF NOT EXISTS integration_definitions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(100) NOT NULL UNIQUE,
    name VARCHAR(150) NOT NULL,
    provider VARCHAR(100) NOT NULL,
    description TEXT,
    icon VARCHAR(255),
    color VARCHAR(50),
    category VARCHAR(100),
    active BOOLEAN DEFAULT TRUE,
    created_at DATETIME,
    updated_at DATETIME
);

CREATE TABLE IF NOT EXISTS tenant_integrations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    integration_definition_id BIGINT NOT NULL,
    code VARCHAR(100) NOT NULL,
    enabled BOOLEAN DEFAULT FALSE,
    connected BOOLEAN DEFAULT FALSE,
    status VARCHAR(50),
    health VARCHAR(50),
    environment VARCHAR(50),
    connected_by BIGINT,
    connected_at DATETIME,
    disconnected_at DATETIME,
    last_synced_at DATETIME,
    created_at DATETIME,
    updated_at DATETIME,
    INDEX idx_tenant_integration_tenant (tenant_id),
    INDEX idx_tenant_integration_code (tenant_id, code),
    FOREIGN KEY (integration_definition_id) REFERENCES integration_definitions(id)
);

CREATE TABLE IF NOT EXISTS integration_credentials (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_integration_id BIGINT NOT NULL,
    api_key_encrypted TEXT,
    api_secret_encrypted TEXT,
    client_id_encrypted TEXT,
    client_secret_encrypted TEXT,
    access_token_encrypted TEXT,
    refresh_token_encrypted TEXT,
    token_expiry DATETIME,
    scopes TEXT,
    created_at DATETIME,
    updated_at DATETIME,
    FOREIGN KEY (tenant_integration_id) REFERENCES tenant_integrations(id)
);

CREATE TABLE IF NOT EXISTS integration_settings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_integration_id BIGINT NOT NULL,
    setting_key VARCHAR(150),
    setting_value TEXT,
    encrypted BOOLEAN DEFAULT FALSE,
    created_at DATETIME,
    updated_at DATETIME,
    INDEX idx_setting_tenant_integration_key (tenant_integration_id, setting_key),
    FOREIGN KEY (tenant_integration_id) REFERENCES tenant_integrations(id)
);

CREATE TABLE IF NOT EXISTS integration_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    tenant_integration_id BIGINT,
    integration_code VARCHAR(100),
    event_name VARCHAR(150),
    action VARCHAR(150),
    request_payload LONGTEXT,
    response_payload LONGTEXT,
    status VARCHAR(50),
    http_status INT,
    error_message TEXT,
    retry_count INT DEFAULT 0,
    created_at DATETIME,
    INDEX idx_integration_log_tenant (tenant_id),
    INDEX idx_integration_log_code (tenant_id, integration_code)
);

CREATE TABLE IF NOT EXISTS integration_sync_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    tenant_integration_id BIGINT NOT NULL,
    sync_type VARCHAR(100),
    status VARCHAR(50),
    message TEXT,
    started_at DATETIME,
    completed_at DATETIME,
    records_processed INT,
    records_success INT,
    records_failed INT,
    INDEX idx_sync_history_tenant (tenant_id),
    INDEX idx_sync_history_tenant_integration (tenant_integration_id),
    FOREIGN KEY (tenant_integration_id) REFERENCES tenant_integrations(id)
);

CREATE TABLE IF NOT EXISTS api_keys (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    key_name VARCHAR(150),
    api_key_hash VARCHAR(64),
    api_secret_hash VARCHAR(64),
    masked_key VARCHAR(100),
    permissions TEXT,
    ip_whitelist TEXT,
    expiry_date DATETIME,
    status VARCHAR(50),
    created_by BIGINT,
    created_at DATETIME,
    revoked_at DATETIME,
    INDEX idx_api_key_tenant (tenant_id)
);

CREATE TABLE IF NOT EXISTS api_key_usage_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    api_key_id BIGINT,
    endpoint VARCHAR(255),
    method VARCHAR(20),
    ip_address VARCHAR(100),
    status VARCHAR(50),
    created_at DATETIME,
    INDEX idx_api_key_usage_tenant (tenant_id),
    INDEX idx_api_key_usage_key (api_key_id)
);

CREATE TABLE IF NOT EXISTS webhook_subscriptions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    name VARCHAR(150),
    webhook_url TEXT,
    secret_key_encrypted TEXT,
    events TEXT,
    enabled BOOLEAN DEFAULT TRUE,
    created_at DATETIME,
    updated_at DATETIME,
    INDEX idx_webhook_sub_tenant (tenant_id)
);

CREATE TABLE IF NOT EXISTS webhook_delivery_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    webhook_subscription_id BIGINT,
    event_name VARCHAR(150),
    payload LONGTEXT,
    response LONGTEXT,
    status VARCHAR(50),
    http_status INT,
    retry_count INT DEFAULT 0,
    next_retry_at DATETIME,
    created_at DATETIME,
    INDEX idx_webhook_delivery_tenant (tenant_id),
    INDEX idx_webhook_delivery_sub (webhook_subscription_id)
);

CREATE TABLE IF NOT EXISTS external_event_mappings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    provider VARCHAR(100),
    external_event_id VARCHAR(255),
    internal_module VARCHAR(100),
    internal_reference_id BIGINT,
    metadata_json LONGTEXT,
    created_at DATETIME,
    INDEX idx_external_event_tenant (tenant_id),
    INDEX idx_external_event_provider (tenant_id, provider, external_event_id)
);

-- Seed integration definitions
INSERT INTO integration_definitions (code, name, provider, description, color, category, active, created_at, updated_at)
VALUES
('GOOGLE', 'Google', 'GOOGLE', 'Connect Google services like Gmail, Calendar, Drive, Meet and Sheets', '#4285F4', 'PRODUCTIVITY', TRUE, NOW(), NOW()),
('META', 'Meta', 'META', 'Capture Facebook and Instagram leads', '#1877F2', 'MARKETING', TRUE, NOW(), NOW()),
('WHATSAPP', 'WhatsApp', 'WHATSAPP', 'Send WhatsApp alerts and messages', '#25D366', 'MESSAGING', TRUE, NOW(), NOW()),
('ZAPIER', 'Zapier', 'ZAPIER', 'Connect with external apps using Zapier', '#FF4A00', 'AUTOMATION', TRUE, NOW(), NOW()),
('WEBHOOK', 'Webhook', 'WEBHOOK', 'Send real-time event data to external systems', '#6B7280', 'AUTOMATION', TRUE, NOW(), NOW()),
('ZOOM', 'Zoom', 'ZOOM', 'Create and manage online meetings', '#2D8CFF', 'MEETINGS', TRUE, NOW(), NOW()),
('CASHFREE', 'Cashfree', 'CASHFREE', 'Accept and verify online payments', '#6933D3', 'PAYMENT', TRUE, NOW(), NOW()),
('API_KEY', 'API Keys', 'API_KEY', 'Allow external systems to access APIs securely', '#111827', 'SECURITY', TRUE, NOW(), NOW())
ON DUPLICATE KEY UPDATE name = VALUES(name), description = VALUES(description), color = VALUES(color), updated_at = NOW();
