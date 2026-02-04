import React, { useState, useEffect } from 'react';
import styled from 'styled-components';
import { useTranslation } from 'react-i18next';
import { useDispatch, useSelector } from 'react-redux';
import { RootState } from '../../store';
import { setMembers, setLoading, setError } from '../../store/slices/membersSlice';
import { membersService } from '../../services/membersService';
import DataTable, { Column } from '../UI/DataTable';
import ProfileCard from '../UI/ProfileCard';
import Button from '../UI/Button';
import LoadingSpinner from '../UI/LoadingSpinner';

const DirectoryContainer = styled.div`
  max-width: 1200px;
  margin: 0 auto;
  padding: ${({ theme }) => theme.spacing[6]};
`;

const DirectoryHeader = styled.div`
  margin-bottom: ${({ theme }) => theme.spacing[8]};
  text-align: center;
`;

const DirectoryTitle = styled.h1`
  font-size: ${({ theme }) => theme.fontSizes['3xl']};
  font-weight: ${({ theme }) => theme.fontWeights.bold};
  color: ${({ theme }) => theme.colors.gray[900]};
  margin-bottom: ${({ theme }) => theme.spacing[4]};
`;

const DirectoryDescription = styled.p`
  font-size: ${({ theme }) => theme.fontSizes.lg};
  color: ${({ theme }) => theme.colors.gray[600]};
  max-width: 600px;
  margin: 0 auto;
  line-height: ${({ theme }) => theme.lineHeights.relaxed};
`;

const ViewToggle = styled.div`
  display: flex;
  justify-content: center;
  margin-bottom: ${({ theme }) => theme.spacing[6]};
  gap: ${({ theme }) => theme.spacing[2]};
`;

const HierarchySection = styled.div`
  margin-bottom: ${({ theme }) => theme.spacing[8]};
`;

const HierarchyTitle = styled.h2`
  font-size: ${({ theme }) => theme.fontSizes['2xl']};
  font-weight: ${({ theme }) => theme.fontWeights.semibold};
  color: ${({ theme }) => theme.colors.gray[900]};
  margin-bottom: ${({ theme }) => theme.spacing[6]};
  text-align: center;
`;

const HierarchyGrid = styled.div`
  display: grid;
  gap: ${({ theme }) => theme.spacing[6]};
  
  @media (min-width: ${({ theme }) => theme.breakpoints.md}) {
    grid-template-columns: repeat(2, 1fr);
  }
  
  @media (min-width: ${({ theme }) => theme.breakpoints.lg}) {
    grid-template-columns: repeat(3, 1fr);
  }
`;

const MembersGrid = styled.div`
  display: grid;
  gap: ${({ theme }) => theme.spacing[4]};
  
  @media (min-width: ${({ theme }) => theme.breakpoints.md}) {
    grid-template-columns: repeat(2, 1fr);
  }
  
  @media (min-width: ${({ theme }) => theme.breakpoints.lg}) {
    grid-template-columns: repeat(3, 1fr);
  }
`;

interface MemberDirectoryProps {
  onMemberClick?: (memberId: string) => void;
  showAdminActions?: boolean;
}

const MemberDirectory: React.FC<MemberDirectoryProps> = ({
  onMemberClick,
  showAdminActions = false,
}) => {
  const { t } = useTranslation();
  const dispatch = useDispatch();
  const { members, isLoading, error } = useSelector((state: RootState) => state.members);
  const [viewMode, setViewMode] = useState<'hierarchy' | 'grid' | 'table'>('hierarchy');

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

  const getHierarchyGroups = () => {
    const groups: { [key: number]: typeof members } = {};
    members.forEach(member => {
      const level = member.hierarchyLevel || 0;
      if (!groups[level]) {
        groups[level] = [];
      }
      groups[level].push(member);
    });
    
    // Sort by hierarchy level (lower numbers = higher hierarchy)
    return Object.keys(groups)
      .map(Number)
      .sort((a, b) => a - b)
      .map(level => ({
        level,
        members: groups[level].sort((a, b) => a.name.localeCompare(b.name))
      }));
  };

  const tableColumns: Column<typeof members[0]>[] = [
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
    },
    {
      key: 'phone',
      title: t('members.phone'),
      sortable: true,
    },
    {
      key: 'hierarchyLevel',
      title: t('members.hierarchy'),
      sortable: true,
      render: (value) => `Level ${value || 0}`,
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
  ];

  if (showAdminActions) {
    tableColumns.push({
      key: 'actions',
      title: t('common.actions'),
      render: (_, member) => (
        <div style={{ display: 'flex', gap: '8px' }}>
          <Button
            size="sm"
            variant="secondary"
            onClick={() => onMemberClick?.(member.id)}
          >
            {t('common.edit')}
          </Button>
        </div>
      ),
    });
  }

  if (isLoading) {
    return <LoadingSpinner fullScreen text={t('members.loading')} />;
  }

  if (error) {
    return (
      <DirectoryContainer>
        <div style={{ textAlign: 'center', color: '#dc2626' }}>
          <h3>{t('common.error')}</h3>
          <p>{error}</p>
          <Button onClick={loadMembers}>{t('common.retry')}</Button>
        </div>
      </DirectoryContainer>
    );
  }

  return (
    <DirectoryContainer>
      <DirectoryHeader>
        <DirectoryTitle>{t('members.directory.title')}</DirectoryTitle>
        <DirectoryDescription>
          {t('members.directory.description')}
        </DirectoryDescription>
      </DirectoryHeader>

      <ViewToggle>
        <Button
          variant={viewMode === 'hierarchy' ? 'primary' : 'secondary'}
          size="sm"
          onClick={() => setViewMode('hierarchy')}
        >
          {t('members.view.hierarchy')}
        </Button>
        <Button
          variant={viewMode === 'grid' ? 'primary' : 'secondary'}
          size="sm"
          onClick={() => setViewMode('grid')}
        >
          {t('members.view.grid')}
        </Button>
        <Button
          variant={viewMode === 'table' ? 'primary' : 'secondary'}
          size="sm"
          onClick={() => setViewMode('table')}
        >
          {t('members.view.table')}
        </Button>
      </ViewToggle>

      {viewMode === 'hierarchy' && (
        <div>
          {getHierarchyGroups().map(({ level, members: levelMembers }) => (
            <HierarchySection key={level}>
              <HierarchyTitle>
                {level === 0 ? t('members.hierarchy.executive') : 
                 level === 1 ? t('members.hierarchy.senior') :
                 t('members.hierarchy.general')}
              </HierarchyTitle>
              <HierarchyGrid>
                {levelMembers.map(member => (
                  <ProfileCard
                    key={member.id}
                    name={member.name}
                    title={member.position}
                    subtitle={member.email}
                    imageUrl={member.photoUrl}
                    clickable={!!onMemberClick}
                    onClick={() => onMemberClick?.(member.id)}
                    details={[
                      { label: t('members.phone'), value: member.phone || t('common.notProvided') },
                      { label: t('members.tenure'), value: member.tenureStartDate ? 
                        `${member.tenureStartDate}${member.tenureEndDate ? ` - ${member.tenureEndDate}` : ' - Present'}` : 
                        t('common.notSpecified') 
                      },
                    ]}
                    tags={[
                      ...(member.isActive ? [{ label: t('common.active'), variant: 'success' as const }] : []),
                    ]}
                  />
                ))}
              </HierarchyGrid>
            </HierarchySection>
          ))}
        </div>
      )}

      {viewMode === 'grid' && (
        <MembersGrid>
          {members.map(member => (
            <ProfileCard
              key={member.id}
              name={member.name}
              title={member.position}
              subtitle={member.email}
              imageUrl={member.photoUrl}
              clickable={!!onMemberClick}
              onClick={() => onMemberClick?.(member.id)}
              details={[
                { label: t('members.phone'), value: member.phone || t('common.notProvided') },
                { label: t('members.hierarchy'), value: `Level ${member.hierarchyLevel || 0}` },
              ]}
              tags={[
                ...(member.isActive ? [{ label: t('common.active'), variant: 'success' as const }] : []),
                ...(member.isProminent ? [{ label: t('members.prominent'), variant: 'primary' as const }] : []),
              ]}
            />
          ))}
        </MembersGrid>
      )}

      {viewMode === 'table' && (
        <DataTable
          data={members}
          columns={tableColumns}
          title={t('members.directory.title')}
          searchPlaceholder={t('members.search.placeholder')}
          onRowClick={onMemberClick ? (member) => onMemberClick(member.id) : undefined}
          emptyTitle={t('members.empty.title')}
          emptyDescription={t('members.empty.description')}
        />
      )}
    </DirectoryContainer>
  );
};

export default MemberDirectory;