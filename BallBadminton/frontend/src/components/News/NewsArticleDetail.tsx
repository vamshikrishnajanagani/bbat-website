import React, { useEffect } from 'react';
import { useParams, Navigate } from 'react-router-dom';
import styled from 'styled-components';
import { useAppSelector, useAppDispatch } from '../../store';
import { setSelectedArticle } from '../../store/slices/newsSlice';
import { newsService } from '../../services/newsService';
import LoadingSpinner from '../UI/LoadingSpinner';

const ArticleContainer = styled.article`
  max-width: 800px;
  margin: 0 auto;
  padding: ${({ theme }) => theme.spacing[6]};
`;

const ArticleHeader = styled.header`
  margin-bottom: ${({ theme }) => theme.spacing[6]};
`;

const ArticleTitle = styled.h1`
  font-size: ${({ theme }) => theme.fontSizes['4xl']};
  font-weight: ${({ theme }) => theme.fontWeights.bold};
  color: ${({ theme }) => theme.colors.gray[900]};
  margin-bottom: ${({ theme }) => theme.spacing[4]};
  line-height: ${({ theme }) => theme.lineHeights.tight};
`;

const ArticleMeta = styled.div`
  display: flex;
  flex-wrap: wrap;
  gap: ${({ theme }) => theme.spacing[4]};
  color: ${({ theme }) => theme.colors.gray[600]};
  font-size: ${({ theme }) => theme.fontSizes.sm};
  margin-bottom: ${({ theme }) => theme.spacing[4]};
`;

const FeaturedImage = styled.img`
  width: 100%;
  height: 400px;
  object-fit: cover;
  border-radius: ${({ theme }) => theme.borderRadius.lg};
  margin-bottom: ${({ theme }) => theme.spacing[6]};
`;

const ArticleContent = styled.div`
  font-size: ${({ theme }) => theme.fontSizes.lg};
  line-height: ${({ theme }) => theme.lineHeights.relaxed};
  color: ${({ theme }) => theme.colors.gray[800]};
  
  p {
    margin-bottom: ${({ theme }) => theme.spacing[4]};
  }
  
  h2, h3, h4 {
    margin-top: ${({ theme }) => theme.spacing[6]};
    margin-bottom: ${({ theme }) => theme.spacing[4]};
  }
`;

const TagList = styled.div`
  display: flex;
  flex-wrap: wrap;
  gap: ${({ theme }) => theme.spacing[2]};
  margin-top: ${({ theme }) => theme.spacing[6]};
`;

const Tag = styled.span`
  background: ${({ theme }) => theme.colors.primary[100]};
  color: ${({ theme }) => theme.colors.primary[800]};
  padding: ${({ theme }) => theme.spacing[1]} ${({ theme }) => theme.spacing[3]};
  border-radius: ${({ theme }) => theme.borderRadius.full};
  font-size: ${({ theme }) => theme.fontSizes.sm};
`;

export const NewsArticleDetail: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const dispatch = useAppDispatch();
  const { selectedArticle, isLoading } = useAppSelector(state => state.news);

  useEffect(() => {
    const fetchArticle = async () => {
      if (id) {
        try {
          const article = await newsService.getArticleById(id);
          dispatch(setSelectedArticle(article));
        } catch (error) {
          console.error('Failed to fetch article:', error);
        }
      }
    };

    fetchArticle();
  }, [id, dispatch]);

  if (isLoading) {
    return <LoadingSpinner />;
  }

  if (!selectedArticle) {
    return <Navigate to="/news" replace />;
  }

  return (
    <ArticleContainer>
      <ArticleHeader>
        <ArticleTitle>{selectedArticle.title}</ArticleTitle>
        <ArticleMeta>
          <span>By {selectedArticle.author}</span>
          <span>Published {new Date(selectedArticle.publishedAt).toLocaleDateString()}</span>
          <span>Category: {selectedArticle.category}</span>
        </ArticleMeta>
      </ArticleHeader>

      {selectedArticle.featuredImage && (
        <FeaturedImage 
          src={selectedArticle.featuredImage} 
          alt={selectedArticle.title}
        />
      )}

      <ArticleContent>
        <div dangerouslySetInnerHTML={{ __html: selectedArticle.content }} />
      </ArticleContent>

      {selectedArticle.tags.length > 0 && (
        <TagList>
          {selectedArticle.tags.map(tag => (
            <Tag key={tag}>{tag}</Tag>
          ))}
        </TagList>
      )}
    </ArticleContainer>
  );
};