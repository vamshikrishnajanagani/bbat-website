import React, { useState, useEffect } from 'react';
import styled from 'styled-components';
import { useTranslation } from 'react-i18next';
import { playersService } from '../../services/playersService';
import { Player } from '../../store/slices/playersSlice';
import DataTable, { Column } from '../UI/DataTable';
import ProfileCard from '../UI/ProfileCard';
import Button from '../UI/Button';
import LoadingSpinner from '../UI/LoadingSpinner';
import Card from '../UI/Card';

const RankingsContainer = styled.div`
  max-width: 1200px;
  margin: 0 auto;
  padding: ${({ theme }) => theme.spacing[6]};
`;

const RankingsHeader = styled.div`
  margin-bottom: ${({ theme }) => theme.spacing[8]};
  text-align: center;
`;

const RankingsTitle = styled.h1`
  font-size: ${({ theme }) => theme.fontSizes['3xl']};
  font-weight: ${({ theme }) => theme.fontWeights.bold};
  color: ${({ theme }) => theme.colors.gray[900]};
  margin-bottom: ${({ theme }) => theme.spacing[4]};
`;

const CategoryTabs = styled.div`
  display: flex;
  justify-content: center;
  margin-bottom: ${({ theme }) => theme.spacing[6]};
  border-bottom: 1px solid ${({ theme }) => theme.colors.gray[200]};
`;

const CategoryTab = styled.button<{ active: boolean }>`
  padding: ${({ theme }) => theme.spacing[3]} ${({ theme }) => theme.spacing[6]};
  border: none;
  background: none;
  font-size: ${({ theme }) => theme.fontSizes.base};
  font-weight: ${({ theme }) => theme.fontWeights.medium};
  color: ${({ active, theme }) => active ? theme.colors.primary[600] : theme.colors.gray[600]};
  border-bottom: 2px solid ${({ active, theme }) => active ? theme.colors.primary[600] : 'transparent'};
  cursor: pointer;
  transition: all ${({ theme }) => theme.transitions.fast};
  
  &:hover {
    color: ${({ theme }) => theme.colors.primary[600]};
  }
`;

const ViewToggle = styled.div`
  display: flex;
  justify-content: center;
  margin-bottom: ${({ theme }) => theme.spacing[6]};
  gap: ${({ theme }) => theme.spacing[2]};
`;

const TopPlayersSection = styled.div`
  margin-bottom: ${({ theme }) => theme.spacing[8]};
`;

const PodiumContainer = styled.div`
  display: flex;
  justify-content: center;
  align-items: end;
  gap: ${({ theme }) => theme.spacing[4]};
  margin-bottom: ${({ theme }) => theme.spacing[8]};
  
  @media (max-width: ${({ theme }) => theme.breakpoints.md}) {
    flex-direction: column;
    align-items: center;
  }
`;

const PodiumPosition = styled.div<{ position: number }>`
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  
  ${({ position }) => {
    switch (position) {
      case 1:
        return 'order: 2;';
      case 2:
        return 'order: 1;';
      case 3:
        return 'order: 3;';
      default:
        return '';
    }
  }}
  
  @media (max-width: ${({ theme }) => theme.breakpoints.md}) {
    order: ${({ position }) => position} !important;
  }
`;

const PodiumStep = styled.div<{ position: number }>`
  width: 120px;
  background: ${({ position, theme }) => {
    switch (position) {
      case 1: return `linear-gradient(135deg, ${theme.colors.warning[400]}, ${theme.colors.warning[600]})`;
      case 2: return `linear-gradient(135deg, ${theme.colors.gray[300]}, ${theme.colors.gray[500]})`;
      case 3: return `linear-gradient(135deg, ${theme.colors.warning[400]}, ${theme.colors.warning[600]})`;
      default: return theme.colors.gray[200];
    }
  }};
  height: ${({ position }) => {
    switch (position) {
      case 1: return '100px';
      case 2: return '80px';
      case 3: return '60px';
      default: return '40px';
    }
  }};
  border-radius: ${({ theme }) => theme.borderRadius.lg} ${({ theme }) => theme.borderRadius.lg} 0 0;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: ${({ theme }) => theme.fontSizes.xl};
  font-weight: ${({ theme }) => theme.fontWeights.bold};
  color: white;
  margin-top: ${({ theme }) => theme.spacing[4]};
`;

const PlayerAvatar = styled.div`
  width: 80px;
  height: 80px;
  border-radius: 50%;
  background: ${({ theme }) => theme.colors.primary[100]};
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: ${({ theme }) => theme.fontSizes.xl};
  font-weight: ${({ theme }) => theme.fontWeights.bold};
  color: ${({ theme }) => theme.colors.primary[600]};
  margin-bottom: ${({ theme }) => theme.spacing[2]};
  border: 3px solid white;
  box-shadow: ${({ theme }) => theme.shadows.lg};
  
  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
    border-radius: 50%;
  }
`;

const PlayerName = styled.h3`
  margin: 0 0 ${({ theme }) => theme.spacing[1]} 0;
  font-size: ${({ theme }) => theme.fontSizes.base};
  font-weight: ${({ theme }) => theme.fontWeights.semibold};
  color: ${({ theme }) => theme.colors.gray[900]};
`;

const PlayerStats = styled.div`
  font-size: ${({ theme }) => theme.fontSizes.sm};
  color: ${({ theme }) => theme.colors.gray[600]};
`;

const RankingsList = styled.div`
  display: grid;
  gap: ${({ theme }) => theme.spacing[4]};
`;

const RankingItem = styled.div`
  display: flex;
  align-items: center;
  padding: ${({ theme }) => theme.spacing[4]};
  background: white;
  border-radius: ${({ theme }) => theme.borderRadius.lg};
  box-shadow: ${({ theme }) => theme.shadows.sm};
  border: 1px solid ${({ theme }) => theme.colors.gray[200]};
  transition: all ${({ theme }) => theme.transitions.base};
  cursor: pointer;
  
  &:hover {
    transform: translateY(-2px);
    box-shadow: ${({ theme }) => theme.shadows.md};
  }
`;

const RankNumber = styled.div<{ position: number }>`
  width: 40px;
  height: 40px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: ${({ theme }) => theme.fontWeights.bold};
  margin-right: ${({ theme }) => theme.spacing[4]};
  
  ${({ position, theme }) => {
    if (position <= 3) {
      const colors = {
        1: { bg: theme.colors.warning[100], text: theme.colors.warning[700] },
        2: { bg: theme.colors.gray[100], text: theme.colors.gray[700] },
        3: { bg: theme.colors.warning[100], text: theme.colors.warning[700] },
      };
      const color = colors[position as keyof typeof colors];
      return `
        background: ${color.bg};
        color: ${color.text};
      `;
    }
    return `
      background: ${theme.colors.gray[50]};
      color: ${theme.colors.gray[600]};
    `;
  }}
`;

const PlayerInfo = styled.div`
  display: flex;
  align-items: center;
  flex: 1;
  gap: ${({ theme }) => theme.spacing[3]};
`;

const PlayerDetails = styled.div`
  flex: 1;
`;

const PlayerStatsGrid = styled.div`
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(100px, 1fr));
  gap: ${({ theme }) => theme.spacing[4]};
  margin-left: auto;
  text-align: center;
`;

const StatItem = styled.div`
  .stat-value {
    font-size: ${({ theme }) => theme.fontSizes.base};
    font-weight: ${({ theme }) => theme.fontWeights.semibold};
    color: ${({ theme }) => theme.colors.gray[900]};
  }
  
  .stat-label {
    font-size: ${({ theme }) => theme.fontSizes.xs};
    color: ${({ theme }) => theme.colors.gray[500]};
    text-transform: uppercase;
    letter-spacing: 0.05em;
  }
`;

interface PlayerRankingsProps {
  onPlayerClick?: (playerId: string) => void;
}

const PlayerRankings: React.FC<PlayerRankingsProps> = ({ onPlayerClick }) => {
  const { t } = useTranslation();
  const [players, setPlayers] = useState<Player[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [selectedCategory, setSelectedCategory] = useState<string>('');
  const [viewMode, setViewMode] = useState<'podium' | 'list' | 'table'>('podium');

  useEffect(() => {
    loadRankings();
  }, [selectedCategory]);

  const loadRankings = async () => {
    try {
      setIsLoading(true);
      setError(null);
      const rankingsData = await playersService.getPlayerRankings(selectedCategory || undefined);
      setPlayers(rankingsData);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed to load rankings');
    } finally {
      setIsLoading(false);
    }
  };

  const getInitials = (name: string) => {
    return name
      .split(' ')
      .map(word => word.charAt(0))
      .join('')
      .toUpperCase()
      .slice(0, 2);
  };

  const formatCategory = (category: string) => {
    const categoryMap: { [key: string]: string } = {
      'MEN': t('players.category.men'),
      'WOMEN': t('players.category.women'),
      'JUNIOR': t('players.category.junior'),
      'SENIOR': t('players.category.senior'),
    };
    return categoryMap[category] || category;
  };

  const categories = [
    { value: '', label: t('players.category.all') },
    { value: 'MEN', label: t('players.category.men') },
    { value: 'WOMEN', label: t('players.category.women') },
    { value: 'JUNIOR', label: t('players.category.junior') },
    { value: 'SENIOR', label: t('players.category.senior') },
  ];

  const tableColumns: Column<Player>[] = [
    {
      key: 'ranking',
      title: t('players.ranking'),
      sortable: false,
      render: (_, player, index) => (
        <RankNumber position={index + 1}>
          {index + 1}
        </RankNumber>
      ),
    },
    {
      key: 'name',
      title: t('players.name'),
      sortable: false,
      render: (value, player) => (
        <div style={{ display: 'flex', alignItems: 'center', gap: '12px' }}>
          <div
            style={{
              width: '40px',
              height: '40px',
              borderRadius: '50%',
              background: player.profilePhotoUrl ? `url(${player.profilePhotoUrl})` : '#e5e7eb',
              backgroundSize: 'cover',
              backgroundPosition: 'center',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              color: '#6b7280',
              fontWeight: 'bold',
            }}
          >
            {!player.profilePhotoUrl && getInitials(player.name)}
          </div>
          <div>
            <div style={{ fontWeight: '600' }}>{value}</div>
            <div style={{ fontSize: '14px', color: '#6b7280' }}>
              {player.districtName || t('common.notSpecified')}
            </div>
          </div>
        </div>
      ),
    },
    {
      key: 'category',
      title: t('players.category'),
      sortable: false,
      render: (value) => formatCategory(value),
    },
    {
      key: 'statistics',
      title: t('players.winPercentage'),
      sortable: false,
      render: (_, player) => {
        const winPercentage = player.statistics?.winPercentage || 0;
        return `${winPercentage.toFixed(1)}%`;
      },
    },
    {
      key: 'totalPoints',
      title: t('players.points'),
      sortable: false,
      render: (_, player) => (player.statistics?.totalPoints || 0).toLocaleString(),
    },
  ];

  if (isLoading) {
    return <LoadingSpinner fullScreen text={t('players.rankings.loading')} />;
  }

  if (error) {
    return (
      <RankingsContainer>
        <Card>
          <Card.Content>
            <div style={{ textAlign: 'center', color: '#dc2626' }}>
              <h3>{t('common.error')}</h3>
              <p>{error}</p>
              <Button onClick={loadRankings} style={{ marginTop: '16px' }}>
                {t('common.retry')}
              </Button>
            </div>
          </Card.Content>
        </Card>
      </RankingsContainer>
    );
  }

  const topThree = players.slice(0, 3);
  const remainingPlayers = players.slice(3);

  return (
    <RankingsContainer>
      <RankingsHeader>
        <RankingsTitle>{t('players.rankings.title')}</RankingsTitle>
      </RankingsHeader>

      <CategoryTabs>
        {categories.map(category => (
          <CategoryTab
            key={category.value}
            active={selectedCategory === category.value}
            onClick={() => setSelectedCategory(category.value)}
          >
            {category.label}
          </CategoryTab>
        ))}
      </CategoryTabs>

      <ViewToggle>
        <Button
          variant={viewMode === 'podium' ? 'primary' : 'secondary'}
          size="sm"
          onClick={() => setViewMode('podium')}
        >
          {t('players.rankings.view.podium')}
        </Button>
        <Button
          variant={viewMode === 'list' ? 'primary' : 'secondary'}
          size="sm"
          onClick={() => setViewMode('list')}
        >
          {t('players.rankings.view.list')}
        </Button>
        <Button
          variant={viewMode === 'table' ? 'primary' : 'secondary'}
          size="sm"
          onClick={() => setViewMode('table')}
        >
          {t('players.rankings.view.table')}
        </Button>
      </ViewToggle>

      {viewMode === 'podium' && topThree.length >= 3 && (
        <TopPlayersSection>
          <PodiumContainer>
            {topThree.map((player, index) => (
              <PodiumPosition key={player.id} position={index + 1}>
                <PlayerAvatar onClick={() => onPlayerClick?.(player.id)}>
                  {player.profilePhotoUrl ? (
                    <img src={player.profilePhotoUrl} alt={player.name} />
                  ) : (
                    getInitials(player.name)
                  )}
                </PlayerAvatar>
                <PlayerName>{player.name}</PlayerName>
                <PlayerStats>
                  {formatCategory(player.category)} • {player.districtName}
                </PlayerStats>
                <PlayerStats>
                  {(player.statistics?.winPercentage || 0).toFixed(1)}% Win Rate
                </PlayerStats>
                <PodiumStep position={index + 1}>
                  #{index + 1}
                </PodiumStep>
              </PodiumPosition>
            ))}
          </PodiumContainer>
        </TopPlayersSection>
      )}

      {viewMode === 'list' && (
        <RankingsList>
          {players.map((player, index) => (
            <RankingItem key={player.id} onClick={() => onPlayerClick?.(player.id)}>
              <RankNumber position={index + 1}>
                {index + 1}
              </RankNumber>
              
              <PlayerInfo>
                <PlayerAvatar>
                  {player.profilePhotoUrl ? (
                    <img src={player.profilePhotoUrl} alt={player.name} />
                  ) : (
                    getInitials(player.name)
                  )}
                </PlayerAvatar>
                
                <PlayerDetails>
                  <PlayerName>{player.name}</PlayerName>
                  <PlayerStats>
                    {formatCategory(player.category)} • {player.districtName}
                  </PlayerStats>
                </PlayerDetails>
              </PlayerInfo>
              
              <PlayerStatsGrid>
                <StatItem>
                  <div className="stat-value">
                    {(player.statistics?.winPercentage || 0).toFixed(1)}%
                  </div>
                  <div className="stat-label">{t('players.winRate')}</div>
                </StatItem>
                
                <StatItem>
                  <div className="stat-value">
                    {player.statistics?.matchesWon || 0}
                  </div>
                  <div className="stat-label">{t('players.wins')}</div>
                </StatItem>
                
                <StatItem>
                  <div className="stat-value">
                    {(player.statistics?.totalPoints || 0).toLocaleString()}
                  </div>
                  <div className="stat-label">{t('players.points')}</div>
                </StatItem>
              </PlayerStatsGrid>
            </RankingItem>
          ))}
        </RankingsList>
      )}

      {viewMode === 'table' && (
        <DataTable
          data={players}
          columns={tableColumns}
          title={t('players.rankings.title')}
          onRowClick={onPlayerClick ? (player) => onPlayerClick(player.id) : undefined}
          emptyTitle={t('players.rankings.empty.title')}
          emptyDescription={t('players.rankings.empty.description')}
        />
      )}

      {players.length === 0 && !isLoading && (
        <Card>
          <Card.Content>
            <div style={{ textAlign: 'center', padding: '48px 24px' }}>
              <h4>{t('players.rankings.empty.title')}</h4>
              <p>{t('players.rankings.empty.description')}</p>
            </div>
          </Card.Content>
        </Card>
      )}
    </RankingsContainer>
  );
};

export default PlayerRankings;