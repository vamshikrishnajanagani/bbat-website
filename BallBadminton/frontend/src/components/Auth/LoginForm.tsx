import React from 'react';
import styled from 'styled-components';

const LoginContainer = styled.div`
  max-width: 400px;
  margin: 0 auto;
  padding: ${({ theme }) => theme.spacing[6]};
`;

const LoginForm: React.FC = () => {
  return (
    <LoginContainer>
      <h2>Login</h2>
      <p>Login form will be implemented here.</p>
    </LoginContainer>
  );
};

export default LoginForm;