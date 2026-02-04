import React, { useState, useEffect } from 'react';
import styled from 'styled-components';
import { useTranslation } from 'react-i18next';
import { useNavigate } from 'react-router-dom';
import { newsService } from '../../services/newsService';
import { NewsArticle } from '../../store/slices/newsSlice';
import LoadingSpinner from '../UI/LoadingSpinner';

const ArchiveContainer = styled.div`
  display: flex;
  flex-direction: column;
  gap: ${({ theme }) => theme.spacing[6]};
`;

const YearSection = styled.div`
  margin-bottom: ${({ theme }) => theme.spacing[8]};
`;

const YearHeader = styled.h2`
  font-size: ${({ theme }) => theme.fontSizes['2xl']};
  font-weight: ${({ theme }) => theme.fontWeights.bold};
  color: ${({ theme }) => theme.colors.gray[900]};
  margin-bottom: ${({ theme }) => theme.spacing[4]};
  padding-bottom: ${({ theme }) => theme.spacing[2]};
  border-bottom: 2px solid ${({ theme }) => theme.colors.primary[500]};
`;

const MonthSection = styled.div`
  margin-bottom: ${({ theme }) => theme.spacing[6]};
`;

const MonthHeader = styled.h3`
  font-size: ${({ theme }) => theme.fontSizes.xl};
  font-weight: ${({ theme }) => theme.fontWeights.semibold};
  color: ${({ theme }) => theme.colors.gray[800]};
  margin-bottom: ${({ theme }) => theme.spacing[3]};
`;

const ArticleList = styled.div`
  display: flex;
  flex-direction: column;
  gap: ${({ theme }) => theme.spacing[3]};
  padding-left: ${({ theme }) => theme.spacing[4]};
`;

const ArticleItem = styled.div`
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: ${({ theme }) => theme.spacing[3]};
  border-left: 3px solid ${({ theme }) => theme.colors.gray[300]};
  padding-left: ${({ theme }) => theme.spacing[4]};
  cursor: pointer;
  transition: all 0.2s;
  
  &:hover {
    border-left-color: ${({ theme }) => theme.colors.primary[500]};
    background-color: ${({ theme }) => theme.colors.gray[50]};
  }
`;

const ArticleInfo = styled.div`
  flex: 1;
`;

const ArticleTitle = styled.h4`
  font-size: ${({ theme }) => theme.fontSizes.base};
  font-weight: ${({ theme }) => theme.fontWeights.medium};
  color: ${({ theme }) => theme.colors.gray[900]};
  margin-bottom: ${({ theme }) => theme.spacing[1]};
`;

const ArticleCategory = styled.span`
  font-size: ${({ theme }) => theme.fontSizes.sm};
  color: ${({ theme }) => theme.colors.gray[600]};
`;

const ArticleDate = styled.span`
  font-size: ${({ theme }) => theme.fontSizes.sm};
  color: ${({ theme }) => theme.colors.gray[500]};
  white-space: nowrap;
`;

const EmptyState = styled.div`
  text-align: center;
  padding: ${({ theme }) => theme.spacing[12]};
  color: ${({ theme }) => theme.colors.gray[500]};
`;

interface GroupedArticles {
  [year: string]: {
    [month: string]: NewsArticle[];
  };
}

const NewsArchive: React.FC = () => {
  const { t } = useTranslation();
  const navigate = useNavigate();
  const [groupedArticles, setGroupedArticles] = useState<GroupedArticles>({});
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    loadArchive();
  }, []);

  const loadArchive = async () => {
    setIsLoading(true);
    try {
      const response = await newsService.getArticles({ isPublished: true, size: 1000 });
      const grouped = groupArticlesByDate(response.data);
      setGroupedArticles(grouped);
    } catch (error) {
      console.error('Failed to load archive:', error);
    } finally {
      setIsLoading(false);
    }
  };

  const groupArticlesByDate = (articles: NewsArticle[]): GroupedArticles => {
    const grouped: GroupedArticles = {};

    articles.forEach((article) => {
      const date = new Date(article.publishedAt);
      const year = date.getFullYear().toString();
      const month = date.toLocaleString('en-US', { month: 'long' });

      if (!grouped[year]) {
        grouped[year] = {};
      }

      if (!grouped[year][month]) {
        grouped[year][month] = [];
      }

      grouped[year][month].push(article);
    });

    return grouped;
  };

  const handleArticleClick = (articleId: string) => {
    navigate(`/news/${articleId}`);
  };

  const formatDate = (dateString: string) => {
    return new Date(dateString).toLocaleDateString('en-US', {
      month: 'short',
      day: 'numeric',
    });
  };

  if (isLoading) {
    return <LoadingSpinner />;
  }

  const years = Object.keys(groupedArticles).sort((a, b) => parseInt(b) - parseInt(a));

  if (years.length === 0) {
    return (
      <EmptyState>
        <h3>{t('news.noArchive')}</h3>
        <p>{t('news.noArchiveDescription')}</p>
      </EmptyState>
    );
  }

  return (
    <ArchiveContainer>
      {years.map((year) => (
        <YearSection key={year}>
          <YearHeader>{year}</YearHeader>
          {Object.keys(groupedArticles[year])
            .sort((a, b) => {
              const monthOrder = [
                'January', 'February', 'March', 'April', 'May', 'June',
                'July', 'August', 'September', 'October', 'November', 'December'
              ];
              return monthOrder.indexOf(b) - monthOrder.indexOf(a);
            })
            .map((month) => (
              <MonthSection key={month}>
                <MonthHeader>{month}</MonthHeader>
                <ArticleList>
                  {groupedArticles[year][month].map((article) => (
                    <ArticleItem key={article.id} onClick={() => handleArticleClick(article.id)}>
                      <ArticleInfo>
                        <ArticleTitle>{article.title}</ArticleTitle>
                        <ArticleCategory>{article.category}</ArticleCategory>
                      </ArticleInfo>
                      <ArticleDate>{formatDate(article.publishedAt)}</ArticleDate>
                    </ArticleItem>
                  ))}
                </ArticleList>
              </MonthSection>
            ))}
        </YearSection>
      ))}
    </ArchiveContainer>
  );
};

export default NewsArchive;
