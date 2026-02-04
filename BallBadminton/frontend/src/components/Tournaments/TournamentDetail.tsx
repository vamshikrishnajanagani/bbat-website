import React, { useEffect, useState } from 'react';
import styled from 'styled-components';
import { useTranslation } from 'react-i18next';
import { Tournament } from '../../store/slices/tournamentsSlice';
import { tournamentsService } from '../../services/tournamentsService';
import Button from '../UI/Button';
import LoadingSpinner from '../UI/LoadingSpinner';
import TournamentBracket from './TournamentBracket';
import TournamentResults from './TournamentResults';
import TournamentRegistrationForm from './TournamentRegistrationForm';

const DetailContainer = styled.div`
  max-width: 1200px;
  margin: 0 auto;
`;

const Header = styled.div`
  background: white;
  padding: ${({ theme }) => theme.spacing[8]};
  border-radius: ${({ theme }) => theme.borderRadius.lg};
  margin-bottom: ${({ theme }) => theme.spacing[6]};
  box-shadow: ${({ theme }) => theme.shadows.md};
`;

const TitleRow = styled.div`
  display: flex;
  justify-content: space-between;
  align-items: start;
  margin-bottom: ${({ theme }) => theme.spacing[4]};
`;

const Title = styled.h1`
  font-size: ${({ theme }) => theme.fontSizes['3xl']};
  font-weight: ${({ theme }) => theme.fontWeights.bold};
  color: ${({ theme }) => theme.colors.gray[900]};
  margin: 0;
`;

const StatusBadge = styled.span<{ status: string }>`
  padding: ${({ theme }) => theme.spacing[2]} ${({ theme }) => theme.spacing[4]};
  border-radius: ${({ theme }) => theme.borderRadius.full};
  font-size: ${({ theme }) => theme.fontSizes.sm};
  font-weight: ${({ theme }) => theme.fontWeights.medium};
  background: ${({ status, theme }) => {
    switch (status) {
      case 'Upcoming': return theme.colors.info[100];
      case 'Ongoing': return theme.colors.success[100];
      case 'Completed': return theme.colors.gray[100];
      case 'Cancelled': return theme.colors.error[100];
      default: return theme.colors.gray[100];
    }
  }};
  color: ${({ status, theme }) => {
    switch (status) {
      case 'Upcoming': return theme.colors.info[800];
      case 'Ongoing': return theme.colors.success[800];
      case 'Completed': return theme.colors.gray[800];
      case 'Cancelled': return theme.colors.error[800];
      default: return theme.colors.gray[800];
    }
  }};
`;

const Description = styled.p`
  color: ${({ theme }) => theme.colors.gray[700]};
  line-height: 1.6;
  margin-bottom: ${({ theme }) => theme.spacing[6]};
`;

const InfoGrid = styled.div`
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: ${({ theme }) => theme.spacing[4]};
  margin-bottom: ${({ theme }) => theme.spacing[6]};
`;

const InfoItem = styled.div`
  display: flex;
  flex-direction: column;
  gap: ${({ theme }) => theme.spacing[1]};
`;

const InfoLabel = styled.span`
  font-size: ${({ theme }) => theme.fontSizes.sm};
  color: ${({ theme }) => theme.colors.gray[600]};
  font-weight: ${({ theme }) => theme.fontWeights.medium};
`;

const InfoValue = styled.span`
  font-size: ${({ theme }) => theme.fontSizes.base};
  color: ${({ theme }) => theme.colors.gray[900]};
  font-weight: ${({ theme }) => theme.fontWeights.semibold};
`;

const CategoriesSection = styled.div`
  margin-top: ${({ theme }) => theme.spacing[6]};
`;

const SectionTitle = styled.h3`
  font-size: ${({ theme }) => theme.fontSizes.xl};
  font-weight: ${({ theme }) => theme.fontWeights.semibold};
  color: ${({ theme }) => theme.colors.gray[900]};
  margin-bottom: ${({ theme }) => theme.spacing[4]};
`;

const CategoryGrid = styled.div`
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: ${({ theme }) => theme.spacing[3]};
`;

const CategoryCard = styled.div`
  padding: ${({ theme }) => theme.spacing[4]};
  background: ${({ theme }) => theme.colors.gray[50]};
  border-radius: ${({ theme }) => theme.borderRadius.md};
  border: 1px solid ${({ theme }) => theme.colors.gray[200]};
`;

const CategoryName = styled.div`
  font-weight: ${({ theme }) => theme.fontWeights.semibold};
  color: ${({ theme }) => theme.colors.gray[900]};
  margin-bottom: ${({ theme }) => theme.spacing[2]};
`;

const CategoryDetail = styled.div`
  font-size: ${({ theme }) => theme.fontSizes.sm};
  color: ${({ theme }) => theme.colors.gray[600]};
`;

const TabContainer = styled.div`
  margin-top: ${({ theme }) => theme.spacing[6]};
`;

const TabList = styled.div`
  display: flex;
  gap: ${({ theme }) => theme.spacing[2]};
  border-bottom: 2px solid ${({ theme }) => theme.colors.gray[200]};
  margin-bottom: ${({ theme }) => theme.spacing[6]};
`;

const Tab = styled.button<{ active: boolean }>`
  padding: ${({ theme }) => theme.spacing[3]} ${({ theme }) => theme.spacing[6]};
  background: none;
  border: none;
  border-bottom: 2px solid ${({ active, theme }) => 
    active ? theme.colors.primary[600] : 'transparent'};
  color: ${({ active, theme }) => 
    active ? theme.colors.primary[600] : theme.colors.gray[600]};
  font-weight: ${({ active, theme }) => 
    active ? theme.fontWeights.semibold : theme.fontWeights.medium};
  cursor: pointer;
  transition: all 0.2s;
  margin-bottom: -2px;

  &:hover {
    color: ${({ theme }) => theme.colors.primary[600]};
  }
`;

interface TournamentDetailProps {
  tournamentId: string;
}

const TournamentDetail: React.FC<TournamentDetailProps> = ({ tournamentId }) => {
  const { t } = useTranslation();
  const [tournament, setTournament] = useState<Tournament | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [activeTab, setActiveTab] = useState<'details' | 'bracket' | 'results'>('details');
  const [showRegistration, setShowRegistration] = useState(false);
  const [bracket, setBracket] = useState<any>(null);
  const [results, setResults] = useState<any>(null);

  useEffect(() => {
    loadTournament();
  }, [tournamentId]);

  useEffect(() => {
    if (activeTab === 'bracket' && !bracket) {
      loadBracket();
    } else if (activeTab === 'results' && !results) {
      loadResults();
    }
  }, [activeTab]);

  const loadTournament = async () => {
    try {
      setIsLoading(true);
      const data = await tournamentsService.getTournamentById(tournamentId);
      setTournament(data);
    } catch (error) {
      console.error('Failed to load tournament:', error);
    } finally {
      setIsLoading(false);
    }
  };

  const loadBracket = async () => {
    try {
      const data = await tournamentsService.getTournamentBracket(tournamentId);
      setBracket(data);
    } catch (error) {
      console.error('Failed to load bracket:', error);
    }
  };

  const loadResults = async () => {
    try {
      const data = await tournamentsService.getTournamentResults(tournamentId);
      setResults(data);
    } catch (error) {
      console.error('Failed to load results:', error);
    }
  };

  const formatDate = (dateString: string) => {
    return new Date(dateString).toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'long',
      day: 'numeric'
    });
  };

  const canRegister = tournament?.status === 'Upcoming' && 
    new Date(tournament.registrationDeadline) > new Date();

  if (isLoading) {
    return <LoadingSpinner />;
  }

  if (!tournament) {
    return <div>Tournament not found</div>;
  }

  return (
    <DetailContainer>
      <Header>
        <TitleRow>
          <Title>{tournament.name}</Title>
          <StatusBadge status={tournament.status}>{tournament.status}</StatusBadge>
        </TitleRow>

        <Description>{tournament.description}</Description>

        <InfoGrid>
          <InfoItem>
            <InfoLabel>{t('tournaments.dates')}</InfoLabel>
            <InfoValue>
              {formatDate(tournament.startDate)} - {formatDate(tournament.endDate)}
            </InfoValue>
          </InfoItem>

          <InfoItem>
            <InfoLabel>{t('tournaments.venue')}</InfoLabel>
            <InfoValue>{tournament.venue}</InfoValue>
          </InfoItem>

          <InfoItem>
            <InfoLabel>{t('tournaments.district')}</InfoLabel>
            <InfoValue>{tournament.district}</InfoValue>
          </InfoItem>

          <InfoItem>
            <InfoLabel>{t('tournaments.entryFee')}</InfoLabel>
            <InfoValue>₹{tournament.entryFee}</InfoValue>
          </InfoItem>

          <InfoItem>
            <InfoLabel>{t('tournaments.registrationDeadline')}</InfoLabel>
            <InfoValue>{formatDate(tournament.registrationDeadline)}</InfoValue>
          </InfoItem>

          <InfoItem>
            <InfoLabel>{t('tournaments.maxParticipants')}</InfoLabel>
            <InfoValue>{tournament.maxParticipants}</InfoValue>
          </InfoItem>
        </InfoGrid>

        {canRegister && (
          <Button onClick={() => setShowRegistration(true)}>
            {t('tournaments.register')}
          </Button>
        )}

        <CategoriesSection>
          <SectionTitle>{t('tournaments.categories')}</SectionTitle>
          <CategoryGrid>
            {tournament.categories.map(category => (
              <CategoryCard key={category.id}>
                <CategoryName>{category.name}</CategoryName>
                <CategoryDetail>{category.gender} - {category.ageGroup}</CategoryDetail>
                <CategoryDetail>Max: {category.maxParticipants}</CategoryDetail>
              </CategoryCard>
            ))}
          </CategoryGrid>
        </CategoriesSection>
      </Header>

      <TabContainer>
        <TabList>
          <Tab active={activeTab === 'details'} onClick={() => setActiveTab('details')}>
            {t('tournaments.tabs.details')}
          </Tab>
          <Tab active={activeTab === 'bracket'} onClick={() => setActiveTab('bracket')}>
            {t('tournaments.tabs.bracket')}
          </Tab>
          <Tab active={activeTab === 'results'} onClick={() => setActiveTab('results')}>
            {t('tournaments.tabs.results')}
          </Tab>
        </TabList>

        {activeTab === 'bracket' && <TournamentBracket bracket={bracket} />}
        {activeTab === 'results' && <TournamentResults results={results} />}
      </TabContainer>

      {showRegistration && (
        <TournamentRegistrationForm
          tournament={tournament}
          isOpen={showRegistration}
          onClose={() => setShowRegistration(false)}
          onSuccess={loadTournament}
        />
      )}
    </DetailContainer>
  );
};

export default TournamentDetail;
