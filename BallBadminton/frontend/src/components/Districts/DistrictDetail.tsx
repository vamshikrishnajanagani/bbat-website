import React, { useEffect, useState } from 'react';
import styled from 'styled-components';
import { useTranslation } from 'react-i18next';
import { District } from '../../store/slices/districtsSlice';
import { districtsService, DistrictStatistics } from '../../services/districtsService';
import LoadingSpinner from '../UI/LoadingSpinner';
import Card from '../UI/Card';

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
  align-items: center;
  margin-bottom: ${({ theme }) => theme.spacing[6]};
`;

const Title = styled.h1`
  font-size: ${({ theme }) => theme.fontSizes['3xl']};
  font-weight: ${({ theme }) => theme.fontWeights.bold};
  color: ${({ theme }) => theme.colors.gray[900]};
  margin: 0;
`;

const DistrictCode = styled.span`
  padding: ${({ theme }) => theme.spacing[2]} ${({ theme }) => theme.spacing[4]};
  background: ${({ theme }) => theme.colors.primary[100]};
  color: ${({ theme }) => theme.colors.primary[800]};
  border-radius: ${({ theme }) => theme.borderRadius.full};
  font-size: ${({ theme }) => theme.fontSizes.lg};
  font-weight: ${({ theme }) => theme.fontWeights.semibold};
`;

const InfoGrid = styled.div`
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: ${({ theme }) => theme.spacing[4]};
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

const ContactSection = styled.div`
  margin-top: ${({ theme }) => theme.spacing[6]};
  padding-top: ${({ theme }) => theme.spacing[6]};
  border-top: 1px solid ${({ theme }) => theme.colors.gray[200]};
`;

const SectionTitle = styled.h3`
  font-size: ${({ theme }) => theme.fontSizes.xl};
  font-weight: ${({ theme }) => theme.fontWeights.semibold};
  color: ${({ theme }) => theme.colors.gray[900]};
  margin-bottom: ${({ theme }) => theme.spacing[4]};
`;

const StatsGrid = styled.div`
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: ${({ theme }) => theme.spacing[4]};
  margin-bottom: ${({ theme }) => theme.spacing[6]};
`;

const StatCard = styled(Card)`
  text-align: center;
  padding: ${({ theme }) => theme.spacing[6]};
`;

const StatValue = styled.div`
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

const TopPlayersSection = styled.div`
  background: white;
  padding: ${({ theme }) => theme.spacing[6]};
  border-radius: ${({ theme }) => theme.borderRadius.lg};
  margin-bottom: ${({ theme }) => theme.spacing[6]};
`;

const PlayerList = styled.div`
  display: flex;
  flex-direction: column;
  gap: ${({ theme }) => theme.spacing[3]};
`;

const PlayerItem = styled.div`
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: ${({ theme }) => theme.spacing[3]};
  background: ${({ theme }) => theme.colors.gray[50]};
  border-radius: ${({ theme }) => theme.borderRadius.md};
`;

const PlayerName = styled.span`
  font-weight: ${({ theme }) => theme.fontWeights.medium};
  color: ${({ theme }) => theme.colors.gray[900]};
`;

const PlayerRank = styled.span`
  padding: ${({ theme }) => theme.spacing[1]} ${({ theme }) => theme.spacing[3]};
  background: ${({ theme }) => theme.colors.primary[100]};
  color: ${({ theme }) => theme.colors.primary[800]};
  border-radius: ${({ theme }) => theme.borderRadius.full};
  font-size: ${({ theme }) => theme.fontSizes.sm};
  font-weight: ${({ theme }) => theme.fontWeights.semibold};
`;

interface DistrictDetailProps {
  districtId: string;
}

const DistrictDetail: React.FC<DistrictDetailProps> = ({ districtId }) => {
  const { t } = useTranslation();
  const [district, setDistrict] = useState<District | null>(null);
  const [statistics, setStatistics] = useState<DistrictStatistics | null>(null);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    loadDistrict();
    loadStatistics();
  }, [districtId]);

  const loadDistrict = async () => {
    try {
      setIsLoading(true);
      const data = await districtsService.getDistrictById(districtId);
      setDistrict(data);
    } catch (error) {
      console.error('Failed to load district:', error);
    } finally {
      setIsLoading(false);
    }
  };

  const loadStatistics = async () => {
    try {
      const data = await districtsService.getDistrictStatistics(districtId);
      setStatistics(data);
    } catch (error) {
      console.error('Failed to load statistics:', error);
    }
  };

  const formatNumber = (num: number) => {
    return new Intl.NumberFormat('en-IN').format(num);
  };

  if (isLoading) {
    return <LoadingSpinner />;
  }

  if (!district) {
    return <div>District not found</div>;
  }

  return (
    <DetailContainer>
      <Header>
        <TitleRow>
          <Title>{district.name}</Title>
          <DistrictCode>{district.code}</DistrictCode>
        </TitleRow>

        <InfoGrid>
          <InfoItem>
            <InfoLabel>{t('districts.headquarters')}</InfoLabel>
            <InfoValue>{district.headquarters}</InfoValue>
          </InfoItem>

          <InfoItem>
            <InfoLabel>{t('districts.area')}</InfoLabel>
            <InfoValue>{formatNumber(district.area)} km²</InfoValue>
          </InfoItem>

          <InfoItem>
            <InfoLabel>{t('districts.population')}</InfoLabel>
            <InfoValue>{formatNumber(district.population)}</InfoValue>
          </InfoItem>

          <InfoItem>
            <InfoLabel>{t('districts.coordinates')}</InfoLabel>
            <InfoValue>
              {district.coordinates.latitude.toFixed(4)}, {district.coordinates.longitude.toFixed(4)}
            </InfoValue>
          </InfoItem>
        </InfoGrid>

        {district.contactPerson && (
          <ContactSection>
            <SectionTitle>{t('districts.contact')}</SectionTitle>
            <InfoGrid>
              <InfoItem>
                <InfoLabel>{t('districts.contactPerson')}</InfoLabel>
                <InfoValue>{district.contactPerson}</InfoValue>
              </InfoItem>

              {district.contactEmail && (
                <InfoItem>
                  <InfoLabel>{t('districts.contactEmail')}</InfoLabel>
                  <InfoValue>{district.contactEmail}</InfoValue>
                </InfoItem>
              )}

              {district.contactPhone && (
                <InfoItem>
                  <InfoLabel>{t('districts.contactPhone')}</InfoLabel>
                  <InfoValue>{district.contactPhone}</InfoValue>
                </InfoItem>
              )}
            </InfoGrid>
          </ContactSection>
        )}
      </Header>

      {statistics && (
        <>
          <StatsGrid>
            <StatCard>
              <StatValue>{statistics.playerCount}</StatValue>
              <StatLabel>{t('districts.stats.totalPlayers')}</StatLabel>
            </StatCard>

            <StatCard>
              <StatValue>{statistics.activeClubs}</StatValue>
              <StatLabel>{t('districts.stats.activeClubs')}</StatLabel>
            </StatCard>

            <StatCard>
              <StatValue>{statistics.tournamentsHosted}</StatValue>
              <StatLabel>{t('districts.stats.tournamentsHosted')}</StatLabel>
            </StatCard>

            <StatCard>
              <StatValue>{statistics.upcomingTournaments}</StatValue>
              <StatLabel>{t('districts.stats.upcomingTournaments')}</StatLabel>
            </StatCard>
          </StatsGrid>

          {statistics.topPlayers && statistics.topPlayers.length > 0 && (
            <TopPlayersSection>
              <SectionTitle>{t('districts.topPlayers')}</SectionTitle>
              <PlayerList>
                {statistics.topPlayers.map(player => (
                  <PlayerItem key={player.id}>
                    <PlayerName>{player.name}</PlayerName>
                    <PlayerRank>#{player.ranking}</PlayerRank>
                  </PlayerItem>
                ))}
              </PlayerList>
            </TopPlayersSection>
          )}
        </>
      )}
    </DetailContainer>
  );
};

export default DistrictDetail;
