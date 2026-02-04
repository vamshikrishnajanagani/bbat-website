import { api, ApiResponse } from './api';
import { District } from '../store/slices/districtsSlice';

export interface DistrictStatistics {
  playerCount: number;
  activeClubs: number;
  tournamentsHosted: number;
  upcomingTournaments: number;
  topPlayers: Array<{
    id: string;
    name: string;
    ranking: number;
  }>;
}

export interface CreateDistrictRequest {
  name: string;
  code: string;
  headquarters: string;
  area: number;
  population: number;
  coordinates: {
    latitude: number;
    longitude: number;
  };
  contactPerson?: string;
  contactEmail?: string;
  contactPhone?: string;
}

export interface UpdateDistrictRequest extends Partial<CreateDistrictRequest> {
  id: string;
}

export const districtsService = {
  getDistricts: async (): Promise<District[]> => {
    const response = await api.get<District[]>('/districts');
    return response.data.data;
  },

  getDistrictById: async (id: string): Promise<District> => {
    const response = await api.get<District>(`/districts/${id}`);
    return response.data.data;
  },

  getDistrictByCode: async (code: string): Promise<District> => {
    const response = await api.get<District>(`/districts/code/${code}`);
    return response.data.data;
  },

  createDistrict: async (districtData: CreateDistrictRequest): Promise<District> => {
    const response = await api.post<District>('/districts', districtData);
    return response.data.data;
  },

  updateDistrict: async (districtData: UpdateDistrictRequest): Promise<District> => {
    const { id, ...data } = districtData;
    const response = await api.put<District>(`/districts/${id}`, data);
    return response.data.data;
  },

  deleteDistrict: async (id: string): Promise<void> => {
    await api.delete(`/districts/${id}`);
  },

  getDistrictStatistics: async (id: string): Promise<DistrictStatistics> => {
    const response = await api.get<DistrictStatistics>(`/districts/${id}/statistics`);
    return response.data.data;
  },

  getDistrictPlayers: async (id: string): Promise<any[]> => {
    const response = await api.get<any[]>(`/districts/${id}/players`);
    return response.data.data;
  },

  getDistrictTournaments: async (id: string): Promise<any[]> => {
    const response = await api.get<any[]>(`/districts/${id}/tournaments`);
    return response.data.data;
  },

  searchDistricts: async (query: string): Promise<District[]> => {
    const response = await api.get<District[]>('/districts/search', { params: { q: query } });
    return response.data.data;
  },
};