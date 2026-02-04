import React from 'react';
import styled from 'styled-components';

const RegisterContainer = styled.div`
  max-width: 400px;
  margin: 0 auto;
  padding: ${({ theme }) => theme.spacing[6]};
`;

const RegisterForm: React.FC = () => {
  return (
    <RegisterContainer>
      <h2>Register</h2>
      <p>Registration form will be implemented here.</p>
    </RegisterContainer>
  );
};

export default RegisterForm;