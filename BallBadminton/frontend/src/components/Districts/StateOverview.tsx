import React from 'react';
import styled from 'styled-components';
import { useTranslation } from 'react-i18next';
import { useSelector } from 'react-redux';
import { RootState } from '../../store';
import Card from '../UI/Card';

const OverviewContainer = styled.div`
  background: white;
  border-radius: ${({ theme }) => theme.borderRadius.lg};
  padding: ${({ theme }) => theme.spacing[8]};
  margin-bottom: ${({ theme }) => theme.spacing[6]};
  box-shadow: ${({ theme }) => theme.shadows.md};
`;

const Title = styled.h2`
  font-size: ${({ theme }) => theme.fontSizes['2xl']};
  font-weight: ${({ theme }) => theme.fontWeights.bold};
  color: ${({ theme }) => theme.colors.gray[900]};
  margin-bottom: ${({ theme }) => theme.spacing[6]};
`;

const Description = styled.p`
  color: ${({ theme }) => theme.colors.gray[700]};
  line-height: 1.6;
  margin-bottom: ${({ theme }) => theme.spacing[6]};
  font-size: ${({ theme }) => theme.fontSizes.base};
`;

const StatsGrid = styled.div`
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: ${({ theme }) => theme.spacing[4]};
  margin-bottom: ${({ theme }) => theme.spacing[8]};
`;

const StatCard = styled(Card)`
  text-align: center;
  padding: ${({ theme }) => theme.spacing[6]};
  background: linear-gradient(135deg, ${({ theme }) => theme.colors.primary[50]} 0%, white 100%);
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

const InfoSection = styled.div`
  margin-bottom: ${({ theme }) => theme.spacing[6]};
`;

const SectionTitle = styled.h3`
  font-size: ${({ theme }) => theme.fontSizes.xl};
  font-weight: ${({ theme }) => theme.fontWeights.semibold};
  color: ${({ theme }) => theme.colors.gray[900]};
  margin-bottom: ${({ theme }) => theme.spacing[4]};
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

const HighlightBox = styled.div`
  background: ${({ theme }) => theme.colors.primary[50]};
  border-left: 4px solid ${({ theme }) => theme.colors.primary[600]};
  padding: ${({ theme }) => theme.spacing[4]};
  border-radius: ${({ theme }) => theme.borderRadius.md};
  margin-top: ${({ theme }) => theme.spacing[6]};
`;

const HighlightText = styled.p`
  color: ${({ theme }) => theme.colors.gray[700]};
  line-height: 1.6;
  margin: 0;
`;

const StateOverview: React.FC = () => {
  const { t } = useTranslation();
  const { districts } = useSelector((state: RootState) => state.districts);

  const totalPlayers = districts.reduce((sum, d) => sum + d.playerCount, 0);
  const totalClubs = districts.reduce((sum, d) => sum + d.activeClubs, 0);
  const totalArea = districts.reduce((sum, d) => sum + d.area, 0);
  const totalPopulation = districts.reduce((sum, d) => sum + d.population, 0);

  const formatNumber = (num: number) => {
    return new Intl.NumberFormat('en-IN').format(num);
  };

  return (
    <OverviewContainer>
      <Title>{t('districts.stateOverview.title')}</Title>
      
      <Description>
        {t('districts.stateOverview.description')}
      </Description>

      <StatsGrid>
        <StatCard>
          <StatValue>{districts.length}</StatValue>
          <StatLabel>{t('districts.stateOverview.totalDistricts')}</StatLabel>
        </StatCard>

        <StatCard>
          <StatValue>{formatNumber(totalPlayers)}</StatValue>
          <StatLabel>{t('districts.stateOverview.totalPlayers')}</StatLabel>
        </StatCard>

        <StatCard>
          <StatValue>{formatNumber(totalClubs)}</StatValue>
          <StatLabel>{t('districts.stateOverview.totalClubs')}</StatLabel>
        </StatCard>

        <StatCard>
          <StatValue>{formatNumber(totalArea)}</StatValue>
          <StatLabel>{t('districts.stateOverview.totalArea')}</StatLabel>
        </StatCard>
      </StatsGrid>

      <InfoSection>
        <SectionTitle>{t('districts.stateOverview.aboutTelangana')}</SectionTitle>
        <InfoGrid>
          <InfoItem>
            <InfoLabel>{t('districts.stateOverview.capital')}</InfoLabel>
            <InfoValue>Hyderabad</InfoValue>
          </InfoItem>

          <InfoItem>
            <InfoLabel>{t('districts.stateOverview.formation')}</InfoLabel>
            <InfoValue>June 2, 2014</InfoValue>
          </InfoItem>

          <InfoItem>
            <InfoLabel>{t('districts.stateOverview.population')}</InfoLabel>
            <InfoValue>{formatNumber(totalPopulation)}</InfoValue>
          </InfoItem>

          <InfoItem>
            <InfoLabel>{t('districts.stateOverview.area')}</InfoLabel>
            <InfoValue>{formatNumber(totalArea)} km²</InfoValue>
          </InfoItem>

          <InfoItem>
            <InfoLabel>{t('districts.stateOverview.officialLanguage')}</InfoLabel>
            <InfoValue>Telugu, Urdu</InfoValue>
          </InfoItem>

          <InfoItem>
            <InfoLabel>{t('districts.stateOverview.districts')}</InfoLabel>
            <InfoValue>{districts.length} Districts</InfoValue>
          </InfoItem>
        </InfoGrid>
      </InfoSection>

      <HighlightBox>
        <HighlightText>
          {t('districts.stateOverview.ballBadmintonInfo')}
        </HighlightText>
      </HighlightBox>
    </OverviewContainer>
  );
};

export default StateOverview;
