import { createSlice, PayloadAction } from '@reduxjs/toolkit';

export interface NewsArticle {
  id: string;
  title: string;
  content: string;
  excerpt: string;
  author: string;
  publishedAt: string;
  updatedAt: string;
  category: string;
  tags: string[];
  featuredImage?: string;
  isPublished: boolean;
  isFeatured: boolean;
}

export interface Announcement {
  id: string;
  title: string;
  content: string;
  type: 'info' | 'warning' | 'success' | 'error';
  publishedAt: string;
  expiresAt?: string;
  isActive: boolean;
}

interface NewsState {
  articles: NewsArticle[];
  featuredArticles: NewsArticle[];
  announcements: Announcement[];
  selectedArticle: NewsArticle | null;
  isLoading: boolean;
  error: string | null;
  filters: {
    category: string;
    searchTerm: string;
    dateRange: {
      start: string;
      end: string;
    };
  };
}

const initialState: NewsState = {
  articles: [],
  featuredArticles: [],
  announcements: [],
  selectedArticle: null,
  isLoading: false,
  error: null,
  filters: {
    category: '',
    searchTerm: '',
    dateRange: {
      start: '',
      end: '',
    },
  },
};

const newsSlice = createSlice({
  name: 'news',
  initialState,
  reducers: {
    setLoading: (state, action: PayloadAction<boolean>) => {
      state.isLoading = action.payload;
    },
    setArticles: (state, action: PayloadAction<NewsArticle[]>) => {
      state.articles = action.payload;
      state.featuredArticles = action.payload.filter(a => a.isFeatured && a.isPublished);
      state.error = null;
    },
    setAnnouncements: (state, action: PayloadAction<Announcement[]>) => {
      state.announcements = action.payload.filter(a => a.isActive);
      state.error = null;
    },
    setSelectedArticle: (state, action: PayloadAction<NewsArticle | null>) => {
      state.selectedArticle = action.payload;
    },
    addArticle: (state, action: PayloadAction<NewsArticle>) => {
      state.articles.push(action.payload);
      if (action.payload.isFeatured && action.payload.isPublished) {
        state.featuredArticles.push(action.payload);
      }
    },
    updateArticle: (state, action: PayloadAction<NewsArticle>) => {
      const index = state.articles.findIndex(a => a.id === action.payload.id);
      if (index !== -1) {
        state.articles[index] = action.payload;
      }
      
      const featuredIndex = state.featuredArticles.findIndex(a => a.id === action.payload.id);
      if (action.payload.isFeatured && action.payload.isPublished && featuredIndex === -1) {
        state.featuredArticles.push(action.payload);
      } else if ((!action.payload.isFeatured || !action.payload.isPublished) && featuredIndex !== -1) {
        state.featuredArticles.splice(featuredIndex, 1);
      } else if (featuredIndex !== -1) {
        state.featuredArticles[featuredIndex] = action.payload;
      }
    },
    removeArticle: (state, action: PayloadAction<string>) => {
      state.articles = state.articles.filter(a => a.id !== action.payload);
      state.featuredArticles = state.featuredArticles.filter(a => a.id !== action.payload);
    },
    addAnnouncement: (state, action: PayloadAction<Announcement>) => {
      if (action.payload.isActive) {
        state.announcements.push(action.payload);
      }
    },
    removeAnnouncement: (state, action: PayloadAction<string>) => {
      state.announcements = state.announcements.filter(a => a.id !== action.payload);
    },
    setFilters: (state, action: PayloadAction<Partial<NewsState['filters']>>) => {
      state.filters = { ...state.filters, ...action.payload };
    },
    clearFilters: (state) => {
      state.filters = {
        category: '',
        searchTerm: '',
        dateRange: {
          start: '',
          end: '',
        },
      };
    },
    setError: (state, action: PayloadAction<string>) => {
      state.error = action.payload;
    },
    clearError: (state) => {
      state.error = null;
    },
  },
});

export const {
  setLoading,
  setArticles,
  setAnnouncements,
  setSelectedArticle,
  addArticle,
  updateArticle,
  removeArticle,
  addAnnouncement,
  removeAnnouncement,
  setFilters,
  clearFilters,
  setError,
  clearError,
} = newsSlice.actions;

export default newsSlice.reducer;