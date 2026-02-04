import React, { useEffect, useState } from 'react';
import { useParams, Navigate } from 'react-router-dom';
import styled from 'styled-components';
import { mediaService, MediaGallery, MediaItem } from '../../services/mediaService';
import LoadingSpinner from '../UI/LoadingSpinner';

const GalleryContainer = styled.div`
  max-width: 1200px;
  margin: 0 auto;
  padding: ${({ theme }) => theme.spacing[6]};
`;

const GalleryHeader = styled.header`
  margin-bottom: ${({ theme }) => theme.spacing[6]};
`;

const GalleryTitle = styled.h1`
  font-size: ${({ theme }) => theme.fontSizes['3xl']};
  font-weight: ${({ theme }) => theme.fontWeights.bold};
  color: ${({ theme }) => theme.colors.gray[900]};
  margin-bottom: ${({ theme }) => theme.spacing[2]};
`;

const GalleryDescription = styled.p`
  color: ${({ theme }) => theme.colors.gray[600]};
  font-size: ${({ theme }) => theme.fontSizes.lg};
`;

const MediaGrid = styled.div`
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: ${({ theme }) => theme.spacing[4]};
`;

const MediaCard = styled.div`
  background: white;
  border-radius: ${({ theme }) => theme.borderRadius.lg};
  overflow: hidden;
  box-shadow: ${({ theme }) => theme.shadows.md};
  transition: transform ${({ theme }) => theme.transitions.fast};
  
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

const MediaDescription = styled.p`
  color: ${({ theme }) => theme.colors.gray[600]};
  font-size: ${({ theme }) => theme.fontSizes.sm};
`;

export const MediaGalleryView: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const [gallery, setGallery] = useState<MediaGallery | null>(null);
  const [mediaItems, setMediaItems] = useState<MediaItem[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const fetchGalleryData = async () => {
      if (!id) return;

      try {
        setIsLoading(true);
        const [galleryData, itemsData] = await Promise.all([
          mediaService.getGalleryById(id),
          mediaService.getMediaItems(id)
        ]);
        
        setGallery(galleryData);
        setMediaItems(itemsData.data);
      } catch (error) {
        console.error('Failed to fetch gallery data:', error);
        setError('Failed to load gallery');
      } finally {
        setIsLoading(false);
      }
    };

    fetchGalleryData();
  }, [id]);

  if (isLoading) {
    return <LoadingSpinner />;
  }

  if (error || !gallery) {
    return <Navigate to="/media" replace />;
  }

  return (
    <GalleryContainer>
      <GalleryHeader>
        <GalleryTitle>{gallery.title}</GalleryTitle>
        <GalleryDescription>{gallery.description}</GalleryDescription>
      </GalleryHeader>

      <MediaGrid>
        {mediaItems.map(item => (
          <MediaCard key={item.id}>
            <MediaImage 
              src={item.thumbnailUrl || item.url} 
              alt={item.title}
            />
            <MediaContent>
              <MediaTitle>{item.title}</MediaTitle>
              {item.description && (
                <MediaDescription>{item.description}</MediaDescription>
              )}
            </MediaContent>
          </MediaCard>
        ))}
      </MediaGrid>
    </GalleryContainer>
  );
};