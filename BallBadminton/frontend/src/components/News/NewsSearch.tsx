import React, { useState, useEffect } from 'react';
import styled from 'styled-components';
import { useTranslation } from 'react-i18next';
import { useNavigate } from 'react-router-dom';
import { newsService } from '../../services/newsService';
import { NewsArticle } from '../../store/slices/newsSlice';
import Button from '../UI/Button';

const SearchContainer = styled.div`
  margin-bottom: ${({ theme }) => theme.spacing[6]};
`;

const SearchBar = styled.div`
  display: flex;
  gap: ${({ theme }) => theme.spacing[3]};
  margin-bottom: ${({ theme }) => theme.spacing[4]};
`;

const SearchInput = styled.input`
  flex: 1;
  padding: ${({ theme }) => theme.spacing[3]} ${({ theme }) => theme.spacing[4]};
  border: 2px solid ${({ theme }) => theme.colors.gray[300]};
  border-radius: ${({ theme }) => theme.borderRadius.md};
  font-size: ${({ theme }) => theme.fontSizes.base};
  
  &:focus {
    outline: none;
    border-color: ${({ theme }) => theme.colors.primary[500]};
  }
`;

const SearchResults = styled.div`
  display: flex;
  flex-direction: column;
  gap: ${({ theme }) => theme.spacing[3]};
`;

const ResultItem = styled.div`
  padding: ${({ theme }) => theme.spacing[4]};
  border: 1px solid ${({ theme }) => theme.colors.gray[200]};
  border-radius: ${({ theme }) => theme.borderRadius.md};
  cursor: pointer;
  transition: all 0.2s;
  
  &:hover {
    border-color: ${({ theme }) => theme.colors.primary[500]};
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  }
`;

const ResultTitle = styled.h4`
  font-size: ${({ theme }) => theme.fontSizes.lg};
  font-weight: ${({ theme }) => theme.fontWeights.semibold};
  color: ${({ theme }) => theme.colors.gray[900]};
  margin-bottom: ${({ theme }) => theme.spacing[2]};
`;

const ResultExcerpt = styled.p`
  font-size: ${({ theme }) => theme.fontSizes.sm};
  color: ${({ theme }) => theme.colors.gray[600]};
  margin-bottom: ${({ theme }) => theme.spacing[2]};
  line-height: 1.5;
`;

const ResultMeta = styled.div`
  display: flex;
  gap: ${({ theme }) => theme.spacing[3]};
  font-size: ${({ theme }) => theme.fontSizes.xs};
  color: ${({ theme }) => theme.colors.gray[500]};
`;

const EmptyState = styled.div`
  text-align: center;
  padding: ${({ theme }) => theme.spacing[8]};
  color: ${({ theme }) => theme.colors.gray[500]};
`;

interface NewsSearchProps {
  onResultClick?: (articleId: string) => void;
}

const NewsSearch: React.FC<NewsSearchProps> = ({ onResultClick }) => {
  const { t } = useTranslation();
  const navigate = useNavigate();
  const [searchQuery, setSearchQuery] = useState('');
  const [results, setResults] = useState<NewsArticle[]>([]);
  const [isSearching, setIsSearching] = useState(false);
  const [hasSearched, setHasSearched] = useState(false);

  useEffect(() => {
    const delayDebounceFn = setTimeout(() => {
      if (searchQuery.trim().length >= 3) {
        performSearch(searchQuery);
      } else {
        setResults([]);
        setHasSearched(false);
      }
    }, 500);

    return () => clearTimeout(delayDebounceFn);
  }, [searchQuery]);

  const performSearch = async (query: string) => {
    setIsSearching(true);
    setHasSearched(true);
    try {
      const data = await newsService.searchArticles(query);
      setResults(data);
    } catch (error) {
      console.error('Search failed:', error);
      setResults([]);
    } finally {
      setIsSearching(false);
    }
  };

  const handleResultClick = (articleId: string) => {
    if (onResultClick) {
      onResultClick(articleId);
    } else {
      navigate(`/news/${articleId}`);
    }
  };

  const formatDate = (dateString: string) => {
    return new Date(dateString).toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
    });
  };

  return (
    <SearchContainer>
      <SearchBar>
        <SearchInput
          type="text"
          placeholder={t('news.searchPlaceholder')}
          value={searchQuery}
          onChange={(e) => setSearchQuery(e.target.value)}
        />
      </SearchBar>

      {isSearching && (
        <EmptyState>
          <p>{t('common.searching')}</p>
        </EmptyState>
      )}

      {!isSearching && hasSearched && results.length === 0 && (
        <EmptyState>
          <p>{t('news.noSearchResults')}</p>
        </EmptyState>
      )}

      {!isSearching && results.length > 0 && (
        <SearchResults>
          {results.map((article) => (
            <ResultItem key={article.id} onClick={() => handleResultClick(article.id)}>
              <ResultTitle>{article.title}</ResultTitle>
              <ResultExcerpt>{article.excerpt}</ResultExcerpt>
              <ResultMeta>
                <span>{article.category}</span>
                <span>•</span>
                <span>{formatDate(article.publishedAt)}</span>
              </ResultMeta>
            </ResultItem>
          ))}
        </SearchResults>
      )}
    </SearchContainer>
  );
};

export default NewsSearch;
