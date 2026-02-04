// Jest setup file for Telangana Ball Badminton Association Website Frontend
// This file is automatically loaded by Create React App before running tests

import '@testing-library/jest-dom';

// Mock IntersectionObserver for components that use it
global.IntersectionObserver = class IntersectionObserver {
  constructor() {}
  disconnect() {}
  observe() {}
  unobserve() {}
};

// Mock ResizeObserver for components that use it
global.ResizeObserver = class ResizeObserver {
  constructor() {}
  disconnect() {}
  observe() {}
  unobserve() {}
};

// Mock window.matchMedia for responsive components
Object.defineProperty(window, 'matchMedia', {
  writable: true,
  value: jest.fn().mockImplementation(query => ({
    matches: false,
    media: query,
    onchange: null,
    addListener: jest.fn(), // deprecated
    removeListener: jest.fn(), // deprecated
    addEventListener: jest.fn(),
    removeEventListener: jest.fn(),
    dispatchEvent: jest.fn(),
  })),
});

// Mock window.scrollTo for components that use scrolling
Object.defineProperty(window, 'scrollTo', {
  writable: true,
  value: jest.fn(),
});

// Mock localStorage
const localStorageMock = {
  getItem: jest.fn(),
  setItem: jest.fn(),
  removeItem: jest.fn(),
  clear: jest.fn(),
};
Object.defineProperty(window, 'localStorage', {
  value: localStorageMock,
});

// Mock sessionStorage
const sessionStorageMock = {
  getItem: jest.fn(),
  setItem: jest.fn(),
  removeItem: jest.fn(),
  clear: jest.fn(),
};
Object.defineProperty(window, 'sessionStorage', {
  value: sessionStorageMock,
});

// Mock fetch for API calls
global.fetch = jest.fn();

// Mock console methods to reduce noise in tests
const originalError = console.error;
beforeAll(() => {
  console.error = (...args: any[]) => {
    if (
      typeof args[0] === 'string' &&
      args[0].includes('Warning: ReactDOM.render is no longer supported')
    ) {
      return;
    }
    originalError.call(console, ...args);
  };
});

afterAll(() => {
  console.error = originalError;
});

// Global test utilities
export const mockApiResponse = (data: any, status = 200) => {
  return Promise.resolve({
    ok: status >= 200 && status < 300,
    status,
    json: () => Promise.resolve(data),
    text: () => Promise.resolve(JSON.stringify(data)),
  });
};

export const mockApiError = (message: string, status = 500) => {
  return Promise.reject({
    response: {
      status,
      data: { message },
    },
  });
};

// Test data factories
export const createMockMember = (overrides = {}) => ({
  id: '1',
  name: 'Test Member',
  position: 'Test Position',
  email: 'test@example.com',
  phone: '+91-9876543210',
  biography: 'Test biography',
  photoUrl: 'https://example.com/photo.jpg',
  hierarchyLevel: 1,
  tenureStartDate: '2023-01-01',
  tenureEndDate: null,
  isActive: true,
  isProminent: true,
  ...overrides,
});

export const createMockPlayer = (overrides = {}) => ({
  id: '1',
  name: 'Test Player',
  dateOfBirth: '1995-01-01',
  gender: 'Male',
  district: 'Hyderabad',
  category: 'Men',
  profilePhotoUrl: 'https://example.com/photo.jpg',
  contactEmail: 'player@example.com',
  contactPhone: '+91-9876543210',
  isProminent: true,
  isActive: true,
  statistics: {
    matchesPlayed: 10,
    matchesWon: 8,
    tournamentsParticipated: 5,
    tournamentsWon: 2,
    winPercentage: 80.0,
  },
  ...overrides,
});

export const createMockTournament = (overrides = {}) => ({
  id: '1',
  name: 'Test Tournament',
  description: 'Test tournament description',
  startDate: '2024-03-15',
  endDate: '2024-03-18',
  venue: 'Test Venue',
  district: 'Hyderabad',
  registrationStartDate: '2024-02-01',
  registrationEndDate: '2024-03-01',
  maxParticipants: 100,
  entryFee: 500.0,
  prizeMoneyy: 10000.0,
  status: 'Registration Open',
  tournamentType: 'Singles',
  ageCategory: 'All',
  genderCategory: 'Mixed',
  isFeatured: true,
  ...overrides,
});

export const createMockNewsArticle = (overrides = {}) => ({
  id: '1',
  title: 'Test News Article',
  slug: 'test-news-article',
  summary: 'Test article summary',
  content: 'Test article content',
  featuredImageUrl: 'https://example.com/image.jpg',
  category: 'General',
  author: 'Test Author',
  publishedAt: '2024-01-15T10:00:00Z',
  isPublished: true,
  isFeatured: false,
  language: 'en',
  viewCount: 0,
  ...overrides,
});