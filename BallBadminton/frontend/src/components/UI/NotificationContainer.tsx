import React, { useEffect } from 'react';
import styled from 'styled-components';
import { useAppSelector, useAppDispatch } from '../../store';
import { removeNotification } from '../../store/slices/uiSlice';

const Container = styled.div`
  position: fixed;
  top: 20px;
  right: 20px;
  z-index: ${({ theme }) => theme.zIndex.modal};
  max-width: 400px;
`;

const Notification = styled.div<{ type: string }>`
  background: ${({ theme, type }) => {
    switch (type) {
      case 'success': return theme.colors.success[600];
      case 'error': return theme.colors.error[600];
      case 'warning': return theme.colors.warning[600];
      case 'info': return theme.colors.info[600];
      default: return theme.colors.gray[600];
    }
  }};
  color: white;
  padding: ${({ theme }) => theme.spacing[4]};
  margin-bottom: ${({ theme }) => theme.spacing[2]};
  border-radius: ${({ theme }) => theme.borderRadius.md};
  box-shadow: ${({ theme }) => theme.shadows.lg};
  display: flex;
  justify-content: space-between;
  align-items: center;
  animation: slideIn 0.3s ease-out;
  
  @keyframes slideIn {
    from {
      transform: translateX(100%);
      opacity: 0;
    }
    to {
      transform: translateX(0);
      opacity: 1;
    }
  }
`;

const CloseButton = styled.button`
  background: none;
  border: none;
  color: white;
  cursor: pointer;
  font-size: 18px;
  margin-left: ${({ theme }) => theme.spacing[2]};
  
  &:hover {
    opacity: 0.8;
  }
`;

const NotificationContainer: React.FC = () => {
  const dispatch = useAppDispatch();
  const notifications = useAppSelector(state => state.ui.notifications);

  useEffect(() => {
    notifications.forEach(notification => {
      const timer = setTimeout(() => {
        dispatch(removeNotification(notification.id));
      }, 5000);

      return () => clearTimeout(timer);
    });
  }, [notifications, dispatch]);

  return (
    <Container>
      {notifications.map(notification => (
        <Notification key={notification.id} type={notification.type}>
          <span>{notification.message}</span>
          <CloseButton
            onClick={() => dispatch(removeNotification(notification.id))}
            aria-label="Close notification"
          >
            ×
          </CloseButton>
        </Notification>
      ))}
    </Container>
  );
};

export default NotificationContainer;