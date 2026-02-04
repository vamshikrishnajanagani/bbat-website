import React from 'react';
import { Routes, Route } from 'react-router-dom';
import { Helmet } from 'react-helmet-async';

import Layout from './components/Layout/Layout';
import HomePage from './pages/HomePage';
import MembersPage from './pages/MembersPage';
import PlayersPage from './pages/PlayersPage';
import TournamentsPage from './pages/TournamentsPage';
import DistrictsPage from './pages/DistrictsPage';
import NewsPage from './pages/NewsPage';
import MediaPage from './pages/MediaPage';
import ContactPage from './pages/ContactPage';
import NotFoundPage from './pages/NotFoundPage';
import PrivacyPolicyPage from './pages/PrivacyPolicyPage';
import PrivacySettingsPage from './pages/PrivacySettingsPage';

import { useAuth } from './hooks/useAuth';
import LoadingSpinner from './components/UI/LoadingSpinner';
import SkipLink from './components/UI/SkipLink';
import { AnnouncementBanner } from './components/News';
import { NewsArticleDetail } from './components/News';
import { MediaGalleryView } from './components/Media';

const App: React.FC = () => {
  const { isLoading } = useAuth();

  if (isLoading) {
    return <LoadingSpinner />;
  }

  return (
    <>
      <Helmet>
        <html lang="en" />
        <title>Telangana Ball Badminton Association</title>
        <meta name="description" content="Official website of Telangana Ball Badminton Association - Promoting Ball Badminton in Telangana State" />
        <meta name="keywords" content="ball badminton, telangana, sports, association, tournaments, players" />
        <meta property="og:title" content="Telangana Ball Badminton Association" />
        <meta property="og:description" content="Official website of Telangana Ball Badminton Association" />
        <meta property="og:type" content="website" />
      </Helmet>
      
      <SkipLink href="#main-content">Skip to main content</SkipLink>
      
      <AnnouncementBanner />
      
      <Layout>
        <Routes>
          <Route path="/" element={<HomePage />} />
          <Route path="/members" element={<MembersPage />} />
          <Route path="/players" element={<PlayersPage />} />
          <Route path="/tournaments" element={<TournamentsPage />} />
          <Route path="/districts" element={<DistrictsPage />} />
          <Route path="/news" element={<NewsPage />} />
          <Route path="/news/:id" element={<NewsArticleDetail />} />
          <Route path="/media" element={<MediaPage />} />
          <Route path="/media/gallery/:id" element={<MediaGalleryView />} />
          <Route path="/contact" element={<ContactPage />} />
          <Route path="/privacy-policy" element={<PrivacyPolicyPage />} />
          <Route path="/privacy-settings" element={<PrivacySettingsPage />} />
          <Route path="*" element={<NotFoundPage />} />
        </Routes>
      </Layout>
    </>
  );
};

export default App;