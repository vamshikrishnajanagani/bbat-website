import React from 'react';
import styled, { keyframes } from 'styled-components';

const spin = keyframes`
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
`;

const SpinnerContainer = styled.div<{ fullScreen?: boolean }>`
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  min-height: ${({ fullScreen }) => fullScreen ? '100vh' : '200px'};
  ${({ fullScreen }) => fullScreen && `
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: rgba(255, 255, 255, 0.9);
    z-index: 9999;
  `}
`;

const Spinner = styled.div<{ size: string }>`
  width: ${({ size }) => {
    switch (size) {
      case 'sm': return '20px';
      case 'lg': return '60px';
      default: return '40px';
    }
  }};
  height: ${({ size }) => {
    switch (size) {
      case 'sm': return '20px';
      case 'lg': return '60px';
      default: return '40px';
    }
  }};
  border: 4px solid ${({ theme }) => theme.colors.gray[200]};
  border-top: 4px solid ${({ theme }) => theme.colors.primary[600]};
  border-radius: 50%;
  animation: ${spin} 1s linear infinite;
`;

const LoadingText = styled.p`
  margin-top: ${({ theme }) => theme.spacing[4]};
  color: ${({ theme }) => theme.colors.gray[600]};
  font-size: ${({ theme }) => theme.fontSizes.base};
`;

interface LoadingSpinnerProps {
  size?: 'sm' | 'md' | 'lg';
  fullScreen?: boolean;
  text?: string;
}

const LoadingSpinner: React.FC<LoadingSpinnerProps> = ({ 
  size = 'md', 
  fullScreen = false, 
  text 
}) => {
  return (
    <SpinnerContainer fullScreen={fullScreen}>
      <Spinner size={size} />
      {text && <LoadingText>{text}</LoadingText>}
    </SpinnerContainer>
  );
};

export default LoadingSpinner;