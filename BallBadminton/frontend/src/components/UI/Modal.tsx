import React, { useEffect } from 'react';
import { createPortal } from 'react-dom';
import styled from 'styled-components';
import { useTranslation } from 'react-i18next';
import Button from './Button';

const ModalOverlay = styled.div<{ isOpen: boolean }>`
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: ${({ theme }) => theme.zIndex.modal};
  opacity: ${({ isOpen }) => (isOpen ? 1 : 0)};
  visibility: ${({ isOpen }) => (isOpen ? 'visible' : 'hidden')};
  transition: all ${({ theme }) => theme.transitions.base};
  padding: ${({ theme }) => theme.spacing[4]};
`;

const ModalContainer = styled.div<{ 
  isOpen: boolean; 
  size?: 'sm' | 'md' | 'lg' | 'xl' | 'full';
}>`
  background: white;
  border-radius: ${({ theme }) => theme.borderRadius.lg};
  box-shadow: ${({ theme }) => theme.shadows.xl};
  transform: ${({ isOpen }) => (isOpen ? 'scale(1)' : 'scale(0.95)')};
  transition: all ${({ theme }) => theme.transitions.base};
  max-height: 90vh;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  
  ${({ size }) => {
    switch (size) {
      case 'sm':
        return 'width: 100%; max-width: 400px;';
      case 'lg':
        return 'width: 100%; max-width: 800px;';
      case 'xl':
        return 'width: 100%; max-width: 1200px;';
      case 'full':
        return 'width: 95vw; height: 95vh; max-width: none; max-height: none;';
      default:
        return 'width: 100%; max-width: 600px;';
    }
  }}
`;

const ModalHeader = styled.div`
  padding: ${({ theme }) => theme.spacing[6]};
  border-bottom: 1px solid ${({ theme }) => theme.colors.gray[200]};
  display: flex;
  align-items: center;
  justify-content: space-between;
  flex-shrink: 0;
`;

const ModalTitle = styled.h2`
  margin: 0;
  color: ${({ theme }) => theme.colors.gray[900]};
  font-size: ${({ theme }) => theme.fontSizes.xl};
  font-weight: ${({ theme }) => theme.fontWeights.semibold};
`;

const CloseButton = styled.button`
  background: none;
  border: none;
  color: ${({ theme }) => theme.colors.gray[400]};
  cursor: pointer;
  padding: ${({ theme }) => theme.spacing[2]};
  border-radius: ${({ theme }) => theme.borderRadius.base};
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all ${({ theme }) => theme.transitions.fast};
  
  &:hover {
    background: ${({ theme }) => theme.colors.gray[100]};
    color: ${({ theme }) => theme.colors.gray[600]};
  }
  
  &:focus {
    outline: none;
    box-shadow: 0 0 0 3px ${({ theme }) => theme.colors.primary[100]};
  }
`;

const ModalBody = styled.div<{ padding?: boolean }>`
  flex: 1;
  overflow-y: auto;
  ${({ padding, theme }) => padding && `padding: ${theme.spacing[6]};`}
`;

const ModalFooter = styled.div`
  padding: ${({ theme }) => theme.spacing[6]};
  border-top: 1px solid ${({ theme }) => theme.colors.gray[200]};
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: ${({ theme }) => theme.spacing[3]};
  flex-shrink: 0;
`;

interface ModalProps {
  isOpen: boolean;
  onClose: () => void;
  title?: string;
  size?: 'sm' | 'md' | 'lg' | 'xl' | 'full';
  children: React.ReactNode;
  showCloseButton?: boolean;
  closeOnOverlayClick?: boolean;
  closeOnEscape?: boolean;
}

interface ModalHeaderProps {
  children: React.ReactNode;
}

interface ModalBodyProps {
  children: React.ReactNode;
  padding?: boolean;
}

interface ModalFooterProps {
  children: React.ReactNode;
}

const Modal: React.FC<ModalProps> & {
  Header: React.FC<ModalHeaderProps>;
  Body: React.FC<ModalBodyProps>;
  Footer: React.FC<ModalFooterProps>;
} = ({
  isOpen,
  onClose,
  title,
  size = 'md',
  children,
  showCloseButton = true,
  closeOnOverlayClick = true,
  closeOnEscape = true,
}) => {
  const { t } = useTranslation();

  // Handle escape key
  useEffect(() => {
    if (!closeOnEscape) return;

    const handleEscape = (event: KeyboardEvent) => {
      if (event.key === 'Escape' && isOpen) {
        onClose();
      }
    };

    document.addEventListener('keydown', handleEscape);
    return () => document.removeEventListener('keydown', handleEscape);
  }, [isOpen, onClose, closeOnEscape]);

  // Prevent body scroll when modal is open
  useEffect(() => {
    if (isOpen) {
      document.body.style.overflow = 'hidden';
    } else {
      document.body.style.overflow = 'unset';
    }

    return () => {
      document.body.style.overflow = 'unset';
    };
  }, [isOpen]);

  const handleOverlayClick = (event: React.MouseEvent) => {
    if (closeOnOverlayClick && event.target === event.currentTarget) {
      onClose();
    }
  };

  if (!isOpen) return null;

  const modalContent = (
    <ModalOverlay isOpen={isOpen} onClick={handleOverlayClick}>
      <ModalContainer isOpen={isOpen} size={size}>
        {title && (
          <ModalHeader>
            <ModalTitle>{title}</ModalTitle>
            {showCloseButton && (
              <CloseButton onClick={onClose} aria-label={t('common.close')}>
                ✕
              </CloseButton>
            )}
          </ModalHeader>
        )}
        {children}
      </ModalContainer>
    </ModalOverlay>
  );

  return createPortal(modalContent, document.body);
};

Modal.Header = ({ children }) => (
  <ModalHeader>{children}</ModalHeader>
);

Modal.Body = ({ children, padding = true }) => (
  <ModalBody padding={padding}>{children}</ModalBody>
);

Modal.Footer = ({ children }) => (
  <ModalFooter>{children}</ModalFooter>
);

export default Modal;