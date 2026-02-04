# End-to-End Testing Guide

## Overview

This document provides comprehensive guidance on the end-to-end (E2E) testing suite for the Telangana Ball Badminton Association Website. The E2E tests validate critical user journeys, API integration, data consistency, and system performance.

## Test Structure

### Test Packages

```
backend/src/test/java/com/telangana/ballbadminton/e2e/
├── MemberManagementE2ETest.java          # Member lifecycle and hierarchy tests
├── PlayerManagementE2ETest.java          # Player profiles and achievements tests
├── TournamentManagementE2ETest.java      # Tournament management and registration tests
├── ApiIntegrationE2ETest.java            # API integration and data consistency tests
├── PerformanceE2ETest.java               # Performance and load tests
└── RegressionTestSuite.java              # Complete regression test suite
```

## Test Categories

### 1. Member Management E2E Tests

**Purpose**: Validate member management functionality including CRUD operations, hierarchy ordering, and contact routing.

**Test Coverage**:
- Complete member lifecycle (create, read, update, delete)
- Member hierarchy ordering preservation
- Contact form routing to correct members
- Data consistency across multiple operations
- Required fields validation

**Requirements Validated**: 1.1, 1.2, 1.3, 1.4, 1.5

**Key Test Cases**:
```java
@Test
void testCompleteMemberLifecycle()
// Tests: Create → Read → Update → Verify → Delete → Verify Deletion

@Test
void testMemberHierarchyOrdering()
// Tests: Multiple members with different hierarchy levels are ordered correctly

@Test
void testContactFormRouting()
// Tests: Contact forms route to correct member based on role
```

### 2. Player Management E2E Tests

**Purpose**: Validate player profile management, achievements tracking, statistics, and search functionality.

**Test Coverage**:
- Complete player lifecycle with achievements
- Player search and filtering by district/category
- Player ranking calculation and ordering
- Player statistics aggregation
- Prominent players listing
- Historical data preservation after updates

**Requirements Validated**: 3.1, 3.2, 3.3, 3.4, 3.5

**Key Test Cases**:
```java
@Test
void testCompletePlayerLifecycle()
// Tests: Create player → Add achievements → Update → Verify achievements preserved

@Test
void testPlayerSearchAndFiltering()
// Tests: Filter by district, category, and combined filters

@Test
void testPlayerRankingCalculation()
// Tests: Players ranked correctly by win percentage
```

### 3. Tournament Management E2E Tests

**Purpose**: Validate tournament creation, registration, status updates, and results tracking.

**Test Coverage**:
- Complete tournament lifecycle from creation to completion
- Tournament registration validation
- Tournament capacity management
- Tournament search and filtering
- Data consistency across updates
- Tournament association integrity

**Requirements Validated**: 4.1, 4.2, 4.3, 4.4, 4.5

**Key Test Cases**:
```java
@Test
void testCompleteTournamentLifecycle()
// Tests: Create → Register players → Update status → Add results → Complete

@Test
void testTournamentRegistrationValidation()
// Tests: Registration deadline enforcement

@Test
void testTournamentCapacityManagement()
// Tests: Maximum participant limit enforcement
```

### 4. API Integration and Data Consistency Tests

**Purpose**: Validate cross-entity data consistency, API completeness, and referential integrity.

**Test Coverage**:
- Data round-trip consistency for all entity types
- Real-time update propagation across all endpoints
- API endpoint completeness (CRUD for all entities)
- Cross-entity referential integrity
- District player count aggregation accuracy
- Audit trail creation for all modifications
- Required field completeness in API responses
- Input validation consistency across endpoints

**Requirements Validated**: 7.1, 9.1, 9.2, 9.4, 9.5

**Key Test Cases**:
```java
@Test
void testDataRoundTripConsistency()
// Tests: Store entity → Retrieve → Verify data equivalence

@Test
void testRealTimeUpdatePropagation()
// Tests: Update reflected immediately in all related endpoints

@Test
void testApiEndpointCompleteness()
// Tests: CRUD operations available for all content types

@Test
void testCrossEntityReferentialIntegrity()
// Tests: Foreign key relationships maintained correctly
```

### 5. Performance and Load Tests

**Purpose**: Validate system performance under normal and high-load scenarios.

**Test Coverage**:
- API response time under normal load
- Concurrent request handling
- High-volume data retrieval
- Pagination performance
- Search and filter performance
- Bulk operation performance
- Complex query performance
- Repeated read performance (caching)
- Database connection pool under load

**Requirements Validated**: 7.1 (Page load time < 3 seconds)

**Performance Targets**:
- Single entity retrieval: < 200ms
- List endpoint: < 500ms
- Bulk operations (50 records): < 2 seconds
- Concurrent requests: Average < 1 second
- 95% success rate under high load

**Key Test Cases**:
```java
@Test
void testApiResponseTime()
// Tests: Response times meet performance targets

@Test
void testConcurrentRequestHandling()
// Tests: 50 concurrent requests handled successfully

@Test
void testHighVolumeDataRetrieval()
// Tests: 100 records retrieved in < 1 second
```

## Running the Tests

### Run All E2E Tests

```bash
# Using Gradle
./gradlew test --tests "com.telangana.ballbadminton.e2e.*"

# Run specific test class
./gradlew test --tests "com.telangana.ballbadminton.e2e.MemberManagementE2ETest"

# Run regression test suite
./gradlew test --tests "com.telangana.ballbadminton.e2e.RegressionTestSuite"
```

### Run with Coverage

```bash
./gradlew test jacocoTestReport --tests "com.telangana.ballbadminton.e2e.*"
```

### Run Performance Tests Only

```bash
./gradlew test --tests "com.telangana.ballbadminton.e2e.PerformanceE2ETest"
```

## Test Infrastructure

### Base Test Class

All E2E tests extend `BaseIntegrationTest` which provides:

- **Testcontainers**: PostgreSQL and Redis containers for isolated testing
- **MockMvc**: Web layer testing without starting full server
- **Transaction Rollback**: Automatic cleanup after each test
- **Helper Methods**: JSON conversion, authentication, etc.

### Test Data Management

- Each test class has `@BeforeEach` setup method for test data
- Tests are isolated and don't depend on execution order
- Database is cleaned before each test
- Test data factories available in `TestDataFactory`

### Assertions

Tests use AssertJ for fluent assertions:

```java
assertThat(responseTime).isLessThan(500);
assertThat(players).hasSize(5);
assertThat(member.getName()).isEqualTo("Expected Name");
```

## Best Practices

### 1. Test Isolation

- Each test should be independent
- Clean database state in `@BeforeEach`
- Don't rely on test execution order
- Use transactions for automatic rollback

### 2. Meaningful Test Names

```java
@Test
@DisplayName("E2E: Complete member lifecycle - create, read, update, delete")
void testCompleteMemberLifecycle()
```

### 3. Comprehensive Assertions

- Verify HTTP status codes
- Check response body structure
- Validate data in database
- Test error conditions

### 4. Performance Considerations

- Use `@Transactional` for automatic cleanup
- Batch create test data when possible
- Use appropriate page sizes
- Monitor test execution time

### 5. Error Handling

- Test both success and failure scenarios
- Verify error messages are descriptive
- Check appropriate HTTP status codes
- Validate error response structure

## Continuous Integration

### Pre-commit Checks

```bash
# Run fast unit tests
./gradlew test --tests "com.telangana.ballbadminton.service.*"
./gradlew test --tests "com.telangana.ballbadminton.controller.*"
```

### Pull Request Checks

```bash
# Run all tests including E2E
./gradlew test

# Generate coverage report
./gradlew jacocoTestReport
```

### Nightly Builds

```bash
# Run full regression suite
./gradlew test --tests "com.telangana.ballbadminton.e2e.RegressionTestSuite"

# Run performance tests
./gradlew test --tests "com.telangana.ballbadminton.e2e.PerformanceE2ETest"
```

## Troubleshooting

### Common Issues

#### 1. Testcontainers Not Starting

**Problem**: Docker containers fail to start

**Solution**:
- Ensure Docker is running
- Check Docker daemon is accessible
- Verify sufficient disk space
- Check Docker network configuration

#### 2. Slow Test Execution

**Problem**: Tests take too long to run

**Solution**:
- Use container reuse: `withReuse(true)`
- Reduce test data volume
- Use pagination for large datasets
- Check database indexes

#### 3. Flaky Tests

**Problem**: Tests pass/fail intermittently

**Solution**:
- Add explicit waits for async operations
- Ensure proper test isolation
- Check for race conditions
- Verify transaction boundaries

#### 4. Connection Pool Exhaustion

**Problem**: Tests fail with connection timeout

**Solution**:
- Increase connection pool size in test config
- Ensure connections are properly closed
- Use `@Transactional` for automatic cleanup
- Check for connection leaks

## Test Metrics

### Coverage Goals

- **Line Coverage**: > 80%
- **Branch Coverage**: > 75%
- **E2E Test Coverage**: All critical user journeys

### Performance Benchmarks

| Operation | Target | Measured |
|-----------|--------|----------|
| Single Entity GET | < 200ms | ✓ |
| List Endpoint | < 500ms | ✓ |
| Bulk Update (50) | < 2s | ✓ |
| Concurrent (50) | < 1s avg | ✓ |
| High Volume (100) | < 1s | ✓ |

## Maintenance

### Adding New E2E Tests

1. Create test class in `e2e` package
2. Extend `BaseIntegrationTest`
3. Add `@DisplayName` annotation
4. Implement test methods with clear names
5. Add to regression suite if critical
6. Update this documentation

### Updating Existing Tests

1. Maintain backward compatibility
2. Update test data if schema changes
3. Adjust assertions for new requirements
4. Update documentation
5. Run full regression suite

## References

- [Spring Boot Testing Documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.testing)
- [Testcontainers Documentation](https://www.testcontainers.org/)
- [MockMvc Documentation](https://docs.spring.io/spring-framework/docs/current/reference/html/testing.html#spring-mvc-test-framework)
- [AssertJ Documentation](https://assertj.github.io/doc/)

## Support

For questions or issues with E2E tests:
- Review this documentation
- Check test logs for detailed error messages
- Consult team members
- Create issue in project tracker

---

**Last Updated**: 2024-01-15
**Version**: 1.0.0
**Maintained By**: Telangana Ball Badminton Association Development Team
