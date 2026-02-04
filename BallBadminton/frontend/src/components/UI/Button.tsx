import React from 'react';
import styled, { css } from 'styled-components';

interface ButtonProps {
  variant?: 'primary' | 'secondary' | 'success' | 'warning' | 'error' | 'outline';
  size?: 'sm' | 'md' | 'lg';
  fullWidth?: boolean;
  disabled?: boolean;
  loading?: boolean;
  children: React.ReactNode;
  onClick?: () => void;
  type?: 'button' | 'submit' | 'reset';
  as?: any;
  to?: string;
  style?: React.CSSProperties;
  className?: string;
}

const getVariantStyles = (variant: string) => {
  const variants = {
    primary: css`
      background: ${({ theme }) => theme.colors.primary[600]};
      color: white;
      border: 1px solid ${({ theme }) => theme.colors.primary[600]};
      
      &:hover:not(:disabled) {
        background: ${({ theme }) => theme.colors.primary[700]};
        border-color: ${({ theme }) => theme.colors.primary[700]};
      }
      
      &:focus {
        box-shadow: 0 0 0 3px ${({ theme }) => theme.colors.primary[200]};
      }
    `,
    secondary: css`
      background: ${({ theme }) => theme.colors.gray[600]};
      color: white;
      border: 1px solid ${({ theme }) => theme.colors.gray[600]};
      
      &:hover:not(:disabled) {
        background: ${({ theme }) => theme.colors.gray[700]};
        border-color: ${({ theme }) => theme.colors.gray[700]};
      }
      
      &:focus {
        box-shadow: 0 0 0 3px ${({ theme }) => theme.colors.gray[200]};
      }
    `,
    success: css`
      background: ${({ theme }) => theme.colors.success[600]};
      color: white;
      border: 1px solid ${({ theme }) => theme.colors.success[600]};
      
      &:hover:not(:disabled) {
        background: ${({ theme }) => theme.colors.success[700]};
        border-color: ${({ theme }) => theme.colors.success[700]};
      }
      
      &:focus {
        box-shadow: 0 0 0 3px ${({ theme }) => theme.colors.success[200]};
      }
    `,
    warning: css`
      background: ${({ theme }) => theme.colors.warning[600]};
      color: white;
      border: 1px solid ${({ theme }) => theme.colors.warning[600]};
      
      &:hover:not(:disabled) {
        background: ${({ theme }) => theme.colors.warning[700]};
        border-color: ${({ theme }) => theme.colors.warning[700]};
      }
      
      &:focus {
        box-shadow: 0 0 0 3px ${({ theme }) => theme.colors.warning[200]};
      }
    `,
    error: css`
      background: ${({ theme }) => theme.colors.error[600]};
      color: white;
      border: 1px solid ${({ theme }) => theme.colors.error[600]};
      
      &:hover:not(:disabled) {
        background: ${({ theme }) => theme.colors.error[700]};
        border-color: ${({ theme }) => theme.colors.error[700]};
      }
      
      &:focus {
        box-shadow: 0 0 0 3px ${({ theme }) => theme.colors.error[200]};
      }
    `,
    outline: css`
      background: transparent;
      color: ${({ theme }) => theme.colors.primary[600]};
      border: 1px solid ${({ theme }) => theme.colors.primary[600]};
      
      &:hover:not(:disabled) {
        background: ${({ theme }) => theme.colors.primary[600]};
        color: white;
      }
      
      &:focus {
        box-shadow: 0 0 0 3px ${({ theme }) => theme.colors.primary[200]};
      }
    `,
  };
  
  return variants[variant as keyof typeof variants] || variants.primary;
};

const getSizeStyles = (size: string) => {
  const sizes = {
    sm: css`
      padding: ${({ theme }) => theme.spacing[2]} ${({ theme }) => theme.spacing[3]};
      font-size: ${({ theme }) => theme.fontSizes.sm};
    `,
    md: css`
      padding: ${({ theme }) => theme.spacing[3]} ${({ theme }) => theme.spacing[4]};
      font-size: ${({ theme }) => theme.fontSizes.base};
    `,
    lg: css`
      padding: ${({ theme }) => theme.spacing[4]} ${({ theme }) => theme.spacing[6]};
      font-size: ${({ theme }) => theme.fontSizes.lg};
    `,
  };
  
  return sizes[size as keyof typeof sizes] || sizes.md;
};

const StyledButton = styled.button<ButtonProps>`
  display: inline-flex;
  align-items: center;
  justify-content: center;
  font-family: ${({ theme }) => theme.fonts.primary};
  font-weight: ${({ theme }) => theme.fontWeights.medium};
  border-radius: ${({ theme }) => theme.borderRadius.md};
  cursor: pointer;
  transition: all ${({ theme }) => theme.transitions.fast};
  text-decoration: none;
  
  ${({ variant = 'primary' }) => getVariantStyles(variant)}
  ${({ size = 'md' }) => getSizeStyles(size)}
  
  ${({ fullWidth }) => fullWidth && css`
    width: 100%;
  `}
  
  &:disabled {
    opacity: 0.6;
    cursor: not-allowed;
  }
  
  ${({ loading }) => loading && css`
    cursor: not-allowed;
    
    &::before {
      content: '';
      display: inline-block;
      width: 16px;
      height: 16px;
      margin-right: ${({ theme }) => theme.spacing[2]};
      border: 2px solid transparent;
      border-top: 2px solid currentColor;
      border-radius: 50%;
      animation: spin 1s linear infinite;
    }
    
    @keyframes spin {
      to {
        transform: rotate(360deg);
      }
    }
  `}
`;

const Button: React.FC<ButtonProps> = ({
  children,
  loading,
  disabled,
  ...props
}) => {
  return (
    <StyledButton
      disabled={disabled || loading}
      {...props}
    >
      {children}
    </StyledButton>
  );
};

export default Button;