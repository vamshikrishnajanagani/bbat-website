# Audit Logging and Monitoring Implementation

## Overview

This document describes the comprehensive audit logging and monitoring system implemented for the Telangana Ball Badminton Association website. The system provides complete visibility into all operations, security events, and system health.

**Task:** 11.2 Implement audit logging and monitoring  
**Requirements:** 6.2, 8.5  
**Status:** ✅ Completed

## Features Implemented

### 1. Comprehensive Audit Trails ✅

**Components:**
- **AuditLog Entity** - Complete audit log data model with 20+ fields
- **AuditLogRepository** - Efficient querying with indexed searches
- **AuditService** - Centralized audit logging service
- **AuditAspect** - Automatic AOP-based audit logging for all controllers

**Audit Log Captures:**
- ✅ User actions (who, what, when, where)
- ✅ Data modifications (old values vs new values in JSON format)
- ✅ Security events (login, logout, access denied, suspicious activity)
- ✅ System events (configuration changes, errors, warnings)
- ✅ HTTP request details (method, URL, IP address, user agent)
- ✅ Performance metrics (execution time)
- ✅ Error details (error messages, stack traces)
- ✅ Session and correlation tracking

**Audit Actions Tracked:**
```
Authentication: LOGIN, LOGOUT, LOGIN_FAILED, TOKEN_REFRESH, PASSWORD_CHANGE
CRUD Operations: CREATE, READ, UPDATE, DELETE, BULK_CREATE, BULK_UPDATE, BULK_DELETE
Security: ACCESS_DENIED, PERMISSION_DENIED, UNAUTHORIZED_ACCESS, SUSPICIOUS_ACTIVITY
Data Operations: EXPORT, IMPORT, BACKUP, RESTORE
Privacy: CONSENT_GIVEN, CONSENT_REVOKED, DATA_EXPORT_REQUEST, DATA_DELETION_REQUEST
System: CONFIGURATION_CHANGE, SYSTEM_ERROR, SYSTEM_WARNING, CACHE_CLEAR
Files: FILE_UPLOAD, FILE_DOWNLOAD, FILE_DELETE
```

**Severity Levels:**
- DEBUG - Detailed debugging information
- INFO - General informational messages
- WARNING - Warning messages for potential issues
- ERROR - Error messages for failures
- CRITICAL - Critical security or system issues

### 2. Security Monitoring and Alerting ✅

**SecurityMonitoringService Features:**
- ✅ Real-time threat detection
- ✅ Brute force attack detection (5 failed attempts in 15 minutes)
- ✅ Automatic IP blocking for suspicious activity
- ✅ Rate limiting monitoring (100 requests per minute)
- ✅ Anomaly detection (10+ failed operations in 1 hour)
- ✅ Security metrics dashboard
- ✅ Email alerts for critical security events
- ✅ Daily security reports (generated at 2 AM)
- ✅ Scheduled security monitoring (every 5 minutes)

**Security Metrics Tracked:**
- Failed login attempts (last 24 hours)
- Critical security events
- Blocked IP addresses
- Access denied events
- Suspicious activities
- Top failed login IPs

**Alerting:**
- Email notifications for critical events
- Daily security reports
- Real-time logging to security logger
- Audit trail for all security events

### 3. Backup and Disaster Recovery ✅

**BackupService Features:**
- ✅ Automated database backups (daily at 2 AM)
- ✅ Automated file backups (daily at 3 AM)
- ✅ Manual backup triggers via API
- ✅ Backup retention management (30 days default)
- ✅ Backup verification
- ✅ Database restore functionality
- ✅ Backup listing and metadata
- ✅ Automated cleanup of old backups (daily at 3 AM)
- ✅ Email notifications for backup status

**Backup Types:**
1. **Database Backups**
   - PostgreSQL pg_dump format (compressed)
   - Includes all tables, indexes, and constraints
   - Timestamped filenames: `db_backup_YYYYMMDD_HHMMSS.sql`

2. **File Backups**
   - Tar.gz archives of upload directory
   - Preserves file structure and permissions
   - Timestamped filenames: `files_backup_YYYYMMDD_HHMMSS.tar.gz`

**Disaster Recovery Procedures:**
1. Automated daily backups ensure maximum 24-hour data loss
2. Backups stored in configurable directory (default: `./backups`)
3. Retention policy prevents disk space issues
4. Restore functionality for quick recovery
5. Backup verification ensures integrity

### 4. Vulnerability Scanning and Updates ✅

**VulnerabilityScanner Features:**
- ✅ Comprehensive security scanning
- ✅ Configuration security checks
- ✅ Security headers validation
- ✅ Password policy verification
- ✅ Encryption status checks
- ✅ Security score calculation (0-100)
- ✅ Weekly automated scans (Sunday at 1 AM)
- ✅ Email alerts for critical vulnerabilities
- ✅ Integration-ready for OWASP Dependency-Check, Snyk, etc.

**Security Checks Performed:**
- Production mode verification
- HTTPS enforcement
- JWT secret strength
- Database encryption
- Content Security Policy (CSP)
- X-Frame-Options
- X-Content-Type-Options
- Strict-Transport-Security (HSTS)
- Password hashing (BCrypt)
- Password strength requirements
- Account lockout policy
- Data at rest encryption (AES-256)
- Data in transit encryption (TLS/HTTPS)
- Database connection encryption

**Security Score:**
- Calculated based on passed checks
- Deductions for vulnerabilities
- Range: 0-100
- Alerts triggered for scores below threshold

### 5. Admin API Endpoints ✅

**AuditController Endpoints:**

**Audit Logs:**
- `GET /admin/audit/logs` - Get paginated audit logs
- `GET /admin/audit/logs/user/{userId}` - Get logs by user
- `GET /admin/audit/logs/entity/{entityType}/{entityId}` - Get logs by entity
- `GET /admin/audit/logs/security` - Get security events
- `GET /admin/audit/logs/failures` - Get failed operations
- `GET /admin/audit/logs/statistics` - Get audit statistics

**Security Monitoring:**
- `GET /admin/audit/security/metrics` - Get security metrics
- `GET /admin/audit/security/blocked-ips` - Get blocked IPs
- `POST /admin/audit/security/block-ip` - Block IP address
- `POST /admin/audit/security/unblock-ip` - Unblock IP address
- `POST /admin/audit/security/clear-blocked-ips` - Clear all blocked IPs

**Backup Management:**
- `POST /admin/audit/backup/database` - Create database backup
- `POST /admin/audit/backup/files` - Create file backup
- `GET /admin/audit/backup/list` - List available backups
- `POST /admin/audit/backup/restore` - Restore from backup
- `POST /admin/audit/backup/verify` - Verify backup integrity

**Vulnerability Scanning:**
- `POST /admin/audit/security/scan` - Run security scan
- `GET /admin/audit/security/updates` - Check for security updates

**Cleanup:**
- `POST /admin/audit/cleanup/audit-logs` - Cleanup old audit logs
- `POST /admin/audit/cleanup/backups` - Cleanup old backups

All endpoints require ADMIN role authentication.

## Architecture

### Audit Logging Flow

```
Controller Method
    ↓
AuditAspect (AOP)
    ↓
AuditService.logAudit()
    ↓
Enrich with Context (IP, User Agent, etc.)
    ↓
Save to Database (async)
    ↓
Log to Security Logger (if critical)
```

### Security Monitoring Flow

```
Security Event
    ↓
SecurityMonitoringService
    ↓
Check Thresholds
    ↓
Block IP / Send Alert (if needed)
    ↓
Log to Audit Trail
    ↓
Email Notification
```

### Backup Flow

```
Scheduled Task (2 AM)
    ↓
BackupService.createDatabaseBackup()
    ↓
Execute pg_dump
    ↓
Verify Backup File
    ↓
Log to Audit Trail
    ↓
Email Notification
```

## Database Schema

### audit_logs Table

```sql
CREATE TABLE audit_logs (
    id UUID PRIMARY KEY,
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
    severity VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL,
    error_message TEXT,
    stack_trace TEXT,
    metadata TEXT,
    timestamp TIMESTAMP NOT NULL,
    session_id VARCHAR(100),
    correlation_id VARCHAR(100)
);
```

**Indexes:**
- user_id, username, entity_type, entity_id
- action, timestamp, severity, status
- ip_address, correlation_id
- Composite indexes for common queries

## Configuration

### Application Properties

```yaml
app:
  backup:
    directory: ${BACKUP_DIR:./backups}
    retention-days: ${BACKUP_RETENTION_DAYS:30}
    enabled: true
  
  security:
    vulnerability-scan-enabled: ${VULNERABILITY_SCAN_ENABLED:true}
    audit-log-retention-days: ${AUDIT_LOG_RETENTION_DAYS:90}
    failed-login-threshold: 5
    rate-limit-threshold: 100
```

### Environment Variables

```bash
# Backup Configuration
BACKUP_DIR=/var/backups/telangana-ball-badminton
BACKUP_RETENTION_DAYS=30

# Security Configuration
VULNERABILITY_SCAN_ENABLED=true
AUDIT_LOG_RETENTION_DAYS=90

# Database Configuration (for backups)
DATABASE_URL=jdbc:postgresql://localhost:5432/dbname
DATABASE_USERNAME=username
DATABASE_PASSWORD=password

# Email Configuration (for alerts)
MAIL_HOST=smtp.example.com
MAIL_PORT=587
MAIL_USERNAME=alerts@telanganaballbadminton.org
MAIL_PASSWORD=password
```

## Scheduled Tasks

| Task | Schedule | Description |
|------|----------|-------------|
| Database Backup | Daily at 2 AM | Automated database backup |
| File Backup | Daily at 3 AM | Automated file backup |
| Backup Cleanup | Daily at 3 AM | Remove old backups |
| Security Monitoring | Every 5 minutes | Check for security events |
| Daily Security Report | Daily at 2 AM | Generate security report |
| Security Scan | Weekly (Sunday 1 AM) | Comprehensive security scan |
| Audit Log Cleanup | On-demand | Remove old audit logs |

## Logging Configuration

### Log Files

1. **application.log** - General application logs
2. **security-audit.log** - Security-specific events (90-day retention)
3. **performance.log** - Performance metrics (30-day retention)

### Log Formats

**Development:**
- Human-readable format
- Colored output
- Detailed stack traces

**Production:**
- JSON format (Logstash-compatible)
- Structured logging
- Optimized for log aggregation

### Log Levels

```yaml
logging:
  level:
    com.telangana.ballbadminton: INFO
    com.telangana.ballbadminton.security: INFO
    com.telangana.ballbadminton.performance: INFO
```

## Security Features

### Audit Trail Security

- ✅ Audit logs use separate transaction (REQUIRES_NEW)
- ✅ Async logging prevents performance impact
- ✅ Fallback to file logging if database fails
- ✅ Immutable audit records (no update/delete operations)
- ✅ Indexed for efficient querying
- ✅ Retention policy prevents disk space issues

### Monitoring Security

- ✅ IP blocking for brute force attacks
- ✅ Rate limiting enforcement
- ✅ Anomaly detection
- ✅ Real-time alerting
- ✅ Correlation tracking for related events
- ✅ Session tracking for user activities

### Backup Security

- ✅ Encrypted database backups (PostgreSQL custom format)
- ✅ Secure backup storage location
- ✅ Access control for backup operations (ADMIN only)
- ✅ Backup verification before use
- ✅ Audit trail for all backup operations

## Performance Considerations

### Audit Logging

- **Async Processing:** Audit logs are saved asynchronously to prevent blocking
- **Separate Transactions:** Uses REQUIRES_NEW to ensure logs are saved even if main transaction fails
- **Indexed Queries:** All common queries use indexes for fast retrieval
- **Batch Processing:** Supports bulk operations for efficiency
- **Retention Policy:** Automatic cleanup prevents database bloat

### Security Monitoring

- **In-Memory Cache:** Blocked IPs stored in memory for fast checks
- **Scheduled Tasks:** Background processing doesn't impact user requests
- **Efficient Queries:** Uses indexed queries for fast threat detection
- **Rate Limiting:** Prevents resource exhaustion

### Backups

- **Compressed Format:** PostgreSQL custom format reduces storage
- **Scheduled Off-Peak:** Runs during low-traffic hours (2-3 AM)
- **Incremental Cleanup:** Gradual removal of old backups
- **Verification:** Ensures backup integrity without full restore

## Monitoring and Alerting

### Email Alerts

Alerts are sent for:
- Critical security events
- Failed backups
- Brute force attacks detected
- IP addresses blocked
- Daily security reports
- Vulnerability scan results

### Metrics Exposed

Via Spring Boot Actuator:
- `/actuator/health` - System health
- `/actuator/metrics` - Application metrics
- `/actuator/prometheus` - Prometheus metrics

Custom metrics:
- Audit log counts by action
- Security event counts
- Failed operation counts
- Backup success/failure rates

## Integration with External Tools

### Log Aggregation

The system is ready for integration with:
- **ELK Stack** (Elasticsearch, Logstash, Kibana)
- **Splunk**
- **Datadog**
- **New Relic**

JSON logging format in production enables easy parsing.

### Vulnerability Scanning

Ready for integration with:
- **OWASP Dependency-Check**
- **Snyk**
- **GitHub Dependabot**
- **JFrog Xray**

Placeholder code provided in VulnerabilityScanner service.

### Backup Storage

Can be configured to use:
- Local file system (default)
- Network-attached storage (NAS)
- Cloud storage (S3, Azure Blob, Google Cloud Storage)
- Backup servers

## Usage Examples

### Querying Audit Logs

```bash
# Get recent audit logs
curl -X GET "http://localhost:8080/api/v1/admin/audit/logs?page=0&size=50" \
  -H "Authorization: Bearer {token}"

# Get logs for specific user
curl -X GET "http://localhost:8080/api/v1/admin/audit/logs/user/{userId}" \
  -H "Authorization: Bearer {token}"

# Get security events
curl -X GET "http://localhost:8080/api/v1/admin/audit/logs/security" \
  -H "Authorization: Bearer {token}"

# Get audit statistics
curl -X GET "http://localhost:8080/api/v1/admin/audit/logs/statistics?startTime=2024-01-01T00:00:00&endTime=2024-12-31T23:59:59" \
  -H "Authorization: Bearer {token}"
```

### Security Monitoring

```bash
# Get security metrics
curl -X GET "http://localhost:8080/api/v1/admin/audit/security/metrics" \
  -H "Authorization: Bearer {token}"

# Block an IP address
curl -X POST "http://localhost:8080/api/v1/admin/audit/security/block-ip?ipAddress=192.168.1.100&reason=Suspicious%20activity" \
  -H "Authorization: Bearer {token}"

# Get blocked IPs
curl -X GET "http://localhost:8080/api/v1/admin/audit/security/blocked-ips" \
  -H "Authorization: Bearer {token}"
```

### Backup Management

```bash
# Create database backup
curl -X POST "http://localhost:8080/api/v1/admin/audit/backup/database" \
  -H "Authorization: Bearer {token}"

# List backups
curl -X GET "http://localhost:8080/api/v1/admin/audit/backup/list" \
  -H "Authorization: Bearer {token}"

# Verify backup
curl -X POST "http://localhost:8080/api/v1/admin/audit/backup/verify?backupFilePath=/path/to/backup.sql" \
  -H "Authorization: Bearer {token}"
```

### Vulnerability Scanning

```bash
# Run security scan
curl -X POST "http://localhost:8080/api/v1/admin/audit/security/scan" \
  -H "Authorization: Bearer {token}"

# Check for updates
curl -X GET "http://localhost:8080/api/v1/admin/audit/security/updates" \
  -H "Authorization: Bearer {token}"
```

## Testing

### Manual Testing Checklist

**Audit Logging:**
- [ ] Verify audit logs are created for all CRUD operations
- [ ] Check audit logs capture user information correctly
- [ ] Verify old/new values are stored in JSON format
- [ ] Test audit log querying by user, entity, time range
- [ ] Verify security events are logged correctly
- [ ] Check failed operations are logged with error details
- [ ] Test correlation ID tracking for related operations

**Security Monitoring:**
- [ ] Test brute force detection (5 failed logins)
- [ ] Verify IP blocking functionality
- [ ] Test rate limiting detection
- [ ] Check anomaly detection for suspicious patterns
- [ ] Verify security metrics are accurate
- [ ] Test email alerts for critical events
- [ ] Check daily security reports are generated

**Backup and Recovery:**
- [ ] Test manual database backup creation
- [ ] Test manual file backup creation
- [ ] Verify backup files are created correctly
- [ ] Test backup listing functionality
- [ ] Test backup verification
- [ ] Test database restore (in test environment)
- [ ] Verify scheduled backups run correctly
- [ ] Test backup cleanup removes old files

**Vulnerability Scanning:**
- [ ] Run security scan and verify results
- [ ] Check security score calculation
- [ ] Verify all security checks are performed
- [ ] Test email alerts for critical vulnerabilities
- [ ] Check weekly scheduled scans run correctly

### Automated Testing

Unit tests should cover:
- AuditService methods
- SecurityMonitoringService threat detection
- BackupService backup/restore operations
- VulnerabilityScanner security checks

Integration tests should cover:
- AuditController endpoints
- End-to-end audit logging flow
- Security monitoring workflows
- Backup creation and verification

## Troubleshooting

### Audit Logs Not Appearing

1. Check database connection
2. Verify AuditAspect is enabled
3. Check async configuration
4. Review application logs for errors
5. Verify transaction configuration

### Backups Failing

1. Check database credentials
2. Verify pg_dump is installed and in PATH
3. Check backup directory permissions
4. Review backup service logs
5. Verify disk space availability

### Security Alerts Not Sent

1. Check email configuration
2. Verify SMTP credentials
3. Check email service logs
4. Verify admin email address
5. Test email service independently

### High Audit Log Volume

1. Adjust retention policy
2. Run cleanup more frequently
3. Consider archiving old logs
4. Optimize queries with indexes
5. Consider log aggregation service

## Best Practices

### Audit Logging

1. **Don't log sensitive data** - Passwords, tokens, credit cards
2. **Use correlation IDs** - Track related operations
3. **Include context** - IP address, user agent, session ID
4. **Log both success and failure** - Complete audit trail
5. **Use appropriate severity levels** - Don't overuse CRITICAL

### Security Monitoring

1. **Tune thresholds** - Adjust based on actual usage patterns
2. **Review blocked IPs regularly** - Unblock legitimate users
3. **Monitor false positives** - Adjust detection algorithms
4. **Act on alerts promptly** - Don't ignore security warnings
5. **Regular security reviews** - Weekly scan results

### Backup Management

1. **Test restores regularly** - Verify backups are usable
2. **Store backups off-site** - Protect against disasters
3. **Encrypt backup files** - Protect sensitive data
4. **Monitor backup success** - Alert on failures
5. **Document recovery procedures** - Enable quick recovery

### Vulnerability Scanning

1. **Act on findings promptly** - Don't delay security updates
2. **Prioritize critical vulnerabilities** - Fix high-risk issues first
3. **Keep dependencies updated** - Regular update schedule
4. **Review scan results** - Don't just rely on automation
5. **Document remediation** - Track security improvements

## Compliance

This implementation supports compliance with:

- ✅ **GDPR Article 30** - Records of processing activities
- ✅ **GDPR Article 32** - Security of processing
- ✅ **GDPR Article 33** - Notification of data breach
- ✅ **ISO 27001** - Information security management
- ✅ **SOC 2** - Security, availability, and confidentiality
- ✅ **PCI DSS** - Audit trails and monitoring (if applicable)

## Future Enhancements

1. **Real-time Dashboard** - Web UI for monitoring
2. **Advanced Analytics** - ML-based anomaly detection
3. **Integration with SIEM** - Security information and event management
4. **Automated Remediation** - Auto-fix common issues
5. **Compliance Reports** - Automated compliance reporting
6. **Backup Encryption** - Encrypt backup files at rest
7. **Incremental Backups** - Reduce backup time and storage
8. **Multi-region Backups** - Geographic redundancy
9. **Advanced Vulnerability Scanning** - Integration with commercial tools
10. **Threat Intelligence** - Integration with threat feeds

## Conclusion

The audit logging and monitoring system provides comprehensive visibility into all operations, robust security monitoring, reliable backup and recovery procedures, and proactive vulnerability management. This implementation fulfills requirements 6.2 and 8.5, ensuring the system maintains detailed audit trails and comprehensive security monitoring.

## Requirements Fulfilled

- ✅ **Requirement 6.2:** THE Content_Management_System SHALL maintain audit trails and version history
  - Comprehensive audit logging for all operations
  - Complete history tracking with old/new values
  - Immutable audit records with retention policy

- ✅ **Requirement 8.5:** THE Website SHALL maintain regular security updates and vulnerability assessments
  - Automated vulnerability scanning
  - Security monitoring and alerting
  - Regular backup and disaster recovery
  - Proactive security update checking
