import React from 'react';
import { Link, useLocation } from 'react-router-dom';
import styled from 'styled-components';

const BreadcrumbContainer = styled.nav`
  padding: ${({ theme }) => theme.spacing[4]} ${({ theme }) => theme.spacing[4]};
  background: ${({ theme }) => theme.colors.gray[100]};
  border-bottom: 1px solid ${({ theme }) => theme.colors.gray[200]};
  
  @media (min-width: ${({ theme }) => theme.breakpoints.sm}) {
    padding: ${({ theme }) => theme.spacing[4]} ${({ theme }) => theme.spacing[6]};
  }
`;

const BreadcrumbList = styled.ol`
  display: flex;
  align-items: center;
  list-style: none;
  margin: 0;
  padding: 0;
  font-size: ${({ theme }) => theme.fontSizes.sm};
`;

const BreadcrumbItem = styled.li`
  display: flex;
  align-items: center;
  
  &:not(:last-child)::after {
    content: '/';
    margin: 0 ${({ theme }) => theme.spacing[2]};
    color: ${({ theme }) => theme.colors.gray[400]};
  }
`;

const BreadcrumbLink = styled(Link)`
  color: ${({ theme }) => theme.colors.primary[600]};
  text-decoration: none;
  
  &:hover {
    text-decoration: underline;
  }
  
  &:focus {
    outline: 2px solid ${({ theme }) => theme.colors.primary[500]};
    outline-offset: 2px;
  }
`;

const BreadcrumbText = styled.span`
  color: ${({ theme }) => theme.colors.gray[600]};
`;

const Breadcrumb: React.FC = () => {
  const location = useLocation();
  const pathnames = location.pathname.split('/').filter(x => x);

  const breadcrumbNameMap: { [key: string]: string } = {
    '': 'Home',
    'members': 'Members',
    'players': 'Players',
    'tournaments': 'Tournaments',
    'districts': 'Districts',
    'news': 'News',
    'media': 'Media',
    'contact': 'Contact',
    'privacy-policy': 'Privacy Policy',
    'privacy-settings': 'Privacy Settings',
  };

  if (pathnames.length === 0) {
    return null; // Don't show breadcrumb on home page
  }

  return (
    <BreadcrumbContainer aria-label="Breadcrumb">
      <BreadcrumbList>
        <BreadcrumbItem>
          <BreadcrumbLink to="/">Home</BreadcrumbLink>
        </BreadcrumbItem>
        {pathnames.map((name, index) => {
          const routeTo = `/${pathnames.slice(0, index + 1).join('/')}`;
          const isLast = index === pathnames.length - 1;
          const displayName = breadcrumbNameMap[name] || name;

          return (
            <BreadcrumbItem key={name}>
              {isLast ? (
                <BreadcrumbText aria-current="page">{displayName}</BreadcrumbText>
              ) : (
                <BreadcrumbLink to={routeTo}>{displayName}</BreadcrumbLink>
              )}
            </BreadcrumbItem>
          );
        })}
      </BreadcrumbList>
    </BreadcrumbContainer>
  );
};

export default Breadcrumb;