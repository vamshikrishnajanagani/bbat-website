import React from 'react';
import styled from 'styled-components';

const InfiniteScrollContainer = styled.div`
  width: 100%;
`;

interface InfiniteScrollProps {
  children: React.ReactNode;
  hasMore?: boolean;
  loading?: boolean;
  onLoadMore?: () => void;
}

const InfiniteScroll: React.FC<InfiniteScrollProps> = ({ 
  children, 
  hasMore = false, 
  loading = false, 
  onLoadMore 
}) => {
  return (
    <InfiniteScrollContainer>
      {children}
      {loading && <div>Loading more...</div>}
    </InfiniteScrollContainer>
  );
};

export default InfiniteScroll;