import React from 'react';
import styled from 'styled-components';

const ForgotPasswordContainer = styled.div`
  max-width: 400px;
  margin: 0 auto;
  padding: ${({ theme }) => theme.spacing[6]};
`;

const ForgotPasswordForm: React.FC = () => {
  return (
    <ForgotPasswordContainer>
      <h2>Forgot Password</h2>
      <p>Forgot password form will be implemented here.</p>
    </ForgotPasswordContainer>
  );
};

export default ForgotPasswordForm;