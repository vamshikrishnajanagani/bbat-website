import React from 'react';
import styled from 'styled-components';
import { useTranslation } from 'react-i18next';
import { useAppDispatch, useAppSelector } from '../../store';
import { setLanguage } from '../../store/slices/uiSlice';

const LanguageButton = styled.button<{ active: boolean }>`
  background: ${({ active, theme }) => active ? theme.colors.primary[700] : 'transparent'};
  border: 1px solid ${({ theme }) => theme.colors.primary[300]};
  color: white;
  padding: ${({ theme }) => theme.spacing[2]} ${({ theme }) => theme.spacing[3]};
  border-radius: ${({ theme }) => theme.borderRadius.base};
  cursor: pointer;
  font-size: ${({ theme }) => theme.fontSizes.sm};
  transition: all ${({ theme }) => theme.transitions.fast};
  
  &:hover {
    background: ${({ theme }) => theme.colors.primary[700]};
  }
  
  &:focus {
    outline: 2px solid white;
    outline-offset: 2px;
  }
  
  & + & {
    margin-left: ${({ theme }) => theme.spacing[1]};
  }
`;

const LanguageSwitcher: React.FC = () => {
  const { i18n } = useTranslation();
  const dispatch = useAppDispatch();
  const currentLanguage = useAppSelector(state => state.ui.language);

  const handleLanguageChange = (language: 'en' | 'te') => {
    i18n.changeLanguage(language);
    dispatch(setLanguage(language));
  };

  return (
    <div role="group" aria-label="Language selection">
      <LanguageButton
        active={currentLanguage === 'en'}
        onClick={() => handleLanguageChange('en')}
        aria-label="Switch to English"
      >
        EN
      </LanguageButton>
      <LanguageButton
        active={currentLanguage === 'te'}
        onClick={() => handleLanguageChange('te')}
        aria-label="Switch to Telugu"
      >
        తె
      </LanguageButton>
    </div>
  );
};

export default LanguageSwitcher;