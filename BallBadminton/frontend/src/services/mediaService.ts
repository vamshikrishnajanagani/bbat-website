import { api, PaginatedResponse } from './api';

export interface MediaGallery {
  id: string;
  title: string;
  description: string;
  coverImage?: string;
  category: string;
  eventDate?: string;
  itemCount: number;
  createdAt: string;
  updatedAt: string;
}

export interface MediaItem {
  id: string;
  galleryId: string;
  title: string;
  description?: string;
  url: string;
  thumbnailUrl?: string;
  type: 'image' | 'video';
  tags: string[];
  uploadedAt: string;
}

export interface DownloadResource {
  id: string;
  title: string;
  description: string;
  category: string;
  fileUrl: string;
  fileName: string;
  fileSize: number;
  fileType: string;
  downloadCount: number;
  uploadedAt: string;
}

export interface MediaFilters {
  category?: string;
  type?: 'image' | 'video';
  search?: string;
  page?: number;
  size?: number;
}

export interface DownloadFilters {
  category?: string;
  search?: string;
  page?: number;
  size?: number;
}

export const mediaService = {
  // Gallery operations
  getGalleries: async (filters?: MediaFilters): Promise<PaginatedResponse<MediaGallery>> => {
    const response = await api.get<PaginatedResponse<MediaGallery>>('/media/galleries', { params: filters });
    return response.data.data;
  },

  getGalleryById: async (id: string): Promise<MediaGallery> => {
    const response = await api.get<MediaGallery>(`/media/galleries/${id}`);
    return response.data.data;
  },

  createGallery: async (galleryData: Partial<MediaGallery>): Promise<MediaGallery> => {
    const response = await api.post<MediaGallery>('/media/galleries', galleryData);
    return response.data.data;
  },

  updateGallery: async (id: string, galleryData: Partial<MediaGallery>): Promise<MediaGallery> => {
    const response = await api.put<MediaGallery>(`/media/galleries/${id}`, galleryData);
    return response.data.data;
  },

  deleteGallery: async (id: string): Promise<void> => {
    await api.delete(`/media/galleries/${id}`);
  },

  // Media item operations
  getMediaItems: async (galleryId: string, filters?: MediaFilters): Promise<PaginatedResponse<MediaItem>> => {
    const response = await api.get<PaginatedResponse<MediaItem>>(`/media/galleries/${galleryId}/items`, { params: filters });
    return response.data.data;
  },

  getMediaItemById: async (galleryId: string, itemId: string): Promise<MediaItem> => {
    const response = await api.get<MediaItem>(`/media/galleries/${galleryId}/items/${itemId}`);
    return response.data.data;
  },

  uploadMediaItem: async (galleryId: string, file: File, metadata: Partial<MediaItem>): Promise<MediaItem> => {
    const formData = new FormData();
    formData.append('file', file);
    formData.append('metadata', JSON.stringify(metadata));

    const response = await api.post<MediaItem>(`/media/galleries/${galleryId}/items`, formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    });

    return response.data.data;
  },

  updateMediaItem: async (galleryId: string, itemId: string, itemData: Partial<MediaItem>): Promise<MediaItem> => {
    const response = await api.put<MediaItem>(`/media/galleries/${galleryId}/items/${itemId}`, itemData);
    return response.data.data;
  },

  deleteMediaItem: async (galleryId: string, itemId: string): Promise<void> => {
    await api.delete(`/media/galleries/${galleryId}/items/${itemId}`);
  },

  // Download resources
  getDownloads: async (filters?: DownloadFilters): Promise<PaginatedResponse<DownloadResource>> => {
    const response = await api.get<PaginatedResponse<DownloadResource>>('/downloads', { params: filters });
    return response.data.data;
  },

  getDownloadById: async (id: string): Promise<DownloadResource> => {
    const response = await api.get<DownloadResource>(`/downloads/${id}`);
    return response.data.data;
  },

  createDownload: async (downloadData: Partial<DownloadResource>): Promise<DownloadResource> => {
    const response = await api.post<DownloadResource>('/downloads', downloadData);
    return response.data.data;
  },

  updateDownload: async (id: string, downloadData: Partial<DownloadResource>): Promise<DownloadResource> => {
    const response = await api.put<DownloadResource>(`/downloads/${id}`, downloadData);
    return response.data.data;
  },

  deleteDownload: async (id: string): Promise<void> => {
    await api.delete(`/downloads/${id}`);
  },

  uploadDownloadFile: async (file: File, metadata: Partial<DownloadResource>): Promise<DownloadResource> => {
    const formData = new FormData();
    formData.append('file', file);
    formData.append('metadata', JSON.stringify(metadata));

    const response = await api.post<DownloadResource>('/downloads/upload', formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    });

    return response.data.data;
  },

  trackDownload: async (id: string): Promise<void> => {
    await api.post(`/downloads/${id}/track`);
  },

  getCategories: async (): Promise<string[]> => {
    const response = await api.get<string[]>('/media/categories');
    return response.data.data;
  },

  getDownloadCategories: async (): Promise<string[]> => {
    const response = await api.get<string[]>('/downloads/categories');
    return response.data.data;
  },
};
