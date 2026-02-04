import React from 'react';
import styled from 'styled-components';
import Header from './Header';
import Footer from './Footer';
import Sidebar from './Sidebar';
import Breadcrumb from './Breadcrumb';
import NotificationContainer from '../UI/NotificationContainer';
import { useAppSelector } from '../../store';

const LayoutContainer = styled.div`
  min-height: 100vh;
  display: flex;
  flex-direction: column;
`;

const MainContent = styled.main<{ sidebarOpen: boolean }>`
  flex: 1;
  display: flex;
  transition: margin-left ${({ theme }) => theme.transitions.base};
  
  @media (min-width: ${({ theme }) => theme.breakpoints.lg}) {
    margin-left: ${({ sidebarOpen }) => sidebarOpen ? '250px' : '0'};
  }
`;

const ContentArea = styled.div`
  flex: 1;
  display: flex;
  flex-direction: column;
`;

const PageContent = styled.div`
  flex: 1;
  padding: ${({ theme }) => theme.spacing[6]} ${({ theme }) => theme.spacing[4]};
  
  @media (min-width: ${({ theme }) => theme.breakpoints.sm}) {
    padding: ${({ theme }) => theme.spacing[8]} ${({ theme }) => theme.spacing[6]};
  }
`;

interface LayoutProps {
  children: React.ReactNode;
}

const Layout: React.FC<LayoutProps> = ({ children }) => {
  const { sidebarOpen } = useAppSelector(state => state.ui);

  return (
    <LayoutContainer>
      <Header />
      <MainContent 
        sidebarOpen={sidebarOpen}
        id="main-content"
        role="main"
        aria-label="Main content"
      >
        <Sidebar />
        <ContentArea>
          <Breadcrumb />
          <PageContent>
            {children}
          </PageContent>
          <Footer />
        </ContentArea>
      </MainContent>
      <NotificationContainer />
    </LayoutContainer>
  );
};

export default Layout;