import api from './api';

export interface ConsentRequest {
  consentType: string;
  consentGiven: boolean;
  privacyPolicyVersion: string;
}

export interface PrivacyConsent {
  id: number;
  consentType: string;
  consentGiven: boolean;
  consentDate: string;
  ipAddress: string;
  userAgent: string;
  privacyPolicyVersion: string;
  revoked: boolean;
  revokedDate?: string;
}

export interface DataExportRequest {
  id: number;
  requestDate: string;
  status: 'PENDING' | 'PROCESSING' | 'COMPLETED' | 'FAILED' | 'EXPIRED';
  exportFormat: string;
  filePath?: string;
  completedDate?: string;
  expiryDate?: string;
}

export interface DataDeletionRequest {
  id: number;
  requestDate: string;
  status: 'PENDING_VERIFICATION' | 'VERIFIED' | 'SCHEDULED' | 'PROCESSING' | 'COMPLETED' | 'CANCELLED' | 'FAILED';
  deletionType: 'FULL_ACCOUNT' | 'PERSONAL_DATA_ONLY' | 'SPECIFIC_DATA';
  reason?: string;
  scheduledDate?: string;
  completedDate?: string;
  verified: boolean;
  verifiedDate?: string;
}

export interface PrivacyPolicy {
  version: string;
  effectiveDate: string;
  content: string;
  lastUpdated: string;
}

/**
 * Privacy Service
 * Handles privacy-related API calls including consent management,
 * data export, and data deletion requests
 */
class PrivacyService {
  /**
   * Record user consent
   */
  async recordConsent(request: ConsentRequest): Promise<PrivacyConsent> {
    const response = await api.post('/privacy/consent', request);
    return response.data;
  }

  /**
   * Revoke user consent
   */
  async revokeConsent(consentId: number): Promise<PrivacyConsent> {
    const response = await api.post(`/privacy/consent/${consentId}/revoke`);
    return response.data;
  }

  /**
   * Get user's consent records
   */
  async getUserConsents(): Promise<PrivacyConsent[]> {
    const response = await api.get('/privacy/consent');
    return response.data;
  }

  /**
   * Check if user has active consent for a specific type
   */
  async checkConsent(consentType: string): Promise<boolean> {
    const response = await api.get(`/privacy/consent/check/${consentType}`);
    return response.data.hasConsent;
  }

  /**
   * Request data export
   */
  async requestDataExport(exportFormat: string = 'JSON'): Promise<DataExportRequest> {
    const response = await api.post('/privacy/data-export', { exportFormat });
    return response.data;
  }

  /**
   * Get data export request status
   */
  async getDataExportRequest(requestId: number): Promise<DataExportRequest> {
    const response = await api.get(`/privacy/data-export/${requestId}`);
    return response.data;
  }

  /**
   * Get all data export requests for user
   */
  async getUserDataExportRequests(): Promise<DataExportRequest[]> {
    const response = await api.get('/privacy/data-export');
    return response.data;
  }

  /**
   * Request data deletion
   */
  async requestDataDeletion(
    deletionType: 'FULL_ACCOUNT' | 'PERSONAL_DATA_ONLY' | 'SPECIFIC_DATA',
    reason?: string
  ): Promise<DataDeletionRequest> {
    const response = await api.post('/privacy/data-deletion', {
      deletionType,
      reason,
    });
    return response.data;
  }

  /**
   * Verify data deletion request
   */
  async verifyDataDeletionRequest(
    requestId: number,
    verificationCode: string
  ): Promise<DataDeletionRequest> {
    const response = await api.post(`/privacy/data-deletion/${requestId}/verify`, {
      verificationCode,
    });
    return response.data;
  }

  /**
   * Cancel data deletion request
   */
  async cancelDataDeletionRequest(requestId: number): Promise<DataDeletionRequest> {
    const response = await api.post(`/privacy/data-deletion/${requestId}/cancel`);
    return response.data;
  }

  /**
   * Get data deletion request status
   */
  async getDataDeletionRequest(requestId: number): Promise<DataDeletionRequest> {
    const response = await api.get(`/privacy/data-deletion/${requestId}`);
    return response.data;
  }

  /**
   * Get all data deletion requests for user
   */
  async getUserDataDeletionRequests(): Promise<DataDeletionRequest[]> {
    const response = await api.get('/privacy/data-deletion');
    return response.data;
  }

  /**
   * Get privacy policy (public endpoint)
   */
  async getPrivacyPolicy(): Promise<PrivacyPolicy> {
    const response = await api.get('/privacy/policy');
    return response.data;
  }
}

export default new PrivacyService();
