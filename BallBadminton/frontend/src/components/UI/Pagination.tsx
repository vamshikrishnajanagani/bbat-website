import React from 'react';
import styled from 'styled-components';
import { useTranslation } from 'react-i18next';
import Button from './Button';

const PaginationContainer = styled.div`
  display: flex;
  align-items: center;
  justify-content: center;
  gap: ${({ theme }) => theme.spacing[2]};
  margin: ${({ theme }) => theme.spacing[6]} 0;
  flex-wrap: wrap;
`;

const PaginationInfo = styled.div`
  color: ${({ theme }) => theme.colors.gray[600]};
  font-size: ${({ theme }) => theme.fontSizes.sm};
  margin: 0 ${({ theme }) => theme.spacing[4]};
  white-space: nowrap;
`;

const PageButton = styled.button<{ isActive?: boolean }>`
  padding: ${({ theme }) => theme.spacing[2]} ${({ theme }) => theme.spacing[3]};
  border: 1px solid ${({ theme }) => theme.colors.gray[300]};
  background: ${({ isActive, theme }) => 
    isActive ? theme.colors.primary[600] : 'white'};
  color: ${({ isActive, theme }) => 
    isActive ? 'white' : theme.colors.gray[700]};
  border-radius: ${({ theme }) => theme.borderRadius.base};
  cursor: pointer;
  font-size: ${({ theme }) => theme.fontSizes.sm};
  font-weight: ${({ theme }) => theme.fontWeights.medium};
  transition: all ${({ theme }) => theme.transitions.fast};
  min-width: 40px;
  
  &:hover:not(:disabled) {
    background: ${({ isActive, theme }) => 
      isActive ? theme.colors.primary[700] : theme.colors.gray[50]};
    border-color: ${({ isActive, theme }) => 
      isActive ? theme.colors.primary[700] : theme.colors.gray[400]};
  }
  
  &:disabled {
    opacity: 0.5;
    cursor: not-allowed;
  }
  
  &:focus {
    outline: none;
    box-shadow: 0 0 0 3px ${({ theme }) => theme.colors.primary[100]};
  }
`;

const Ellipsis = styled.span`
  color: ${({ theme }) => theme.colors.gray[400]};
  padding: ${({ theme }) => theme.spacing[2]} ${({ theme }) => theme.spacing[1]};
  font-size: ${({ theme }) => theme.fontSizes.sm};
`;

interface PaginationProps {
  currentPage: number;
  totalPages: number;
  onPageChange: (page: number) => void;
  showInfo?: boolean;
  totalItems?: number;
  itemsPerPage?: number;
  maxVisiblePages?: number;
}

const Pagination: React.FC<PaginationProps> = ({
  currentPage,
  totalPages,
  onPageChange,
  showInfo = true,
  totalItems,
  itemsPerPage,
  maxVisiblePages = 7,
}) => {
  const { t } = useTranslation();

  if (totalPages <= 1) return null;

  const getVisiblePages = () => {
    const pages: (number | 'ellipsis')[] = [];
    
    if (totalPages <= maxVisiblePages) {
      // Show all pages if total is less than max visible
      for (let i = 1; i <= totalPages; i++) {
        pages.push(i);
      }
    } else {
      // Always show first page
      pages.push(1);
      
      const startPage = Math.max(2, currentPage - Math.floor(maxVisiblePages / 2));
      const endPage = Math.min(totalPages - 1, startPage + maxVisiblePages - 3);
      
      // Add ellipsis after first page if needed
      if (startPage > 2) {
        pages.push('ellipsis');
      }
      
      // Add middle pages
      for (let i = startPage; i <= endPage; i++) {
        pages.push(i);
      }
      
      // Add ellipsis before last page if needed
      if (endPage < totalPages - 1) {
        pages.push('ellipsis');
      }
      
      // Always show last page
      if (totalPages > 1) {
        pages.push(totalPages);
      }
    }
    
    return pages;
  };

  const visiblePages = getVisiblePages();

  const getInfoText = () => {
    if (!totalItems || !itemsPerPage) {
      return t('common.pageInfo', { current: currentPage, total: totalPages });
    }
    
    const start = (currentPage - 1) * itemsPerPage + 1;
    const end = Math.min(currentPage * itemsPerPage, totalItems);
    
    return t('common.showingResults', {
      start,
      end,
      total: totalItems,
    });
  };

  return (
    <PaginationContainer>
      <Button
        variant="secondary"
        size="sm"
        disabled={currentPage === 1}
        onClick={() => onPageChange(currentPage - 1)}
      >
        {t('common.previous')}
      </Button>

      {visiblePages.map((page, index) => (
        page === 'ellipsis' ? (
          <Ellipsis key={`ellipsis-${index}`}>...</Ellipsis>
        ) : (
          <PageButton
            key={page}
            isActive={page === currentPage}
            onClick={() => onPageChange(page)}
          >
            {page}
          </PageButton>
        )
      ))}

      <Button
        variant="secondary"
        size="sm"
        disabled={currentPage === totalPages}
        onClick={() => onPageChange(currentPage + 1)}
      >
        {t('common.next')}
      </Button>

      {showInfo && (
        <PaginationInfo>
          {getInfoText()}
        </PaginationInfo>
      )}
    </PaginationContainer>
  );
};

export default Pagination;