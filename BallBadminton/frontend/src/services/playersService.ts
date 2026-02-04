import { api, ApiResponse, PaginatedResponse } from './api';
import { Player, Achievement, PlayerStatistics } from '../store/slices/playersSlice';

export interface PlayerFilters {
  district?: string;
  category?: 'MEN' | 'WOMEN' | 'JUNIOR' | 'SENIOR';
  gender?: 'MALE' | 'FEMALE';
  isProminent?: boolean;
  isActive?: boolean;
  search?: string;
  page?: number;
  size?: number;
}

export interface CreatePlayerRequest {
  name: string;
  dateOfBirth?: string;
  gender?: 'MALE' | 'FEMALE';
  category?: 'MEN' | 'WOMEN' | 'JUNIOR' | 'SENIOR';
  profilePhotoUrl?: string;
  contactEmail?: string;
  contactPhone?: string;
  address?: string;
  districtId?: string;
  isProminent?: boolean;
  isActive?: boolean;
}

export interface UpdatePlayerRequest extends Partial<CreatePlayerRequest> {
  id: string;
}

export const playersService = {
  getPlayers: async (filters?: PlayerFilters): Promise<PaginatedResponse<Player>> => {
    const response = await api.get<PaginatedResponse<Player>>('/players', { params: filters });
    return response.data.data;
  },

  getPlayerById: async (id: string): Promise<Player> => {
    const response = await api.get<Player>(`/players/${id}`);
    return response.data.data;
  },

  getProminentPlayers: async (): Promise<Player[]> => {
    const response = await api.get<Player[]>('/players/prominent');
    return response.data.data;
  },

  createPlayer: async (playerData: CreatePlayerRequest): Promise<Player> => {
    const response = await api.post<Player>('/players', playerData);
    return response.data.data;
  },

  updatePlayer: async (playerData: UpdatePlayerRequest): Promise<Player> => {
    const { id, ...data } = playerData;
    const response = await api.put<Player>(`/players/${id}`, data);
    return response.data.data;
  },

  deletePlayer: async (id: string): Promise<void> => {
    await api.delete(`/players/${id}`);
  },

  uploadPlayerPhoto: async (id: string, file: File): Promise<string> => {
    const formData = new FormData();
    formData.append('photo', file);
    
    const response = await api.post<{ photoUrl: string }>(`/players/${id}/photo`, formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    });
    
    return response.data.data.photoUrl;
  },

  getPlayerAchievements: async (id: string): Promise<Achievement[]> => {
    const response = await api.get<Achievement[]>(`/players/${id}/achievements`);
    return response.data.data;
  },

  addPlayerAchievement: async (playerId: string, achievement: Omit<Achievement, 'id'>): Promise<Achievement> => {
    const response = await api.post<Achievement>(`/players/${playerId}/achievements`, achievement);
    return response.data.data;
  },

  updatePlayerAchievement: async (playerId: string, achievementId: string, achievement: Partial<Achievement>): Promise<Achievement> => {
    const response = await api.put<Achievement>(`/players/${playerId}/achievements/${achievementId}`, achievement);
    return response.data.data;
  },

  deletePlayerAchievement: async (playerId: string, achievementId: string): Promise<void> => {
    await api.delete(`/players/${playerId}/achievements/${achievementId}`);
  },

  getPlayerStatistics: async (id: string): Promise<PlayerStatistics> => {
    const response = await api.get<PlayerStatistics>(`/players/${id}/statistics`);
    return response.data.data;
  },

  updatePlayerStatistics: async (id: string, statistics: Partial<PlayerStatistics>): Promise<PlayerStatistics> => {
    const response = await api.put<PlayerStatistics>(`/players/${id}/statistics`, statistics);
    return response.data.data;
  },

  getPlayerRankings: async (category?: string): Promise<Player[]> => {
    const response = await api.get<Player[]>('/players/rankings', { params: { category } });
    return response.data.data;
  },

  searchPlayers: async (query: string): Promise<Player[]> => {
    const response = await api.get<Player[]>('/players/search', { params: { q: query } });
    return response.data.data;
  },
};