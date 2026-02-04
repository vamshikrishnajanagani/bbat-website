import React, { useState, useEffect } from 'react';
import styled from 'styled-components';
import { useTranslation } from 'react-i18next';
import { useDispatch } from 'react-redux';
import { setSelectedMember, setError } from '../../store/slices/membersSlice';
import { membersService } from '../../services/membersService';
import { Member } from '../../store/slices/membersSlice';
import Card from '../UI/Card';
import Button from '../UI/Button';
import LoadingSpinner from '../UI/LoadingSpinner';
import ContactForm from './ContactForm';
import Modal from '../UI/Modal';

const ProfileContainer = styled.div`
  max-width: 800px;
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

const ProfilePosition = styled.h2`
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
  
  @media (min-width: ${({ theme }) => theme.breakpoints.md}) {
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

const BiographySection = styled.div`
  grid-column: 1 / -1;
`;

const BiographyText = styled.p`
  color: ${({ theme }) => theme.colors.gray[700]};
  line-height: ${({ theme }) => theme.lineHeights.relaxed};
  font-size: ${({ theme }) => theme.fontSizes.base};
  margin: 0;
  white-space: pre-wrap;
`;

const StatusBadge = styled.span<{ active: boolean }>`
  padding: ${({ theme }) => theme.spacing[1]} ${({ theme }) => theme.spacing[3]};
  border-radius: ${({ theme }) => theme.borderRadius.full};
  font-size: ${({ theme }) => theme.fontSizes.xs};
  font-weight: ${({ theme }) => theme.fontWeights.medium};
  background: ${({ active, theme }) => active ? theme.colors.success[100] : theme.colors.error[100]};
  color: ${({ active, theme }) => active ? theme.colors.success[700] : theme.colors.error[700]};
`;

const TagList = styled.div`
  display: flex;
  flex-wrap: wrap;
  gap: ${({ theme }) => theme.spacing[2]};
`;

const Tag = styled.span<{ variant?: 'primary' | 'secondary' }>`
  padding: ${({ theme }) => theme.spacing[1]} ${({ theme }) => theme.spacing[2]};
  border-radius: ${({ theme }) => theme.borderRadius.base};
  font-size: ${({ theme }) => theme.fontSizes.xs};
  font-weight: ${({ theme }) => theme.fontWeights.medium};
  background: ${({ variant, theme }) => 
    variant === 'primary' ? theme.colors.primary[100] : theme.colors.gray[100]};
  color: ${({ variant, theme }) => 
    variant === 'primary' ? theme.colors.primary[700] : theme.colors.gray[700]};
`;

interface MemberProfileProps {
  memberId: string;
  showContactForm?: boolean;
  showAdminActions?: boolean;
  onEdit?: (member: Member) => void;
  onBack?: () => void;
}

const MemberProfile: React.FC<MemberProfileProps> = ({
  memberId,
  showContactForm = true,
  showAdminActions = false,
  onEdit,
  onBack,
}) => {
  const { t } = useTranslation();
  const dispatch = useDispatch();
  const [member, setMember] = useState<Member | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setErrorState] = useState<string | null>(null);
  const [showContact, setShowContact] = useState(false);

  useEffect(() => {
    loadMember();
  }, [memberId]);

  const loadMember = async () => {
    try {
      setIsLoading(true);
      setErrorState(null);
      const memberData = await membersService.getMemberById(memberId);
      setMember(memberData);
      dispatch(setSelectedMember(memberData));
    } catch (err) {
      const errorMessage = err instanceof Error ? err.message : 'Failed to load member';
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

  const formatDate = (dateString: string) => {
    return new Date(dateString).toLocaleDateString('en-IN', {
      year: 'numeric',
      month: 'long',
      day: 'numeric',
    });
  };

  const formatTenure = (startDate?: string, endDate?: string) => {
    if (!startDate) return t('common.notSpecified');
    
    const start = formatDate(startDate);
    const end = endDate ? formatDate(endDate) : t('members.present');
    
    return `${start} - ${end}`;
  };

  if (isLoading) {
    return <LoadingSpinner fullScreen text={t('members.loading')} />;
  }

  if (error || !member) {
    return (
      <ProfileContainer>
        <Card>
          <Card.Content>
            <div style={{ textAlign: 'center', color: '#dc2626' }}>
              <h3>{t('common.error')}</h3>
              <p>{error || t('members.notFound')}</p>
              <div style={{ marginTop: '16px', display: 'flex', gap: '12px', justifyContent: 'center' }}>
                <Button onClick={loadMember}>{t('common.retry')}</Button>
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
          {member.photoUrl ? (
            <img src={member.photoUrl} alt={member.name} />
          ) : (
            getInitials(member.name)
          )}
        </ProfileImage>
        
        <ProfileInfo>
          <ProfileName>{member.name}</ProfileName>
          <ProfilePosition>{member.position}</ProfilePosition>
          
          <TagList>
            <StatusBadge active={member.isActive}>
              {member.isActive ? t('common.active') : t('common.inactive')}
            </StatusBadge>
            {member.isProminent && (
              <Tag variant="primary">{t('members.prominent')}</Tag>
            )}
            {member.isCurrentlyServing && (
              <Tag variant="secondary">{t('members.currentlyServing')}</Tag>
            )}
          </TagList>
          
          <ProfileActions>
            {showContactForm && (
              <Button onClick={() => setShowContact(true)}>
                {t('members.contact')}
              </Button>
            )}
            {showAdminActions && onEdit && (
              <Button variant="secondary" onClick={() => onEdit(member)}>
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
            <Card.Title>{t('members.contactInformation')}</Card.Title>
          </Card.Header>
          <Card.Content>
            <DetailItem>
              <DetailLabel>{t('members.email')}</DetailLabel>
              <DetailValue>{member.email || t('common.notProvided')}</DetailValue>
            </DetailItem>
            <DetailItem>
              <DetailLabel>{t('members.phone')}</DetailLabel>
              <DetailValue>{member.phone || t('common.notProvided')}</DetailValue>
            </DetailItem>
          </Card.Content>
        </DetailCard>

        <DetailCard>
          <Card.Header>
            <Card.Title>{t('members.organizationDetails')}</Card.Title>
          </Card.Header>
          <Card.Content>
            <DetailItem>
              <DetailLabel>{t('members.hierarchy')}</DetailLabel>
              <DetailValue>Level {member.hierarchyLevel || 0}</DetailValue>
            </DetailItem>
            <DetailItem>
              <DetailLabel>{t('members.tenure')}</DetailLabel>
              <DetailValue>
                {formatTenure(member.tenureStartDate, member.tenureEndDate)}
              </DetailValue>
            </DetailItem>
            <DetailItem>
              <DetailLabel>{t('members.joinedOn')}</DetailLabel>
              <DetailValue>{formatDate(member.createdAt)}</DetailValue>
            </DetailItem>
          </Card.Content>
        </DetailCard>

        {member.biography && (
          <BiographySection>
            <DetailCard>
              <Card.Header>
                <Card.Title>{t('members.biography')}</Card.Title>
              </Card.Header>
              <Card.Content>
                <BiographyText>{member.biography}</BiographyText>
              </Card.Content>
            </DetailCard>
          </BiographySection>
        )}
      </ProfileDetails>

      {showContactForm && (
        <Modal
          isOpen={showContact}
          onClose={() => setShowContact(false)}
          title={t('members.contactForm.title', { name: member.name })}
          size="md"
        >
          <ContactForm
            member={member}
            onSuccess={() => setShowContact(false)}
            onCancel={() => setShowContact(false)}
          />
        </Modal>
      )}
    </ProfileContainer>
  );
};

export default MemberProfile;