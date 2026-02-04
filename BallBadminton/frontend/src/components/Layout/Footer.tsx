import React from 'react';
import { Link } from 'react-router-dom';
import styled from 'styled-components';
import { useTranslation } from 'react-i18next';

const FooterContainer = styled.footer`
  background: ${({ theme }) => theme.colors.gray[800]};
  color: white;
  padding: ${({ theme }) => theme.spacing[8]} 0 ${({ theme }) => theme.spacing[4]};
  margin-top: auto;
`;

const FooterContent = styled.div`
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 ${({ theme }) => theme.spacing[4]};
  
  @media (min-width: ${({ theme }) => theme.breakpoints.sm}) {
    padding: 0 ${({ theme }) => theme.spacing[6]};
  }
`;

const FooterGrid = styled.div`
  display: grid;
  grid-template-columns: 1fr;
  gap: ${({ theme }) => theme.spacing[8]};
  margin-bottom: ${({ theme }) => theme.spacing[8]};
  
  @media (min-width: ${({ theme }) => theme.breakpoints.md}) {
    grid-template-columns: repeat(3, 1fr);
  }
`;

const FooterSection = styled.div`
  h3 {
    font-size: ${({ theme }) => theme.fontSizes.lg};
    font-weight: ${({ theme }) => theme.fontWeights.semibold};
    margin-bottom: ${({ theme }) => theme.spacing[4]};
    color: white;
  }
`;

const FooterLink = styled(Link)`
  display: block;
  color: ${({ theme }) => theme.colors.gray[300]};
  text-decoration: none;
  margin-bottom: ${({ theme }) => theme.spacing[2]};
  transition: color ${({ theme }) => theme.transitions.fast};
  
  &:hover {
    color: white;
    text-decoration: none;
  }
  
  &:focus {
    outline: 2px solid white;
    outline-offset: 2px;
  }
`;

const FooterBottom = styled.div`
  border-top: 1px solid ${({ theme }) => theme.colors.gray[700]};
  padding-top: ${({ theme }) => theme.spacing[4]};
  text-align: center;
  color: ${({ theme }) => theme.colors.gray[400]};
  font-size: ${({ theme }) => theme.fontSizes.sm};
`;

const Footer: React.FC = () => {
  const { t } = useTranslation();

  return (
    <FooterContainer role="contentinfo">
      <FooterContent>
        <FooterGrid>
          <FooterSection>
            <h3>Quick Links</h3>
            <FooterLink to="/">Home</FooterLink>
            <FooterLink to="/members">Members</FooterLink>
            <FooterLink to="/players">Players</FooterLink>
            <FooterLink to="/tournaments">Tournaments</FooterLink>
          </FooterSection>
          
          <FooterSection>
            <h3>Information</h3>
            <FooterLink to="/districts">Districts</FooterLink>
            <FooterLink to="/news">News</FooterLink>
            <FooterLink to="/media">Media</FooterLink>
            <FooterLink to="/contact">Contact</FooterLink>
          </FooterSection>
          
          <FooterSection>
            <h3>Legal</h3>
            <FooterLink to="/privacy-policy">Privacy Policy</FooterLink>
            <FooterLink to="/privacy-settings">Privacy Settings</FooterLink>
          </FooterSection>
        </FooterGrid>
        
        <FooterBottom>
          <p>&copy; 2024 Telangana Ball Badminton Association. All rights reserved.</p>
        </FooterBottom>
      </FooterContent>
    </FooterContainer>
  );
};

export default Footer;