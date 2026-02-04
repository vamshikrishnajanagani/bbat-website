import React from 'react';
import styled from 'styled-components';
import { useTranslation } from 'react-i18next';
import { useSelector } from 'react-redux';
import { RootState } from '../../store';
import DataTable from '../UI/DataTable';

const StatisticsContainer = styled.div`
  background: white;
  border-radius: ${({ theme }) => theme.borderRadius.lg};
  padding: ${({ theme }) => theme.spacing[6]};
  box-shadow: ${({ theme }) => theme.shadows.md};
`;

const Title = styled.h3`
  font-size: ${({ theme }) => theme.fontSizes.xl};
  font-weight: ${({ theme }) => theme.fontWeights.semibold};
  color: ${({ theme }) => theme.colors.gray[900]};
  margin-bottom: ${({ theme }) => theme.spacing[6]};
`;

const ChartContainer = styled.div`
  margin-bottom: ${({ theme }) => theme.spacing[8]};
`;

const ChartTitle = styled.h4`
  font-size: ${({ theme }) => theme.fontSizes.lg};
  font-weight: ${({ theme }) => theme.fontWeights.medium};
  color: ${({ theme }) => theme.colors.gray[800]};
  margin-bottom: ${({ theme }) => theme.spacing[4]};
`;

const BarChart = styled.div`
  display: flex;
  flex-direction: column;
  gap: ${({ theme }) => theme.spacing[3]};
`;

const BarRow = styled.div`
  display: flex;
  align-items: center;
  gap: ${({ theme }) => theme.spacing[3]};
`;

const BarLabel = styled.div`
  min-width: 150px;
  font-size: ${({ theme }) => theme.fontSizes.sm};
  color: ${({ theme }) => theme.colors.gray[700]};
  font-weight: ${({ theme }) => theme.fontWeights.medium};
`;

const BarTrack = styled.div`
  flex: 1;
  height: 32px;
  background: ${({ theme }) => theme.colors.gray[100]};
  border-radius: ${({ theme }) => theme.borderRadius.md};
  overflow: hidden;
  position: relative;
`;

const BarFill = styled.div<{ percentage: number; color: string }>`
  height: 100%;
  width: ${({ percentage }) => percentage}%;
  background: ${({ color }) => color};
  transition: width 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: flex-end;
  padding-right: ${({ theme }) => theme.spacing[2]};
`;

const BarValue = styled.span`
  color: white;
  font-size: ${({ theme }) => theme.fontSizes.sm};
  font-weight: ${({ theme }) => theme.fontWeights.semibold};
`;

const ComparisonGrid = styled.div`
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: ${({ theme }) => theme.spacing[4]};
  margin-bottom: ${({ theme }) => theme.spacing[6]};
`;

const ComparisonCard = styled.div`
  padding: ${({ theme }) => theme.spacing[4]};
  background: ${({ theme }) => theme.colors.gray[50]};
  border-radius: ${({ theme }) => theme.borderRadius.md};
  border: 1px solid ${({ theme }) => theme.colors.gray[200]};
`;

const ComparisonLabel = styled.div`
  font-size: ${({ theme }) => theme.fontSizes.sm};
  color: ${({ theme }) => theme.colors.gray[600]};
  margin-bottom: ${({ theme }) => theme.spacing[2]};
`;

const ComparisonValue = styled.div`
  font-size: ${({ theme }) => theme.fontSizes['2xl']};
  font-weight: ${({ theme }) => theme.fontWeights.bold};
  color: ${({ theme }) => theme.colors.primary[600]};
`;

const DistrictStatistics: React.FC = () => {
  const { t } = useTranslation();
  const { districts } = useSelector((state: RootState) => state.districts);

  const sortedByPlayers = [...districts].sort((a, b) => b.playerCount - a.playerCount);
  const sortedByClubs = [...districts].sort((a, b) => b.activeClubs - a.activeClubs);
  
  const maxPlayers = Math.max(...districts.map(d => d.playerCount), 1);
  const maxClubs = Math.max(...districts.map(d => d.activeClubs), 1);

  const avgPlayers = districts.length > 0 
    ? Math.round(districts.reduce((sum, d) => sum + d.playerCount, 0) / districts.length)
    : 0;
  
  const avgClubs = districts.length > 0
    ? Math.round(districts.reduce((sum, d) => sum + d.activeClubs, 0) / districts.length)
    : 0;

  const tableColumns = [
    {
      key: 'name',
      title: t('districts.stats.districtName')
    },
    {
      key: 'playerCount',
      title: t('districts.stats.players')
    },
    {
      key: 'activeClubs',
      title: t('districts.stats.clubs')
    },
    {
      key: 'area',
      title: t('districts.stats.area'),
      render: (value: number) => `${new Intl.NumberFormat('en-IN').format(value)} km²`
    },
    {
      key: 'population',
      title: t('districts.stats.population'),
      render: (value: number) => new Intl.NumberFormat('en-IN').format(value)
    }
  ];

  return (
    <StatisticsContainer>
      <Title>{t('districts.stats.title')}</Title>

      <ComparisonGrid>
        <ComparisonCard>
          <ComparisonLabel>{t('districts.stats.avgPlayers')}</ComparisonLabel>
          <ComparisonValue>{avgPlayers}</ComparisonValue>
        </ComparisonCard>

        <ComparisonCard>
          <ComparisonLabel>{t('districts.stats.avgClubs')}</ComparisonLabel>
          <ComparisonValue>{avgClubs}</ComparisonValue>
        </ComparisonCard>

        <ComparisonCard>
          <ComparisonLabel>{t('districts.stats.mostPlayers')}</ComparisonLabel>
          <ComparisonValue>{sortedByPlayers[0]?.name || '-'}</ComparisonValue>
        </ComparisonCard>

        <ComparisonCard>
          <ComparisonLabel>{t('districts.stats.mostClubs')}</ComparisonLabel>
          <ComparisonValue>{sortedByClubs[0]?.name || '-'}</ComparisonValue>
        </ComparisonCard>
      </ComparisonGrid>

      <ChartContainer>
        <ChartTitle>{t('districts.stats.playersByDistrict')}</ChartTitle>
        <BarChart>
          {sortedByPlayers.slice(0, 10).map(district => (
            <BarRow key={district.id}>
              <BarLabel>{district.name}</BarLabel>
              <BarTrack>
                <BarFill 
                  percentage={(district.playerCount / maxPlayers) * 100}
                  color="#3B82F6"
                >
                  <BarValue>{district.playerCount}</BarValue>
                </BarFill>
              </BarTrack>
            </BarRow>
          ))}
        </BarChart>
      </ChartContainer>

      <ChartContainer>
        <ChartTitle>{t('districts.stats.clubsByDistrict')}</ChartTitle>
        <BarChart>
          {sortedByClubs.slice(0, 10).map(district => (
            <BarRow key={district.id}>
              <BarLabel>{district.name}</BarLabel>
              <BarTrack>
                <BarFill 
                  percentage={(district.activeClubs / maxClubs) * 100}
                  color="#10B981"
                >
                  <BarValue>{district.activeClubs}</BarValue>
                </BarFill>
              </BarTrack>
            </BarRow>
          ))}
        </BarChart>
      </ChartContainer>

      <ChartTitle>{t('districts.stats.detailedComparison')}</ChartTitle>
      <DataTable
        columns={tableColumns}
        data={districts}
      />
    </StatisticsContainer>
  );
};

export default DistrictStatistics;
