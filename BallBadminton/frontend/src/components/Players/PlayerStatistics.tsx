import React from 'react';
import styled from 'styled-components';
import { useTranslation } from 'react-i18next';
import { PlayerStatistics as PlayerStatsType } from '../../store/slices/playersSlice';
import Card from '../UI/Card';

const StatsContainer = styled.div`
  display: grid;
  gap: ${({ theme }) => theme.spacing[6]};
  
  @media (min-width: ${({ theme }) => theme.breakpoints.md}) {
    grid-template-columns: repeat(2, 1fr);
  }
`;

const StatsGrid = styled.div`
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: ${({ theme }) => theme.spacing[4]};
`;

const StatCard = styled.div`
  text-align: center;
  padding: ${({ theme }) => theme.spacing[4]};
  background: ${({ theme }) => theme.colors.gray[50]};
  border-radius: ${({ theme }) => theme.borderRadius.lg};
  border: 1px solid ${({ theme }) => theme.colors.gray[200]};
`;

const StatValue = styled.div<{ highlight?: boolean }>`
  font-size: ${({ theme }) => theme.fontSizes['2xl']};
  font-weight: ${({ theme }) => theme.fontWeights.bold};
  color: ${({ highlight, theme }) => 
    highlight ? theme.colors.primary[600] : theme.colors.gray[900]};
  margin-bottom: ${({ theme }) => theme.spacing[1]};
`;

const StatLabel = styled.div`
  font-size: ${({ theme }) => theme.fontSizes.sm};
  color: ${({ theme }) => theme.colors.gray[600]};
  text-transform: uppercase;
  letter-spacing: 0.05em;
`;

const ProgressBar = styled.div`
  width: 100%;
  height: 8px;
  background: ${({ theme }) => theme.colors.gray[200]};
  border-radius: ${({ theme }) => theme.borderRadius.full};
  overflow: hidden;
  margin: ${({ theme }) => theme.spacing[2]} 0;
`;

const ProgressFill = styled.div<{ percentage: number }>`
  height: 100%;
  width: ${({ percentage }) => Math.min(percentage, 100)}%;
  background: linear-gradient(90deg, 
    ${({ theme }) => theme.colors.primary[500]} 0%, 
    ${({ theme }) => theme.colors.primary[600]} 100%);
  transition: width ${({ theme }) => theme.transitions.base};
`;

const RankingSection = styled.div`
  grid-column: 1 / -1;
`;

const RankingCard = styled(Card)`
  background: linear-gradient(135deg, 
    ${({ theme }) => theme.colors.primary[50]} 0%, 
    ${({ theme }) => theme.colors.info[50]} 100%);
`;

const RankingContent = styled.div`
  display: flex;
  justify-content: space-around;
  align-items: center;
  text-align: center;
`;

const RankingItem = styled.div`
  .rank-value {
    font-size: ${({ theme }) => theme.fontSizes['3xl']};
    font-weight: ${({ theme }) => theme.fontWeights.bold};
    color: ${({ theme }) => theme.colors.primary[600]};
    margin-bottom: ${({ theme }) => theme.spacing[1]};
  }
  
  .rank-label {
    font-size: ${({ theme }) => theme.fontSizes.sm};
    color: ${({ theme }) => theme.colors.gray[600]};
    text-transform: uppercase;
    letter-spacing: 0.05em;
  }
`;

const PerformanceIndicator = styled.div<{ trend: 'up' | 'down' | 'stable' }>`
  display: inline-flex;
  align-items: center;
  gap: ${({ theme }) => theme.spacing[1]};
  padding: ${({ theme }) => theme.spacing[1]} ${({ theme }) => theme.spacing[2]};
  border-radius: ${({ theme }) => theme.borderRadius.base};
  font-size: ${({ theme }) => theme.fontSizes.xs};
  font-weight: ${({ theme }) => theme.fontWeights.medium};
  
  ${({ trend, theme }) => {
    switch (trend) {
      case 'up':
        return `
          background: ${theme.colors.success[100]};
          color: ${theme.colors.success[700]};
        `;
      case 'down':
        return `
          background: ${theme.colors.error[100]};
          color: ${theme.colors.error[700]};
        `;
      default:
        return `
          background: ${theme.colors.gray[100]};
          color: ${theme.colors.gray[700]};
        `;
    }
  }}
`;

const EmptyState = styled.div`
  text-align: center;
  padding: ${({ theme }) => theme.spacing[12]} ${({ theme }) => theme.spacing[4]};
  color: ${({ theme }) => theme.colors.gray[500]};
  grid-column: 1 / -1;
  
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

interface PlayerStatisticsProps {
  playerId: string;
  statistics?: PlayerStatsType;
}

const PlayerStatistics: React.FC<PlayerStatisticsProps> = ({
  playerId,
  statistics,
}) => {
  const { t } = useTranslation();

  if (!statistics) {
    return (
      <EmptyState>
        <h4>{t('players.statistics.empty.title')}</h4>
        <p>{t('players.statistics.empty.description')}</p>
      </EmptyState>
    );
  }

  const winPercentage = statistics.winPercentage || 0;
  const matchesPlayed = statistics.matchesPlayed || 0;
  const matchesWon = statistics.matchesWon || 0;
  const tournamentsParticipated = statistics.tournamentsParticipated || 0;
  const tournamentsWon = statistics.tournamentsWon || 0;
  const currentRanking = statistics.currentRanking;
  const bestRanking = statistics.bestRanking;
  const totalPoints = statistics.totalPoints || 0;

  const tournamentWinRate = tournamentsParticipated > 0 
    ? (tournamentsWon / tournamentsParticipated) * 100 
    : 0;

  const getRankingTrend = () => {
    if (!currentRanking || !bestRanking) return 'stable';
    if (currentRanking < bestRanking) return 'up'; // Lower number = better ranking
    if (currentRanking > bestRanking) return 'down';
    return 'stable';
  };

  return (
    <StatsContainer>
      {/* Match Statistics */}
      <Card>
        <Card.Header>
          <Card.Title>{t('players.statistics.matchStats')}</Card.Title>
        </Card.Header>
        <Card.Content>
          <StatsGrid>
            <StatCard>
              <StatValue highlight>{matchesPlayed}</StatValue>
              <StatLabel>{t('players.statistics.matchesPlayed')}</StatLabel>
            </StatCard>
            
            <StatCard>
              <StatValue highlight>{matchesWon}</StatValue>
              <StatLabel>{t('players.statistics.matchesWon')}</StatLabel>
            </StatCard>
            
            <StatCard style={{ gridColumn: '1 / -1' }}>
              <StatValue highlight>{winPercentage.toFixed(1)}%</StatValue>
              <StatLabel>{t('players.statistics.winPercentage')}</StatLabel>
              <ProgressBar>
                <ProgressFill percentage={winPercentage} />
              </ProgressBar>
            </StatCard>
          </StatsGrid>
        </Card.Content>
      </Card>

      {/* Tournament Statistics */}
      <Card>
        <Card.Header>
          <Card.Title>{t('players.statistics.tournamentStats')}</Card.Title>
        </Card.Header>
        <Card.Content>
          <StatsGrid>
            <StatCard>
              <StatValue>{tournamentsParticipated}</StatValue>
              <StatLabel>{t('players.statistics.tournamentsParticipated')}</StatLabel>
            </StatCard>
            
            <StatCard>
              <StatValue highlight>{tournamentsWon}</StatValue>
              <StatLabel>{t('players.statistics.tournamentsWon')}</StatLabel>
            </StatCard>
            
            <StatCard style={{ gridColumn: '1 / -1' }}>
              <StatValue>{tournamentWinRate.toFixed(1)}%</StatValue>
              <StatLabel>{t('players.statistics.tournamentWinRate')}</StatLabel>
              <ProgressBar>
                <ProgressFill percentage={tournamentWinRate} />
              </ProgressBar>
            </StatCard>
          </StatsGrid>
        </Card.Content>
      </Card>

      {/* Ranking Information */}
      {(currentRanking || bestRanking || totalPoints > 0) && (
        <RankingSection>
          <RankingCard>
            <Card.Header>
              <Card.Title>{t('players.statistics.ranking')}</Card.Title>
            </Card.Header>
            <Card.Content>
              <RankingContent>
                {currentRanking && (
                  <RankingItem>
                    <div className="rank-value">
                      #{currentRanking}
                      <PerformanceIndicator trend={getRankingTrend()}>
                        {getRankingTrend() === 'up' && '↗'}
                        {getRankingTrend() === 'down' && '↘'}
                        {getRankingTrend() === 'stable' && '→'}
                      </PerformanceIndicator>
                    </div>
                    <div className="rank-label">{t('players.statistics.currentRanking')}</div>
                  </RankingItem>
                )}
                
                {bestRanking && (
                  <RankingItem>
                    <div className="rank-value">#{bestRanking}</div>
                    <div className="rank-label">{t('players.statistics.bestRanking')}</div>
                  </RankingItem>
                )}
                
                {totalPoints > 0 && (
                  <RankingItem>
                    <div className="rank-value">{totalPoints.toLocaleString()}</div>
                    <div className="rank-label">{t('players.statistics.totalPoints')}</div>
                  </RankingItem>
                )}
              </RankingContent>
            </Card.Content>
          </RankingCard>
        </RankingSection>
      )}

      {/* Performance Summary */}
      <div style={{ gridColumn: '1 / -1' }}>
        <Card>
          <Card.Header>
            <Card.Title>{t('players.statistics.performanceSummary')}</Card.Title>
          </Card.Header>
          <Card.Content>
          <div style={{ 
            display: 'grid', 
            gridTemplateColumns: 'repeat(auto-fit, minmax(150px, 1fr))', 
            gap: '16px',
            textAlign: 'center' 
          }}>
            <div>
              <div style={{ fontSize: '18px', fontWeight: 'bold', color: '#059669' }}>
                {matchesWon > matchesPlayed * 0.7 ? t('players.statistics.performance.excellent') :
                 matchesWon > matchesPlayed * 0.5 ? t('players.statistics.performance.good') :
                 matchesWon > matchesPlayed * 0.3 ? t('players.statistics.performance.average') :
                 t('players.statistics.performance.needsImprovement')}
              </div>
              <div style={{ fontSize: '14px', color: '#6b7280' }}>
                {t('players.statistics.overallPerformance')}
              </div>
            </div>
            
            <div>
              <div style={{ fontSize: '18px', fontWeight: 'bold', color: '#3b82f6' }}>
                {matchesPlayed > 50 ? t('players.statistics.experience.veteran') :
                 matchesPlayed > 20 ? t('players.statistics.experience.experienced') :
                 matchesPlayed > 5 ? t('players.statistics.experience.developing') :
                 t('players.statistics.experience.newcomer')}
              </div>
              <div style={{ fontSize: '14px', color: '#6b7280' }}>
                {t('players.statistics.experienceLevel')}
              </div>
            </div>
            
            <div>
              <div style={{ fontSize: '18px', fontWeight: 'bold', color: '#8b5cf6' }}>
                {tournamentsWon > 5 ? t('players.statistics.tournament.champion') :
                 tournamentsWon > 2 ? t('players.statistics.tournament.winner') :
                 tournamentsWon > 0 ? t('players.statistics.tournament.participant') :
                 t('players.statistics.tournament.aspiring')}
              </div>
              <div style={{ fontSize: '14px', color: '#6b7280' }}>
                {t('players.statistics.tournamentStatus')}
              </div>
            </div>
          </div>
          </Card.Content>
        </Card>
      </div>
    </StatsContainer>
  );
};

export default PlayerStatistics;