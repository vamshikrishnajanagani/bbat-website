import React from 'react';
import styled from 'styled-components';

const ProfileContainer = styled.div`
  max-width: 600px;
  margin: 0 auto;
  padding: ${({ theme }) => theme.spacing[6]};
`;

const UserProfile: React.FC = () => {
  return (
    <ProfileContainer>
      <h2>User Profile</h2>
      <p>User profile will be implemented here.</p>
    </ProfileContainer>
  );
};

export default UserProfile;