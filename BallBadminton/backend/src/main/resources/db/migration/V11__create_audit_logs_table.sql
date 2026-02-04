-- Create audit_logs table for comprehensive audit trail
-- Requirements: 6.2, 8.5

CREATE TABLE IF NOT EXISTS audit_logs (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID,
    username VARCHAR(100),
    action VARCHAR(50) NOT NULL,
    entity_type VARCHAR(100),
    entity_id VARCHAR(100),
    description TEXT,
    old_values TEXT,
    new_values TEXT,
    ip_address VARCHAR(45),
    user_agent VARCHAR(500),
    request_method VARCHAR(10),
    request_url VARCHAR(500),
    status_code INTEGER,
    execution_time_ms BIGINT,
    severity VARCHAR(20) NOT NULL DEFAULT 'INFO',
    status VARCHAR(20) NOT NULL DEFAULT 'SUCCESS',
    error_message TEXT,
    stack_trace TEXT,
    metadata TEXT,
    timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    session_id VARCHAR(100),
    correlation_id VARCHAR(100)
);

-- Create indexes for efficient querying
CREATE INDEX idx_audit_user_id ON audit_logs(user_id);
CREATE INDEX idx_audit_username ON audit_logs(username);
CREATE INDEX idx_audit_entity_type ON audit_logs(entity_type);
CREATE INDEX idx_audit_entity_type_id ON audit_logs(entity_type, entity_id);
CREATE INDEX idx_audit_action ON audit_logs(action);
CREATE INDEX idx_audit_timestamp ON audit_logs(timestamp DESC);
CREATE INDEX idx_audit_severity ON audit_logs(severity);
CREATE INDEX idx_audit_status ON audit_logs(status);
CREATE INDEX idx_audit_ip_address ON audit_logs(ip_address);
CREATE INDEX idx_audit_correlation_id ON audit_logs(correlation_id);

-- Create composite indexes for common queries
CREATE INDEX idx_audit_user_timestamp ON audit_logs(user_id, timestamp DESC);
CREATE INDEX idx_audit_entity_timestamp ON audit_logs(entity_type, entity_id, timestamp DESC);
CREATE INDEX idx_audit_severity_timestamp ON audit_logs(severity, timestamp DESC);

-- Add comments for documentation
COMMENT ON TABLE audit_logs IS 'Comprehensive audit trail for all system operations';
COMMENT ON COLUMN audit_logs.user_id IS 'User who performed the action (null for system actions)';
COMMENT ON COLUMN audit_logs.action IS 'Type of action performed (LOGIN, CREATE, UPDATE, DELETE, etc.)';
COMMENT ON COLUMN audit_logs.entity_type IS 'Type of entity affected (Member, Player, Tournament, etc.)';
COMMENT ON COLUMN audit_logs.entity_id IS 'ID of the entity affected';
COMMENT ON COLUMN audit_logs.old_values IS 'Previous values before change (JSON format)';
COMMENT ON COLUMN audit_logs.new_values IS 'New values after change (JSON format)';
COMMENT ON COLUMN audit_logs.severity IS 'Severity level (DEBUG, INFO, WARNING, ERROR, CRITICAL)';
COMMENT ON COLUMN audit_logs.status IS 'Operation status (SUCCESS, FAILURE, PARTIAL_SUCCESS, IN_PROGRESS)';
COMMENT ON COLUMN audit_logs.correlation_id IS 'ID for tracking related operations';
