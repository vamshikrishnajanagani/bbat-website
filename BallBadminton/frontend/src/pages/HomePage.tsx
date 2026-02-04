import React from 'react';
import styled from 'styled-components';
import { Link } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import Button from '../components/UI/Button';

const HeroSection = styled.section`
  background: linear-gradient(135deg, ${({ theme }) => theme.colors.primary[600]} 0%, ${({ theme }) => theme.colors.primary[800]} 100%);
  color: white;
  padding: ${({ theme }) => theme.spacing[16]} 0;
  text-align: center;
  margin: -${({ theme }) => theme.spacing[6]} -${({ theme }) => theme.spacing[4]} ${({ theme }) => theme.spacing[12]};
  
  @media (min-width: ${({ theme }) => theme.breakpoints.sm}) {
    margin: -${({ theme }) => theme.spacing[8]} -${({ theme }) => theme.spacing[6]} ${({ theme }) => theme.spacing[12]};
  }
`;

const HeroContent = styled.div`
  max-width: 800px;
  margin: 0 auto;
  padding: 0 ${({ theme }) => theme.spacing[4]};
`;

const HeroTitle = styled.h1`
  font-size: ${({ theme }) => theme.fontSizes['5xl']};
  font-weight: ${({ theme }) => theme.fontWeights.bold};
  margin-bottom: ${({ theme }) => theme.spacing[6]};
  color: white;
  
  @media (max-width: ${({ theme }) => theme.breakpoints.md}) {
    font-size: ${({ theme }) => theme.fontSizes['4xl']};
  }
`;

const HeroSubtitle = styled.p`
  font-size: ${({ theme }) => theme.fontSizes.xl};
  margin-bottom: ${({ theme }) => theme.spacing[8]};
  opacity: 0.9;
  line-height: ${({ theme }) => theme.lineHeights.relaxed};
`;

const HeroActions = styled.div`
  display: flex;
  gap: ${({ theme }) => theme.spacing[4]};
  justify-content: center;
  flex-wrap: wrap;
`;

const FeaturesSection = styled.section`
  padding: ${({ theme }) => theme.spacing[12]} 0;
`;

const FeaturesGrid = styled.div`
  display: grid;
  grid-template-columns: 1fr;
  gap: ${({ theme }) => theme.spacing[8]};
  
  @media (min-width: ${({ theme }) => theme.breakpoints.md}) {
    grid-template-columns: repeat(2, 1fr);
  }
  
  @media (min-width: ${({ theme }) => theme.breakpoints.lg}) {
    grid-template-columns: repeat(3, 1fr);
  }
`;

const FeatureCard = styled.div`
  background: white;
  padding: ${({ theme }) => theme.spacing[6]};
  border-radius: ${({ theme }) => theme.borderRadius.lg};
  box-shadow: ${({ theme }) => theme.shadows.md};
  text-align: center;
  transition: transform ${({ theme }) => theme.transitions.base};
  
  &:hover {
    transform: translateY(-4px);
  }
`;

const FeatureIcon = styled.div`
  font-size: ${({ theme }) => theme.fontSizes['4xl']};
  margin-bottom: ${({ theme }) => theme.spacing[4]};
`;

const FeatureTitle = styled.h3`
  font-size: ${({ theme }) => theme.fontSizes.xl};
  font-weight: ${({ theme }) => theme.fontWeights.semibold};
  margin-bottom: ${({ theme }) => theme.spacing[3]};
  color: ${({ theme }) => theme.colors.gray[900]};
`;

const FeatureDescription = styled.p`
  color: ${({ theme }) => theme.colors.gray[600]};
  line-height: ${({ theme }) => theme.lineHeights.relaxed};
`;

const StatsSection = styled.section`
  background: ${({ theme }) => theme.colors.gray[100]};
  padding: ${({ theme }) => theme.spacing[12]} 0;
  margin: 0 -${({ theme }) => theme.spacing[4]};
  
  @media (min-width: ${({ theme }) => theme.breakpoints.sm}) {
    margin: 0 -${({ theme }) => theme.spacing[6]};
  }
`;

const StatsGrid = styled.div`
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: ${({ theme }) => theme.spacing[6]};
  max-width: 800px;
  margin: 0 auto;
  padding: 0 ${({ theme }) => theme.spacing[4]};
  
  @media (min-width: ${({ theme }) => theme.breakpoints.md}) {
    grid-template-columns: repeat(4, 1fr);
  }
`;

const StatCard = styled.div`
  text-align: center;
`;

const StatNumber = styled.div`
  font-size: ${({ theme }) => theme.fontSizes['3xl']};
  font-weight: ${({ theme }) => theme.fontWeights.bold};
  color: ${({ theme }) => theme.colors.primary[600]};
  margin-bottom: ${({ theme }) => theme.spacing[2]};
`;

const StatLabel = styled.div`
  font-size: ${({ theme }) => theme.fontSizes.sm};
  color: ${({ theme }) => theme.colors.gray[600]};
  font-weight: ${({ theme }) => theme.fontWeights.medium};
`;

const HomePage: React.FC = () => {
  const { t } = useTranslation();

  const features = [
    {
      icon: '👥',
      title: 'Association Members',
      description: 'Meet our dedicated leadership team working to promote Ball Badminton across Telangana.',
      link: '/members'
    },
    {
      icon: '🏆',
      title: 'Player Profiles',
      description: 'Discover talented players from across the state and their achievements.',
      link: '/players'
    },
    {
      icon: '🏸',
      title: 'Tournaments',
      description: 'Stay updated with upcoming tournaments and register for competitions.',
      link: '/tournaments'
    },
    {
      icon: '🗺️',
      title: 'District Information',
      description: 'Explore Ball Badminton activities and statistics across all districts.',
      link: '/districts'
    },
    {
      icon: '📰',
      title: 'Latest News',
      description: 'Read the latest news, updates, and announcements from the association.',
      link: '/news'
    },
    {
      icon: '📸',
      title: 'Media Gallery',
      description: 'Browse photos and videos from tournaments and association events.',
      link: '/media'
    }
  ];

  const stats = [
    { number: '33', label: 'Districts' },
    { number: '500+', label: 'Registered Players' },
    { number: '50+', label: 'Annual Tournaments' },
    { number: '25+', label: 'Active Clubs' }
  ];

  return (
    <>
      <HeroSection aria-labelledby="hero-title">
        <HeroContent>
          <HeroTitle id="hero-title">
            Telangana Ball Badminton Association
          </HeroTitle>
          <HeroSubtitle>
            Promoting Ball Badminton excellence across Telangana state through 
            competitive tournaments, player development, and community engagement.
          </HeroSubtitle>
          <HeroActions>
            <Button as={Link} to="/tournaments" size="lg" aria-label="View all tournaments">
              View Tournaments
            </Button>
            <Button as={Link} to="/players" variant="outline" size="lg" aria-label="Explore player profiles">
              Explore Players
            </Button>
          </HeroActions>
        </HeroContent>
      </HeroSection>

      <FeaturesSection aria-labelledby="features-title">
        <div className="container">
          <h2 id="features-title" style={{ textAlign: 'center', marginBottom: '2rem', fontSize: '2rem' }}>
            Explore Our Features
          </h2>
          <FeaturesGrid>
            {features.map((feature, index) => (
              <FeatureCard key={index}>
                <FeatureIcon role="img" aria-label={feature.title}>{feature.icon}</FeatureIcon>
                <FeatureTitle>{feature.title}</FeatureTitle>
                <FeatureDescription>{feature.description}</FeatureDescription>
                <Button 
                  as={Link} 
                  to={feature.link} 
                  variant="outline" 
                  size="sm" 
                  style={{ marginTop: '1rem' }}
                  aria-label={`Learn more about ${feature.title}`}
                >
                  Learn More
                </Button>
              </FeatureCard>
            ))}
          </FeaturesGrid>
        </div>
      </FeaturesSection>

      <StatsSection aria-labelledby="stats-title">
        <div className="container">
          <h2 id="stats-title" style={{ textAlign: 'center', marginBottom: '2rem', fontSize: '2rem', color: '#1f2937' }}>
            Our Impact
          </h2>
          <StatsGrid>
            {stats.map((stat, index) => (
              <StatCard key={index}>
                <StatNumber aria-label={`${stat.number} ${stat.label}`}>{stat.number}</StatNumber>
                <StatLabel>{stat.label}</StatLabel>
              </StatCard>
            ))}
          </StatsGrid>
        </div>
      </StatsSection>
    </>
  );
};

export default HomePage;