import React, { useEffect, useState } from 'react';
import styled from 'styled-components';
import { useTranslation } from 'react-i18next';
import { mediaService, DownloadResource, DownloadFilters } from '../../services/mediaService';
import Card from '../UI/Card';
import Pagination from '../UI/Pagination';
import LoadingSpinner from '../UI/LoadingSpinner';

const CenterContainer = styled.div`
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

const DownloadList = styled.div`
  display: flex;
  flex-direction: column;
  gap: ${({ theme }) => theme.spacing[4]};
`;

const DownloadCard = styled(Card)`
  display: flex;
  align-items: center;
  gap: ${({ theme }) => theme.spacing[4]};
  padding: ${({ theme }) => theme.spacing[4]};
  transition: all 0.2s;
  
  &:hover {
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  }
`;

const FileIcon = styled.div`
  width: 60px;
  height: 60px;
  background-color: ${({ theme }) => theme.colors.primary[100]};
  border-radius: ${({ theme }) => theme.borderRadius.md};
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: ${({ theme }) => theme.fontSizes['2xl']};
  flex-shrink: 0;
`;

const DownloadInfo = styled.div`
  flex: 1;
`;

const DownloadTitle = styled.h3`
  font-size: ${({ theme }) => theme.fontSizes.lg};
  font-weight: ${({ theme }) => theme.fontWeights.semibold};
  color: ${({ theme }) => theme.colors.gray[900]};
  margin-bottom: ${({ theme }) => theme.spacing[1]};
`;

const DownloadDescription = styled.p`
  font-size: ${({ theme }) => theme.fontSizes.sm};
  color: ${({ theme }) => theme.colors.gray[600]};
  margin-bottom: ${({ theme }) => theme.spacing[2]};
  line-height: 1.5;
`;

const DownloadMeta = styled.div`
  display: flex;
  gap: ${({ theme }) => theme.spacing[4]};
  font-size: ${({ theme }) => theme.fontSizes.xs};
  color: ${({ theme }) => theme.colors.gray[500]};
`;

const MetaItem = styled.span`
  display: flex;
  align-items: center;
  gap: ${({ theme }) => theme.spacing[1]};
`;

const CategoryBadge = styled.span`
  background-color: ${({ theme }) => theme.colors.primary[100]};
  color: ${({ theme }) => theme.colors.primary[700]};
  padding: ${({ theme }) => theme.spacing[1]} ${({ theme }) => theme.spacing[2]};
  border-radius: ${({ theme }) => theme.borderRadius.full};
  font-size: ${({ theme }) => theme.fontSizes.xs};
  font-weight: ${({ theme }) => theme.fontWeights.medium};
`;

const DownloadButton = styled.button`
  padding: ${({ theme }) => theme.spacing[2]} ${({ theme }) => theme.spacing[4]};
  background-color: ${({ theme }) => theme.colors.primary[600]};
  color: white;
  border: none;
  border-radius: ${({ theme }) => theme.borderRadius.md};
  font-size: ${({ theme }) => theme.fontSizes.sm};
  font-weight: ${({ theme }) => theme.fontWeights.medium};
  cursor: pointer;
  transition: background-color 0.2s;
  display: flex;
  align-items: center;
  gap: ${({ theme }) => theme.spacing[2]};
  
  &:hover {
    background-color: ${({ theme }) => theme.colors.primary[700]};
  }
`;

const EmptyState = styled.div`
  text-align: center;
  padding: ${({ theme }) => theme.spacing[12]};
  color: ${({ theme }) => theme.colors.gray[500]};
`;

const getFileIcon = (fileType: string): string => {
  if (fileType.includes('pdf')) return '📄';
  if (fileType.includes('word') || fileType.includes('doc')) return '📝';
  if (fileType.includes('excel') || fileType.includes('sheet')) return '📊';
  if (fileType.includes('image')) return '🖼️';
  if (fileType.includes('zip') || fileType.includes('rar')) return '📦';
  return '📁';
};

const formatFileSize = (bytes: number): string => {
  if (bytes < 1024) return bytes + ' B';
  if (bytes < 1024 * 1024) return (bytes / 1024).toFixed(1) + ' KB';
  return (bytes / (1024 * 1024)).toFixed(1) + ' MB';
};

const DownloadCenter: React.FC = () => {
  const { t } = useTranslation();
  const [downloads, setDownloads] = useState<DownloadResource[]>([]);
  const [categories, setCategories] = useState<string[]>([]);
  const [filters, setFilters] = useState<DownloadFilters>({ page: 1, size: 10 });
  const [totalPages, setTotalPages] = useState(1);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    loadCategories();
  }, []);

  useEffect(() => {
    loadDownloads();
  }, [filters]);

  const loadCategories = async () => {
    try {
      const cats = await mediaService.getDownloadCategories();
      setCategories(cats);
    } catch (error) {
      console.error('Failed to load categories:', error);
    }
  };

  const loadDownloads = async () => {
    setIsLoading(true);
    try {
      const response = await mediaService.getDownloads(filters);
      setDownloads(response.data);
      setTotalPages(response.pagination.totalPages);
    } catch (error) {
      console.error('Failed to load downloads:', error);
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

  const handleDownload = async (download: DownloadResource) => {
    try {
      await mediaService.trackDownload(download.id);
      window.open(download.fileUrl, '_blank');
    } catch (error) {
      console.error('Failed to track download:', error);
      window.open(download.fileUrl, '_blank');
    }
  };

  if (isLoading) {
    return <LoadingSpinner />;
  }

  return (
    <CenterContainer>
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

      {downloads.length === 0 ? (
        <EmptyState>
          <h3>{t('media.noDownloads')}</h3>
          <p>{t('media.noDownloadsDescription')}</p>
        </EmptyState>
      ) : (
        <>
          <DownloadList>
            {downloads.map((download) => (
              <DownloadCard key={download.id}>
                <FileIcon>{getFileIcon(download.fileType)}</FileIcon>
                <DownloadInfo>
                  <DownloadTitle>{download.title}</DownloadTitle>
                  <DownloadDescription>{download.description}</DownloadDescription>
                  <DownloadMeta>
                    <CategoryBadge>{download.category}</CategoryBadge>
                    <MetaItem>
                      <span>📦</span>
                      {formatFileSize(download.fileSize)}
                    </MetaItem>
                    <MetaItem>
                      <span>⬇️</span>
                      {download.downloadCount} {t('media.downloads')}
                    </MetaItem>
                  </DownloadMeta>
                </DownloadInfo>
                <DownloadButton onClick={() => handleDownload(download)}>
                  <span>⬇️</span>
                  {t('media.download')}
                </DownloadButton>
              </DownloadCard>
            ))}
          </DownloadList>

          {totalPages > 1 && (
            <Pagination
              currentPage={filters.page || 1}
              totalPages={totalPages}
              onPageChange={handlePageChange}
            />
          )}
        </>
      )}
    </CenterContainer>
  );
};

export default DownloadCenter;
