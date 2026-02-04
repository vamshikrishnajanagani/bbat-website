# API Versioning Strategy

## Overview

The Telangana Ball Badminton Association API uses a comprehensive versioning strategy to ensure backward compatibility, smooth migrations, and clear communication of changes to API consumers.

## Table of Contents

1. [Versioning Approach](#versioning-approach)
2. [Version Lifecycle](#version-lifecycle)
3. [Breaking vs Non-Breaking Changes](#breaking-vs-non-breaking-changes)
4. [Deprecation Policy](#deprecation-policy)
5. [Migration Guide](#migration-guide)
6. [Version Detection](#version-detection)
7. [Best Practices](#best-practices)

## Versioning Approach

### URL-Based Versioning

The API uses URL-based versioning as the primary versioning mechanism:

```
https://api.telanganaballbadminton.org/api/v1/members
https://api.telanganaballbadminton.org/api/v2/members
```

**Benefits:**
- Clear and explicit version in the URL
- Easy to understand and implement
- Simple routing and caching
- Version-specific documentation

### Version Header (Alternative)

Clients can also specify the API version using the `Accept` header:

```http
Accept: application/vnd.telangana-ball-badminton.v1+json
```

This approach is useful for:
- Gradual migration testing
- A/B testing different versions
- Custom version negotiation

### Semantic Versioning

The API follows semantic versioning principles:

```
MAJOR.MINOR.PATCH
```

- **MAJOR** (v1, v2, v3): Breaking changes, new major features
- **MINOR** (1.1, 1.2): Backward-compatible new features
- **PATCH** (1.0.1, 1.0.2): Backward-compatible bug fixes

## Version Lifecycle

### Version States

Each API version goes through the following states:

```
Development → Beta → Stable → Deprecated → Sunset
```

#### 1. Development
- **Status**: Under active development
- **Availability**: Not publicly available
- **Stability**: Frequent changes expected
- **Support**: No support guarantees

#### 2. Beta
- **Status**: Feature complete, testing phase
- **Availability**: Available for early adopters
- **Stability**: Minor changes possible
- **Support**: Limited support, feedback encouraged
- **Duration**: 2-3 months

#### 3. Stable
- **Status**: Production-ready
- **Availability**: Fully available for all users
- **Stability**: Only backward-compatible changes
- **Support**: Full support and maintenance
- **Duration**: Minimum 18 months

#### 4. Deprecated
- **Status**: Marked for removal
- **Availability**: Still available but not recommended
- **Stability**: No new features, critical fixes only
- **Support**: Limited support, migration assistance provided
- **Duration**: 6 months minimum

#### 5. Sunset
- **Status**: Removed from service
- **Availability**: No longer available
- **Stability**: N/A
- **Support**: No support

### Current Version Status

| Version | Status | Release Date | Deprecation Date | Sunset Date |
|---------|--------|--------------|------------------|-------------|
| v1 | Stable | 2023-12-01 | TBD | TBD |
| v2 | Development | TBD | N/A | N/A |

## Breaking vs Non-Breaking Changes

### Breaking Changes (Require New Major Version)

Breaking changes require a new major version (v1 → v2):

1. **Removing endpoints**
   ```
   ❌ DELETE /api/v1/old-endpoint
   ```

2. **Removing required fields**
   ```json
   ❌ {
     "name": "John Doe"
     // "email" field removed
   }
   ```

3. **Changing field types**
   ```json
   ❌ {
     "age": "25"  // Changed from number to string
   }
   ```

4. **Renaming fields**
   ```json
   ❌ {
     "fullName": "John Doe"  // Was "name"
   }
   ```

5. **Changing authentication mechanism**
   ```
   ❌ OAuth 2.0 → API Keys
   ```

6. **Changing response structure**
   ```json
   ❌ {
     "data": {
       "members": [...]  // Was directly an array
     }
   }
   ```

7. **Changing HTTP status codes**
   ```
   ❌ 200 OK → 201 Created (for same operation)
   ```

8. **Removing query parameters**
   ```
   ❌ GET /members?filter=active  // filter parameter removed
   ```

### Non-Breaking Changes (Same Major Version)

Non-breaking changes can be added to the current version:

1. **Adding new endpoints**
   ```
   ✅ POST /api/v1/new-endpoint
   ```

2. **Adding optional fields**
   ```json
   ✅ {
     "name": "John Doe",
     "nickname": "JD"  // New optional field
   }
   ```

3. **Adding new query parameters**
   ```
   ✅ GET /members?filter=active&sortBy=name  // sortBy is new
   ```

4. **Adding new response fields**
   ```json
   ✅ {
     "name": "John Doe",
     "createdAt": "2023-12-01"  // New field
   }
   ```

5. **Adding new enum values**
   ```json
   ✅ {
     "status": "PENDING"  // New status value
   }
   ```

6. **Relaxing validation rules**
   ```
   ✅ Email field: required → optional
   ```

7. **Adding new HTTP methods to existing endpoints**
   ```
   ✅ PATCH /members/{id}  // GET, POST, PUT already exist
   ```

## Deprecation Policy

### Deprecation Process

When an endpoint or feature is deprecated, we follow a structured process:

#### 1. Announcement (T-6 months)

- Public announcement on developer portal
- Email notification to all registered API consumers
- Update documentation with deprecation notice
- Add deprecation warnings to API responses

#### 2. Deprecation Headers (T-6 months)

Add deprecation headers to affected endpoints:

```http
HTTP/1.1 200 OK
Deprecation: true
Sunset: Sat, 01 Jun 2024 00:00:00 GMT
Link: <https://docs.telanganaballbadminton.org/migration/v1-to-v2>; rel="deprecation"
Warning: 299 - "This endpoint is deprecated and will be removed on 2024-06-01. Please migrate to /api/v2/members"
```

#### 3. Migration Guide (T-6 months)

Publish comprehensive migration guide including:
- List of deprecated endpoints
- Replacement endpoints or alternatives
- Code examples for migration
- Breaking changes summary
- Timeline and deadlines

#### 4. Increased Warnings (T-3 months)

- More prominent warnings in documentation
- Dashboard notifications for API consumers
- Email reminders about upcoming sunset
- Monitoring and alerting for deprecated endpoint usage

#### 5. Final Notice (T-1 month)

- Final email notification
- Dashboard alerts
- Increased warning severity in API responses
- Support team ready to assist with migrations

#### 6. Sunset (T-0)

- Endpoint removed or returns 410 Gone
- Redirect to new version if applicable
- Support for migration issues

### Deprecation Response Format

Deprecated endpoints return additional information:

```json
{
  "data": { ... },
  "deprecation": {
    "deprecated": true,
    "sunsetDate": "2024-06-01T00:00:00Z",
    "message": "This endpoint is deprecated. Please use /api/v2/members instead.",
    "migrationGuide": "https://docs.telanganaballbadminton.org/migration/v1-to-v2",
    "alternativeEndpoint": "/api/v2/members"
  }
}
```

### Currently Deprecated Endpoints

| Endpoint | Deprecated Since | Sunset Date | Alternative |
|----------|------------------|-------------|-------------|
| None | - | - | - |

*No endpoints are currently deprecated.*

## Migration Guide

### Planning Your Migration

#### 1. Assess Impact

- Identify which deprecated endpoints you're using
- Review the migration guide for each endpoint
- Estimate development effort required
- Plan testing and rollout strategy

#### 2. Test in Staging

- Update your code to use new endpoints
- Test thoroughly in staging environment
- Verify all functionality works as expected
- Check for performance differences

#### 3. Gradual Rollout

- Deploy to a small percentage of users first
- Monitor for errors and issues
- Gradually increase rollout percentage
- Keep old version as fallback

#### 4. Complete Migration

- Deploy to all users
- Monitor for issues
- Remove old version code
- Update documentation

### Migration Checklist

```markdown
- [ ] Review deprecation notices and migration guide
- [ ] Update API client to use new endpoints
- [ ] Update request/response models if needed
- [ ] Update authentication if changed
- [ ] Test all affected functionality
- [ ] Update error handling for new error formats
- [ ] Update documentation and code comments
- [ ] Deploy to staging and test
- [ ] Deploy to production with monitoring
- [ ] Remove old version code after verification
```

### Example Migration: v1 to v2

**Scenario**: Migrating from v1 to v2 member endpoint

**v1 Endpoint (Deprecated)**:
```http
GET /api/v1/members/{id}

Response:
{
  "id": "123",
  "name": "John Doe",
  "position": "President"
}
```

**v2 Endpoint (New)**:
```http
GET /api/v2/members/{id}

Response:
{
  "id": "123",
  "profile": {
    "fullName": "John Doe",
    "displayName": "John",
    "position": {
      "title": "President",
      "level": 1
    }
  },
  "metadata": {
    "createdAt": "2023-01-01T00:00:00Z",
    "updatedAt": "2023-12-01T00:00:00Z"
  }
}
```

**Migration Code (JavaScript)**:

```javascript
// Before (v1)
async function getMember(id) {
  const response = await fetch(`/api/v1/members/${id}`);
  const member = await response.json();
  return {
    id: member.id,
    name: member.name,
    position: member.position
  };
}

// After (v2)
async function getMember(id) {
  const response = await fetch(`/api/v2/members/${id}`);
  const data = await response.json();
  return {
    id: data.id,
    name: data.profile.fullName,
    position: data.profile.position.title
  };
}
```

## Version Detection

### Detecting API Version in Responses

All API responses include version information in headers:

```http
HTTP/1.1 200 OK
X-API-Version: 1.0.0
X-API-Version-Major: 1
X-API-Version-Minor: 0
X-API-Version-Patch: 0
```

### Checking Version Compatibility

```javascript
async function checkAPIVersion() {
  const response = await fetch('/api/v1/members');
  const version = response.headers.get('X-API-Version');
  const major = parseInt(response.headers.get('X-API-Version-Major'));
  
  if (major !== 1) {
    console.warn(`API version mismatch. Expected v1, got v${major}`);
  }
  
  return version;
}
```

### Version Negotiation

Request specific version using Accept header:

```javascript
async function getMembersWithVersion(version = 'v1') {
  const response = await fetch('/api/v1/members', {
    headers: {
      'Accept': `application/vnd.telangana-ball-badminton.${version}+json`
    }
  });
  return response.json();
}
```

## Best Practices

### For API Consumers

1. **Always specify the version explicitly**
   ```javascript
   // Good
   const API_BASE = 'https://api.telanganaballbadminton.org/api/v1';
   
   // Bad
   const API_BASE = 'https://api.telanganaballbadminton.org/api';
   ```

2. **Monitor deprecation headers**
   ```javascript
   const response = await fetch(url);
   if (response.headers.get('Deprecation')) {
     console.warn('Using deprecated endpoint:', url);
     console.warn('Sunset date:', response.headers.get('Sunset'));
   }
   ```

3. **Use version detection**
   ```javascript
   const version = response.headers.get('X-API-Version');
   if (!isCompatible(version, '1.0.0')) {
     throw new Error('Incompatible API version');
   }
   ```

4. **Plan for migrations early**
   - Subscribe to API announcements
   - Review deprecation notices regularly
   - Start migration planning 3+ months before sunset

5. **Test with new versions early**
   - Test beta versions when available
   - Provide feedback on new features
   - Report issues early

### For API Developers

1. **Maintain backward compatibility within major versions**
   - Only add, never remove or change existing fields
   - Make new fields optional
   - Provide default values

2. **Document all changes**
   - Update changelog for every release
   - Document breaking changes clearly
   - Provide migration examples

3. **Communicate early and often**
   - Announce deprecations 6+ months in advance
   - Send regular reminders
   - Provide migration support

4. **Support multiple versions**
   - Support at least 2 major versions simultaneously
   - Maintain old versions during transition period
   - Provide clear sunset dates

5. **Monitor usage**
   - Track deprecated endpoint usage
   - Identify consumers still using old versions
   - Reach out proactively to help with migration

## Version History

### v1.0.0 (Current - Stable)

**Release Date**: December 1, 2023  
**Status**: Stable  
**Support Until**: TBD

**Features**:
- Complete CRUD operations for all entities
- JWT authentication and authorization
- Pagination, filtering, and search
- File upload and media management
- Email notifications
- Audit logging
- Caching and performance optimization

**Known Issues**: None

### v2.0.0 (Planned)

**Expected Release**: Q2 2024  
**Status**: Development  
**Beta Release**: Q1 2024

**Planned Features**:
- GraphQL support
- WebSocket real-time updates
- Enhanced search with Elasticsearch
- Advanced analytics and reporting
- Improved file management
- Batch operations API
- Webhook support

**Breaking Changes**:
- TBD (will be documented before beta release)

## Support and Resources

### Documentation

- **API Documentation**: https://api.telanganaballbadminton.org/swagger-ui.html
- **Developer Portal**: https://developers.telanganaballbadminton.org
- **Changelog**: https://www.telanganaballbadminton.org/api/changelog
- **Migration Guides**: https://docs.telanganaballbadminton.org/migrations

### Support Channels

- **Email**: api-support@telanganaballbadminton.org
- **GitHub Issues**: https://github.com/telangana-ball-badminton/api/issues
- **Developer Forum**: https://forum.telanganaballbadminton.org
- **Status Page**: https://status.telanganaballbadminton.org

### Notifications

Subscribe to API updates:
- **Email Newsletter**: https://www.telanganaballbadminton.org/api/subscribe
- **RSS Feed**: https://www.telanganaballbadminton.org/api/feed
- **Twitter**: @TelanganaAPI

---

**Last Updated**: December 2023  
**Document Version**: 1.0.0  
**API Version**: 1.0.0
