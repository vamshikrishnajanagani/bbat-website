import React, { useState, useEffect } from 'react';
import styled from 'styled-components';
import { useTranslation } from 'react-i18next';
import { useDispatch, useSelector } from 'react-redux';
import { RootState } from '../../store';
import { setPlayers, setLoading, setError, setFilters } from '../../store/slices/playersSlice';
import { playersService, PlayerFilters } from '../../services/playersService';
import DataTable, { Column } from '../UI/DataTable';
import ProfileCard from '../UI/ProfileCard';
import Button from '../UI/Button';
import LoadingSpinner from '../UI/LoadingSpinner';

const DirectoryContainer = styled.div`
  max-width: 1200px;
  margin: 0 auto;
  padding: ${({ theme }) => theme.spacing[6]};
`;

const DirectoryHeader = styled.div`
  margin-bottom: ${({ theme }) => theme.spacing[8]};
  text-align: center;
`;

const DirectoryTitle = styled.h1`
  font-size: ${({ theme }) => theme.fontSizes['3xl']};
  font-weight: ${({ theme }) => theme.fontWeights.bold};
  color: ${({ theme }) => theme.colors.gray[900]};
  margin-bottom: ${({ theme }) => theme.spacing[4]};
`;

const DirectoryDescription = styled.p`
  font-size: ${({ theme }) => theme.fontSizes.lg};
  color: ${({ theme }) => theme.colors.gray[600]};
  max-width: 600px;
  margin: 0 auto;
  line-height: ${({ theme }) => theme.lineHeights.relaxed};
`;

const ViewToggle = styled.div`
  display: flex;
  justify-content: center;
  margin-bottom: ${({ theme }) => theme.spacing[6]};
  gap: ${({ theme }) => theme.spacing[2]};
`;

const FilterSection = styled.div`
  display: flex;
  flex-wrap: wrap;
  gap: ${({ theme }) => theme.spacing[4]};
  margin-bottom: ${({ theme }) => theme.spacing[6]};
  padding: ${({ theme }) => theme.spacing[4]};
  background: ${({ theme }) => theme.colors.gray[50]};
  border-radius: ${({ theme }) => theme.borderRadius.lg};
`;

const FilterGroup = styled.div`
  display: flex;
  flex-direction: column;
  gap: ${({ theme }) => theme.spacing[2]};
  min-width: 200px;
`;

const FilterLabel = styled.label`
  font-size: ${({ theme }) => theme.fontSizes.sm};
  font-weight: ${({ theme }) => theme.fontWeights.medium};
  color: ${({ theme }) => theme.colors.gray[700]};
`;

const FilterSelect = styled.select`
  padding: ${({ theme }) => theme.spacing[2]} ${({ theme }) => theme.spacing[3]};
  border: 1px solid ${({ theme }) => theme.colors.gray[300]};
  border-radius: ${({ theme }) => theme.borderRadius.base};
  font-size: ${({ theme }) => theme.fontSizes.sm};
  background: white;
  
  &:focus {
    outline: none;
    border-color: ${({ theme }) => theme.colors.primary[500]};
    box-shadow: 0 0 0 3px ${({ theme }) => theme.colors.primary[100]};
  }
`;

const SearchInput = styled.input`
  padding: ${({ theme }) => theme.spacing[2]} ${({ theme }) => theme.spacing[3]};
  border: 1px solid ${({ theme }) => theme.colors.gray[300]};
  border-radius: ${({ theme }) => theme.borderRadius.base};
  font-size: ${({ theme }) => theme.fontSizes.sm};
  min-width: 250px;
  
  &:focus {
    outline: none;
    border-color: ${({ theme }) => theme.colors.primary[500]};
    box-shadow: 0 0 0 3px ${({ theme }) => theme.colors.primary[100]};
  }
`;

const ProminentSection = styled.div`
  margin-bottom: ${({ theme }) => theme.spacing[8]};
`;

const SectionTitle = styled.h2`
  font-size: ${({ theme }) => theme.fontSizes['2xl']};
  font-weight: ${({ theme }) => theme.fontWeights.semibold};
  color: ${({ theme }) => theme.colors.gray[900]};
  margin-bottom: ${({ theme }) => theme.spacing[6]};
  text-align: center;
`;

const PlayersGrid = styled.div`
  display: grid;
  gap: ${({ theme }) => theme.spacing[4]};
  
  @media (min-width: ${({ theme }) => theme.breakpoints.md}) {
    grid-template-columns: repeat(2, 1fr);
  }
  
  @media (min-width: ${({ theme }) => theme.breakpoints.lg}) {
    grid-template-columns: repeat(3, 1fr);
  }
`;

const StatsCard = styled.div`
  background: white;
  padding: ${({ theme }) => theme.spacing[4]};
  border-radius: ${({ theme }) => theme.borderRadius.lg};
  box-shadow: ${({ theme }) => theme.shadows.sm};
  border: 1px solid ${({ theme }) => theme.colors.gray[200]};
  text-align: center;
  
  .stat-value {
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

interface PlayerDirectoryProps {
  onPlayerClick?: (playerId: string) => void;
  showAdminActions?: boolean;
}

const PlayerDirectory: React.FC<PlayerDirectoryProps> = ({
  onPlayerClick,
  showAdminActions = false,
}) => {
  const { t } = useTranslation();
  const dispatch = useDispatch();
  const { players, prominentPlayers, isLoading, error, filters } = useSelector((state: RootState) => state.players);
  const [viewMode, setViewMode] = useState<'grid' | 'table'>('grid');
  const [showProminentOnly, setShowProminentOnly] = useState(false);

  useEffect(() => {
    loadPlayers();
  }, [filters]);

  const loadPlayers = async () => {
    try {
      dispatch(setLoading(true));
      const playerFilters: PlayerFilters = {
        district: filters.district || undefined,
        category: filters.category as any || undefined,
        search: filters.searchTerm || undefined,
        isProminent: showProminentOnly || undefined,
      };
      
      const playersData = await playersService.getPlayers(playerFilters);
      dispatch(setPlayers(playersData.data || []));
    } catch (err) {
      dispatch(setError(err instanceof Error ? err.message : 'Failed to load players'));
    } finally {
      dispatch(setLoading(false));
    }
  };

  const handleFilterChange = (key: keyof typeof filters, value: string) => {
    dispatch(setFilters({ [key]: value }));
  };

  const clearFilters = () => {
    dispatch(setFilters({ district: '', category: '', searchTerm: '' }));
    setShowProminentOnly(false);
  };

  const calculateAge = (dateOfBirth: string) => {
    const today = new Date();
    const birthDate = new Date(dateOfBirth);
    let age = today.getFullYear() - birthDate.getFullYear();
    const monthDiff = today.getMonth() - birthDate.getMonth();
    
    if (monthDiff < 0 || (monthDiff === 0 && today.getDate() < birthDate.getDate())) {
      age--;
    }
    
    return age;
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

  const tableColumns: Column<typeof players[0]>[] = [
    {
      key: 'name',
      title: t('players.name'),
      sortable: true,
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
            {!player.profilePhotoUrl && player.name.charAt(0).toUpperCase()}
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
      key: 'age',
      title: t('players.age'),
      sortable: true,
      render: (_, player) => player.dateOfBirth ? calculateAge(player.dateOfBirth) : t('common.notSpecified'),
    },
    {
      key: 'category',
      title: t('players.category'),
      sortable: true,
      render: (value) => formatCategory(value),
    },
    {
      key: 'statistics',
      title: t('players.winPercentage'),
      sortable: true,
      render: (_, player) => {
        const winPercentage = player.statistics?.winPercentage || 0;
        return `${winPercentage.toFixed(1)}%`;
      },
    },
    {
      key: 'totalAchievements',
      title: t('players.achievements'),
      sortable: true,
      render: (value) => value || 0,
    },
  ];

  if (showAdminActions) {
    tableColumns.push({
      key: 'actions',
      title: t('common.actions'),
      render: (_, player) => (
        <div style={{ display: 'flex', gap: '8px' }}>
          <Button
            size="sm"
            variant="secondary"
            onClick={() => onPlayerClick?.(player.id)}
          >
            {t('common.view')}
          </Button>
        </div>
      ),
    });
  }

  const displayPlayers = showProminentOnly ? prominentPlayers : players;

  if (isLoading && players.length === 0) {
    return <LoadingSpinner fullScreen text={t('players.loading')} />;
  }

  return (
    <DirectoryContainer>
      <DirectoryHeader>
        <DirectoryTitle>{t('players.directory.title')}</DirectoryTitle>
        <DirectoryDescription>
          {t('players.directory.description')}
        </DirectoryDescription>
      </DirectoryHeader>

      {/* Prominent Players Section */}
      {prominentPlayers.length > 0 && !showProminentOnly && (
        <ProminentSection>
          <SectionTitle>{t('players.prominentPlayers')}</SectionTitle>
          <PlayersGrid>
            {prominentPlayers.slice(0, 6).map(player => (
              <ProfileCard
                key={player.id}
                name={player.name}
                title={formatCategory(player.category)}
                subtitle={player.districtName}
                imageUrl={player.profilePhotoUrl}
                clickable={!!onPlayerClick}
                onClick={() => onPlayerClick?.(player.id)}
                stats={[
                  { 
                    label: t('players.winRate'), 
                    value: `${(player.statistics?.winPercentage || 0).toFixed(1)}%` 
                  },
                  { 
                    label: t('players.achievements'), 
                    value: player.totalAchievements || 0 
                  },
                ]}
                tags={[
                  { label: t('players.prominent'), variant: 'primary' },
                  ...(player.statistics?.currentRanking ? [{ 
                    label: `#${player.statistics.currentRanking}`, 
                    variant: 'secondary' as const 
                  }] : []),
                ]}
              />
            ))}
          </PlayersGrid>
          
          {prominentPlayers.length > 6 && (
            <div style={{ textAlign: 'center', marginTop: '24px' }}>
              <Button 
                variant="secondary" 
                onClick={() => setShowProminentOnly(true)}
              >
                {t('players.viewAllProminent')}
              </Button>
            </div>
          )}
        </ProminentSection>
      )}

      {/* Filters */}
      <FilterSection>
        <FilterGroup>
          <FilterLabel>{t('players.search')}</FilterLabel>
          <SearchInput
            type="text"
            placeholder={t('players.search.placeholder')}
            value={filters.searchTerm}
            onChange={(e) => handleFilterChange('searchTerm', e.target.value)}
          />
        </FilterGroup>
        
        <FilterGroup>
          <FilterLabel>{t('players.filter.district')}</FilterLabel>
          <FilterSelect
            value={filters.district}
            onChange={(e) => handleFilterChange('district', e.target.value)}
          >
            <option value="">{t('common.all')}</option>
            <option value="hyderabad">Hyderabad</option>
            <option value="warangal">Warangal</option>
            <option value="nizamabad">Nizamabad</option>
            <option value="karimnagar">Karimnagar</option>
            <option value="khammam">Khammam</option>
          </FilterSelect>
        </FilterGroup>
        
        <FilterGroup>
          <FilterLabel>{t('players.filter.category')}</FilterLabel>
          <FilterSelect
            value={filters.category}
            onChange={(e) => handleFilterChange('category', e.target.value)}
          >
            <option value="">{t('common.all')}</option>
            <option value="MEN">{t('players.category.men')}</option>
            <option value="WOMEN">{t('players.category.women')}</option>
            <option value="JUNIOR">{t('players.category.junior')}</option>
            <option value="SENIOR">{t('players.category.senior')}</option>
          </FilterSelect>
        </FilterGroup>
        
        <FilterGroup>
          <FilterLabel>{t('players.filter.type')}</FilterLabel>
          <FilterSelect
            value={showProminentOnly ? 'prominent' : 'all'}
            onChange={(e) => setShowProminentOnly(e.target.value === 'prominent')}
          >
            <option value="all">{t('players.filter.allPlayers')}</option>
            <option value="prominent">{t('players.filter.prominentOnly')}</option>
          </FilterSelect>
        </FilterGroup>
        
        <FilterGroup style={{ justifyContent: 'flex-end' }}>
          <Button variant="secondary" size="sm" onClick={clearFilters}>
            {t('common.clearFilters')}
          </Button>
        </FilterGroup>
      </FilterSection>

      {/* View Toggle */}
      <ViewToggle>
        <Button
          variant={viewMode === 'grid' ? 'primary' : 'secondary'}
          size="sm"
          onClick={() => setViewMode('grid')}
        >
          {t('players.view.grid')}
        </Button>
        <Button
          variant={viewMode === 'table' ? 'primary' : 'secondary'}
          size="sm"
          onClick={() => setViewMode('table')}
        >
          {t('players.view.table')}
        </Button>
      </ViewToggle>

      {error && (
        <div style={{ 
          background: '#fef2f2', 
          border: '1px solid #fecaca', 
          color: '#dc2626', 
          padding: '12px', 
          borderRadius: '8px', 
          marginBottom: '24px',
          textAlign: 'center'
        }}>
          {error}
        </div>
      )}

      {/* Players Display */}
      {viewMode === 'grid' ? (
        <PlayersGrid>
          {displayPlayers.map(player => (
            <ProfileCard
              key={player.id}
              name={player.name}
              title={formatCategory(player.category)}
              subtitle={player.districtName}
              imageUrl={player.profilePhotoUrl}
              clickable={!!onPlayerClick}
              onClick={() => onPlayerClick?.(player.id)}
              stats={[
                { 
                  label: t('players.age'), 
                  value: player.dateOfBirth ? calculateAge(player.dateOfBirth) : t('common.notSpecified')
                },
                { 
                  label: t('players.winRate'), 
                  value: `${(player.statistics?.winPercentage || 0).toFixed(1)}%` 
                },
                { 
                  label: t('players.achievements'), 
                  value: player.totalAchievements || 0 
                },
              ]}
              tags={[
                ...(player.isProminent ? [{ label: t('players.prominent'), variant: 'primary' as const }] : []),
                ...(player.statistics?.currentRanking ? [{ 
                  label: `#${player.statistics.currentRanking}`, 
                  variant: 'secondary' as const 
                }] : []),
              ]}
            />
          ))}
        </PlayersGrid>
      ) : (
        <DataTable
          data={displayPlayers}
          columns={tableColumns}
          title={showProminentOnly ? t('players.prominentPlayers') : t('players.allPlayers')}
          searchPlaceholder={t('players.search.placeholder')}
          onRowClick={onPlayerClick ? (player) => onPlayerClick(player.id) : undefined}
          loading={isLoading}
          emptyTitle={t('players.empty.title')}
          emptyDescription={t('players.empty.description')}
        />
      )}
    </DirectoryContainer>
  );
};

export default PlayerDirectory;