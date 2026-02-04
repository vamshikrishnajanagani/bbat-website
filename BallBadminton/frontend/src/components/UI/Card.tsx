import React from 'react';
import styled from 'styled-components';

const CardContainer = styled.div<{
  variant?: 'default' | 'elevated' | 'outlined';
  clickable?: boolean;
  padding?: 'sm' | 'md' | 'lg';
}>`
  background: white;
  border-radius: ${({ theme }) => theme.borderRadius.lg};
  transition: all ${({ theme }) => theme.transitions.base};
  
  ${({ variant, theme }) => {
    switch (variant) {
      case 'elevated':
        return `
          box-shadow: ${theme.shadows.lg};
          border: none;
        `;
      case 'outlined':
        return `
          border: 1px solid ${theme.colors.gray[200]};
          box-shadow: none;
        `;
      default:
        return `
          box-shadow: ${theme.shadows.sm};
          border: 1px solid ${theme.colors.gray[200]};
        `;
    }
  }}
  
  ${({ clickable, theme }) => clickable && `
    cursor: pointer;
    
    &:hover {
      box-shadow: ${theme.shadows.md};
      transform: translateY(-2px);
    }
  `}
  
  ${({ padding, theme }) => {
    switch (padding) {
      case 'sm':
        return `padding: ${theme.spacing[4]};`;
      case 'lg':
        return `padding: ${theme.spacing[8]};`;
      default:
        return `padding: ${theme.spacing[6]};`;
    }
  }}
`;

const CardHeader = styled.div<{ withBorder?: boolean }>`
  margin-bottom: ${({ theme }) => theme.spacing[4]};
  
  ${({ withBorder, theme }) => withBorder && `
    padding-bottom: ${theme.spacing[4]};
    border-bottom: 1px solid ${theme.colors.gray[200]};
  `}
`;

const CardTitle = styled.h3<{ size?: 'sm' | 'md' | 'lg' }>`
  margin: 0 0 ${({ theme }) => theme.spacing[2]} 0;
  color: ${({ theme }) => theme.colors.gray[900]};
  font-weight: ${({ theme }) => theme.fontWeights.semibold};
  
  ${({ size, theme }) => {
    switch (size) {
      case 'sm':
        return `font-size: ${theme.fontSizes.base};`;
      case 'lg':
        return `font-size: ${theme.fontSizes.xl};`;
      default:
        return `font-size: ${theme.fontSizes.lg};`;
    }
  }}
`;

const CardSubtitle = styled.p`
  margin: 0;
  color: ${({ theme }) => theme.colors.gray[600]};
  font-size: ${({ theme }) => theme.fontSizes.sm};
`;

const CardContent = styled.div`
  color: ${({ theme }) => theme.colors.gray[700]};
  line-height: 1.6;
`;

const CardFooter = styled.div<{ withBorder?: boolean }>`
  margin-top: ${({ theme }) => theme.spacing[4]};
  
  ${({ withBorder, theme }) => withBorder && `
    padding-top: ${theme.spacing[4]};
    border-top: 1px solid ${theme.colors.gray[200]};
  `}
`;

const CardActions = styled.div`
  display: flex;
  align-items: center;
  gap: ${({ theme }) => theme.spacing[3]};
  flex-wrap: wrap;
`;

interface CardProps {
  children: React.ReactNode;
  variant?: 'default' | 'elevated' | 'outlined';
  clickable?: boolean;
  padding?: 'sm' | 'md' | 'lg';
  onClick?: () => void;
  className?: string;
}

interface CardHeaderProps {
  children: React.ReactNode;
  withBorder?: boolean;
}

interface CardTitleProps {
  children: React.ReactNode;
  size?: 'sm' | 'md' | 'lg';
}

interface CardSubtitleProps {
  children: React.ReactNode;
}

interface CardContentProps {
  children: React.ReactNode;
}

interface CardFooterProps {
  children: React.ReactNode;
  withBorder?: boolean;
}

interface CardActionsProps {
  children: React.ReactNode;
}

const Card: React.FC<CardProps> & {
  Header: React.FC<CardHeaderProps>;
  Title: React.FC<CardTitleProps>;
  Subtitle: React.FC<CardSubtitleProps>;
  Content: React.FC<CardContentProps>;
  Footer: React.FC<CardFooterProps>;
  Actions: React.FC<CardActionsProps>;
} = ({ children, variant = 'default', clickable = false, padding = 'md', onClick, className }) => {
  return (
    <CardContainer
      variant={variant}
      clickable={clickable}
      padding={padding}
      onClick={onClick}
      className={className}
    >
      {children}
    </CardContainer>
  );
};

Card.Header = ({ children, withBorder = false }) => (
  <CardHeader withBorder={withBorder}>{children}</CardHeader>
);

Card.Title = ({ children, size = 'md' }) => (
  <CardTitle size={size}>{children}</CardTitle>
);

Card.Subtitle = ({ children }) => (
  <CardSubtitle>{children}</CardSubtitle>
);

Card.Content = ({ children }) => (
  <CardContent>{children}</CardContent>
);

Card.Footer = ({ children, withBorder = false }) => (
  <CardFooter withBorder={withBorder}>{children}</CardFooter>
);

Card.Actions = ({ children }) => (
  <CardActions>{children}</CardActions>
);

export default Card;