/**
 * Accessibility Utilities
 * Helper functions for WCAG 2.1 compliance
 */

/**
 * Trap focus within a modal or dialog
 */
export const trapFocus = (element: HTMLElement) => {
  const focusableElements = element.querySelectorAll<HTMLElement>(
    'a[href], button:not([disabled]), textarea:not([disabled]), input:not([disabled]), select:not([disabled]), [tabindex]:not([tabindex="-1"])'
  );
  
  const firstFocusable = focusableElements[0];
  const lastFocusable = focusableElements[focusableElements.length - 1];

  const handleTabKey = (e: KeyboardEvent) => {
    if (e.key !== 'Tab') return;

    if (e.shiftKey) {
      if (document.activeElement === firstFocusable) {
        lastFocusable.focus();
        e.preventDefault();
      }
    } else {
      if (document.activeElement === lastFocusable) {
        firstFocusable.focus();
        e.preventDefault();
      }
    }
  };

  element.addEventListener('keydown', handleTabKey);

  return () => {
    element.removeEventListener('keydown', handleTabKey);
  };
};

/**
 * Announce message to screen readers
 */
export const announceToScreenReader = (message: string, priority: 'polite' | 'assertive' = 'polite') => {
  const announcement = document.createElement('div');
  announcement.setAttribute('role', 'status');
  announcement.setAttribute('aria-live', priority);
  announcement.setAttribute('aria-atomic', 'true');
  announcement.className = 'sr-only';
  announcement.textContent = message;
  
  document.body.appendChild(announcement);
  
  setTimeout(() => {
    document.body.removeChild(announcement);
  }, 1000);
};

/**
 * Check if element is visible to screen readers
 */
export const isVisibleToScreenReader = (element: HTMLElement): boolean => {
  return (
    element.getAttribute('aria-hidden') !== 'true' &&
    !element.hasAttribute('hidden') &&
    element.style.display !== 'none' &&
    element.style.visibility !== 'hidden'
  );
};

/**
 * Get accessible label for an element
 */
export const getAccessibleLabel = (element: HTMLElement): string => {
  return (
    element.getAttribute('aria-label') ||
    element.getAttribute('aria-labelledby') ||
    element.textContent ||
    ''
  ).trim();
};

/**
 * Check color contrast ratio (WCAG AA requires 4.5:1 for normal text)
 */
export const getContrastRatio = (foreground: string, background: string): number => {
  const getLuminance = (color: string): number => {
    const rgb = color.match(/\d+/g)?.map(Number) || [0, 0, 0];
    const [r, g, b] = rgb.map(val => {
      const sRGB = val / 255;
      return sRGB <= 0.03928 ? sRGB / 12.92 : Math.pow((sRGB + 0.055) / 1.055, 2.4);
    });
    return 0.2126 * r + 0.7152 * g + 0.0722 * b;
  };

  const l1 = getLuminance(foreground);
  const l2 = getLuminance(background);
  const lighter = Math.max(l1, l2);
  const darker = Math.min(l1, l2);
  
  return (lighter + 0.05) / (darker + 0.05);
};

/**
 * Manage focus for route changes
 */
export const manageFocusOnRouteChange = () => {
  const mainContent = document.getElementById('main-content');
  if (mainContent) {
    mainContent.setAttribute('tabindex', '-1');
    mainContent.focus();
    mainContent.removeAttribute('tabindex');
  }
};

/**
 * Add keyboard navigation support
 */
export const addKeyboardNavigation = (
  element: HTMLElement,
  onEnter?: () => void,
  onEscape?: () => void
) => {
  const handleKeyDown = (e: KeyboardEvent) => {
    if (e.key === 'Enter' && onEnter) {
      e.preventDefault();
      onEnter();
    } else if (e.key === 'Escape' && onEscape) {
      e.preventDefault();
      onEscape();
    }
  };

  element.addEventListener('keydown', handleKeyDown);

  return () => {
    element.removeEventListener('keydown', handleKeyDown);
  };
};

/**
 * Create visually hidden but screen reader accessible text
 */
export const createScreenReaderOnlyStyle = (): React.CSSProperties => ({
  position: 'absolute',
  width: '1px',
  height: '1px',
  padding: '0',
  margin: '-1px',
  overflow: 'hidden',
  clip: 'rect(0, 0, 0, 0)',
  whiteSpace: 'nowrap',
  border: '0'
});

/**
 * Validate ARIA attributes
 */
export const validateAriaAttributes = (element: HTMLElement): string[] => {
  const errors: string[] = [];
  
  // Check for aria-labelledby pointing to non-existent element
  const labelledBy = element.getAttribute('aria-labelledby');
  if (labelledBy && !document.getElementById(labelledBy)) {
    errors.push(`aria-labelledby references non-existent element: ${labelledBy}`);
  }
  
  // Check for aria-describedby pointing to non-existent element
  const describedBy = element.getAttribute('aria-describedby');
  if (describedBy && !document.getElementById(describedBy)) {
    errors.push(`aria-describedby references non-existent element: ${describedBy}`);
  }
  
  // Check for required aria-label or aria-labelledby on certain roles
  const role = element.getAttribute('role');
  const requiresLabel = ['button', 'link', 'checkbox', 'radio', 'textbox'];
  if (role && requiresLabel.includes(role)) {
    const hasLabel = element.getAttribute('aria-label') || 
                     element.getAttribute('aria-labelledby') ||
                     element.textContent?.trim();
    if (!hasLabel) {
      errors.push(`Element with role="${role}" requires accessible label`);
    }
  }
  
  return errors;
};
