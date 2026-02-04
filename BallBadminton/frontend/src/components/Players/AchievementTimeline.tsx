import React, { useState, useEffect } from 'react';
import styled from 'styled-components';
import { useTranslation } from 'react-i18next';
import { playersService } from '../../services/playersService';
import { Achievement } from '../../store/slices/playersSlice';
import Card from '../UI/Card';
import Button from '../UI/Button';
import LoadingSpinner from '../UI/LoadingSpinner';

const TimelineContainer = styled.div`
  position: relative;
`;

const TimelineList = styled.div`
  position: relative;
  
  &::before {
    content: '';
    position: absolute;
    left: 20px;
    top: 0;
    bottom: 0;
    width: 2px;
    background: ${({ theme }) => theme.colors.gray[200]};
  }
`;

const TimelineItem = styled.div`
  position: relative;
  padding-left: 60px;
  margin-bottom: ${({ theme }) => theme.spacing[6]};
  
  &:last-child {
    margin-bottom: 0;
  }
`;

const TimelineMarker = styled.div<{ level: 'INTERNATIONAL' | 'NATIONAL' | 'STATE' | 'DISTRICT' | 'LOCAL' }>`
  position: absolute;
  left: 12px;
  top: 8px;
  width: 16px;
  height: 16px;
  border-radius: 50%;
  border: 3px solid white;
  box-shadow: 0 0 0 2px ${({ theme }) => theme.colors.gray[200]};
  
  ${({ level, theme }) => {
    switch (level) {
      case 'INTERNATIONAL':
        return `background: ${theme.colors.secondary[500]};`;
      case 'NATIONAL':
        return `background: ${theme.colors.info[500]};`;
      case 'STATE':
        return `background: ${theme.colors.success[500]};`;
      case 'DISTRICT':
        return `background: ${theme.colors.warning[500]};`;
      default:
        return `background: ${theme.colors.gray[400]};`;
    }
  }}
`;

const AchievementCard = styled(Card)<{ level: string }>`
  transition: all ${({ theme }) => theme.transitions.base};
  
  &:hover {
    transform: translateY(-2px);
    box-shadow: ${({ theme }) => theme.shadows.lg};
  }
  
  ${({ level, theme }) => {
    if (level === 'INTERNATIONAL' || level === 'NATIONAL') {
      return `border-left: 4px solid ${theme.colors.primary[500]};`;
    }
    return '';
  }}
`;

const AchievementHeader = styled.div`
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: ${({ theme }) => theme.spacing[3]};
`;

const AchievementTitle = styled.h3`
  margin: 0;
  font-size: ${({ theme }) => theme.fontSizes.lg};
  font-weight: ${({ theme }) => theme.fontWeights.semibold};
  color: ${({ theme }) => theme.colors.gray[900]};
`;

const AchievementDate = styled.span`
  font-size: ${({ theme }) => theme.fontSizes.sm};
  color: ${({ theme }) => theme.colors.gray[500]};
  white-space: nowrap;
`;

const AchievementMeta = styled.div`
  display: flex;
  flex-wrap: wrap;
  gap: ${({ theme }) => theme.spacing[2]};
  margin-bottom: ${({ theme }) => theme.spacing[3]};
`;

const MetaTag = styled.span<{ variant?: 'level' | 'position' | 'category' }>`
  padding: ${({ theme }) => theme.spacing[1]} ${({ theme }) => theme.spacing[2]};
  border-radius: ${({ theme }) => theme.borderRadius.base};
  font-size: ${({ theme }) => theme.fontSizes.xs};
  font-weight: ${({ theme }) => theme.fontWeights.medium};
  
  ${({ variant, theme }) => {
    switch (variant) {
      case 'level':
        return `
          background: ${theme.colors.primary[100]};
          color: ${theme.colors.primary[700]};
        `;
      case 'position':
        return `
          background: ${theme.colors.success[100]};
          color: ${theme.colors.success[700]};
        `;
      case 'category':
        return `
          background: ${theme.colors.info[100]};
          color: ${theme.colors.info[700]};
        `;
      default:
        return `
          background: ${theme.colors.gray[100]};
          color: ${theme.colors.gray[700]};
        `;
    }
  }}
`;

const AchievementDescription = styled.p`
  margin: 0;
  color: ${({ theme }) => theme.colors.gray[600]};
  font-size: ${({ theme }) => theme.fontSizes.sm};
  line-height: ${({ theme }) => theme.lineHeights.relaxed};
`;

const VerificationBadge = styled.span<{ verified: boolean }>`
  display: inline-flex;
  align-items: center;
  gap: ${({ theme }) => theme.spacing[1]};
  padding: ${({ theme }) => theme.spacing[1]} ${({ theme }) => theme.spacing[2]};
  border-radius: ${({ theme }) => theme.borderRadius.base};
  font-size: ${({ theme }) => theme.fontSizes.xs};
  font-weight: ${({ theme }) => theme.fontWeights.medium};
  
  ${({ verified, theme }) => verified ? `
    background: ${theme.colors.success[100]};
    color: ${theme.colors.success[700]};
  ` : `
    background: ${theme.colors.warning[100]};
    color: ${theme.colors.warning[700]};
  `}
`;

const EmptyState = styled.div`
  text-align: center;
  padding: ${({ theme }) => theme.spacing[12]} ${({ theme }) => theme.spacing[4]};
  color: ${({ theme }) => theme.colors.gray[500]};
  
  h4 {
    margin: 0 0 ${({ theme }) => theme.spacing[2]} 0;
    color: ${({ theme }) => theme.colors.gray[700]};
    font-size: ${({ theme }) => theme.fontSizes.lg};
    font-weight: ${({ theme }) => theme.fontWeights.medium};
  }
  
  p {
    margin: 0;
    font-size: ${({ theme }) => theme.fontSizes.sm};
  }
`;

const LoadMoreButton = styled.div`
  text-align: center;
  margin-top: ${({ theme }) => theme.spacing[6]};
`;

interface AchievementTimelineProps {
  playerId: string;
  achievements?: Achievement[];
}

const AchievementTimeline: React.FC<AchievementTimelineProps> = ({
  playerId,
  achievements: initialAchievements = [],
}) => {
  const { t } = useTranslation();
  const [achievements, setAchievements] = useState<Achievement[]>(initialAchievements);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [hasMore, setHasMore] = useState(initialAchievements.length >= 5);

  useEffect(() => {
    if (initialAchievements.length === 0) {
      loadAchievements();
    }
  }, [playerId]);

  const loadAchievements = async () => {
    try {
      setIsLoading(true);
      setError(null);
      const achievementsData = await playersService.getPlayerAchievements(playerId);
      setAchievements(achievementsData);
      setHasMore(false); // All achievements loaded
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed to load achievements');
    } finally {
      setIsLoading(false);
    }
  };

  const formatDate = (dateString: string) => {
    return new Date(dateString).toLocaleDateString('en-IN', {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
    });
  };

  const formatLevel = (level: string) => {
    const levelMap: { [key: string]: string } = {
      'INTERNATIONAL': t('players.achievement.level.international'),
      'NATIONAL': t('players.achievement.level.national'),
      'STATE': t('players.achievement.level.state'),
      'DISTRICT': t('players.achievement.level.district'),
      'LOCAL': t('players.achievement.level.local'),
    };
    return levelMap[level] || level;
  };

  const formatPosition = (position: number) => {
    if (position === 1) return t('players.achievement.position.first');
    if (position === 2) return t('players.achievement.position.second');
    if (position === 3) return t('players.achievement.position.third');
    return t('players.achievement.position.nth', { position });
  };

  if (isLoading && achievements.length === 0) {
    return <LoadingSpinner text={t('players.achievements.loading')} />;
  }

  if (error) {
    return (
      <Card>
        <Card.Content>
          <div style={{ textAlign: 'center', color: '#dc2626' }}>
            <h4>{t('common.error')}</h4>
            <p>{error}</p>
            <Button onClick={loadAchievements} style={{ marginTop: '16px' }}>
              {t('common.retry')}
            </Button>
          </div>
        </Card.Content>
      </Card>
    );
  }

  if (achievements.length === 0) {
    return (
      <EmptyState>
        <h4>{t('players.achievements.empty.title')}</h4>
        <p>{t('players.achievements.empty.description')}</p>
      </EmptyState>
    );
  }

  return (
    <TimelineContainer>
      <TimelineList>
        {achievements.map((achievement) => (
          <TimelineItem key={achievement.id}>
            <TimelineMarker level={achievement.level as any} />
            
            <AchievementCard level={achievement.level}>
              <Card.Content>
                <AchievementHeader>
                  <AchievementTitle>{achievement.title}</AchievementTitle>
                  <AchievementDate>
                    {achievement.achievementDate ? formatDate(achievement.achievementDate) : t('common.notSpecified')}
                  </AchievementDate>
                </AchievementHeader>
                
                <AchievementMeta>
                  <MetaTag variant="level">
                    {formatLevel(achievement.level)}
                  </MetaTag>
                  
                  {achievement.position && (
                    <MetaTag variant="position">
                      {formatPosition(achievement.position)}
                    </MetaTag>
                  )}
                  
                  {achievement.category && (
                    <MetaTag variant="category">
                      {achievement.category}
                    </MetaTag>
                  )}
                  
                  <VerificationBadge verified={achievement.isVerified || false}>
                    {achievement.isVerified ? '✓' : '⏳'}
                    {achievement.isVerified ? t('players.achievement.verified') : t('players.achievement.pending')}
                  </VerificationBadge>
                </AchievementMeta>
                
                {achievement.description && (
                  <AchievementDescription>
                    {achievement.description}
                  </AchievementDescription>
                )}
                
                {achievement.tournamentName && (
                  <div style={{ 
                    marginTop: '12px', 
                    fontSize: '14px', 
                    color: '#6b7280',
                    fontStyle: 'italic' 
                  }}>
                    {t('players.achievement.tournament')}: {achievement.tournamentName}
                  </div>
                )}
              </Card.Content>
            </AchievementCard>
          </TimelineItem>
        ))}
      </TimelineList>
      
      {hasMore && (
        <LoadMoreButton>
          <Button 
            variant="secondary" 
            onClick={loadAchievements}
            disabled={isLoading}
          >
            {isLoading ? t('common.loading') : t('players.achievements.loadMore')}
          </Button>
        </LoadMoreButton>
      )}
    </TimelineContainer>
  );
};

export default AchievementTimeline;