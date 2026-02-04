import React from 'react';
import { Link, useLocation } from 'react-router-dom';
import styled from 'styled-components';
import { useTranslation } from 'react-i18next';
import { useAuth } from '../../hooks/useAuth';

const AdminContainer = styled.div`
  display: flex;
  min-height: 100vh;
  background: ${({ theme }) => theme.colors.gray[50]};
`;

const AdminSidebar = styled.aside`
  width: 280px;
  background: white;
  border-right: 1px solid ${({ theme }) => theme.colors.gray[200]};
  box-shadow: ${({ theme }) => theme.shadows.sm};
  
  @media (max-width: ${({ theme }) => theme.breakpoints.lg}) {
    width: 240px;
  }
`;

const SidebarHeader = styled.div`
  padding: ${({ theme }) => theme.spacing[6]};
  border-bottom: 1px solid ${({ theme }) => theme.colors.gray[200]};
  
  h2 {
    margin: 0;
    color: ${({ theme }) => theme.colors.gray[900]};
    font-size: ${({ theme }) => theme.fontSizes.lg};
    font-weight: ${({ theme }) => theme.fontWeights.semibold};
  }
  
  p {
    margin: ${({ theme }) => theme.spacing[1]} 0 0 0;
    color: ${({ theme }) => theme.colors.gray[600]};
    font-size: ${({ theme }) => theme.fontSizes.sm};
  }
`;

const SidebarNav = styled.nav`
  padding: ${({ theme }) => theme.spacing[4]} 0;
`;

const NavSection = styled.div`
  margin-bottom: ${({ theme }) => theme.spacing[6]};
  
  &:last-child {
    margin-bottom: 0;
  }
`;

const NavSectionTitle = styled.h3`
  color: ${({ theme }) => theme.colors.gray[500]};
  font-size: ${({ theme }) => theme.fontSizes.xs};
  font-weight: ${({ theme }) => theme.fontWeights.semibold};
  text-transform: uppercase;
  letter-spacing: 0.05em;
  padding: 0 ${({ theme }) => theme.spacing[6]};
  margin: 0 0 ${({ theme }) => theme.spacing[3]} 0;
`;

const NavList = styled.ul`
  list-style: none;
  padding: 0;
  margin: 0;
`;

const NavItem = styled.li`
  margin: 0;
`;

const NavLink = styled(Link)<{ isActive: boolean }>`
  display: flex;
  align-items: center;
  padding: ${({ theme }) => theme.spacing[3]} ${({ theme }) => theme.spacing[6]};
  color: ${({ isActive, theme }) => 
    isActive ? theme.colors.primary[600] : theme.colors.gray[700]};
  text-decoration: none;
  font-weight: ${({ theme }) => theme.fontWeights.medium};
  background: ${({ isActive, theme }) => 
    isActive ? theme.colors.primary[50] : 'transparent'};
  border-right: ${({ isActive, theme }) => 
    isActive ? `3px solid ${theme.colors.primary[600]}` : '3px solid transparent'};
  transition: all ${({ theme }) => theme.transitions.fast};
  
  &:hover {
    background: ${({ theme }) => theme.colors.gray[50]};
    color: ${({ theme }) => theme.colors.primary[600]};
    text-decoration: none;
  }
`;

const NavIcon = styled.span`
  margin-right: ${({ theme }) => theme.spacing[3]};
  font-size: ${({ theme }) => theme.fontSizes.lg};
  width: 20px;
  text-align: center;
`;

const AdminMain = styled.main`
  flex: 1;
  display: flex;
  flex-direction: column;
`;

const AdminHeader = styled.header`
  background: white;
  border-bottom: 1px solid ${({ theme }) => theme.colors.gray[200]};
  padding: ${({ theme }) => theme.spacing[4]} ${({ theme }) => theme.spacing[6]};
  display: flex;
  align-items: center;
  justify-content: space-between;
`;

const PageTitle = styled.h1`
  margin: 0;
  color: ${({ theme }) => theme.colors.gray[900]};
  font-size: ${({ theme }) => theme.fontSizes.xl};
  font-weight: ${({ theme }) => theme.fontWeights.semibold};
`;

const UserInfo = styled.div`
  display: flex;
  align-items: center;
  gap: ${({ theme }) => theme.spacing[3]};
  color: ${({ theme }) => theme.colors.gray[600]};
  font-size: ${({ theme }) => theme.fontSizes.sm};
`;

const AdminContent = styled.div`
  flex: 1;
  padding: ${({ theme }) => theme.spacing[6]};
  overflow-y: auto;
`;

interface AdminLayoutProps {
  children: React.ReactNode;
  title?: string;
}

const AdminLayout: React.FC<AdminLayoutProps> = ({ children, title }) => {
  const { t } = useTranslation();
  const location = useLocation();
  const { user } = useAuth();

  const navigationItems = [
    {
      section: t('admin.overview'),
      items: [
        { path: '/admin', label: t('admin.dashboard'), icon: '📊' },
        { path: '/admin/analytics', label: t('admin.analytics'), icon: '📈' },
      ]
    },
    {
      section: t('admin.contentManagement'),
      items: [
        { path: '/admin/members', label: t('admin.members'), icon: '👥' },
        { path: '/admin/players', label: t('admin.players'), icon: '🏆' },
        { path: '/admin/tournaments', label: t('admin.tournaments'), icon: '🏸' },
        { path: '/admin/news', label: t('admin.news'), icon: '📰' },
        { path: '/admin/media', label: t('admin.media'), icon: '📸' },
      ]
    },
    {
      section: t('admin.system'),
      items: [
        { path: '/admin/users', label: t('admin.users'), icon: '👤' },
        { path: '/admin/settings', label: t('admin.settings'), icon: '⚙️' },
        { path: '/admin/logs', label: t('admin.logs'), icon: '📋' },
      ]
    }
  ];

  const getPageTitle = () => {
    if (title) return title;
    
    const currentPath = location.pathname;
    for (const section of navigationItems) {
      const item = section.items.find(item => item.path === currentPath);
      if (item) return item.label;
    }
    
    return t('admin.dashboard');
  };

  return (
    <AdminContainer>
      <AdminSidebar>
        <SidebarHeader>
          <h2>{t('admin.adminPanel')}</h2>
          <p>{t('admin.contentManagementSystem')}</p>
        </SidebarHeader>
        
        <SidebarNav>
          {navigationItems.map((section, sectionIndex) => (
            <NavSection key={sectionIndex}>
              <NavSectionTitle>{section.section}</NavSectionTitle>
              <NavList>
                {section.items.map((item) => (
                  <NavItem key={item.path}>
                    <NavLink
                      to={item.path}
                      isActive={location.pathname === item.path}
                    >
                      <NavIcon>{item.icon}</NavIcon>
                      {item.label}
                    </NavLink>
                  </NavItem>
                ))}
              </NavList>
            </NavSection>
          ))}
        </SidebarNav>
      </AdminSidebar>

      <AdminMain>
        <AdminHeader>
          <PageTitle>{getPageTitle()}</PageTitle>
          <UserInfo>
            <span>{t('admin.welcomeBack')}, {user?.name}</span>
            <Link to="/profile" style={{ color: 'inherit', textDecoration: 'none' }}>
              👤
            </Link>
          </UserInfo>
        </AdminHeader>
        
        <AdminContent>
          {children}
        </AdminContent>
      </AdminMain>
    </AdminContainer>
  );
};

export default AdminLayout;