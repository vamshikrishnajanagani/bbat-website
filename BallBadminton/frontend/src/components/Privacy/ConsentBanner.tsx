import React, { useEffect, useState } from 'react';
import styled from 'styled-components';
import { Link } from 'react-router-dom';
import privacyService from '../../services/privacyService';

const Banner = styled.div<{ show: boolean }>`
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background-color: white;
  border-top: 2px solid ${props => props.theme.colors.primary};
  box-shadow: 0 -2px 10px rgba(0, 0, 0, 0.1);
  padding: 1.5rem;
  z-index: 1000;
  transform: translateY(${props => (props.show ? '0' : '100%')});
  transition: transform 0.3s ease-in-out;
`;

const Content = styled.div`
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 2rem;

  @media (max-width: 768px) {
    flex-direction: column;
    gap: 1rem;
  }
`;

const Message = styled.div`
  flex: 1;
  color: ${props => props.theme.colors.gray[900]};
  line-height: 1.6;

  a {
    color: ${props => props.theme.colors.primary};
    text-decoration: underline;
  }
`;

const ButtonGroup = styled.div`
  display: flex;
  gap: 1rem;

  @media (max-width: 768px) {
    width: 100%;
    flex-direction: column;
  }
`;

const Button = styled.button<{ variant?: 'primary' | 'secondary' }>`
  padding: 0.75rem 1.5rem;
  border: none;
  border-radius: 4px;
  font-size: 1rem;
  cursor: pointer;
  transition: opacity 0.2s;
  white-space: nowrap;

  ${props =>
    props.variant === 'secondary'
      ? `
    background-color: transparent;
    color: ${props.theme.colors.primary};
    border: 1px solid ${props.theme.colors.primary};
  `
      : `
    background-color: ${props.theme.colors.primary};
    color: white;
  `}

  &:hover {
    opacity: 0.9;
  }

  @media (max-width: 768px) {
    width: 100%;
  }
`;

interface ConsentBannerProps {
  privacyPolicyVersion?: string;
}

/**
 * Consent Banner Component
 * Displays a cookie/privacy consent banner for first-time visitors
 */
const ConsentBanner: React.FC<ConsentBannerProps> = ({ privacyPolicyVersion = '1.0.0' }) => {
  const [show, setShow] = useState(false);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    checkConsentStatus();
  }, []);

  const checkConsentStatus = async () => {
    try {
      // Check if user is authenticated
      const token = localStorage.getItem('authToken');
      if (!token) {
        // For non-authenticated users, check localStorage
        const localConsent = localStorage.getItem('privacyConsent');
        if (!localConsent) {
          setShow(true);
        }
        return;
      }

      // For authenticated users, check with API
      const hasConsent = await privacyService.checkConsent('PRIVACY_POLICY');
      if (!hasConsent) {
        setShow(true);
      }
    } catch (error) {
      console.error('Error checking consent status:', error);
      // Show banner on error to be safe
      setShow(true);
    }
  };

  const handleAccept = async () => {
    setLoading(true);
    try {
      const token = localStorage.getItem('authToken');
      
      if (token) {
        // For authenticated users, record consent via API
        await privacyService.recordConsent({
          consentType: 'PRIVACY_POLICY',
          consentGiven: true,
          privacyPolicyVersion,
        });
      } else {
        // For non-authenticated users, store in localStorage
        localStorage.setItem('privacyConsent', JSON.stringify({
          consentGiven: true,
          consentDate: new Date().toISOString(),
          privacyPolicyVersion,
        }));
      }
      
      setShow(false);
    } catch (error) {
      console.error('Error recording consent:', error);
      // Still hide banner even if API call fails
      localStorage.setItem('privacyConsent', JSON.stringify({
        consentGiven: true,
        consentDate: new Date().toISOString(),
        privacyPolicyVersion,
      }));
      setShow(false);
    } finally {
      setLoading(false);
    }
  };

  const handleDecline = () => {
    // Store decline in localStorage
    localStorage.setItem('privacyConsent', JSON.stringify({
      consentGiven: false,
      consentDate: new Date().toISOString(),
      privacyPolicyVersion,
    }));
    setShow(false);
  };

  return (
    <Banner show={show}>
      <Content>
        <Message>
          We use cookies and similar technologies to enhance your experience on our website. 
          By continuing to use this site, you consent to our use of cookies and agree to our{' '}
          <Link to="/privacy-policy">Privacy Policy</Link>.
        </Message>
        <ButtonGroup>
          <Button variant="secondary" onClick={handleDecline} disabled={loading}>
            Decline
          </Button>
          <Button onClick={handleAccept} disabled={loading}>
            {loading ? 'Processing...' : 'Accept'}
          </Button>
        </ButtonGroup>
      </Content>
    </Banner>
  );
};

export default ConsentBanner;
