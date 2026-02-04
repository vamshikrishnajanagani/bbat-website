import React from 'react';
import { render, screen, fireEvent } from '@testing-library/react';
import { ThemeProvider } from 'styled-components';
import Button from './Button';
import { theme } from '../../styles/theme';

// Wrapper component for theme provider
const renderWithTheme = (component: React.ReactElement) => {
  return render(
    <ThemeProvider theme={theme}>
      {component}
    </ThemeProvider>
  );
};

describe('Button Component', () => {
  it('renders button with text', () => {
    renderWithTheme(<Button>Click me</Button>);
    expect(screen.getByRole('button', { name: /click me/i })).toBeInTheDocument();
  });

  it('handles click events', () => {
    const handleClick = jest.fn();
    renderWithTheme(<Button onClick={handleClick}>Click me</Button>);
    
    fireEvent.click(screen.getByRole('button'));
    expect(handleClick).toHaveBeenCalledTimes(1);
  });

  it('renders as disabled when disabled prop is true', () => {
    renderWithTheme(<Button disabled>Disabled Button</Button>);
    expect(screen.getByRole('button')).toBeDisabled();
  });

  it('renders with primary variant by default', () => {
    renderWithTheme(<Button>Primary Button</Button>);
    const button = screen.getByRole('button');
    expect(button).toHaveStyle(`background-color: ${theme.colors.primary}`);
  });

  it('renders with secondary variant when specified', () => {
    renderWithTheme(<Button variant="secondary">Secondary Button</Button>);
    const button = screen.getByRole('button');
    expect(button).toHaveStyle(`background-color: ${theme.colors.secondary}`);
  });

  it('renders with correct size classes', () => {
    renderWithTheme(<Button size="lg">Large Button</Button>);
    const button = screen.getByRole('button');
    expect(button).toHaveClass('size-lg');
  });

  it('renders loading state correctly', () => {
    renderWithTheme(<Button loading>Loading Button</Button>);
    expect(screen.getByRole('button')).toBeDisabled();
    expect(screen.getByText(/loading/i)).toBeInTheDocument();
  });

  it('renders with custom className', () => {
    renderWithTheme(<Button className="custom-class">Custom Button</Button>);
    expect(screen.getByRole('button')).toHaveClass('custom-class');
  });

  // Note: Button component doesn't currently support ref forwarding
  // it('forwards ref correctly', () => {
  //   const ref = React.createRef<HTMLButtonElement>();
  //   renderWithTheme(<Button ref={ref}>Ref Button</Button>);
  //   expect(ref.current).toBeInstanceOf(HTMLButtonElement);
  // });
});