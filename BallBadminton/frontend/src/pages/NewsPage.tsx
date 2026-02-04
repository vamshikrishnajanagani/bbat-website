import React, { useEffect, useState } from 'react';
import styled from 'styled-components';
import { Link } from 'react-router-dom';
import { useAppSelector, useAppDispatch } from '../store';
import { setArticles, setLoading } from '../store/slices/newsSlice';
import { newsService } from '../services/newsService';
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

const FilterSection = styled.div`
  display: flex;
  gap: ${({ theme }) => theme.spacing[4]};
  margin-bottom: ${({ theme }) => theme.spacing[6]};
  flex-wrap: wrap;
  align-items: center;
`;

const CategoryFilter = styled.select`
  padding: ${({ theme }) => theme.spacing[2]} ${({ theme }) => theme.spacing[3]};
  border: 1px solid ${({ theme }) => theme.colors.gray[300]};
  border-radius: ${({ theme }) => theme.borderRadius.md};
  font-size: ${({ theme }) => theme.fontSizes.base};
`;

const SearchInput = styled.input`
  flex: 1;
  min-width: 250px;
  padding: ${({ theme }) => theme.spacing[2]} ${({ theme }) => theme.spacing[3]};
  border: 1px solid ${({ theme }) => theme.colors.gray[300]};
  border-radius: ${({ theme }) => theme.borderRadius.md};
  font-size: ${({ theme }) => theme.fontSizes.base};
`;

const NewsGrid = styled.div`
  display: grid;
  grid-template-columns: 1fr;
  gap: ${({ theme }) => theme.spacing[6]};
  
  @media (min-width: ${({ theme }) => theme.breakpoints.lg}) {
    grid-template-columns: 2fr 1fr;
  }
`;

const MainNews = styled.div`
  display: flex;
  flex-direction: column;
  gap: ${({ theme }) => theme.spacing[6]};
`;

const FeaturedArticle = styled.article`
  background: white;
  border-radius: ${({ theme }) => theme.borderRadius.lg};
  overflow: hidden;
  box-shadow: ${({ theme }) => theme.shadows.lg};
`;

const FeaturedImage = styled.img`
  width: 100%;
  height: 300px;
  object-fit: cover;
`;

const FeaturedContent = styled.div`
  padding: ${({ theme }) => theme.spacing[6]};
`;

const ArticleCategory = styled.span`
  display: inline-block;
  padding: ${({ theme }) => theme.spacing[1]} ${({ theme }) => theme.spacing[3]};
  background: ${({ theme }) => theme.colors.primary[100]};
  color: ${({ theme }) => theme.colors.primary[800]};
  border-radius: ${({ theme }) => theme.borderRadius.full};
  font-size: ${({ theme }) => theme.fontSizes.sm};
  font-weight: ${({ theme }) => theme.fontWeights.medium};
  text-transform: uppercase;
  letter-spacing: 0.5px;
  margin-bottom: ${({ theme }) => theme.spacing[3]};
`;

const ArticleTitle = styled.h2`
  font-size: ${({ theme }) => theme.fontSizes['2xl']};
  font-weight: ${({ theme }) => theme.fontWeights.bold};
  color: ${({ theme }) => theme.colors.gray[900]};
  margin-bottom: ${({ theme }) => theme.spacing[3]};
  line-height: ${({ theme }) => theme.lineHeights.tight};
`;

const ArticleExcerpt = styled.p`
  font-size: ${({ theme }) => theme.fontSizes.base};
  color: ${({ theme }) => theme.colors.gray[600]};
  line-height: ${({ theme }) => theme.lineHeights.relaxed};
  margin-bottom: ${({ theme }) => theme.spacing[4]};
`;

const ArticleMeta = styled.div`
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: ${({ theme }) => theme.fontSizes.sm};
  color: ${({ theme }) => theme.colors.gray[500]};
  margin-bottom: ${({ theme }) => theme.spacing[4]};
`;

const RegularArticles = styled.div`
  display: flex;
  flex-direction: column;
  gap: ${({ theme }) => theme.spacing[4]};
`;

const ArticleCard = styled.article`
  background: white;
  border-radius: ${({ theme }) => theme.borderRadius.lg};
  padding: ${({ theme }) => theme.spacing[6]};
  box-shadow: ${({ theme }) => theme.shadows.md};
  transition: transform ${({ theme }) => theme.transitions.base};
  
  &:hover {
    transform: translateY(-2px);
  }
`;

const CardTitle = styled.h3`
  font-size: ${({ theme }) => theme.fontSizes.lg};
  font-weight: ${({ theme }) => theme.fontWeights.semibold};
  color: ${({ theme }) => theme.colors.gray[900]};
  margin-bottom: ${({ theme }) => theme.spacing[2]};
  line-height: ${({ theme }) => theme.lineHeights.tight};
`;

const Sidebar = styled.aside`
  display: flex;
  flex-direction: column;
  gap: ${({ theme }) => theme.spacing[6]};
`;

const SidebarSection = styled.div`
  background: white;
  border-radius: ${({ theme }) => theme.borderRadius.lg};
  padding: ${({ theme }) => theme.spacing[6]};
  box-shadow: ${({ theme }) => theme.shadows.md};
`;

const SidebarTitle = styled.h3`
  font-size: ${({ theme }) => theme.fontSizes.lg};
  font-weight: ${({ theme }) => theme.fontWeights.semibold};
  color: ${({ theme }) => theme.colors.gray[900]};
  margin-bottom: ${({ theme }) => theme.spacing[4]};
`;

const NewsPage: React.FC = () => {
  const dispatch = useAppDispatch();
  const { articles, isLoading } = useAppSelector(state => state.news);
  const [categoryFilter, setCategoryFilter] = useState('all');
  const [searchTerm, setSearchTerm] = useState('');

  useEffect(() => {
    const fetchNews = async () => {
      try {
        dispatch(setLoading(true));
        const data = await newsService.getArticles({ 
          category: categoryFilter !== 'all' ? categoryFilter : undefined,
          search: searchTerm || undefined
        });
        dispatch(setArticles(Array.isArray(data) ? data : (data as any).data || []));
      } catch (error) {
        console.error('Failed to fetch news:', error);
      } finally {
        dispatch(setLoading(false));
      }
    };

    fetchNews();
  }, [dispatch, categoryFilter, searchTerm]);

  const formatDate = (dateString: string) => {
    return new Date(dateString).toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'long',
      day: 'numeric'
    });
  };

  const featuredArticle = articles.find(article => article.isFeatured) || articles[0];
  const regularArticles = articles.filter(article => article.id !== featuredArticle?.id);

  if (isLoading) {
    return <LoadingSpinner text="Loading news..." />;
  }

  return (
    <PageContainer>
      <PageHeader>
        <PageTitle>Latest News</PageTitle>
        <PageDescription>
          Stay updated with the latest news, announcements, and updates from the 
          Telangana Ball Badminton Association.
        </PageDescription>
      </PageHeader>

      <FilterSection>
        <CategoryFilter 
          value={categoryFilter} 
          onChange={(e) => setCategoryFilter(e.target.value)}
          aria-label="Filter by category"
        >
          <option value="all">All Categories</option>
          <option value="tournaments">Tournaments</option>
          <option value="announcements">Announcements</option>
          <option value="achievements">Achievements</option>
          <option value="events">Events</option>
        </CategoryFilter>

        <SearchInput
          type="text"
          placeholder="Search news..."
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
          aria-label="Search news articles"
        />
      </FilterSection>

      <NewsGrid>
        <MainNews>
          {featuredArticle && (
            <FeaturedArticle>
              <FeaturedImage 
                src={featuredArticle.featuredImage || '/default-news.jpg'} 
                alt={featuredArticle.title}
              />
              <FeaturedContent>
                <ArticleCategory>{featuredArticle.category}</ArticleCategory>
                <ArticleTitle>{featuredArticle.title}</ArticleTitle>
                <ArticleMeta>
                  <span>By {featuredArticle.author}</span>
                  <span>{formatDate(featuredArticle.publishedAt)}</span>
                </ArticleMeta>
                <ArticleExcerpt>{featuredArticle.excerpt}</ArticleExcerpt>
                <Button as={Link} to={`/news/${featuredArticle.id}`}>
                  Read More
                </Button>
              </FeaturedContent>
            </FeaturedArticle>
          )}

          <RegularArticles>
            {regularArticles.map(article => (
              <ArticleCard key={article.id}>
                <ArticleCategory>{article.category}</ArticleCategory>
                <CardTitle>
                  <Link 
                    to={`/news/${article.id}`}
                    style={{ textDecoration: 'none', color: 'inherit' }}
                  >
                    {article.title}
                  </Link>
                </CardTitle>
                <ArticleMeta>
                  <span>By {article.author}</span>
                  <span>{formatDate(article.publishedAt)}</span>
                </ArticleMeta>
                <ArticleExcerpt>{article.excerpt}</ArticleExcerpt>
                <Button as={Link} to={`/news/${article.id}`} size="sm" variant="outline">
                  Read More
                </Button>
              </ArticleCard>
            ))}
          </RegularArticles>
        </MainNews>

        <Sidebar>
          <SidebarSection>
            <SidebarTitle>Recent Announcements</SidebarTitle>
            <p style={{ fontSize: '0.875rem', color: '#6b7280' }}>
              Important updates and announcements from the association.
            </p>
          </SidebarSection>

          <SidebarSection>
            <SidebarTitle>Upcoming Events</SidebarTitle>
            <p style={{ fontSize: '0.875rem', color: '#6b7280' }}>
              Don't miss these upcoming tournaments and events.
            </p>
          </SidebarSection>

          <SidebarSection>
            <SidebarTitle>Newsletter</SidebarTitle>
            <p style={{ fontSize: '0.875rem', color: '#6b7280', marginBottom: '1rem' }}>
              Subscribe to get the latest updates delivered to your inbox.
            </p>
            <Button size="sm" fullWidth>
              Subscribe
            </Button>
          </SidebarSection>
        </Sidebar>
      </NewsGrid>

      {articles.length === 0 && !isLoading && (
        <div style={{ textAlign: 'center', padding: '2rem' }}>
          <p>No news articles found.</p>
        </div>
      )}
    </PageContainer>
  );
};

export default NewsPage;