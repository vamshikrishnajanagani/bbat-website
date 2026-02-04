import React, { useEffect, useState } from 'react';
import styled from 'styled-components';
import { useAppSelector, useAppDispatch } from '../store';
import { setTournaments, setLoading } from '../store/slices/tournamentsSlice';
import { tournamentsService } from '../services/tournamentsService';
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

const FilterTabs = styled.div`
  display: flex;
  gap: ${({ theme }) => theme.spacing[2]};
  margin-bottom: ${({ theme }) => theme.spacing[6]};
  border-bottom: 1px solid ${({ theme }) => theme.colors.gray[200]};
`;

const FilterTab = styled.button<{ active: boolean }>`
  padding: ${({ theme }) => theme.spacing[3]} ${({ theme }) => theme.spacing[4]};
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

const TournamentsGrid = styled.div`
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: ${({ theme }) => theme.spacing[6]};
`;

const TournamentCard = styled.div`
  background: white;
  border-radius: ${({ theme }) => theme.borderRadius.lg};
  padding: ${({ theme }) => theme.spacing[6]};
  box-shadow: ${({ theme }) => theme.shadows.md};
  transition: transform ${({ theme }) => theme.transitions.base};
  
  &:hover {
    transform: translateY(-2px);
  }
`;

const TournamentHeader = styled.div`
  margin-bottom: ${({ theme }) => theme.spacing[4]};
`;

const TournamentTitle = styled.h3`
  font-size: ${({ theme }) => theme.fontSizes.xl};
  font-weight: ${({ theme }) => theme.fontWeights.semibold};
  color: ${({ theme }) => theme.colors.gray[900]};
  margin-bottom: ${({ theme }) => theme.spacing[2]};
`;

const TournamentStatus = styled.span<{ status: string }>`
  display: inline-block;
  padding: ${({ theme }) => theme.spacing[1]} ${({ theme }) => theme.spacing[3]};
  border-radius: ${({ theme }) => theme.borderRadius.full};
  font-size: ${({ theme }) => theme.fontSizes.sm};
  font-weight: ${({ theme }) => theme.fontWeights.medium};
  text-transform: uppercase;
  letter-spacing: 0.5px;
  
  ${({ status, theme }) => {
    switch (status) {
      case 'upcoming':
        return `
          background: ${theme.colors.info[100]};
          color: ${theme.colors.info[800]};
        `;
      case 'ongoing':
        return `
          background: ${theme.colors.success[100]};
          color: ${theme.colors.success[800]};
        `;
      case 'completed':
        return `
          background: ${theme.colors.gray[100]};
          color: ${theme.colors.gray[800]};
        `;
      default:
        return `
          background: ${theme.colors.gray[100]};
          color: ${theme.colors.gray[800]};
        `;
    }
  }}
`;

const TournamentDetails = styled.div`
  margin-bottom: ${({ theme }) => theme.spacing[4]};
`;

const DetailItem = styled.div`
  display: flex;
  justify-content: space-between;
  margin-bottom: ${({ theme }) => theme.spacing[2]};
  font-size: ${({ theme }) => theme.fontSizes.sm};
`;

const DetailLabel = styled.span`
  color: ${({ theme }) => theme.colors.gray[600]};
`;

const DetailValue = styled.span`
  color: ${({ theme }) => theme.colors.gray[900]};
  font-weight: ${({ theme }) => theme.fontWeights.medium};
`;

const TournamentActions = styled.div`
  display: flex;
  gap: ${({ theme }) => theme.spacing[3]};
`;

const TournamentsPage: React.FC = () => {
  const dispatch = useAppDispatch();
  const { tournaments, isLoading } = useAppSelector(state => state.tournaments);
  const [activeTab, setActiveTab] = useState<'upcoming' | 'ongoing' | 'completed'>('upcoming');

  useEffect(() => {
    const fetchTournaments = async () => {
      try {
        dispatch(setLoading(true));
        const data = await tournamentsService.getTournaments({ 
          status: activeTab.charAt(0).toUpperCase() + activeTab.slice(1) as any
        });
        dispatch(setTournaments(Array.isArray(data) ? data : (data as any).data || []));
      } catch (error) {
        console.error('Failed to fetch tournaments:', error);
      } finally {
        dispatch(setLoading(false));
      }
    };

    fetchTournaments();
  }, [dispatch, activeTab]);

  const formatDate = (dateString: string) => {
    return new Date(dateString).toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'short',
      day: 'numeric'
    });
  };

  if (isLoading) {
    return <LoadingSpinner text="Loading tournaments..." />;
  }

  return (
    <PageContainer>
      <PageHeader>
        <PageTitle>Tournaments</PageTitle>
        <PageDescription>
          Stay updated with Ball Badminton tournaments across Telangana. 
          Register for upcoming events and follow ongoing competitions.
        </PageDescription>
      </PageHeader>

      <FilterTabs>
        <FilterTab 
          active={activeTab === 'upcoming'} 
          onClick={() => setActiveTab('upcoming')}
        >
          Upcoming
        </FilterTab>
        <FilterTab 
          active={activeTab === 'ongoing'} 
          onClick={() => setActiveTab('ongoing')}
        >
          Ongoing
        </FilterTab>
        <FilterTab 
          active={activeTab === 'completed'} 
          onClick={() => setActiveTab('completed')}
        >
          Completed
        </FilterTab>
      </FilterTabs>

      <TournamentsGrid>
        {tournaments.map(tournament => (
          <TournamentCard key={tournament.id}>
            <TournamentHeader>
              <TournamentTitle>{tournament.name}</TournamentTitle>
              <TournamentStatus status={tournament.status.toLowerCase()}>
                {tournament.status}
              </TournamentStatus>
            </TournamentHeader>

            <TournamentDetails>
              <DetailItem>
                <DetailLabel>Start Date:</DetailLabel>
                <DetailValue>{formatDate(tournament.startDate)}</DetailValue>
              </DetailItem>
              <DetailItem>
                <DetailLabel>End Date:</DetailLabel>
                <DetailValue>{formatDate(tournament.endDate)}</DetailValue>
              </DetailItem>
              <DetailItem>
                <DetailLabel>Location:</DetailLabel>
                <DetailValue>{(tournament as any).venue || 'TBD'}</DetailValue>
              </DetailItem>
              <DetailItem>
                <DetailLabel>Type:</DetailLabel>
                <DetailValue>{(tournament as any).tournamentType || 'General'}</DetailValue>
              </DetailItem>
              <DetailItem>
                <DetailLabel>Prize Money:</DetailLabel>
                <DetailValue>₹{(tournament as any).prizeMoney?.toLocaleString() || 'TBD'}</DetailValue>
              </DetailItem>
              <DetailItem>
                <DetailLabel>Participants:</DetailLabel>
                <DetailValue>
                  {(tournament as any).currentRegistrationCount || 0} / {tournament.maxParticipants || 'Unlimited'}
                </DetailValue>
              </DetailItem>
            </TournamentDetails>

            <TournamentActions>
              <Button size="sm" variant="outline">
                View Details
              </Button>
              {tournament.status.toLowerCase() === 'upcoming' && (
                <Button size="sm">
                  Register
                </Button>
              )}
              {tournament.status.toLowerCase() === 'ongoing' && (
                <Button size="sm" variant="secondary">
                  Live Scores
                </Button>
              )}
              {tournament.status.toLowerCase() === 'completed' && (
                <Button size="sm" variant="secondary">
                  Results
                </Button>
              )}
            </TournamentActions>
          </TournamentCard>
        ))}
      </TournamentsGrid>

      {tournaments.length === 0 && !isLoading && (
        <div style={{ textAlign: 'center', padding: '2rem' }}>
          <p>No {activeTab} tournaments found.</p>
        </div>
      )}
    </PageContainer>
  );
};

export default TournamentsPage;