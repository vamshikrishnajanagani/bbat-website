import React from 'react';
import styled from 'styled-components';
import { useTranslation } from 'react-i18next';
import DataTable from '../UI/DataTable';

const ResultsContainer = styled.div`
  background: white;
  border-radius: ${({ theme }) => theme.borderRadius.lg};
  padding: ${({ theme }) => theme.spacing[6]};
`;

const SectionTitle = styled.h3`
  font-size: ${({ theme }) => theme.fontSizes.xl};
  font-weight: ${({ theme }) => theme.fontWeights.semibold};
  color: ${({ theme }) => theme.colors.gray[900]};
  margin-bottom: ${({ theme }) => theme.spacing[4]};
`;

const CategorySection = styled.div`
  margin-bottom: ${({ theme }) => theme.spacing[8]};

  &:last-child {
    margin-bottom: 0;
  }
`;

const CategoryTitle = styled.h4`
  font-size: ${({ theme }) => theme.fontSizes.lg};
  font-weight: ${({ theme }) => theme.fontWeights.medium};
  color: ${({ theme }) => theme.colors.gray[800]};
  margin-bottom: ${({ theme }) => theme.spacing[3]};
`;

const Medal = styled.span<{ position: number }>`
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 24px;
  height: 24px;
  border-radius: 50%;
  font-weight: ${({ theme }) => theme.fontWeights.bold};
  font-size: ${({ theme }) => theme.fontSizes.xs};
  background: ${({ position }) => {
    switch (position) {
      case 1: return '#FFD700';
      case 2: return '#C0C0C0';
      case 3: return '#CD7F32';
      default: return '#E5E7EB';
    }
  }};
  color: ${({ position }) => position <= 3 ? 'white' : '#6B7280'};
`;

const NoResults = styled.div`
  text-align: center;
  padding: ${({ theme }) => theme.spacing[8]};
  color: ${({ theme }) => theme.colors.gray[500]};
`;

interface Result {
  position: number;
  playerName: string;
  district: string;
  matchesPlayed: number;
  matchesWon: number;
  points: number;
}

interface CategoryResult {
  categoryName: string;
  results: Result[];
}

interface TournamentResultsProps {
  results?: CategoryResult[];
}

const TournamentResults: React.FC<TournamentResultsProps> = ({ results }) => {
  const { t } = useTranslation();

  if (!results || results.length === 0) {
    return (
      <ResultsContainer>
        <NoResults>
          {t('tournaments.results.noData')}
        </NoResults>
      </ResultsContainer>
    );
  }

  const columns = [
    {
      key: 'position',
      title: t('tournaments.results.position'),
      render: (value: number) => <Medal position={value}>{value}</Medal>
    },
    {
      key: 'playerName',
      title: t('tournaments.results.player')
    },
    {
      key: 'district',
      title: t('tournaments.results.district')
    },
    {
      key: 'matchesPlayed',
      title: t('tournaments.results.matchesPlayed')
    },
    {
      key: 'matchesWon',
      title: t('tournaments.results.matchesWon')
    },
    {
      key: 'points',
      title: t('tournaments.results.points')
    }
  ];

  return (
    <ResultsContainer>
      <SectionTitle>{t('tournaments.results.title')}</SectionTitle>
      
      {results.map((category, index) => (
        <CategorySection key={index}>
          <CategoryTitle>{category.categoryName}</CategoryTitle>
          <DataTable
            columns={columns}
            data={category.results}
          />
        </CategorySection>
      ))}
    </ResultsContainer>
  );
};

export default TournamentResults;
