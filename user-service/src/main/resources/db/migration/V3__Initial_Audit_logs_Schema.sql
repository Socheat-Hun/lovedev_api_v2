DROP TABLE IF EXISTS audit_logs CASCADE;
-- ============================================
-- 7. AUDIT_LOGS TABLE
-- ============================================
CREATE TABLE audit_logs (
                            id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                            user_id UUID,
                            action VARCHAR(50) NOT NULL,
                            entity_type VARCHAR(50),
                            entity_id VARCHAR(255),
                            old_value JSONB,
                            new_value JSONB,
                            ip_address VARCHAR(45),
                            user_agent VARCHAR(500),
                            description TEXT,
                            created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                            CONSTRAINT fk_audit_log_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
);

CREATE INDEX idx_audit_user ON audit_logs(user_id);
CREATE INDEX idx_audit_action ON audit_logs(action);
CREATE INDEX idx_audit_entity ON audit_logs(entity_type, entity_id);
CREATE INDEX idx_audit_created ON audit_logs(created_at);