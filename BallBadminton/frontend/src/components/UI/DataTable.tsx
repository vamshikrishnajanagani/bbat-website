import React, { useState, useMemo } from 'react';
import styled from 'styled-components';
import { useTranslation } from 'react-i18next';
import Button from './Button';

const TableContainer = styled.div`
  background: white;
  border-radius: ${({ theme }) => theme.borderRadius.lg};
  box-shadow: ${({ theme }) => theme.shadows.sm};
  border: 1px solid ${({ theme }) => theme.colors.gray[200]};
  overflow: hidden;
`;

const TableHeader = styled.div`
  padding: ${({ theme }) => theme.spacing[4]} ${({ theme }) => theme.spacing[6]};
  border-bottom: 1px solid ${({ theme }) => theme.colors.gray[200]};
  display: flex;
  align-items: center;
  justify-content: space-between;
  flex-wrap: wrap;
  gap: ${({ theme }) => theme.spacing[4]};
`;

const TableTitle = styled.h3`
  margin: 0;
  color: ${({ theme }) => theme.colors.gray[900]};
  font-size: ${({ theme }) => theme.fontSizes.lg};
  font-weight: ${({ theme }) => theme.fontWeights.semibold};
`;

const TableControls = styled.div`
  display: flex;
  align-items: center;
  gap: ${({ theme }) => theme.spacing[3]};
  flex-wrap: wrap;
`;

const SearchInput = styled.input`
  padding: ${({ theme }) => theme.spacing[2]} ${({ theme }) => theme.spacing[3]};
  border: 1px solid ${({ theme }) => theme.colors.gray[300]};
  border-radius: ${({ theme }) => theme.borderRadius.base};
  font-size: ${({ theme }) => theme.fontSizes.sm};
  min-width: 200px;
  
  &:focus {
    outline: none;
    border-color: ${({ theme }) => theme.colors.primary[500]};
    box-shadow: 0 0 0 3px ${({ theme }) => theme.colors.primary[100]};
  }
`;

const FilterSelect = styled.select`
  padding: ${({ theme }) => theme.spacing[2]} ${({ theme }) => theme.spacing[3]};
  border: 1px solid ${({ theme }) => theme.colors.gray[300]};
  border-radius: ${({ theme }) => theme.borderRadius.base};
  font-size: ${({ theme }) => theme.fontSizes.sm};
  background: white;
  
  &:focus {
    outline: none;
    border-color: ${({ theme }) => theme.colors.primary[500]};
    box-shadow: 0 0 0 3px ${({ theme }) => theme.colors.primary[100]};
  }
`;

const Table = styled.table`
  width: 100%;
  border-collapse: collapse;
`;

const TableHead = styled.thead`
  background: ${({ theme }) => theme.colors.gray[50]};
`;

const TableRow = styled.tr<{ clickable?: boolean }>`
  border-bottom: 1px solid ${({ theme }) => theme.colors.gray[200]};
  
  &:last-child {
    border-bottom: none;
  }
  
  ${({ clickable, theme }) => clickable && `
    cursor: pointer;
    transition: background-color ${theme.transitions.fast};
    
    &:hover {
      background: ${theme.colors.gray[50]};
    }
  `}
`;

const TableHeaderCell = styled.th<{ sortable?: boolean }>`
  padding: ${({ theme }) => theme.spacing[3]} ${({ theme }) => theme.spacing[4]};
  text-align: left;
  font-weight: ${({ theme }) => theme.fontWeights.semibold};
  color: ${({ theme }) => theme.colors.gray[700]};
  font-size: ${({ theme }) => theme.fontSizes.sm};
  
  ${({ sortable, theme }) => sortable && `
    cursor: pointer;
    user-select: none;
    position: relative;
    
    &:hover {
      background: ${theme.colors.gray[100]};
    }
    
    &::after {
      content: '↕';
      position: absolute;
      right: ${theme.spacing[2]};
      opacity: 0.5;
      font-size: ${theme.fontSizes.xs};
    }
  `}
`;

const TableCell = styled.td`
  padding: ${({ theme }) => theme.spacing[3]} ${({ theme }) => theme.spacing[4]};
  color: ${({ theme }) => theme.colors.gray[900]};
  font-size: ${({ theme }) => theme.fontSizes.sm};
  vertical-align: middle;
`;

const EmptyState = styled.div`
  padding: ${({ theme }) => theme.spacing[12]} ${({ theme }) => theme.spacing[6]};
  text-align: center;
  color: ${({ theme }) => theme.colors.gray[500]};
  
  h4 {
    margin: 0 0 ${({ theme }) => theme.spacing[2]} 0;
    color: ${({ theme }) => theme.colors.gray[700]};
    font-size: ${({ theme }) => theme.fontSizes.lg};
    font-weight: ${({ theme }) => theme.fontWeights.medium};
  }
  
  p {
    margin: 0;
    font-size: ${({ theme }) => theme.fontSizes.sm};
  }
`;

const LoadingState = styled.div`
  padding: ${({ theme }) => theme.spacing[12]} ${({ theme }) => theme.spacing[6]};
  text-align: center;
  color: ${({ theme }) => theme.colors.gray[500]};
`;

const TableFooter = styled.div`
  padding: ${({ theme }) => theme.spacing[4]} ${({ theme }) => theme.spacing[6]};
  border-top: 1px solid ${({ theme }) => theme.colors.gray[200]};
  display: flex;
  align-items: center;
  justify-content: between;
  flex-wrap: wrap;
  gap: ${({ theme }) => theme.spacing[4]};
`;

const ResultsInfo = styled.div`
  color: ${({ theme }) => theme.colors.gray[600]};
  font-size: ${({ theme }) => theme.fontSizes.sm};
  flex: 1;
`;

const PaginationControls = styled.div`
  display: flex;
  align-items: center;
  gap: ${({ theme }) => theme.spacing[2]};
`;

export interface Column<T> {
  key: keyof T | string;
  title: string;
  sortable?: boolean;
  render?: (value: any, row: T, index: number) => React.ReactNode;
  width?: string;
}

export interface Filter {
  key: string;
  label: string;
  options: Array<{ value: string; label: string }>;
}

interface DataTableProps<T> {
  data: T[];
  columns: Column<T>[];
  title?: string;
  searchable?: boolean;
  searchPlaceholder?: string;
  filters?: Filter[];
  onRowClick?: (row: T, index: number) => void;
  loading?: boolean;
  emptyTitle?: string;
  emptyDescription?: string;
  pageSize?: number;
  actions?: React.ReactNode;
}

function DataTable<T extends Record<string, any>>({
  data,
  columns,
  title,
  searchable = true,
  searchPlaceholder,
  filters = [],
  onRowClick,
  loading = false,
  emptyTitle,
  emptyDescription,
  pageSize = 10,
  actions,
}: DataTableProps<T>) {
  const { t } = useTranslation();
  const [searchTerm, setSearchTerm] = useState('');
  const [sortConfig, setSortConfig] = useState<{
    key: string;
    direction: 'asc' | 'desc';
  } | null>(null);
  const [activeFilters, setActiveFilters] = useState<Record<string, string>>({});
  const [currentPage, setCurrentPage] = useState(1);

  // Filter and search data
  const filteredData = useMemo(() => {
    let filtered = [...data];

    // Apply search
    if (searchTerm) {
      filtered = filtered.filter(item =>
        Object.values(item).some(value =>
          String(value).toLowerCase().includes(searchTerm.toLowerCase())
        )
      );
    }

    // Apply filters
    Object.entries(activeFilters).forEach(([key, value]) => {
      if (value) {
        filtered = filtered.filter(item => String(item[key]) === value);
      }
    });

    return filtered;
  }, [data, searchTerm, activeFilters]);

  // Sort data
  const sortedData = useMemo(() => {
    if (!sortConfig) return filteredData;

    return [...filteredData].sort((a, b) => {
      const aValue = a[sortConfig.key];
      const bValue = b[sortConfig.key];

      if (aValue < bValue) {
        return sortConfig.direction === 'asc' ? -1 : 1;
      }
      if (aValue > bValue) {
        return sortConfig.direction === 'asc' ? 1 : -1;
      }
      return 0;
    });
  }, [filteredData, sortConfig]);

  // Paginate data
  const paginatedData = useMemo(() => {
    const startIndex = (currentPage - 1) * pageSize;
    return sortedData.slice(startIndex, startIndex + pageSize);
  }, [sortedData, currentPage, pageSize]);

  const totalPages = Math.ceil(sortedData.length / pageSize);

  const handleSort = (key: string) => {
    setSortConfig(current => {
      if (current?.key === key) {
        return {
          key,
          direction: current.direction === 'asc' ? 'desc' : 'asc',
        };
      }
      return { key, direction: 'asc' };
    });
  };

  const handleFilterChange = (filterKey: string, value: string) => {
    setActiveFilters(prev => ({ ...prev, [filterKey]: value }));
    setCurrentPage(1); // Reset to first page when filtering
  };

  const getCellValue = (row: T, column: Column<T>) => {
    if (column.render) {
      return column.render(row[column.key as keyof T], row, 0);
    }
    return row[column.key as keyof T];
  };

  return (
    <TableContainer>
      {(title || searchable || filters.length > 0 || actions) && (
        <TableHeader>
          <div>
            {title && <TableTitle>{title}</TableTitle>}
          </div>
          <TableControls>
            {searchable && (
              <SearchInput
                type="text"
                placeholder={searchPlaceholder || t('common.search')}
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
              />
            )}
            {filters.map(filter => (
              <FilterSelect
                key={filter.key}
                value={activeFilters[filter.key] || ''}
                onChange={(e) => handleFilterChange(filter.key, e.target.value)}
              >
                <option value="">{filter.label}</option>
                {filter.options.map(option => (
                  <option key={option.value} value={option.value}>
                    {option.label}
                  </option>
                ))}
              </FilterSelect>
            ))}
            {actions}
          </TableControls>
        </TableHeader>
      )}

      {loading ? (
        <LoadingState>
          <div>{t('common.loading')}</div>
        </LoadingState>
      ) : paginatedData.length === 0 ? (
        <EmptyState>
          <h4>{emptyTitle || t('common.noDataFound')}</h4>
          <p>{emptyDescription || t('common.noDataDescription')}</p>
        </EmptyState>
      ) : (
        <Table>
          <TableHead>
            <TableRow>
              {columns.map(column => (
                <TableHeaderCell
                  key={String(column.key)}
                  sortable={column.sortable}
                  onClick={column.sortable ? () => handleSort(String(column.key)) : undefined}
                  style={{ width: column.width }}
                >
                  {column.title}
                  {sortConfig?.key === column.key && (
                    <span style={{ marginLeft: '4px' }}>
                      {sortConfig.direction === 'asc' ? '↑' : '↓'}
                    </span>
                  )}
                </TableHeaderCell>
              ))}
            </TableRow>
          </TableHead>
          <tbody>
            {paginatedData.map((row, index) => (
              <TableRow
                key={index}
                clickable={!!onRowClick}
                onClick={onRowClick ? () => onRowClick(row, index) : undefined}
              >
                {columns.map(column => (
                  <TableCell key={String(column.key)}>
                    {getCellValue(row, column)}
                  </TableCell>
                ))}
              </TableRow>
            ))}
          </tbody>
        </Table>
      )}

      {totalPages > 1 && (
        <TableFooter>
          <ResultsInfo>
            {t('common.showingResults', {
              start: (currentPage - 1) * pageSize + 1,
              end: Math.min(currentPage * pageSize, sortedData.length),
              total: sortedData.length,
            })}
          </ResultsInfo>
          <PaginationControls>
            <Button
              variant="secondary"
              size="sm"
              disabled={currentPage === 1}
              onClick={() => setCurrentPage(prev => prev - 1)}
            >
              {t('common.previous')}
            </Button>
            <span style={{ fontSize: '14px', color: '#666' }}>
              {currentPage} / {totalPages}
            </span>
            <Button
              variant="secondary"
              size="sm"
              disabled={currentPage === totalPages}
              onClick={() => setCurrentPage(prev => prev + 1)}
            >
              {t('common.next')}
            </Button>
          </PaginationControls>
        </TableFooter>
      )}
    </TableContainer>
  );
}

export default DataTable;