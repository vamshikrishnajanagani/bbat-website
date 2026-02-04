import React, { useState } from 'react';
import styled from 'styled-components';
import { useTranslation } from 'react-i18next';
import { District } from '../../store/slices/districtsSlice';

const MapContainer = styled.div`
  background: white;
  border-radius: ${({ theme }) => theme.borderRadius.lg};
  padding: ${({ theme }) => theme.spacing[6]};
  box-shadow: ${({ theme }) => theme.shadows.md};
`;

const MapTitle = styled.h3`
  font-size: ${({ theme }) => theme.fontSizes.xl};
  font-weight: ${({ theme }) => theme.fontWeights.semibold};
  color: ${({ theme }) => theme.colors.gray[900]};
  margin-bottom: ${({ theme }) => theme.spacing[4]};
`;

const MapWrapper = styled.div`
  position: relative;
  width: 100%;
  height: 600px;
  background: ${({ theme }) => theme.colors.gray[100]};
  border-radius: ${({ theme }) => theme.borderRadius.md};
  overflow: hidden;
`;

const SVGMap = styled.svg`
  width: 100%;
  height: 100%;
`;

const DistrictPath = styled.path<{ isSelected: boolean }>`
  fill: ${({ isSelected, theme }) => 
    isSelected ? theme.colors.primary[500] : theme.colors.primary[200]};
  stroke: ${({ theme }) => theme.colors.primary[700]};
  stroke-width: 1;
  cursor: pointer;
  transition: fill 0.2s;

  &:hover {
    fill: ${({ theme }) => theme.colors.primary[400]};
  }
`;

const DistrictLabel = styled.text`
  font-size: 12px;
  font-weight: ${({ theme }) => theme.fontWeights.medium};
  fill: ${({ theme }) => theme.colors.gray[900]};
  pointer-events: none;
  text-anchor: middle;
`;

const Legend = styled.div`
  display: flex;
  gap: ${({ theme }) => theme.spacing[4]};
  margin-top: ${({ theme }) => theme.spacing[4]};
  padding: ${({ theme }) => theme.spacing[4]};
  background: ${({ theme }) => theme.colors.gray[50]};
  border-radius: ${({ theme }) => theme.borderRadius.md};
`;

const LegendItem = styled.div`
  display: flex;
  align-items: center;
  gap: ${({ theme }) => theme.spacing[2]};
  font-size: ${({ theme }) => theme.fontSizes.sm};
  color: ${({ theme }) => theme.colors.gray[700]};
`;

const LegendColor = styled.div<{ color: string }>`
  width: 20px;
  height: 20px;
  background: ${({ color }) => color};
  border-radius: ${({ theme }) => theme.borderRadius.sm};
  border: 1px solid ${({ theme }) => theme.colors.gray[300]};
`;

const InfoPanel = styled.div`
  margin-top: ${({ theme }) => theme.spacing[4]};
  padding: ${({ theme }) => theme.spacing[4]};
  background: ${({ theme }) => theme.colors.gray[50]};
  border-radius: ${({ theme }) => theme.borderRadius.md};
`;

const InfoTitle = styled.h4`
  font-size: ${({ theme }) => theme.fontSizes.lg};
  font-weight: ${({ theme }) => theme.fontWeights.semibold};
  color: ${({ theme }) => theme.colors.gray[900]};
  margin-bottom: ${({ theme }) => theme.spacing[2]};
`;

const InfoText = styled.p`
  font-size: ${({ theme }) => theme.fontSizes.sm};
  color: ${({ theme }) => theme.colors.gray[600]};
  margin: ${({ theme }) => theme.spacing[1]} 0;
`;

const PlaceholderMessage = styled.div`
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100%;
  color: ${({ theme }) => theme.colors.gray[500]};
  font-size: ${({ theme }) => theme.fontSizes.lg};
  text-align: center;
  padding: ${({ theme }) => theme.spacing[8]};
`;

interface DistrictMapProps {
  districts: District[];
  selectedDistrictId?: string;
  onDistrictClick?: (id: string) => void;
}

const DistrictMap: React.FC<DistrictMapProps> = ({ 
  districts, 
  selectedDistrictId,
  onDistrictClick 
}) => {
  const { t } = useTranslation();
  const [hoveredDistrict, setHoveredDistrict] = useState<District | null>(null);

  // Simplified district coordinates for Telangana (placeholder data)
  // In a real implementation, this would use actual GeoJSON data
  const districtPaths: Record<string, string> = {
    'Hyderabad': 'M 300,250 L 350,240 L 360,280 L 320,290 Z',
    'Rangareddy': 'M 250,280 L 300,250 L 320,290 L 280,310 Z',
    'Medchal-Malkajgiri': 'M 300,200 L 350,190 L 350,240 L 300,250 Z',
    'Warangal': 'M 400,150 L 480,140 L 490,200 L 420,210 Z',
    'Karimnagar': 'M 350,100 L 430,90 L 440,150 L 370,160 Z',
    'Khammam': 'M 500,250 L 580,240 L 590,300 L 520,310 Z',
    'Nalgonda': 'M 400,280 L 480,270 L 490,330 L 420,340 Z',
    'Nizamabad': 'M 250,80 L 330,70 L 340,130 L 270,140 Z',
    'Adilabad': 'M 300,30 L 380,20 L 390,80 L 320,90 Z',
    'Mahbubnagar': 'M 200,350 L 280,340 L 290,400 L 220,410 Z',
  };

  const handleDistrictClick = (district: District) => {
    if (onDistrictClick) {
      onDistrictClick(district.id);
    }
  };

  const selectedDistrict = districts.find(d => d.id === selectedDistrictId);
  const displayDistrict = hoveredDistrict || selectedDistrict;

  return (
    <MapContainer>
      <MapTitle>{t('districts.map.title')}</MapTitle>
      
      <MapWrapper>
        <SVGMap viewBox="0 0 800 500">
          {districts.map(district => {
            const path = districtPaths[district.name];
            if (!path) return null;

            return (
              <g key={district.id}>
                <DistrictPath
                  d={path}
                  isSelected={district.id === selectedDistrictId}
                  onClick={() => handleDistrictClick(district)}
                  onMouseEnter={() => setHoveredDistrict(district)}
                  onMouseLeave={() => setHoveredDistrict(null)}
                />
                <DistrictLabel
                  x={district.coordinates.longitude * 10}
                  y={district.coordinates.latitude * 10}
                >
                  {district.code}
                </DistrictLabel>
              </g>
            );
          })}
        </SVGMap>

        {districts.length === 0 && (
          <PlaceholderMessage>
            {t('districts.map.placeholder')}
            <br />
            <small>{t('districts.map.interactiveMapComingSoon')}</small>
          </PlaceholderMessage>
        )}
      </MapWrapper>

      <Legend>
        <LegendItem>
          <LegendColor color="#93C5FD" />
          <span>{t('districts.map.legend.default')}</span>
        </LegendItem>
        <LegendItem>
          <LegendColor color="#3B82F6" />
          <span>{t('districts.map.legend.selected')}</span>
        </LegendItem>
      </Legend>

      {displayDistrict && (
        <InfoPanel>
          <InfoTitle>{displayDistrict.name}</InfoTitle>
          <InfoText>
            {t('districts.headquarters')}: {displayDistrict.headquarters}
          </InfoText>
          <InfoText>
            {t('districts.players')}: {displayDistrict.playerCount}
          </InfoText>
          <InfoText>
            {t('districts.clubs')}: {displayDistrict.activeClubs}
          </InfoText>
        </InfoPanel>
      )}
    </MapContainer>
  );
};

export default DistrictMap;
