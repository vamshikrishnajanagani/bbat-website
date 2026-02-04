import React from 'react';
import styled from 'styled-components';

const SkipLinkButton = styled.a`
  position: absolute;
  left: -9999px;
  z-index: 999;
  padding: 1em;
  background-color: ${({ theme }) => theme.colors.primary[600]};
  color: white;
  text-decoration: none;
  font-weight: ${({ theme }) => theme.fontWeights.semibold};
  border-radius: ${({ theme }) => theme.borderRadius.base};
  
  &:focus {
    left: 1em;
    top: 1em;
    outline: 2px solid white;
    outline-offset: 2px;
  }
`;

interface SkipLinkProps {
  href: string;
  children: React.ReactNode;
}

const SkipLink: React.FC<SkipLinkProps> = ({ href, children }) => {
  return (
    <SkipLinkButton href={href}>
      {children}
    </SkipLinkButton>
  );
};

export default SkipLink;