import React, { useState, useEffect } from 'react';
import styled from 'styled-components';
import { useTranslation } from 'react-i18next';
import { useDispatch } from 'react-redux';
import { setSelectedPlayer, setError } from '../../store/slices/playersSlice';
import { playersService } from '../../services/playersService';
import { Player } from '../../store/slices/playersSlice';
import Card from '../UI/Card';
import Button from '../UI/Button';
import LoadingSpinner from '../UI/LoadingSpinner';
import AchievementTimeline from './AchievementTimeline';
import PlayerStatistics from './PlayerStatistics';

const ProfileContainer = styled.div`
  max-width: 1000px;
  margin: 0 auto;
  padding: ${({ theme }) => theme.spacing[6]};
`;

const ProfileHeader = styled.div`
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  margin-bottom: ${({ theme }) => theme.spacing[8]};
  
  @media (min-width: ${({ theme }) => theme.breakpoints.md}) {
    flex-direction: row;
    text-align: left;
  }
`;

const ProfileImage = styled.div`
  width: 150px;
  height: 150px;
  border-radius: 50%;
  background: ${({ theme }) => theme.colors.primary[100]};
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: ${({ theme }) => theme.fontSizes['4xl']};
  font-weight: ${({ theme }) => theme.fontWeights.bold};
  color: ${({ theme }) => theme.colors.primary[600]};
  margin-bottom: ${({ theme }) => theme.spacing[4]};
  flex-shrink: 0;
  
  @media (min-width: ${({ theme }) => theme.breakpoints.md}) {
    margin-bottom: 0;
    margin-right: ${({ theme }) => theme.spacing[6]};
  }
  
  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
    border-radius: 50%;
  }
`;

const ProfileInfo = styled.div`
  flex: 1;
`;

const ProfileName = styled.h1`
  font-size: ${({ theme }) => theme.fontSizes['3xl']};
  font-weight: ${({ theme }) => theme.fontWeights.bold};
  color: ${({ theme }) => theme.colors.gray[900]};
  margin-bottom: ${({ theme }) => theme.spacing[2]};
`;

const ProfileCategory = styled.h2`
  font-size: ${({ theme }) => theme.fontSizes.xl};
  font-weight: ${({ theme }) => theme.fontWeights.semibold};
  color: ${({ theme }) => theme.colors.primary[600]};
  margin-bottom: ${({ theme }) => theme.spacing[4]};
`;

const ProfileActions = styled.div`
  display: flex;
  gap: ${({ theme }) => theme.spacing[3]};
  flex-wrap: wrap;
  justify-content: center;
  
  @media (min-width: ${({ theme }) => theme.breakpoints.md}) {
    justify-content: flex-start;
  }
`;

const ProfileDetails = styled.div`
  display: grid;
  gap: ${({ theme }) => theme.spacing[6]};
  margin-bottom: ${({ theme }) => theme.spacing[8]};
  
  @media (min-width: ${({ theme }) => theme.breakpoints.lg}) {
    grid-template-columns: 1fr 1fr;
  }
`;

const DetailCard = styled(Card)`
  height: fit-content;
`;

const DetailItem = styled.div`
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  padding: ${({ theme }) => theme.spacing[3]} 0;
  border-bottom: 1px solid ${({ theme }) => theme.colors.gray[100]};
  
  &:last-child {
    border-bottom: none;
  }
`;

const DetailLabel = styled.span`
  font-weight: ${({ theme }) => theme.fontWeights.medium};
  color: ${({ theme }) => theme.colors.gray[700]};
  font-size: ${({ theme }) => theme.fontSizes.sm};
  flex-shrink: 0;
  margin-right: ${({ theme }) => theme.spacing[4]};
`;

const DetailValue = styled.span`
  color: ${({ theme }) => theme.colors.gray[900]};
  font-size: ${({ theme }) => theme.fontSizes.sm};
  text-align: right;
  word-break: break-word;
`;

const TagList = styled.div`
  display: flex;
  flex-wrap: wrap;
  gap: ${({ theme }) => theme.spacing[2]};
  margin-bottom: ${({ theme }) => theme.spacing[4]};
`;

const Tag = styled.span<{ variant?: 'primary' | 'secondary' | 'success' | 'warning' }>`
  padding: ${({ theme }) => theme.spacing[1]} ${({ theme }) => theme.spacing[2]};
  border-radius: ${({ theme }) => theme.borderRadius.base};
  font-size: ${({ theme }) => theme.fontSizes.xs};
  font-weight: ${({ theme }) => theme.fontWeights.medium};
  
  ${({ variant, theme }) => {
    switch (variant) {
      case 'primary':
        return `
          background: ${theme.colors.primary[100]};
          color: ${theme.colors.primary[700]};
        `;
      case 'success':
        return `
          background: ${theme.colors.success[100]};
          color: ${theme.colors.success[700]};
        `;
      case 'warning':
        return `
          background: ${theme.colors.warning[100]};
          color: ${theme.colors.warning[700]};
        `;
      default:
        return `
          background: ${theme.colors.gray[100]};
          color: ${theme.colors.gray[700]};
        `;
    }
  }}
`;

const TabContainer = styled.div`
  margin-top: ${({ theme }) => theme.spacing[8]};
`;

const TabList = styled.div`
  display: flex;
  border-bottom: 1px solid ${({ theme }) => theme.colors.gray[200]};
  margin-bottom: ${({ theme }) => theme.spacing[6]};
`;

const Tab = styled.button<{ active: boolean }>`
  padding: ${({ theme }) => theme.spacing[3]} ${({ theme }) => theme.spacing[4]};
  border: none;
  background: none;
  font-size: ${({ theme }) => theme.fontSizes.sm};
  font-weight: ${({ theme }) => theme.fontWeights.medium};
  color: ${({ active, theme }) => active ? theme.colors.primary[600] : theme.colors.gray[600]};
  border-bottom: 2px solid ${({ active, theme }) => active ? theme.colors.primary[600] : 'transparent'};
  cursor: pointer;
  transition: all ${({ theme }) => theme.transitions.fast};
  
  &:hover {
    color: ${({ theme }) => theme.colors.primary[600]};
  }
`;

const TabContent = styled.div`
  min-height: 300px;
`;

interface PlayerProfileProps {
  playerId: string;
  showAdminActions?: boolean;
  onEdit?: (player: Player) => void;
  onBack?: () => void;
}

const PlayerProfile: React.FC<PlayerProfileProps> = ({
  playerId,
  showAdminActions = false,
  onEdit,
  onBack,
}) => {
  const { t } = useTranslation();
  const dispatch = useDispatch();
  const [player, setPlayer] = useState<Player | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setErrorState] = useState<string | null>(null);
  const [activeTab, setActiveTab] = useState<'achievements' | 'statistics'>('achievements');

  useEffect(() => {
    loadPlayer();
  }, [playerId]);

  const loadPlayer = async () => {
    try {
      setIsLoading(true);
      setErrorState(null);
      const playerData = await playersService.getPlayerById(playerId);
      setPlayer(playerData);
      dispatch(setSelectedPlayer(playerData));
    } catch (err) {
      const errorMessage = err instanceof Error ? err.message : 'Failed to load player';
      setErrorState(errorMessage);
      dispatch(setError(errorMessage));
    } finally {
      setIsLoading(false);
    }
  };

  const getInitials = (name: string) => {
    return name
      .split(' ')
      .map(word => word.charAt(0))
      .join('')
      .toUpperCase()
      .slice(0, 2);
  };

  const calculateAge = (dateOfBirth: string) => {
    const today = new Date();
    const birthDate = new Date(dateOfBirth);
    let age = today.getFullYear() - birthDate.getFullYear();
    const monthDiff = today.getMonth() - birthDate.getMonth();
    
    if (monthDiff < 0 || (monthDiff === 0 && today.getDate() < birthDate.getDate())) {
      age--;
    }
    
    return age;
  };

  const formatDate = (dateString: string) => {
    return new Date(dateString).toLocaleDateString('en-IN', {
      year: 'numeric',
      month: 'long',
      day: 'numeric',
    });
  };

  const formatCategory = (category: string) => {
    const categoryMap: { [key: string]: string } = {
      'MEN': t('players.category.men'),
      'WOMEN': t('players.category.women'),
      'JUNIOR': t('players.category.junior'),
      'SENIOR': t('players.category.senior'),
    };
    return categoryMap[category] || category;
  };

  const formatGender = (gender: string) => {
    const genderMap: { [key: string]: string } = {
      'MALE': t('players.gender.male'),
      'FEMALE': t('players.gender.female'),
    };
    return genderMap[gender] || gender;
  };

  if (isLoading) {
    return <LoadingSpinner fullScreen text={t('players.loading')} />;
  }

  if (error || !player) {
    return (
      <ProfileContainer>
        <Card>
          <Card.Content>
            <div style={{ textAlign: 'center', color: '#dc2626' }}>
              <h3>{t('common.error')}</h3>
              <p>{error || t('players.notFound')}</p>
              <div style={{ marginTop: '16px', display: 'flex', gap: '12px', justifyContent: 'center' }}>
                <Button onClick={loadPlayer}>{t('common.retry')}</Button>
                {onBack && (
                  <Button variant="secondary" onClick={onBack}>
                    {t('common.back')}
                  </Button>
                )}
              </div>
            </div>
          </Card.Content>
        </Card>
      </ProfileContainer>
    );
  }

  return (
    <ProfileContainer>
      <ProfileHeader>
        <ProfileImage>
          {player.profilePhotoUrl ? (
            <img src={player.profilePhotoUrl} alt={player.name} />
          ) : (
            getInitials(player.name)
          )}
        </ProfileImage>
        
        <ProfileInfo>
          <ProfileName>{player.name}</ProfileName>
          <ProfileCategory>{formatCategory(player.category)}</ProfileCategory>
          
          <TagList>
            {player.isProminent && (
              <Tag variant="primary">{t('players.prominent')}</Tag>
            )}
            {player.statistics?.currentRanking && (
              <Tag variant="secondary">#{player.statistics.currentRanking}</Tag>
            )}
            {player.isActive ? (
              <Tag variant="success">{t('common.active')}</Tag>
            ) : (
              <Tag>{t('common.inactive')}</Tag>
            )}
          </TagList>
          
          <ProfileActions>
            {showAdminActions && onEdit && (
              <Button variant="secondary" onClick={() => onEdit(player)}>
                {t('common.edit')}
              </Button>
            )}
            {onBack && (
              <Button variant="secondary" onClick={onBack}>
                {t('common.back')}
              </Button>
            )}
          </ProfileActions>
        </ProfileInfo>
      </ProfileHeader>

      <ProfileDetails>
        <DetailCard>
          <Card.Header>
            <Card.Title>{t('players.personalInformation')}</Card.Title>
          </Card.Header>
          <Card.Content>
            <DetailItem>
              <DetailLabel>{t('players.age')}</DetailLabel>
              <DetailValue>
                {player.dateOfBirth ? `${calculateAge(player.dateOfBirth)} years` : t('common.notSpecified')}
              </DetailValue>
            </DetailItem>
            <DetailItem>
              <DetailLabel>{t('players.dateOfBirth')}</DetailLabel>
              <DetailValue>
                {player.dateOfBirth ? formatDate(player.dateOfBirth) : t('common.notSpecified')}
              </DetailValue>
            </DetailItem>
            <DetailItem>
              <DetailLabel>{t('players.gender')}</DetailLabel>
              <DetailValue>
                {player.gender ? formatGender(player.gender) : t('common.notSpecified')}
              </DetailValue>
            </DetailItem>
            <DetailItem>
              <DetailLabel>{t('players.district')}</DetailLabel>
              <DetailValue>{player.districtName || t('common.notSpecified')}</DetailValue>
            </DetailItem>
          </Card.Content>
        </DetailCard>

        <DetailCard>
          <Card.Header>
            <Card.Title>{t('players.contactInformation')}</Card.Title>
          </Card.Header>
          <Card.Content>
            <DetailItem>
              <DetailLabel>{t('players.email')}</DetailLabel>
              <DetailValue>{player.contactEmail || t('common.notProvided')}</DetailValue>
            </DetailItem>
            <DetailItem>
              <DetailLabel>{t('players.phone')}</DetailLabel>
              <DetailValue>{player.contactPhone || t('common.notProvided')}</DetailValue>
            </DetailItem>
            <DetailItem>
              <DetailLabel>{t('players.address')}</DetailLabel>
              <DetailValue>{player.address || t('common.notProvided')}</DetailValue>
            </DetailItem>
            <DetailItem>
              <DetailLabel>{t('players.joinedOn')}</DetailLabel>
              <DetailValue>{formatDate(player.createdAt)}</DetailValue>
            </DetailItem>
          </Card.Content>
        </DetailCard>
      </ProfileDetails>

      <TabContainer>
        <TabList>
          <Tab
            active={activeTab === 'achievements'}
            onClick={() => setActiveTab('achievements')}
          >
            {t('players.achievements')} ({player.totalAchievements || 0})
          </Tab>
          <Tab
            active={activeTab === 'statistics'}
            onClick={() => setActiveTab('statistics')}
          >
            {t('players.statistics')}
          </Tab>
        </TabList>

        <TabContent>
          {activeTab === 'achievements' && (
            <AchievementTimeline 
              playerId={player.id}
              achievements={player.recentAchievements || []}
            />
          )}
          {activeTab === 'statistics' && (
            <PlayerStatistics 
              playerId={player.id}
              statistics={player.statistics}
            />
          )}
        </TabContent>
      </TabContainer>
    </ProfileContainer>
  );
};

export default PlayerProfile;