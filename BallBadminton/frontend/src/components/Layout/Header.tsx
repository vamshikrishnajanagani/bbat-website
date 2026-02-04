import React from 'react';
import { Link } from 'react-router-dom';
import styled from 'styled-components';
import { useTranslation } from 'react-i18next';
import { useAppDispatch, useAppSelector } from '../../store';
import { toggleSidebar } from '../../store/slices/uiSlice';
import { useAuth } from '../../hooks/useAuth';
import Button from '../UI/Button';
import LanguageSwitcher from '../UI/LanguageSwitcher';

const HeaderContainer = styled.header`
  background: ${({ theme }) => theme.colors.primary[600]};
  color: white;
  padding: ${({ theme }) => theme.spacing[4]} ${({ theme }) => theme.spacing[6]};
  box-shadow: ${({ theme }) => theme.shadows.md};
  position: sticky;
  top: 0;
  z-index: ${({ theme }) => theme.zIndex.sticky};
`;

const HeaderContent = styled.div`
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  align-items: center;
  justify-content: space-between;
`;

const Logo = styled(Link)`
  display: flex;
  align-items: center;
  text-decoration: none;
  color: white;
  font-size: ${({ theme }) => theme.fontSizes.xl};
  font-weight: ${({ theme }) => theme.fontWeights.bold};
  
  &:hover {
    color: white;
    text-decoration: none;
  }
  
  &:focus {
    outline: 2px solid white;
    outline-offset: 4px;
  }
`;

const LogoText = styled.span`
  margin-left: ${({ theme }) => theme.spacing[3]};
  
  @media (max-width: ${({ theme }) => theme.breakpoints.sm}) {
    display: none;
  }
`;

const Navigation = styled.nav`
  display: none;
  
  @media (min-width: ${({ theme }) => theme.breakpoints.md}) {
    display: flex;
    align-items: center;
    gap: ${({ theme }) => theme.spacing[6]};
  }
`;

const NavLink = styled(Link)`
  color: white;
  text-decoration: none;
  font-weight: ${({ theme }) => theme.fontWeights.medium};
  transition: color ${({ theme }) => theme.transitions.fast};
  
  &:hover {
    color: ${({ theme }) => theme.colors.primary[200]};
    text-decoration: none;
  }
  
  &:focus {
    outline: 2px solid white;
    outline-offset: 4px;
  }
`;

const HeaderActions = styled.div`
  display: flex;
  align-items: center;
  gap: ${({ theme }) => theme.spacing[4]};
`;

const MenuButton = styled.button`
  display: flex;
  align-items: center;
  justify-content: center;
  background: none;
  border: none;
  color: white;
  padding: ${({ theme }) => theme.spacing[2]};
  border-radius: ${({ theme }) => theme.borderRadius.base};
  cursor: pointer;
  transition: background-color ${({ theme }) => theme.transitions.fast};
  
  &:hover {
    background: ${({ theme }) => theme.colors.primary[700]};
  }
  
  &:focus {
    outline: 2px solid white;
    outline-offset: 2px;
  }
  
  @media (min-width: ${({ theme }) => theme.breakpoints.lg}) {
    display: none;
  }
`;

const UserMenu = styled.div`
  display: flex;
  align-items: center;
  gap: ${({ theme }) => theme.spacing[3]};
`;

const Header: React.FC = () => {
  const { t } = useTranslation();
  const dispatch = useAppDispatch();
  const { isAuthenticated, user, logout } = useAuth();
  const { sidebarOpen } = useAppSelector(state => state.ui);

  const handleMenuToggle = () => {
    dispatch(toggleSidebar());
  };

  return (
    <HeaderContainer role="banner">
      <HeaderContent>
        <Logo to="/" aria-label="Telangana Ball Badminton Association Home">
          <span role="img" aria-label="Badminton">🏸</span>
          <LogoText>TBBA</LogoText>
        </Logo>

        <Navigation role="navigation" aria-label="Main navigation">
          <NavLink to="/">{t('navigation.home')}</NavLink>
          <NavLink to="/members">{t('navigation.members')}</NavLink>
          <NavLink to="/players">{t('navigation.players')}</NavLink>
          <NavLink to="/tournaments">{t('navigation.tournaments')}</NavLink>
          <NavLink to="/districts">{t('navigation.districts')}</NavLink>
          <NavLink to="/news">{t('navigation.news')}</NavLink>
          <NavLink to="/media">{t('navigation.media')}</NavLink>
        </Navigation>

        <HeaderActions>
          <LanguageSwitcher />

          {isAuthenticated ? (
            <UserMenu>
              <span aria-label={`Logged in as ${user?.name}`}>Welcome, {user?.name}</span>
              <Button variant="secondary" size="sm" onClick={logout} aria-label="Logout">
                {t('auth.logout')}
              </Button>
            </UserMenu>
          ) : (
            <Button as={Link} to="/login" variant="secondary" size="sm" aria-label="Login">
              {t('auth.login')}
            </Button>
          )}

          <MenuButton 
            onClick={handleMenuToggle}
            aria-label={sidebarOpen ? "Close navigation menu" : "Open navigation menu"}
            aria-expanded={sidebarOpen}
            aria-controls="sidebar-navigation"
          >
            <span aria-hidden="true">☰</span>
          </MenuButton>
        </HeaderActions>
      </HeaderContent>
    </HeaderContainer>
  );
};

export default Header;