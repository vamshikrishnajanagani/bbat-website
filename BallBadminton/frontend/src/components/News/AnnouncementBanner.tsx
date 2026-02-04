import React, { useEffect } from 'react';
import styled from 'styled-components';
import { useAppSelector, useAppDispatch } from '../../store';
import { setAnnouncements } from '../../store/slices/newsSlice';
import { newsService } from '../../services/newsService';

const BannerContainer = styled.div<{ type: string }>`
  background: ${({ theme, type }) => {
    switch (type) {
      case 'success': return theme.colors.success[600];
      case 'warning': return theme.colors.warning[600];
      case 'error': return theme.colors.error[600];
      case 'info': return theme.colors.info[600];
      default: return theme.colors.primary[600];
    }
  }};
  color: white;
  padding: ${({ theme }) => theme.spacing[3]} ${({ theme }) => theme.spacing[4]};
  text-align: center;
  font-weight: ${({ theme }) => theme.fontWeights.medium};
  
  @media (min-width: ${({ theme }) => theme.breakpoints.sm}) {
    padding: ${({ theme }) => theme.spacing[3]} ${({ theme }) => theme.spacing[6]};
  }
`;

const AnnouncementText = styled.p`
  margin: 0;
  font-size: ${({ theme }) => theme.fontSizes.sm};
  
  @media (min-width: ${({ theme }) => theme.breakpoints.sm}) {
    font-size: ${({ theme }) => theme.fontSizes.base};
  }
`;

export const AnnouncementBanner: React.FC = () => {
  const dispatch = useAppDispatch();
  const announcements = useAppSelector(state => state.news.announcements);

  useEffect(() => {
    const fetchAnnouncements = async () => {
      try {
        const data = await newsService.getActiveAnnouncements();
        dispatch(setAnnouncements(data));
      } catch (error) {
        console.error('Failed to fetch announcements:', error);
      }
    };

    fetchAnnouncements();
  }, [dispatch]);

  if (announcements.length === 0) {
    return null;
  }

  // Show the most recent announcement
  const announcement = announcements[0];

  return (
    <BannerContainer type={announcement.type} role="banner" aria-live="polite">
      <AnnouncementText>{announcement.content}</AnnouncementText>
    </BannerContainer>
  );
};