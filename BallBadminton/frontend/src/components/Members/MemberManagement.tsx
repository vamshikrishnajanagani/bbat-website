import React, { useState, useEffect } from 'react';
import styled from 'styled-components';
import { useTranslation } from 'react-i18next';
import { useDispatch, useSelector } from 'react-redux';
import { RootState } from '../../store';
import { setMembers, setLoading, setError, addMember, updateMember, removeMember } from '../../store/slices/membersSlice';
import { membersService } from '../../services/membersService';
import { Member } from '../../store/slices/membersSlice';
import DataTable, { Column } from '../UI/DataTable';
import Button from '../UI/Button';
import Modal from '../UI/Modal';
import LoadingSpinner from '../UI/LoadingSpinner';
import MemberForm from './MemberForm';
import MemberProfile from './MemberProfile';

const ManagementContainer = styled.div`
  padding: ${({ theme }) => theme.spacing[6]};
`;

const ManagementHeader = styled.div`
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: ${({ theme }) => theme.spacing[6]};
  flex-wrap: wrap;
  gap: ${({ theme }) => theme.spacing[4]};
`;

const ManagementTitle = styled.h1`
  font-size: ${({ theme }) => theme.fontSizes['2xl']};
  font-weight: ${({ theme }) => theme.fontWeights.bold};
  color: ${({ theme }) => theme.colors.gray[900]};
  margin: 0;
`;

const ActionButtons = styled.div`
  display: flex;
  gap: ${({ theme }) => theme.spacing[3]};
  flex-wrap: wrap;
`;

const StatsGrid = styled.div`
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: ${({ theme }) => theme.spacing[4]};
  margin-bottom: ${({ theme }) => theme.spacing[6]};
`;

const StatCard = styled.div`
  background: white;
  padding: ${({ theme }) => theme.spacing[4]};
  border-radius: ${({ theme }) => theme.borderRadius.lg};
  box-shadow: ${({ theme }) => theme.shadows.sm};
  border: 1px solid ${({ theme }) => theme.colors.gray[200]};
  
  .stat-value {
    font-size: ${({ theme }) => theme.fontSizes['2xl']};
    font-weight: ${({ theme }) => theme.fontWeights.bold};
    color: ${({ theme }) => theme.colors.primary[600]};
    margin-bottom: ${({ theme }) => theme.spacing[1]};
  }
  
  .stat-label {
    font-size: ${({ theme }) => theme.fontSizes.sm};
    color: ${({ theme }) => theme.colors.gray[600]};
    text-transform: uppercase;
    letter-spacing: 0.05em;
  }
`;

const ConfirmDialog = styled.div`
  text-align: center;
  padding: ${({ theme }) => theme.spacing[4]};
  
  h3 {
    margin: 0 0 ${({ theme }) => theme.spacing[3]} 0;
    color: ${({ theme }) => theme.colors.gray[900]};
    font-size: ${({ theme }) => theme.fontSizes.lg};
  }
  
  p {
    margin: 0 0 ${({ theme }) => theme.spacing[6]} 0;
    color: ${({ theme }) => theme.colors.gray[600]};
    font-size: ${({ theme }) => theme.fontSizes.sm};
  }
  
  .actions {
    display: flex;
    gap: ${({ theme }) => theme.spacing[3]};
    justify-content: center;
  }
`;

interface MemberManagementProps {
  className?: string;
}

const MemberManagement: React.FC<MemberManagementProps> = ({ className }) => {
  const { t } = useTranslation();
  const dispatch = useDispatch();
  const { members, isLoading, error } = useSelector((state: RootState) => state.members);
  
  const [showForm, setShowForm] = useState(false);
  const [showProfile, setShowProfile] = useState(false);
  const [selectedMember, setSelectedMember] = useState<Member | null>(null);
  const [memberToDelete, setMemberToDelete] = useState<Member | null>(null);
  const [isDeleting, setIsDeleting] = useState(false);

  useEffect(() => {
    loadMembers();
  }, []);

  const loadMembers = async () => {
    try {
      dispatch(setLoading(true));
      const membersData = await membersService.getMembers();
      dispatch(setMembers(membersData));
    } catch (err) {
      dispatch(setError(err instanceof Error ? err.message : 'Failed to load members'));
    } finally {
      dispatch(setLoading(false));
    }
  };

  const handleCreateMember = () => {
    setSelectedMember(null);
    setShowForm(true);
  };

  const handleEditMember = (member: Member) => {
    setSelectedMember(member);
    setShowForm(true);
  };

  const handleViewMember = (member: Member) => {
    setSelectedMember(member);
    setShowProfile(true);
  };

  const handleDeleteMember = (member: Member) => {
    setMemberToDelete(member);
  };

  const confirmDelete = async () => {
    if (!memberToDelete) return;

    try {
      setIsDeleting(true);
      await membersService.deleteMember(memberToDelete.id);
      dispatch(removeMember(memberToDelete.id));
      setMemberToDelete(null);
    } catch (err) {
      dispatch(setError(err instanceof Error ? err.message : 'Failed to delete member'));
    } finally {
      setIsDeleting(false);
    }
  };

  const handleFormSuccess = (member: Member) => {
    if (selectedMember) {
      dispatch(updateMember(member));
    } else {
      dispatch(addMember(member));
    }
    setShowForm(false);
    setSelectedMember(null);
  };

  const getStats = () => {
    const activeMembers = members.filter(m => m.isActive).length;
    const prominentMembers = members.filter(m => m.isProminent).length;
    const executiveMembers = members.filter(m => m.hierarchyLevel === 0).length;
    
    return {
      total: members.length,
      active: activeMembers,
      prominent: prominentMembers,
      executive: executiveMembers,
    };
  };

  const tableColumns: Column<Member>[] = [
    {
      key: 'name',
      title: t('members.name'),
      sortable: true,
      render: (value, member) => (
        <div style={{ display: 'flex', alignItems: 'center', gap: '12px' }}>
          <div
            style={{
              width: '40px',
              height: '40px',
              borderRadius: '50%',
              background: member.photoUrl ? `url(${member.photoUrl})` : '#e5e7eb',
              backgroundSize: 'cover',
              backgroundPosition: 'center',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              color: '#6b7280',
              fontWeight: 'bold',
            }}
          >
            {!member.photoUrl && member.name.charAt(0).toUpperCase()}
          </div>
          <div>
            <div style={{ fontWeight: '600' }}>{value}</div>
            <div style={{ fontSize: '14px', color: '#6b7280' }}>{member.position}</div>
          </div>
        </div>
      ),
    },
    {
      key: 'email',
      title: t('members.email'),
      sortable: true,
      render: (value) => value || t('common.notProvided'),
    },
    {
      key: 'phone',
      title: t('members.phone'),
      sortable: true,
      render: (value) => value || t('common.notProvided'),
    },
    {
      key: 'hierarchyLevel',
      title: t('members.hierarchy'),
      sortable: true,
      render: (value) => {
        const level = value || 0;
        const labels = {
          0: t('members.hierarchy.executive'),
          1: t('members.hierarchy.senior'),
          2: t('members.hierarchy.general'),
        };
        return labels[level as keyof typeof labels] || `Level ${level}`;
      },
    },
    {
      key: 'isActive',
      title: t('members.status'),
      sortable: true,
      render: (value) => (
        <span
          style={{
            padding: '4px 8px',
            borderRadius: '12px',
            fontSize: '12px',
            fontWeight: '500',
            background: value ? '#dcfce7' : '#fef2f2',
            color: value ? '#166534' : '#dc2626',
          }}
        >
          {value ? t('common.active') : t('common.inactive')}
        </span>
      ),
    },
    {
      key: 'actions',
      title: t('common.actions'),
      render: (_, member) => (
        <div style={{ display: 'flex', gap: '8px' }}>
          <Button
            size="sm"
            variant="secondary"
            onClick={() => handleViewMember(member)}
          >
            {t('common.view')}
          </Button>
          <Button
            size="sm"
            variant="secondary"
            onClick={() => handleEditMember(member)}
          >
            {t('common.edit')}
          </Button>
          <Button
            size="sm"
            variant="error"
            onClick={() => handleDeleteMember(member)}
          >
            {t('common.delete')}
          </Button>
        </div>
      ),
    },
  ];

  const stats = getStats();

  if (isLoading && members.length === 0) {
    return <LoadingSpinner fullScreen text={t('members.loading')} />;
  }

  return (
    <ManagementContainer className={className}>
      <ManagementHeader>
        <ManagementTitle>{t('members.management.title')}</ManagementTitle>
        <ActionButtons>
          <Button onClick={loadMembers} variant="secondary">
            {t('common.refresh')}
          </Button>
          <Button onClick={handleCreateMember}>
            {t('members.management.addMember')}
          </Button>
        </ActionButtons>
      </ManagementHeader>

      <StatsGrid>
        <StatCard>
          <div className="stat-value">{stats.total}</div>
          <div className="stat-label">{t('members.stats.total')}</div>
        </StatCard>
        <StatCard>
          <div className="stat-value">{stats.active}</div>
          <div className="stat-label">{t('members.stats.active')}</div>
        </StatCard>
        <StatCard>
          <div className="stat-value">{stats.prominent}</div>
          <div className="stat-label">{t('members.stats.prominent')}</div>
        </StatCard>
        <StatCard>
          <div className="stat-value">{stats.executive}</div>
          <div className="stat-label">{t('members.stats.executive')}</div>
        </StatCard>
      </StatsGrid>

      {error && (
        <div style={{ 
          background: '#fef2f2', 
          border: '1px solid #fecaca', 
          color: '#dc2626', 
          padding: '12px', 
          borderRadius: '8px', 
          marginBottom: '24px',
          textAlign: 'center'
        }}>
          {error}
        </div>
      )}

      <DataTable
        data={members}
        columns={tableColumns}
        title={t('members.management.memberList')}
        searchPlaceholder={t('members.search.placeholder')}
        loading={isLoading}
        emptyTitle={t('members.empty.title')}
        emptyDescription={t('members.empty.description')}
        filters={[
          {
            key: 'isActive',
            label: t('members.filter.status'),
            options: [
              { value: 'true', label: t('common.active') },
              { value: 'false', label: t('common.inactive') },
            ],
          },
          {
            key: 'hierarchyLevel',
            label: t('members.filter.hierarchy'),
            options: [
              { value: '0', label: t('members.hierarchy.executive') },
              { value: '1', label: t('members.hierarchy.senior') },
              { value: '2', label: t('members.hierarchy.general') },
            ],
          },
        ]}
      />

      {/* Member Form Modal */}
      <Modal
        isOpen={showForm}
        onClose={() => {
          setShowForm(false);
          setSelectedMember(null);
        }}
        title={selectedMember ? t('members.management.editMember') : t('members.management.addMember')}
        size="lg"
      >
        <MemberForm
          member={selectedMember || undefined}
          onSuccess={handleFormSuccess}
          onCancel={() => {
            setShowForm(false);
            setSelectedMember(null);
          }}
        />
      </Modal>

      {/* Member Profile Modal */}
      <Modal
        isOpen={showProfile}
        onClose={() => {
          setShowProfile(false);
          setSelectedMember(null);
        }}
        title={selectedMember?.name}
        size="lg"
      >
        {selectedMember && (
          <MemberProfile
            memberId={selectedMember.id}
            showContactForm={false}
            showAdminActions={true}
            onEdit={(member) => {
              setShowProfile(false);
              handleEditMember(member);
            }}
            onBack={() => {
              setShowProfile(false);
              setSelectedMember(null);
            }}
          />
        )}
      </Modal>

      {/* Delete Confirmation Modal */}
      <Modal
        isOpen={!!memberToDelete}
        onClose={() => setMemberToDelete(null)}
        title={t('members.management.deleteMember')}
        size="sm"
      >
        <ConfirmDialog>
          <h3>{t('common.confirmDelete')}</h3>
          <p>
            {t('members.management.deleteConfirmation', { 
              name: memberToDelete?.name 
            })}
          </p>
          <div className="actions">
            <Button
              variant="secondary"
              onClick={() => setMemberToDelete(null)}
              disabled={isDeleting}
            >
              {t('common.cancel')}
            </Button>
            <Button
              variant="error"
              onClick={confirmDelete}
              disabled={isDeleting}
            >
              {isDeleting ? t('common.deleting') : t('common.delete')}
            </Button>
          </div>
        </ConfirmDialog>
      </Modal>
    </ManagementContainer>
  );
};

export default MemberManagement;