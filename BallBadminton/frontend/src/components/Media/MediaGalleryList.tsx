import React, { useEffect, useState } from 'react';
import styled from 'styled-components';
import { useTranslation } from 'react-i18next';
import { useNavigate } from 'react-router-dom';
import { mediaService, MediaGallery, MediaFilters } from '../../services/mediaService';
import Card from '../UI/Card';
import Pagination from '../UI/Pagination';
import LoadingSpinner from '../UI/LoadingSpinner';

const ListContainer = styled.div`
  display: flex;
  flex-direction: column;
  gap: ${({ theme }) => theme.spacing[6]};
`;

const FilterBar = styled.div`
  display: flex;
  gap: ${({ theme }) => theme.spacing[4]};
  flex-wrap: wrap;
  margin-bottom: ${({ theme }) => theme.spacing[6]};
`;

const Select = styled.select`
  padding: ${({ theme }) => theme.spacing[2]} ${({ theme }) => theme.spacing[3]};
  border: 1px solid ${({ theme }) => theme.colors.gray[300]};
  border-radius: ${({ theme }) => theme.borderRadius.md};
  font-size: ${({ theme }) => theme.fontSizes.base};
  
  &:focus {
    outline: none;
    border-color: ${({ theme }) => theme.colors.primary[500]};
  }
`;

const GalleryGrid = styled.div`
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: ${({ theme }) => theme.spacing[6]};
`;

const GalleryCard = styled(Card)`
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
  overflow: hidden;
  
  &:hover {
    transform: translateY(-4px);
    box-shadow: 0 10px 20px rgba(0, 0, 0, 0.1);
  }
`;

const CoverImage = styled.img`
  width: 100%;
  height: 200px;
  object-fit: cover;
`;

const GalleryContent = styled.div`
  padding: ${({ theme }) => theme.spacing[4]};
`;

const GalleryTitle = styled.h3`
  font-size: ${({ theme }) => theme.fontSizes.xl};
  font-weight: ${({ theme }) => theme.fontWeights.semibold};
  color: ${({ theme }) => theme.colors.gray[900]};
  margin-bottom: ${({ theme }) => theme.spacing[2]};
`;

const GalleryDescription = styled.p`
  font-size: ${({ theme }) => theme.fontSizes.sm};
  color: ${({ theme }) => theme.colors.gray[600]};
  margin-bottom: ${({ theme }) => theme.spacing[3]};
  line-height: 1.5;
`;

const GalleryMeta = styled.div`
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: ${({ theme }) => theme.fontSizes.xs};
  color: ${({ theme }) => theme.colors.gray[500]};
`;

const CategoryBadge = styled.span`
  background-color: ${({ theme }) => theme.colors.primary[100]};
  color: ${({ theme }) => theme.colors.primary[700]};
  padding: ${({ theme }) => theme.spacing[1]} ${({ theme }) => theme.spacing[2]};
  border-radius: ${({ theme }) => theme.borderRadius.full};
  font-size: ${({ theme }) => theme.fontSizes.xs};
  font-weight: ${({ theme }) => theme.fontWeights.medium};
`;

const ItemCount = styled.span`
  display: flex;
  align-items: center;
  gap: ${({ theme }) => theme.spacing[1]};
`;

const EmptyState = styled.div`
  text-align: center;
  padding: ${({ theme }) => theme.spacing[12]};
  color: ${({ theme }) => theme.colors.gray[500]};
`;

interface MediaGalleryListProps {
  initialFilters?: MediaFilters;
}

const MediaGalleryList: React.FC<MediaGalleryListProps> = ({ initialFilters }) => {
  const { t } = useTranslation();
  const navigate = useNavigate();
  const [galleries, setGalleries] = useState<MediaGallery[]>([]);
  const [categories, setCategories] = useState<string[]>([]);
  const [filters, setFilters] = useState<MediaFilters>(initialFilters || { page: 1, size: 12 });
  const [totalPages, setTotalPages] = useState(1);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    loadCategories();
  }, []);

  useEffect(() => {
    loadGalleries();
  }, [filters]);

  const loadCategories = async () => {
    try {
      const cats = await mediaService.getCategories();
      setCategories(cats);
    } catch (error) {
      console.error('Failed to load categories:', error);
    }
  };

  const loadGalleries = async () => {
    setIsLoading(true);
    try {
      const response = await mediaService.getGalleries(filters);
      setGalleries(response.data);
      setTotalPages(response.pagination.totalPages);
    } catch (error) {
      console.error('Failed to load galleries:', error);
    } finally {
      setIsLoading(false);
    }
  };

  const handleCategoryChange = (category: string) => {
    setFilters({ ...filters, category: category || undefined, page: 1 });
  };

  const handlePageChange = (page: number) => {
    setFilters({ ...filters, page });
  };

  const handleGalleryClick = (galleryId: string) => {
    navigate(`/media/gallery/${galleryId}`);
  };

  const formatDate = (dateString: string) => {
    return new Date(dateString).toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'long',
      day: 'numeric',
    });
  };

  if (isLoading) {
    return <LoadingSpinner />;
  }

  return (
    <ListContainer>
      <FilterBar>
        <Select value={filters.category || ''} onChange={(e) => handleCategoryChange(e.target.value)}>
          <option value="">{t('media.allCategories')}</option>
          {categories.map((category) => (
            <option key={category} value={category}>
              {category}
            </option>
          ))}
        </Select>
      </FilterBar>

      {galleries.length === 0 ? (
        <EmptyState>
          <h3>{t('media.noGalleries')}</h3>
          <p>{t('media.noGalleriesDescription')}</p>
        </EmptyState>
      ) : (
        <>
          <GalleryGrid>
            {galleries.map((gallery) => (
              <GalleryCard key={gallery.id} onClick={() => handleGalleryClick(gallery.id)}>
                {gallery.coverImage && (
                  <CoverImage src={gallery.coverImage} alt={gallery.title} />
                )}
                <GalleryContent>
                  <GalleryTitle>{gallery.title}</GalleryTitle>
                  <GalleryDescription>{gallery.description}</GalleryDescription>
                  <GalleryMeta>
                    <CategoryBadge>{gallery.category}</CategoryBadge>
                    <ItemCount>
                      <span>📷</span>
                      <span>{gallery.itemCount} {t('media.items')}</span>
                    </ItemCount>
                  </GalleryMeta>
                </GalleryContent>
              </GalleryCard>
            ))}
          </GalleryGrid>

          {totalPages > 1 && (
            <Pagination
              currentPage={filters.page || 1}
              totalPages={totalPages}
              onPageChange={handlePageChange}
            />
          )}
        </>
      )}
    </ListContainer>
  );
};

export default MediaGalleryList;
