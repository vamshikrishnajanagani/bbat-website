import React, { useState } from 'react';
import styled from 'styled-components';
import { mediaService } from '../services/mediaService';
import LoadingSpinner from '../components/UI/LoadingSpinner';
import Button from '../components/UI/Button';

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

const FilterTabs = styled.div`
  display: flex;
  gap: ${({ theme }) => theme.spacing[2]};
  margin-bottom: ${({ theme }) => theme.spacing[6]};
  border-bottom: 1px solid ${({ theme }) => theme.colors.gray[200]};
`;

const FilterTab = styled.button<{ active: boolean }>`
  padding: ${({ theme }) => theme.spacing[3]} ${({ theme }) => theme.spacing[4]};
  border: none;
  background: none;
  font-size: ${({ theme }) => theme.fontSizes.base};
  font-weight: ${({ theme }) => theme.fontWeights.medium};
  color: ${({ active, theme }) => active ? theme.colors.primary[600] : theme.colors.gray[600]};
  border-bottom: 2px solid ${({ active, theme }) => active ? theme.colors.primary[600] : 'transparent'};
  cursor: pointer;
  transition: all ${({ theme }) => theme.transitions.fast};
  
  &:hover {
    color: ${({ theme }) => theme.colors.primary[600]};
  }
`;

const MediaGrid = styled.div`
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: ${({ theme }) => theme.spacing[6]};
`;

const MediaCard = styled.div`
  background: white;
  border-radius: ${({ theme }) => theme.borderRadius.lg};
  overflow: hidden;
  box-shadow: ${({ theme }) => theme.shadows.md};
  transition: transform ${({ theme }) => theme.transitions.base};
  
  &:hover {
    transform: translateY(-2px);
  }
`;

const MediaImage = styled.img`
  width: 100%;
  height: 200px;
  object-fit: cover;
`;

const MediaContent = styled.div`
  padding: ${({ theme }) => theme.spacing[4]};
`;

const MediaTitle = styled.h3`
  font-size: ${({ theme }) => theme.fontSizes.lg};
  font-weight: ${({ theme }) => theme.fontWeights.semibold};
  color: ${({ theme }) => theme.colors.gray[900]};
  margin-bottom: ${({ theme }) => theme.spacing[2]};
`;

const MediaMeta = styled.div`
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: ${({ theme }) => theme.fontSizes.sm};
  color: ${({ theme }) => theme.colors.gray[500]};
  margin-bottom: ${({ theme }) => theme.spacing[3]};
`;

const MediaDescription = styled.p`
  font-size: ${({ theme }) => theme.fontSizes.sm};
  color: ${({ theme }) => theme.colors.gray[600]};
  line-height: ${({ theme }) => theme.lineHeights.relaxed};
  margin-bottom: ${({ theme }) => theme.spacing[4]};
`;

const DownloadSection = styled.div`
  background: ${({ theme }) => theme.colors.gray[50]};
  border-radius: ${({ theme }) => theme.borderRadius.lg};
  padding: ${({ theme }) => theme.spacing[8]};
  margin-top: ${({ theme }) => theme.spacing[8]};
  text-align: center;
`;

const DownloadTitle = styled.h2`
  font-size: ${({ theme }) => theme.fontSizes['2xl']};
  font-weight: ${({ theme }) => theme.fontWeights.bold};
  color: ${({ theme }) => theme.colors.gray[900]};
  margin-bottom: ${({ theme }) => theme.spacing[4]};
`;

const DownloadGrid = styled.div`
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: ${({ theme }) => theme.spacing[4]};
  margin-top: ${({ theme }) => theme.spacing[6]};
`;

const DownloadCard = styled.div`
  background: white;
  border-radius: ${({ theme }) => theme.borderRadius.lg};
  padding: ${({ theme }) => theme.spacing[6]};
  box-shadow: ${({ theme }) => theme.shadows.md};
`;

const DownloadIcon = styled.div`
  font-size: ${({ theme }) => theme.fontSizes['3xl']};
  margin-bottom: ${({ theme }) => theme.spacing[3]};
`;

const DownloadItemTitle = styled.h3`
  font-size: ${({ theme }) => theme.fontSizes.lg};
  font-weight: ${({ theme }) => theme.fontWeights.semibold};
  color: ${({ theme }) => theme.colors.gray[900]};
  margin-bottom: ${({ theme }) => theme.spacing[2]};
`;

const DownloadItemDescription = styled.p`
  font-size: ${({ theme }) => theme.fontSizes.sm};
  color: ${({ theme }) => theme.colors.gray[600]};
  margin-bottom: ${({ theme }) => theme.spacing[4]};
`;

const MediaPage: React.FC = () => {
  const [activeTab, setActiveTab] = useState<'photos' | 'videos' | 'downloads'>('photos');
  const [isLoading, setIsLoading] = useState(false);

  // Mock data - in real app this would come from API
  const mediaItems = [
    {
      id: 1,
      title: 'State Championship 2024',
      type: 'photo',
      imageUrl: '/tournament-1.jpg',
      description: 'Highlights from the annual state championship tournament.',
      date: '2024-01-15',
      itemCount: 45
    },
    {
      id: 2,
      title: 'District Tournament Warangal',
      type: 'photo',
      imageUrl: '/tournament-2.jpg',
      description: 'Action shots from the Warangal district tournament.',
      date: '2024-01-10',
      itemCount: 32
    },
    {
      id: 3,
      title: 'Training Camp Highlights',
      type: 'video',
      imageUrl: '/training-camp.jpg',
      description: 'Video highlights from the annual training camp.',
      date: '2024-01-05',
      duration: '15:30'
    }
  ];

  const downloadItems = [
    {
      id: 1,
      title: 'Tournament Rules & Regulations',
      description: 'Official rules and regulations for Ball Badminton tournaments.',
      icon: '📋',
      fileType: 'PDF',
      fileSize: '2.5 MB'
    },
    {
      id: 2,
      title: 'Registration Forms',
      description: 'Player and team registration forms for tournaments.',
      icon: '📝',
      fileType: 'PDF',
      fileSize: '1.2 MB'
    },
    {
      id: 3,
      title: 'Association Brochure',
      description: 'Information brochure about TBBA and Ball Badminton.',
      icon: '📖',
      fileType: 'PDF',
      fileSize: '5.8 MB'
    },
    {
      id: 4,
      title: 'Training Manual',
      description: 'Comprehensive training manual for players and coaches.',
      icon: '🏸',
      fileType: 'PDF',
      fileSize: '12.3 MB'
    }
  ];

  const formatDate = (dateString: string) => {
    return new Date(dateString).toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'short',
      day: 'numeric'
    });
  };

  const filteredMedia = mediaItems.filter(item => {
    if (activeTab === 'downloads') return false;
    return item.type === (activeTab === 'photos' ? 'photo' : 'video');
  });

  return (
    <PageContainer>
      <PageHeader>
        <PageTitle>Media Gallery</PageTitle>
        <PageDescription>
          Browse photos and videos from tournaments, training camps, and association events. 
          Download official documents and resources.
        </PageDescription>
      </PageHeader>

      <FilterTabs>
        <FilterTab 
          active={activeTab === 'photos'} 
          onClick={() => setActiveTab('photos')}
        >
          Photos
        </FilterTab>
        <FilterTab 
          active={activeTab === 'videos'} 
          onClick={() => setActiveTab('videos')}
        >
          Videos
        </FilterTab>
        <FilterTab 
          active={activeTab === 'downloads'} 
          onClick={() => setActiveTab('downloads')}
        >
          Downloads
        </FilterTab>
      </FilterTabs>

      {activeTab !== 'downloads' && (
        <>
          {isLoading ? (
            <LoadingSpinner text="Loading media..." />
          ) : (
            <MediaGrid>
              {filteredMedia.map(item => (
                <MediaCard key={item.id}>
                  <MediaImage 
                    src={item.imageUrl} 
                    alt={item.title}
                  />
                  <MediaContent>
                    <MediaTitle>{item.title}</MediaTitle>
                    <MediaMeta>
                      <span>{formatDate(item.date)}</span>
                      <span>
                        {item.type === 'photo' 
                          ? `${item.itemCount} photos` 
                          : item.duration
                        }
                      </span>
                    </MediaMeta>
                    <MediaDescription>{item.description}</MediaDescription>
                    <Button size="sm" variant="outline">
                      {item.type === 'photo' ? 'View Gallery' : 'Watch Video'}
                    </Button>
                  </MediaContent>
                </MediaCard>
              ))}
            </MediaGrid>
          )}

          {filteredMedia.length === 0 && !isLoading && (
            <div style={{ textAlign: 'center', padding: '2rem' }}>
              <p>No {activeTab} available at the moment.</p>
            </div>
          )}
        </>
      )}

      {activeTab === 'downloads' && (
        <DownloadSection>
          <DownloadTitle>Official Documents & Resources</DownloadTitle>
          <p style={{ fontSize: '1.125rem', color: '#6b7280', marginBottom: '2rem' }}>
            Download official forms, rules, and educational materials.
          </p>

          <DownloadGrid>
            {downloadItems.map(item => (
              <DownloadCard key={item.id}>
                <DownloadIcon>{item.icon}</DownloadIcon>
                <DownloadItemTitle>{item.title}</DownloadItemTitle>
                <DownloadItemDescription>{item.description}</DownloadItemDescription>
                <div style={{ fontSize: '0.875rem', color: '#6b7280', marginBottom: '1rem' }}>
                  {item.fileType} • {item.fileSize}
                </div>
                <Button size="sm">
                  Download
                </Button>
              </DownloadCard>
            ))}
          </DownloadGrid>
        </DownloadSection>
      )}
    </PageContainer>
  );
};

export default MediaPage;