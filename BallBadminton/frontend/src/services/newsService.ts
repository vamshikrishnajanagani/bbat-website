import { api, ApiResponse, PaginatedResponse } from './api';
import { NewsArticle, Announcement } from '../store/slices/newsSlice';

export interface NewsFilters {
  category?: string;
  isPublished?: boolean;
  isFeatured?: boolean;
  search?: string;
  startDate?: string;
  endDate?: string;
  page?: number;
  size?: number;
}

export interface CreateArticleRequest {
  title: string;
  content: string;
  excerpt: string;
  category: string;
  tags: string[];
  featuredImage?: string;
  isPublished?: boolean;
  isFeatured?: boolean;
}

export interface UpdateArticleRequest extends Partial<CreateArticleRequest> {
  id: string;
}

export interface CreateAnnouncementRequest {
  title: string;
  content: string;
  type: 'info' | 'warning' | 'success' | 'error';
  expiresAt?: string;
}

export interface UpdateAnnouncementRequest extends Partial<CreateAnnouncementRequest> {
  id: string;
}

export const newsService = {
  getArticles: async (filters?: NewsFilters): Promise<PaginatedResponse<NewsArticle>> => {
    const response = await api.get<PaginatedResponse<NewsArticle>>('/news', { params: filters });
    return response.data.data;
  },

  getArticleById: async (id: string): Promise<NewsArticle> => {
    const response = await api.get<NewsArticle>(`/news/${id}`);
    return response.data.data;
  },

  getFeaturedArticles: async (): Promise<NewsArticle[]> => {
    const response = await api.get<NewsArticle[]>('/news/featured');
    return response.data.data;
  },

  getLatestArticles: async (limit: number = 5): Promise<NewsArticle[]> => {
    const response = await api.get<NewsArticle[]>('/news/latest', { params: { limit } });
    return response.data.data;
  },

  createArticle: async (articleData: CreateArticleRequest): Promise<NewsArticle> => {
    const response = await api.post<NewsArticle>('/news', articleData);
    return response.data.data;
  },

  updateArticle: async (articleData: UpdateArticleRequest): Promise<NewsArticle> => {
    const { id, ...data } = articleData;
    const response = await api.put<NewsArticle>(`/news/${id}`, data);
    return response.data.data;
  },

  deleteArticle: async (id: string): Promise<void> => {
    await api.delete(`/news/${id}`);
  },

  publishArticle: async (id: string): Promise<NewsArticle> => {
    const response = await api.patch<NewsArticle>(`/news/${id}/publish`);
    return response.data.data;
  },

  unpublishArticle: async (id: string): Promise<NewsArticle> => {
    const response = await api.patch<NewsArticle>(`/news/${id}/unpublish`);
    return response.data.data;
  },

  uploadArticleImage: async (id: string, file: File): Promise<string> => {
    const formData = new FormData();
    formData.append('image', file);
    
    const response = await api.post<{ imageUrl: string }>(`/news/${id}/image`, formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    });
    
    return response.data.data.imageUrl;
  },

  getAnnouncements: async (): Promise<Announcement[]> => {
    const response = await api.get<Announcement[]>('/announcements');
    return response.data.data;
  },

  getActiveAnnouncements: async (): Promise<Announcement[]> => {
    const response = await api.get<Announcement[]>('/announcements/active');
    return response.data.data;
  },

  createAnnouncement: async (announcementData: CreateAnnouncementRequest): Promise<Announcement> => {
    const response = await api.post<Announcement>('/announcements', announcementData);
    return response.data.data;
  },

  updateAnnouncement: async (announcementData: UpdateAnnouncementRequest): Promise<Announcement> => {
    const { id, ...data } = announcementData;
    const response = await api.put<Announcement>(`/announcements/${id}`, data);
    return response.data.data;
  },

  deleteAnnouncement: async (id: string): Promise<void> => {
    await api.delete(`/announcements/${id}`);
  },

  searchArticles: async (query: string): Promise<NewsArticle[]> => {
    const response = await api.get<NewsArticle[]>('/news/search', { params: { q: query } });
    return response.data.data;
  },

  getArticleCategories: async (): Promise<string[]> => {
    const response = await api.get<string[]>('/news/categories');
    return response.data.data;
  },
};