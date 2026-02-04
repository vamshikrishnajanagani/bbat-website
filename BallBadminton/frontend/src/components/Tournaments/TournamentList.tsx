import React, { useEffect, useState } from 'react';
import styled from 'styled-components';
import { useTranslation } from 'react-i18next';
import { useDispatch, useSelector } from 'react-redux';
import { RootState } from '../../store';
import { setTournaments, setLoading, setError, setFilters } from '../../store/slices/tournamentsSlice';
import { tournamentsService } from '../../services/tournamentsService';
import Card from '../UI/Card';
import Button from '../UI/Button';
import LoadingSpinner from '../UI/LoadingSpinner';

const ListContainer = styled.div`
  display: flex;
  flex-direction: column;
  gap: ${({ theme }) => theme.spacing[6]};
`;

const FilterBar = styled.div`
  display: flex;
  gap: ${({ theme }) => theme.spacing[4]};
  flex-wrap: wrap;
  padding: ${({ theme }) => theme.spacing[4]};
  background: ${({ theme }) => theme.colors.gray[50]};
  border-radius: ${({ theme }) => theme.borderRadius.lg};
`;

const Select = styled.select`
  padding: ${({ theme }) => theme.spacing[2]} ${({ theme }) => theme.spacing[3]};
  border: 1px solid ${({ theme }) => theme.colors.gray[300]};
  border-radius: ${({ theme }) => theme.borderRadius.md};
  font-size: ${({ theme }) => theme.fontSizes.base};
`;

const TournamentGrid = styled.div`
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: ${({ theme }) => theme.spacing[6]};
`;

const TournamentCard = styled(Card)`
  cursor: pointer;
  transition: transform 0.2s;

  &:hover {
    transform: translateY(-4px);
  }
`;

const TournamentHeader = styled.div`
  display: flex;
  justify-content: space-between;
  align-items: start;
  margin-bottom: ${({ theme }) => theme.spacing[3]};
`;

const TournamentTitle = styled.h3`
  font-size: ${({ theme }) => theme.fontSizes.xl};
  font-weight: ${({ theme }) => theme.fontWeights.semibold};
  color: ${({ theme }) => theme.colors.gray[900]};
  margin: 0;
`;

const StatusBadge = styled.span<{ status: string }>`
  padding: ${({ theme }) => theme.spacing[1]} ${({ theme }) => theme.spacing[3]};
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

const TournamentInfo = styled.div`
  display: flex;
  flex-direction: column;
  gap: ${({ theme }) => theme.spacing[2]};
  color: ${({ theme }) => theme.colors.gray[600]};
  font-size: ${({ theme }) => theme.fontSizes.sm};
`;

const InfoRow = styled.div`
  display: flex;
  align-items: center;
  gap: ${({ theme }) => theme.spacing[2]};
`;

interface TournamentListProps {
  onTournamentClick: (id: string) => void;
  view?: 'calendar' | 'list';
}

const TournamentList: React.FC<TournamentListProps> = ({ onTournamentClick, view = 'list' }) => {
  const { t } = useTranslation();
  const dispatch = useDispatch();
  const { tournaments, isLoading, error, filters } = useSelector((state: RootState) => state.tournaments);
  const { districts } = useSelector((state: RootState) => state.districts);

  useEffect(() => {
    loadTournaments();
  }, [filters]);

  const loadTournaments = async () => {
    try {
      dispatch(setLoading(true));
      const serviceFilters = {
        ...filters,
        status: filters.status as 'Upcoming' | 'Ongoing' | 'Completed' | 'Cancelled' | undefined,
      };
      // Remove empty string values
      if (!serviceFilters.status) delete serviceFilters.status;
      if (!serviceFilters.district) delete serviceFilters.district;
      
      const data = await tournamentsService.getTournaments(serviceFilters);
      dispatch(setTournaments(data.data));
    } catch (err: any) {
      dispatch(setError(err.message || 'Failed to load tournaments'));
    } finally {
      dispatch(setLoading(false));
    }
  };

  const handleFilterChange = (key: string, value: string) => {
    dispatch(setFilters({ [key]: value }));
  };

  const formatDate = (dateString: string) => {
    return new Date(dateString).toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'short',
      day: 'numeric'
    });
  };

  if (isLoading) {
    return <LoadingSpinner />;
  }

  if (error) {
    return <div>Error: {error}</div>;
  }

  return (
    <ListContainer>
      <FilterBar>
        <Select
          value={filters.status}
          onChange={(e) => handleFilterChange('status', e.target.value)}
        >
          <option value="">{t('tournaments.filters.allStatuses')}</option>
          <option value="Upcoming">{t('tournaments.status.upcoming')}</option>
          <option value="Ongoing">{t('tournaments.status.ongoing')}</option>
          <option value="Completed">{t('tournaments.status.completed')}</option>
          <option value="Cancelled">{t('tournaments.status.cancelled')}</option>
        </Select>

        <Select
          value={filters.district}
          onChange={(e) => handleFilterChange('district', e.target.value)}
        >
          <option value="">{t('tournaments.filters.allDistricts')}</option>
          {districts.map(district => (
            <option key={district.id} value={district.name}>{district.name}</option>
          ))}
        </Select>
      </FilterBar>

      <TournamentGrid>
        {tournaments.map(tournament => (
          <TournamentCard key={tournament.id} onClick={() => onTournamentClick(tournament.id)}>
            <TournamentHeader>
              <TournamentTitle>{tournament.name}</TournamentTitle>
              <StatusBadge status={tournament.status}>{tournament.status}</StatusBadge>
            </TournamentHeader>

            <TournamentInfo>
              <InfoRow>
                <span>📅</span>
                <span>{formatDate(tournament.startDate)} - {formatDate(tournament.endDate)}</span>
              </InfoRow>
              <InfoRow>
                <span>📍</span>
                <span>{tournament.venue}, {tournament.district}</span>
              </InfoRow>
              <InfoRow>
                <span>👥</span>
                <span>{tournament.categories.length} Categories</span>
              </InfoRow>
              <InfoRow>
                <span>💰</span>
                <span>Entry Fee: ₹{tournament.entryFee}</span>
              </InfoRow>
            </TournamentInfo>
          </TournamentCard>
        ))}
      </TournamentGrid>

      {tournaments.length === 0 && (
        <div style={{ textAlign: 'center', padding: '2rem', color: '#666' }}>
          {t('tournaments.noTournaments')}
        </div>
      )}
    </ListContainer>
  );
};

export default TournamentList;
