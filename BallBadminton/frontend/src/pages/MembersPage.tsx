import React, { useEffect } from 'react';
import styled from 'styled-components';
import { useAppSelector, useAppDispatch } from '../store';
import { setMembers, setLoading } from '../store/slices/membersSlice';
import { membersService } from '../services/membersService';
import LoadingSpinner from '../components/UI/LoadingSpinner';

const PageContainer = styled.div`
  max-width: 1200px;
  margin: 0 auto;
`;

const PageHeader = styled.header`
  margin-bottom: ${({ theme }) => theme.spacing[8]};
`;

const PageTitle = styled.h1`
  font-size: ${({ theme }) => theme.fontSizes['4xl']};
  font-weight: ${({ theme }) => theme.fontWeights.bold};
  color: ${({ theme }) => theme.colors.gray[900]};
  margin-bottom: ${({ theme }) => theme.spacing[4]};
`;

const PageDescription = styled.p`
  font-size: ${({ theme }) => theme.fontSizes.lg};
  color: ${({ theme }) => theme.colors.gray[600]};
  line-height: ${({ theme }) => theme.lineHeights.relaxed};
`;

const MembersGrid = styled.div`
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: ${({ theme }) => theme.spacing[6]};
`;

const MemberCard = styled.div`
  background: white;
  border-radius: ${({ theme }) => theme.borderRadius.lg};
  padding: ${({ theme }) => theme.spacing[6]};
  box-shadow: ${({ theme }) => theme.shadows.md};
  text-align: center;
  transition: transform ${({ theme }) => theme.transitions.base};
  
  &:hover {
    transform: translateY(-2px);
  }
`;

const MemberPhoto = styled.img`
  width: 120px;
  height: 120px;
  border-radius: 50%;
  object-fit: cover;
  margin: 0 auto ${({ theme }) => theme.spacing[4]};
`;

const MemberName = styled.h3`
  font-size: ${({ theme }) => theme.fontSizes.xl};
  font-weight: ${({ theme }) => theme.fontWeights.semibold};
  color: ${({ theme }) => theme.colors.gray[900]};
  margin-bottom: ${({ theme }) => theme.spacing[2]};
`;

const MemberPosition = styled.p`
  font-size: ${({ theme }) => theme.fontSizes.base};
  color: ${({ theme }) => theme.colors.primary[600]};
  font-weight: ${({ theme }) => theme.fontWeights.medium};
  margin-bottom: ${({ theme }) => theme.spacing[3]};
`;

const MemberBio = styled.p`
  font-size: ${({ theme }) => theme.fontSizes.sm};
  color: ${({ theme }) => theme.colors.gray[600]};
  line-height: ${({ theme }) => theme.lineHeights.relaxed};
`;

const MembersPage: React.FC = () => {
  const dispatch = useAppDispatch();
  const { members, isLoading } = useAppSelector(state => state.members);

  useEffect(() => {
    const fetchMembers = async () => {
      try {
        dispatch(setLoading(true));
        const data = await membersService.getMembers({ isActive: true });
        dispatch(setMembers(Array.isArray(data) ? data : (data as any).data || []));
      } catch (error) {
        console.error('Failed to fetch members:', error);
      } finally {
        dispatch(setLoading(false));
      }
    };

    fetchMembers();
  }, [dispatch]);

  if (isLoading) {
    return <LoadingSpinner />;
  }

  return (
    <PageContainer>
      <PageHeader>
        <PageTitle>Association Members</PageTitle>
        <PageDescription>
          Meet the dedicated leadership team working to promote Ball Badminton across Telangana state.
          Our members bring years of experience and passion for the sport.
        </PageDescription>
      </PageHeader>

      <MembersGrid>
        {members.map(member => (
          <MemberCard key={member.id}>
            <MemberPhoto 
              src={member.photoUrl || '/default-avatar.png'} 
              alt={member.name}
            />
            <MemberName>{member.name}</MemberName>
            <MemberPosition>{member.position}</MemberPosition>
            <MemberBio>{member.biography}</MemberBio>
          </MemberCard>
        ))}
      </MembersGrid>
    </PageContainer>
  );
};

export default MembersPage;