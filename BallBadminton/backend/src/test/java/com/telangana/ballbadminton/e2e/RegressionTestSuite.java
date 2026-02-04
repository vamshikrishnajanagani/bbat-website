package com.telangana.ballbadminton.e2e;

import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

/**
 * Regression Test Suite
 * 
 * This suite runs all end-to-end tests to ensure system stability
 * and prevent regressions when making changes.
 * 
 * Test Coverage:
 * - Member Management E2E Tests
 * - Player Management E2E Tests
 * - Tournament Management E2E Tests
 * - API Integration and Data Consistency Tests
 * - Performance and Load Tests
 * 
 * Run this suite before:
 * - Deploying to production
 * - Merging major feature branches
 * - After significant refactoring
 * 
 * @author Telangana Ball Badminton Association
 * @version 1.0.0
 */
@Suite
@SuiteDisplayName("Telangana Ball Badminton - Regression Test Suite")
@SelectPackages("com.telangana.ballbadminton.e2e")
public class RegressionTestSuite {
    // This class remains empty, it is used only as a holder for the above annotations
}
