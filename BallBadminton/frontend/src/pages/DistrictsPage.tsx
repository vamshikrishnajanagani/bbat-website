import React, { useEffect, useState } from 'react';
import styled from 'styled-components';
import { useAppSelector, useAppDispatch } from '../store';
import { setDistricts, setLoading } from '../store/slices/districtsSlice';
import { districtsService } from '../services/districtsService';
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

const StatsOverview = styled.div`
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: ${({ theme }) => theme.spacing[4]};
  margin-bottom: ${({ theme }) => theme.spacing[8]};
`;

const StatCard = styled.div`
  background: white;
  padding: ${({ theme }) => theme.spacing[6]};
  border-radius: ${({ theme }) => theme.borderRadius.lg};
  box-shadow: ${({ theme }) => theme.shadows.md};
  text-align: center;
`;

const StatNumber = styled.div`
  font-size: ${({ theme }) => theme.fontSizes['3xl']};
  font-weight: ${({ theme }) => theme.fontWeights.bold};
  color: ${({ theme }) => theme.colors.primary[600]};
  margin-bottom: ${({ theme }) => theme.spacing[2]};
`;

const StatLabel = styled.div`
  font-size: ${({ theme }) => theme.fontSizes.sm};
  color: ${({ theme }) => theme.colors.gray[600]};
  font-weight: ${({ theme }) => theme.fontWeights.medium};
`;

const DistrictsGrid = styled.div`
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: ${({ theme }) => theme.spacing[6]};
`;

const DistrictCard = styled.div`
  background: white;
  border-radius: ${({ theme }) => theme.borderRadius.lg};
  padding: ${({ theme }) => theme.spacing[6]};
  box-shadow: ${({ theme }) => theme.shadows.md};
  transition: transform ${({ theme }) => theme.transitions.base};
  
  &:hover {
    transform: translateY(-2px);
  }
`;

const DistrictName = styled.h3`
  font-size: ${({ theme }) => theme.fontSizes.xl};
  font-weight: ${({ theme }) => theme.fontWeights.semibold};
  color: ${({ theme }) => theme.colors.gray[900]};
  margin-bottom: ${({ theme }) => theme.spacing[4]};
`;

const DistrictStats = styled.div`
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: ${({ theme }) => theme.spacing[4]};
  margin-bottom: ${({ theme }) => theme.spacing[4]};
`;

const DistrictStatItem = styled.div`
  text-align: center;
`;

const DistrictStatValue = styled.div`
  font-size: ${({ theme }) => theme.fontSizes.lg};
  font-weight: ${({ theme }) => theme.fontWeights.bold};
  color: ${({ theme }) => theme.colors.primary[600]};
`;

const DistrictStatLabel = styled.div`
  font-size: ${({ theme }) => theme.fontSizes.xs};
  color: ${({ theme }) => theme.colors.gray[500]};
  text-transform: uppercase;
  letter-spacing: 0.5px;
`;

const DistrictDescription = styled.p`
  font-size: ${({ theme }) => theme.fontSizes.sm};
  color: ${({ theme }) => theme.colors.gray[600]};
  line-height: ${({ theme }) => theme.lineHeights.relaxed};
  margin-bottom: ${({ theme }) => theme.spacing[4]};
`;

const DistrictsPage: React.FC = () => {
  const dispatch = useAppDispatch();
  const { districts, isLoading } = useAppSelector(state => state.districts);
  const [searchTerm, setSearchTerm] = useState('');

  useEffect(() => {
    const fetchDistricts = async () => {
      try {
        dispatch(setLoading(true));
        const data = await districtsService.getDistricts();
        dispatch(setDistricts(Array.isArray(data) ? data : (data as any).data || []));
      } catch (error) {
        console.error('Failed to fetch districts:', error);
      } finally {
        dispatch(setLoading(false));
      }
    };

    fetchDistricts();
  }, [dispatch]);

  const filteredDistricts = districts.filter(district =>
    district.name.toLowerCase().includes(searchTerm.toLowerCase())
  );

  const totalPlayers = districts.reduce((sum, district) => sum + ((district as any).playerCount || 0), 0);
  const totalClubs = districts.reduce((sum, district) => sum + ((district as any).clubCount || 0), 0);
  const totalTournaments = districts.reduce((sum, district) => sum + ((district as any).tournamentCount || 0), 0);

  if (isLoading) {
    return <LoadingSpinner text="Loading districts..." />;
  }

  return (
    <PageContainer>
      <PageHeader>
        <PageTitle>Districts</PageTitle>
        <PageDescription>
          Explore Ball Badminton activities across all 33 districts of Telangana. 
          View statistics, player counts, and tournament information for each district.
        </PageDescription>
      </PageHeader>

      <StatsOverview>
        <StatCard>
          <StatNumber>{districts.length}</StatNumber>
          <StatLabel>Total Districts</StatLabel>
        </StatCard>
        <StatCard>
          <StatNumber>{totalPlayers}</StatNumber>
          <StatLabel>Registered Players</StatLabel>
        </StatCard>
        <StatCard>
          <StatNumber>{totalClubs}</StatNumber>
          <StatLabel>Active Clubs</StatLabel>
        </StatCard>
        <StatCard>
          <StatNumber>{totalTournaments}</StatNumber>
          <StatLabel>Annual Tournaments</StatLabel>
        </StatCard>
      </StatsOverview>

      <div style={{ marginBottom: '2rem' }}>
        <input
          type="text"
          placeholder="Search districts..."
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
          style={{
            width: '100%',
            maxWidth: '400px',
            padding: '0.75rem 1rem',
            border: '1px solid #d1d5db',
            borderRadius: '0.5rem',
            fontSize: '1rem'
          }}
        />
      </div>

      <DistrictsGrid>
        {filteredDistricts.map(district => (
          <DistrictCard key={district.id}>
            <DistrictName>{district.name}</DistrictName>
            
            <DistrictStats>
              <DistrictStatItem>
                <DistrictStatValue>{(district as any).playerCount || 0}</DistrictStatValue>
                <DistrictStatLabel>Players</DistrictStatLabel>
              </DistrictStatItem>
              <DistrictStatItem>
                <DistrictStatValue>{(district as any).clubCount || 0}</DistrictStatValue>
                <DistrictStatLabel>Clubs</DistrictStatLabel>
              </DistrictStatItem>
              <DistrictStatItem>
                <DistrictStatValue>{(district as any).tournamentCount || 0}</DistrictStatValue>
                <DistrictStatLabel>Tournaments</DistrictStatLabel>
              </DistrictStatItem>
              <DistrictStatItem>
                <DistrictStatValue>{(district as any).championshipWins || 0}</DistrictStatValue>
                <DistrictStatLabel>Championships</DistrictStatLabel>
              </DistrictStatItem>
            </DistrictStats>

            <DistrictDescription>
              {(district as any).description || `${district.name} is actively participating in Ball Badminton activities with growing player participation and regular tournaments.`}
            </DistrictDescription>

            <Button size="sm" variant="outline" fullWidth>
              View Details
            </Button>
          </DistrictCard>
        ))}
      </DistrictsGrid>

      {filteredDistricts.length === 0 && !isLoading && (
        <div style={{ textAlign: 'center', padding: '2rem' }}>
          <p>No districts found matching your search.</p>
        </div>
      )}
    </PageContainer>
  );
};

export default DistrictsPage;