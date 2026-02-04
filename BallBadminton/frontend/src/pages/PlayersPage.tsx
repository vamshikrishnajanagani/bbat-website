import React, { useEffect, useState } from 'react';
import styled from 'styled-components';
import { useAppSelector, useAppDispatch } from '../store';
import { setPlayers, setLoading } from '../store/slices/playersSlice';
import { playersService } from '../services/playersService';
import LoadingSpinner from '../components/UI/LoadingSpinner';
import Button from '../components/UI/Button';

const PageContainer = styled.div`
  max-width: 1200px;
  margin: 0 auto;
`;

const PageHeader = styled.header`
  margin-bottom: ${({ theme }) => theme.spacing[8]};
`;

const PageTitle = styled.h1`
  font-size: ${({ theme }) => theme.fontSizes['4xl']};
  font-weight: ${({ theme }) => theme.fontWeights.bold};
  color: ${({ theme }) => theme.colors.gray[900]};
  margin-bottom: ${({ theme }) => theme.spacing[4]};
`;

const PageDescription = styled.p`
  font-size: ${({ theme }) => theme.fontSizes.lg};
  color: ${({ theme }) => theme.colors.gray[600]};
  line-height: ${({ theme }) => theme.lineHeights.relaxed};
`;

const FilterSection = styled.div`
  display: flex;
  gap: ${({ theme }) => theme.spacing[4]};
  margin-bottom: ${({ theme }) => theme.spacing[6]};
  flex-wrap: wrap;
`;

const FilterSelect = styled.select`
  padding: ${({ theme }) => theme.spacing[2]} ${({ theme }) => theme.spacing[3]};
  border: 1px solid ${({ theme }) => theme.colors.gray[300]};
  border-radius: ${({ theme }) => theme.borderRadius.md};
  font-size: ${({ theme }) => theme.fontSizes.base};
`;

const PlayersGrid = styled.div`
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: ${({ theme }) => theme.spacing[6]};
`;

const PlayerCard = styled.div`
  background: white;
  border-radius: ${({ theme }) => theme.borderRadius.lg};
  padding: ${({ theme }) => theme.spacing[6]};
  box-shadow: ${({ theme }) => theme.shadows.md};
  text-align: center;
  transition: transform ${({ theme }) => theme.transitions.base};
  
  &:hover {
    transform: translateY(-2px);
  }
`;

const PlayerPhoto = styled.img`
  width: 100px;
  height: 100px;
  border-radius: 50%;
  object-fit: cover;
  margin: 0 auto ${({ theme }) => theme.spacing[4]};
`;

const PlayerName = styled.h3`
  font-size: ${({ theme }) => theme.fontSizes.xl};
  font-weight: ${({ theme }) => theme.fontWeights.semibold};
  color: ${({ theme }) => theme.colors.gray[900]};
  margin-bottom: ${({ theme }) => theme.spacing[2]};
`;

const PlayerDistrict = styled.p`
  font-size: ${({ theme }) => theme.fontSizes.base};
  color: ${({ theme }) => theme.colors.primary[600]};
  font-weight: ${({ theme }) => theme.fontWeights.medium};
  margin-bottom: ${({ theme }) => theme.spacing[3]};
`;

const PlayerStats = styled.div`
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: ${({ theme }) => theme.spacing[3]};
  margin-bottom: ${({ theme }) => theme.spacing[4]};
`;

const StatItem = styled.div`
  text-align: center;
`;

const StatValue = styled.div`
  font-size: ${({ theme }) => theme.fontSizes.lg};
  font-weight: ${({ theme }) => theme.fontWeights.bold};
  color: ${({ theme }) => theme.colors.primary[600]};
`;

const StatLabel = styled.div`
  font-size: ${({ theme }) => theme.fontSizes.xs};
  color: ${({ theme }) => theme.colors.gray[500]};
  text-transform: uppercase;
  letter-spacing: 0.5px;
`;

const PlayersPage: React.FC = () => {
  const dispatch = useAppDispatch();
  const { players, isLoading } = useAppSelector(state => state.players);
  const [districtFilter, setDistrictFilter] = useState('all');
  const [categoryFilter, setCategoryFilter] = useState('all');

  useEffect(() => {
    const fetchPlayers = async () => {
      try {
        dispatch(setLoading(true));
        const data = await playersService.getPlayers({ 
          isActive: true,
          district: districtFilter !== 'all' ? districtFilter : undefined,
          category: categoryFilter !== 'all' ? categoryFilter as any : undefined,
        });
        dispatch(setPlayers(Array.isArray(data) ? data : (data as any).data || []));
      } catch (error) {
        console.error('Failed to fetch players:', error);
      } finally {
        dispatch(setLoading(false));
      }
    };

    fetchPlayers();
  }, [dispatch, districtFilter, categoryFilter]);

  if (isLoading) {
    return <LoadingSpinner text="Loading players..." />;
  }

  return (
    <PageContainer>
      <PageHeader>
        <PageTitle>Player Directory</PageTitle>
        <PageDescription>
          Discover talented Ball Badminton players from across Telangana state. 
          Browse by district or category to find players and their achievements.
        </PageDescription>
      </PageHeader>

      <FilterSection>
        <FilterSelect 
          value={districtFilter} 
          onChange={(e) => setDistrictFilter(e.target.value)}
          aria-label="Filter by district"
        >
          <option value="all">All Districts</option>
          <option value="hyderabad">Hyderabad</option>
          <option value="warangal">Warangal</option>
          <option value="nizamabad">Nizamabad</option>
          <option value="karimnagar">Karimnagar</option>
        </FilterSelect>

        <FilterSelect 
          value={categoryFilter} 
          onChange={(e) => setCategoryFilter(e.target.value)}
          aria-label="Filter by category"
        >
          <option value="all">All Categories</option>
          <option value="men">Men</option>
          <option value="women">Women</option>
          <option value="junior">Junior</option>
          <option value="senior">Senior</option>
        </FilterSelect>
      </FilterSection>

      <PlayersGrid>
        {players.map(player => (
          <PlayerCard key={player.id}>
            <PlayerPhoto 
              src={(player as any).photoUrl || '/default-player.png'} 
              alt={player.name}
            />
            <PlayerName>{player.name}</PlayerName>
            <PlayerDistrict>{(player as any).district || 'Unknown'}</PlayerDistrict>
            
            <PlayerStats>
              <StatItem>
                <StatValue>{(player.statistics as any)?.matchesPlayed || 0}</StatValue>
                <StatLabel>Matches</StatLabel>
              </StatItem>
              <StatItem>
                <StatValue>{(player.statistics as any)?.wins || 0}</StatValue>
                <StatLabel>Wins</StatLabel>
              </StatItem>
              <StatItem>
                <StatValue>{(player as any).achievements?.length || 0}</StatValue>
                <StatLabel>Awards</StatLabel>
              </StatItem>
              <StatItem>
                <StatValue>{(player.statistics as any)?.ranking || 'N/A'}</StatValue>
                <StatLabel>Rank</StatLabel>
              </StatItem>
            </PlayerStats>

            <Button size="sm" variant="outline">
              View Profile
            </Button>
          </PlayerCard>
        ))}
      </PlayersGrid>

      {players.length === 0 && !isLoading && (
        <div style={{ textAlign: 'center', padding: '2rem' }}>
          <p>No players found matching your criteria.</p>
        </div>
      )}
    </PageContainer>
  );
};

export default PlayersPage;