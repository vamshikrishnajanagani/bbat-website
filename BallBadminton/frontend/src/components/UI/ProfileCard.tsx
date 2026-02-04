import React from 'react';
import styled from 'styled-components';
import { useTranslation } from 'react-i18next';
import Card from './Card';
import Button from './Button';

const ProfileImage = styled.div<{ size?: 'sm' | 'md' | 'lg' }>`
  border-radius: 50%;
  background: ${({ theme }) => theme.colors.primary[100]};
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: ${({ theme }) => theme.fontWeights.bold};
  color: ${({ theme }) => theme.colors.primary[600]};
  flex-shrink: 0;
  
  ${({ size, theme }) => {
    switch (size) {
      case 'sm':
        return `
          width: 40px;
          height: 40px;
          font-size: ${theme.fontSizes.sm};
        `;
      case 'lg':
        return `
          width: 80px;
          height: 80px;
          font-size: ${theme.fontSizes.xl};
        `;
      default:
        return `
          width: 60px;
          height: 60px;
          font-size: ${theme.fontSizes.lg};
        `;
    }
  }}
`;

const ProfileHeader = styled.div<{ layout?: 'horizontal' | 'vertical' }>`
  display: flex;
  align-items: ${({ layout }) => layout === 'vertical' ? 'center' : 'flex-start'};
  flex-direction: ${({ layout }) => layout === 'vertical' ? 'column' : 'row'};
  gap: ${({ theme }) => theme.spacing[4]};
  margin-bottom: ${({ theme }) => theme.spacing[4]};
`;

const ProfileInfo = styled.div<{ layout?: 'horizontal' | 'vertical' }>`
  flex: 1;
  text-align: ${({ layout }) => layout === 'vertical' ? 'center' : 'left'};
  
  h3 {
    margin: 0 0 ${({ theme }) => theme.spacing[1]} 0;
    color: ${({ theme }) => theme.colors.gray[900]};
    font-size: ${({ theme }) => theme.fontSizes.lg};
    font-weight: ${({ theme }) => theme.fontWeights.semibold};
  }
  
  p {
    margin: 0;
    color: ${({ theme }) => theme.colors.gray[600]};
    font-size: ${({ theme }) => theme.fontSizes.sm};
  }
`;

const ProfileStats = styled.div`
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(120px, 1fr));
  gap: ${({ theme }) => theme.spacing[4]};
  margin: ${({ theme }) => theme.spacing[4]} 0;
`;

const StatItem = styled.div`
  text-align: center;
  
  .stat-value {
    display: block;
    font-size: ${({ theme }) => theme.fontSizes.xl};
    font-weight: ${({ theme }) => theme.fontWeights.bold};
    color: ${({ theme }) => theme.colors.primary[600]};
    margin-bottom: ${({ theme }) => theme.spacing[1]};
  }
  
  .stat-label {
    font-size: ${({ theme }) => theme.fontSizes.xs};
    color: ${({ theme }) => theme.colors.gray[600]};
    text-transform: uppercase;
    letter-spacing: 0.05em;
  }
`;

const ProfileDetails = styled.div`
  display: flex;
  flex-direction: column;
  gap: ${({ theme }) => theme.spacing[2]};
  
  .detail-item {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: ${({ theme }) => theme.spacing[2]} 0;
    border-bottom: 1px solid ${({ theme }) => theme.colors.gray[100]};
    
    &:last-child {
      border-bottom: none;
    }
    
    .label {
      font-weight: ${({ theme }) => theme.fontWeights.medium};
      color: ${({ theme }) => theme.colors.gray[700]};
      font-size: ${({ theme }) => theme.fontSizes.sm};
    }
    
    .value {
      color: ${({ theme }) => theme.colors.gray[900]};
      font-size: ${({ theme }) => theme.fontSizes.sm};
    }
  }
`;

const TagList = styled.div`
  display: flex;
  flex-wrap: wrap;
  gap: ${({ theme }) => theme.spacing[2]};
  margin: ${({ theme }) => theme.spacing[3]} 0;
`;

const Tag = styled.span<{ variant?: 'primary' | 'secondary' | 'success' | 'warning' }>`
  padding: ${({ theme }) => theme.spacing[1]} ${({ theme }) => theme.spacing[2]};
  border-radius: ${({ theme }) => theme.borderRadius.base};
  font-size: ${({ theme }) => theme.fontSizes.xs};
  font-weight: ${({ theme }) => theme.fontWeights.medium};
  
  ${({ variant, theme }) => {
    switch (variant) {
      case 'primary':
        return `
          background: ${theme.colors.primary[100]};
          color: ${theme.colors.primary[700]};
        `;
      case 'success':
        return `
          background: ${theme.colors.success[100]};
          color: ${theme.colors.success[700]};
        `;
      case 'warning':
        return `
          background: ${theme.colors.warning[100]};
          color: ${theme.colors.warning[700]};
        `;
      default:
        return `
          background: ${theme.colors.gray[100]};
          color: ${theme.colors.gray[700]};
        `;
    }
  }}
`;

interface ProfileCardProps {
  name: string;
  title?: string;
  subtitle?: string;
  imageUrl?: string;
  layout?: 'horizontal' | 'vertical';
  imageSize?: 'sm' | 'md' | 'lg';
  stats?: Array<{
    label: string;
    value: string | number;
  }>;
  details?: Array<{
    label: string;
    value: string;
  }>;
  tags?: Array<{
    label: string;
    variant?: 'primary' | 'secondary' | 'success' | 'warning';
  }>;
  actions?: React.ReactNode;
  onClick?: () => void;
  clickable?: boolean;
}

const ProfileCard: React.FC<ProfileCardProps> = ({
  name,
  title,
  subtitle,
  imageUrl,
  layout = 'horizontal',
  imageSize = 'md',
  stats = [],
  details = [],
  tags = [],
  actions,
  onClick,
  clickable = false,
}) => {
  const { t } = useTranslation();

  const getInitials = (name: string) => {
    return name
      .split(' ')
      .map(word => word.charAt(0))
      .join('')
      .toUpperCase()
      .slice(0, 2);
  };

  return (
    <Card clickable={clickable} onClick={onClick}>
      <ProfileHeader layout={layout}>
        <ProfileImage size={imageSize}>
          {imageUrl ? (
            <img
              src={imageUrl}
              alt={name}
              style={{
                width: '100%',
                height: '100%',
                objectFit: 'cover',
                borderRadius: '50%',
              }}
            />
          ) : (
            getInitials(name)
          )}
        </ProfileImage>
        
        <ProfileInfo layout={layout}>
          <h3>{name}</h3>
          {title && <p><strong>{title}</strong></p>}
          {subtitle && <p>{subtitle}</p>}
        </ProfileInfo>
      </ProfileHeader>

      {tags.length > 0 && (
        <TagList>
          {tags.map((tag, index) => (
            <Tag key={index} variant={tag.variant}>
              {tag.label}
            </Tag>
          ))}
        </TagList>
      )}

      {stats.length > 0 && (
        <ProfileStats>
          {stats.map((stat, index) => (
            <StatItem key={index}>
              <span className="stat-value">{stat.value}</span>
              <span className="stat-label">{stat.label}</span>
            </StatItem>
          ))}
        </ProfileStats>
      )}

      {details.length > 0 && (
        <ProfileDetails>
          {details.map((detail, index) => (
            <div key={index} className="detail-item">
              <span className="label">{detail.label}</span>
              <span className="value">{detail.value}</span>
            </div>
          ))}
        </ProfileDetails>
      )}

      {actions && (
        <Card.Footer withBorder>
          <Card.Actions>
            {actions}
          </Card.Actions>
        </Card.Footer>
      )}
    </Card>
  );
};

export default ProfileCard;