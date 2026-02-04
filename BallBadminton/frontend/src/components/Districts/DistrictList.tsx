import React, { useEffect } from 'react';
import styled from 'styled-components';
import { useTranslation } from 'react-i18next';
import { useDispatch, useSelector } from 'react-redux';
import { RootState } from '../../store';
import { setDistricts, setLoading, setError } from '../../store/slices/districtsSlice';
import { districtsService } from '../../services/districtsService';
import Card from '../UI/Card';
import LoadingSpinner from '../UI/LoadingSpinner';

const ListContainer = styled.div`
  display: flex;
  flex-direction: column;
  gap: ${({ theme }) => theme.spacing[6]};
`;

const DistrictGrid = styled.div`
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: ${({ theme }) => theme.spacing[6]};
`;

const DistrictCard = styled(Card)`
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;

  &:hover {
    transform: translateY(-4px);
    box-shadow: ${({ theme }) => theme.shadows.lg};
  }
`;

const DistrictHeader = styled.div`
  display: flex;
  justify-content: space-between;
  align-items: start;
  margin-bottom: ${({ theme }) => theme.spacing[3]};
`;

const DistrictName = styled.h3`
  font-size: ${({ theme }) => theme.fontSizes.xl};
  font-weight: ${({ theme }) => theme.fontWeights.semibold};
  color: ${({ theme }) => theme.colors.gray[900]};
  margin: 0;
`;

const DistrictCode = styled.span`
  padding: ${({ theme }) => theme.spacing[1]} ${({ theme }) => theme.spacing[3]};
  background: ${({ theme }) => theme.colors.primary[100]};
  color: ${({ theme }) => theme.colors.primary[800]};
  border-radius: ${({ theme }) => theme.borderRadius.full};
  font-size: ${({ theme }) => theme.fontSizes.sm};
  font-weight: ${({ theme }) => theme.fontWeights.medium};
`;

const DistrictInfo = styled.div`
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

const StatsRow = styled.div`
  display: flex;
  justify-content: space-around;
  margin-top: ${({ theme }) => theme.spacing[4]};
  padding-top: ${({ theme }) => theme.spacing[4]};
  border-top: 1px solid ${({ theme }) => theme.colors.gray[200]};
`;

const StatItem = styled.div`
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: ${({ theme }) => theme.spacing[1]};
`;

const StatValue = styled.span`
  font-size: ${({ theme }) => theme.fontSizes.xl};
  font-weight: ${({ theme }) => theme.fontWeights.bold};
  color: ${({ theme }) => theme.colors.primary[600]};
`;

const StatLabel = styled.span`
  font-size: ${({ theme }) => theme.fontSizes.xs};
  color: ${({ theme }) => theme.colors.gray[600]};
  text-align: center;
`;

interface DistrictListProps {
  onDistrictClick: (id: string) => void;
}

const DistrictList: React.FC<DistrictListProps> = ({ onDistrictClick }) => {
  const { t } = useTranslation();
  const dispatch = useDispatch();
  const { districts, isLoading, error } = useSelector((state: RootState) => state.districts);

  useEffect(() => {
    loadDistricts();
  }, []);

  const loadDistricts = async () => {
    try {
      dispatch(setLoading(true));
      const data = await districtsService.getDistricts();
      dispatch(setDistricts(data));
    } catch (err: any) {
      dispatch(setError(err.message || 'Failed to load districts'));
    } finally {
      dispatch(setLoading(false));
    }
  };

  const formatNumber = (num: number) => {
    return new Intl.NumberFormat('en-IN').format(num);
  };

  if (isLoading) {
    return <LoadingSpinner />;
  }

  if (error) {
    return <div>Error: {error}</div>;
  }

  return (
    <ListContainer>
      <DistrictGrid>
        {districts.map(district => (
          <DistrictCard key={district.id} onClick={() => onDistrictClick(district.id)}>
            <DistrictHeader>
              <DistrictName>{district.name}</DistrictName>
              <DistrictCode>{district.code}</DistrictCode>
            </DistrictHeader>

            <DistrictInfo>
              <InfoRow>
                <span>🏛️</span>
                <span>{t('districts.headquarters')}: {district.headquarters}</span>
              </InfoRow>
              <InfoRow>
                <span>📏</span>
                <span>{t('districts.area')}: {formatNumber(district.area)} km²</span>
              </InfoRow>
              <InfoRow>
                <span>👥</span>
                <span>{t('districts.population')}: {formatNumber(district.population)}</span>
              </InfoRow>
            </DistrictInfo>

            <StatsRow>
              <StatItem>
                <StatValue>{district.playerCount}</StatValue>
                <StatLabel>{t('districts.players')}</StatLabel>
              </StatItem>
              <StatItem>
                <StatValue>{district.activeClubs}</StatValue>
                <StatLabel>{t('districts.clubs')}</StatLabel>
              </StatItem>
            </StatsRow>
          </DistrictCard>
        ))}
      </DistrictGrid>

      {districts.length === 0 && (
        <div style={{ textAlign: 'center', padding: '2rem', color: '#666' }}>
          {t('districts.noDistricts')}
        </div>
      )}
    </ListContainer>
  );
};

export default DistrictList;
