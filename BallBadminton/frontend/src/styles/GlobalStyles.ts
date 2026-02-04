import { createGlobalStyle } from 'styled-components';

export const GlobalStyles = createGlobalStyle`
  /* Reset and base styles */
  *, *::before, *::after {
    box-sizing: border-box;
    margin: 0;
    padding: 0;
  }

  html {
    font-size: 16px;
    line-height: 1.5;
    -webkit-text-size-adjust: 100%;
    -webkit-font-smoothing: antialiased;
    -moz-osx-font-smoothing: grayscale;
    scroll-behavior: smooth;
  }

  body {
    font-family: ${({ theme }) => theme.fonts.primary};
    font-size: ${({ theme }) => theme.fontSizes.base};
    font-weight: ${({ theme }) => theme.fontWeights.normal};
    line-height: ${({ theme }) => theme.lineHeights.normal};
    color: ${({ theme }) => theme.colors.gray[800]};
    background-color: ${({ theme }) => theme.colors.gray[50]};
    min-height: 100vh;
  }

  /* Typography */
  h1, h2, h3, h4, h5, h6 {
    font-family: ${({ theme }) => theme.fonts.secondary};
    font-weight: ${({ theme }) => theme.fontWeights.semibold};
    line-height: ${({ theme }) => theme.lineHeights.tight};
    color: ${({ theme }) => theme.colors.gray[900]};
    margin-bottom: ${({ theme }) => theme.spacing[4]};
  }

  h1 {
    font-size: ${({ theme }) => theme.fontSizes['4xl']};
    font-weight: ${({ theme }) => theme.fontWeights.bold};
  }

  h2 {
    font-size: ${({ theme }) => theme.fontSizes['3xl']};
  }

  h3 {
    font-size: ${({ theme }) => theme.fontSizes['2xl']};
  }

  h4 {
    font-size: ${({ theme }) => theme.fontSizes.xl};
  }

  h5 {
    font-size: ${({ theme }) => theme.fontSizes.lg};
  }

  h6 {
    font-size: ${({ theme }) => theme.fontSizes.base};
  }

  p {
    margin-bottom: ${({ theme }) => theme.spacing[4]};
    line-height: ${({ theme }) => theme.lineHeights.relaxed};
  }

  /* Links */
  a {
    color: ${({ theme }) => theme.colors.primary[600]};
    text-decoration: none;
    transition: color ${({ theme }) => theme.transitions.fast};

    &:hover {
      color: ${({ theme }) => theme.colors.primary[700]};
      text-decoration: underline;
    }

    &:focus {
      outline: 2px solid ${({ theme }) => theme.colors.primary[500]};
      outline-offset: 2px;
    }

    &:focus:not(:focus-visible) {
      outline: none;
    }

    &:focus-visible {
      outline: 2px solid ${({ theme }) => theme.colors.primary[500]};
      outline-offset: 2px;
    }
  }

  /* Lists */
  ul, ol {
    margin-bottom: ${({ theme }) => theme.spacing[4]};
    padding-left: ${({ theme }) => theme.spacing[6]};
  }

  li {
    margin-bottom: ${({ theme }) => theme.spacing[2]};
  }

  /* Images */
  img {
    max-width: 100%;
    height: auto;
    display: block;
  }

  /* Forms */
  input, textarea, select, button {
    font-family: inherit;
    font-size: inherit;
  }

  button {
    cursor: pointer;
    border: none;
    background: none;
    padding: 0;
  }

  /* Focus styles for accessibility */
  *:focus {
    outline: 2px solid ${({ theme }) => theme.colors.primary[500]};
    outline-offset: 2px;
  }

  *:focus:not(:focus-visible) {
    outline: none;
  }

  *:focus-visible {
    outline: 2px solid ${({ theme }) => theme.colors.primary[500]};
    outline-offset: 2px;
  }

  /* High contrast mode support */
  @media (prefers-contrast: high) {
    * {
      border-color: currentColor !important;
    }
  }

  /* Reduced motion support */
  @media (prefers-reduced-motion: reduce) {
    *,
    *::before,
    *::after {
      animation-duration: 0.01ms !important;
      animation-iteration-count: 1 !important;
      transition-duration: 0.01ms !important;
      scroll-behavior: auto !important;
    }
  }

  /* Utility classes */
  .sr-only {
    position: absolute;
    width: 1px;
    height: 1px;
    padding: 0;
    margin: -1px;
    overflow: hidden;
    clip: rect(0, 0, 0, 0);
    white-space: nowrap;
    border: 0;
  }

  .sr-only-focusable:focus {
    position: static;
    width: auto;
    height: auto;
    overflow: visible;
    clip: auto;
    white-space: normal;
  }

  .container {
    width: 100%;
    max-width: 1200px;
    margin: 0 auto;
    padding: 0 ${({ theme }) => theme.spacing[4]};

    @media (min-width: ${({ theme }) => theme.breakpoints.sm}) {
      padding: 0 ${({ theme }) => theme.spacing[6]};
    }

    @media (min-width: ${({ theme }) => theme.breakpoints.lg}) {
      padding: 0 ${({ theme }) => theme.spacing[8]};
    }
  }

  /* Skip link styles */
  .skip-link {
    position: absolute;
    left: -9999px;
    z-index: 999;
    padding: 1em;
    background-color: ${({ theme }) => theme.colors.primary[600]};
    color: white;
    text-decoration: none;
    font-weight: ${({ theme }) => theme.fontWeights.semibold};
    border-radius: ${({ theme }) => theme.borderRadius.base};
    
    &:focus {
      left: 1em;
      top: 1em;
      outline: 2px solid white;
      outline-offset: 2px;
    }
  }

  /* Scrollbar styles */
  ::-webkit-scrollbar {
    width: 8px;
    height: 8px;
  }

  ::-webkit-scrollbar-track {
    background: ${({ theme }) => theme.colors.gray[100]};
  }

  ::-webkit-scrollbar-thumb {
    background: ${({ theme }) => theme.colors.gray[400]};
    border-radius: ${({ theme }) => theme.borderRadius.base};
  }

  ::-webkit-scrollbar-thumb:hover {
    background: ${({ theme }) => theme.colors.gray[500]};
  }

  /* Print styles */
  @media print {
    * {
      background: transparent !important;
      color: black !important;
      box-shadow: none !important;
      text-shadow: none !important;
    }

    a, a:visited {
      text-decoration: underline;
    }

    a[href]:after {
      content: " (" attr(href) ")";
    }

    abbr[title]:after {
      content: " (" attr(title) ")";
    }

    .no-print {
      display: none !important;
    }

    header, footer, nav, aside {
      display: none !important;
    }
  }

  /* Animation utilities */
  .fade-in {
    animation: fadeIn 0.3s ease-in-out;
  }

  .slide-in-up {
    animation: slideInUp 0.3s ease-out;
  }

  @keyframes fadeIn {
    from {
      opacity: 0;
    }
    to {
      opacity: 1;
    }
  }

  @keyframes slideInUp {
    from {
      transform: translateY(20px);
      opacity: 0;
    }
    to {
      transform: translateY(0);
      opacity: 1;
    }
  }

  /* Loading spinner */
  .spinner {
    display: inline-block;
    width: 20px;
    height: 20px;
    border: 3px solid ${({ theme }) => theme.colors.gray[200]};
    border-radius: 50%;
    border-top-color: ${({ theme }) => theme.colors.primary[600]};
    animation: spin 1s ease-in-out infinite;
  }

  @keyframes spin {
    to {
      transform: rotate(360deg);
    }
  }

  /* ARIA live region styles */
  [aria-live] {
    position: relative;
  }

  /* Ensure proper text spacing for readability (WCAG 2.1) */
  p, li, td, th {
    line-height: 1.5;
  }

  /* Minimum touch target size (44x44px for WCAG 2.1) */
  button, a, input[type="button"], input[type="submit"] {
    min-height: 44px;
    min-width: 44px;
  }
`;