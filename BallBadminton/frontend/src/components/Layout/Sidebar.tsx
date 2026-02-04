import React from 'react';
import { Link, useLocation } from 'react-router-dom';
import styled from 'styled-components';
import { useTranslation } from 'react-i18next';
import { useAppSelector, useAppDispatch } from '../../store';
import { setSidebarOpen } from '../../store/slices/uiSlice';

const SidebarContainer = styled.aside<{ isOpen: boolean }>`
  position: fixed;
  top: 0;
  left: 0;
  height: 100vh;
  width: 250px;
  background: ${({ theme }) => theme.colors.gray[900]};
  color: white;
  transform: translateX(${({ isOpen }) => isOpen ? '0' : '-100%'});
  transition: transform ${({ theme }) => theme.transitions.base};
  z-index: ${({ theme }) => theme.zIndex.modal};
  overflow-y: auto;
  
  @media (min-width: ${({ theme }) => theme.breakpoints.lg}) {
    position: static;
    transform: translateX(${({ isOpen }) => isOpen ? '0' : '-100%'});
    height: auto;
  }
`;

const SidebarHeader = styled.div`
  padding: ${({ theme }) => theme.spacing[4]};
  border-bottom: 1px solid ${({ theme }) => theme.colors.gray[700]};
  
  h2 {
    font-size: ${({ theme }) => theme.fontSizes.lg};
    font-weight: ${({ theme }) => theme.fontWeights.semibold};
    margin: 0;
    color: white;
  }
`;

const SidebarNav = styled.nav`
  padding: ${({ theme }) => theme.spacing[4]} 0;
`;

const NavItem = styled(Link)<{ active: boolean }>`
  display: block;
  padding: ${({ theme }) => theme.spacing[3]} ${({ theme }) => theme.spacing[4]};
  color: ${({ active, theme }) => active ? theme.colors.primary[300] : theme.colors.gray[300]};
  text-decoration: none;
  transition: all ${({ theme }) => theme.transitions.fast};
  border-left: 3px solid ${({ active, theme }) => active ? theme.colors.primary[500] : 'transparent'};
  
  &:hover {
    background: ${({ theme }) => theme.colors.gray[800]};
    color: white;
    text-decoration: none;
  }
  
  &:focus {
    outline: 2px solid ${({ theme }) => theme.colors.primary[500]};
    outline-offset: -2px;
  }
`;

const CloseButton = styled.button`
  position: absolute;
  top: ${({ theme }) => theme.spacing[4]};
  right: ${({ theme }) => theme.spacing[4]};
  background: none;
  border: none;
  color: white;
  font-size: 24px;
  cursor: pointer;
  
  &:hover {
    opacity: 0.8;
  }
  
  &:focus {
    outline: 2px solid white;
    outline-offset: 2px;
  }
  
  @media (min-width: ${({ theme }) => theme.breakpoints.lg}) {
    display: none;
  }
`;

const Sidebar: React.FC = () => {
  const { t } = useTranslation();
  const location = useLocation();
  const dispatch = useAppDispatch();
  const { sidebarOpen } = useAppSelector(state => state.ui);

  const handleClose = () => {
    dispatch(setSidebarOpen(false));
  };

  const navItems = [
    { path: '/', label: 'Home' },
    { path: '/members', label: 'Members' },
    { path: '/players', label: 'Players' },
    { path: '/tournaments', label: 'Tournaments' },
    { path: '/districts', label: 'Districts' },
    { path: '/news', label: 'News' },
    { path: '/media', label: 'Media' },
    { path: '/contact', label: 'Contact' },
  ];

  return (
    <SidebarContainer 
      isOpen={sidebarOpen}
      id="sidebar-navigation"
      aria-label="Main navigation"
    >
      <CloseButton 
        onClick={handleClose}
        aria-label="Close navigation menu"
      >
        ×
      </CloseButton>
      
      <SidebarHeader>
        <h2>Navigation</h2>
      </SidebarHeader>
      
      <SidebarNav>
        {navItems.map(item => (
          <NavItem
            key={item.path}
            to={item.path}
            active={location.pathname === item.path}
            onClick={handleClose}
          >
            {item.label}
          </NavItem>
        ))}
      </SidebarNav>
    </SidebarContainer>
  );
};

export default Sidebar;