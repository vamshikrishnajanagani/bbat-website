import React, { ReactElement } from 'react';
import { render, RenderOptions } from '@testing-library/react';
import { BrowserRouter } from 'react-router-dom';

// Simple test utilities

interface CustomRenderOptions extends Omit<RenderOptions, 'wrapper'> {
  // Add any custom options here
}

export const renderWithRouter = (
  ui: ReactElement,
  options: CustomRenderOptions = {}
) => {
  const RouterWrapper: React.FC<{ children: React.ReactNode }> = ({
    children,
  }) => <BrowserRouter>{children}</BrowserRouter>;

  return render(ui, { wrapper: RouterWrapper, ...options });
};

// Re-export everything from testing library
export * from '@testing-library/react';
export { default as userEvent } from '@testing-library/user-event';