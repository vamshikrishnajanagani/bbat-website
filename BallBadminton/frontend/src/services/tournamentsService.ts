import { api, ApiResponse, PaginatedResponse } from './api';
import { Tournament, TournamentCategory } from '../store/slices/tournamentsSlice';

export interface TournamentFilters {
  status?: 'Upcoming' | 'Ongoing' | 'Completed' | 'Cancelled';
  district?: string;
  startDate?: string;
  endDate?: string;
  search?: string;
  page?: number;
  size?: number;
}

export interface CreateTournamentRequest {
  name: string;
  description: string;
  startDate: string;
  endDate: string;
  venue: string;
  district: string;
  categories: Omit<TournamentCategory, 'id'>[];
  registrationDeadline: string;
  maxParticipants: number;
  entryFee: number;
}

export interface UpdateTournamentRequest extends Partial<CreateTournamentRequest> {
  id: string;
}

export interface TournamentRegistration {
  tournamentId: string;
  categoryId: string;
  playerName: string;
  playerEmail: string;
  playerPhone: string;
  district: string;
  emergencyContact: string;
}

export const tournamentsService = {
  getTournaments: async (filters?: TournamentFilters): Promise<PaginatedResponse<Tournament>> => {
    const response = await api.get<PaginatedResponse<Tournament>>('/tournaments', { params: filters });
    return response.data.data;
  },

  getTournamentById: async (id: string): Promise<Tournament> => {
    const response = await api.get<Tournament>(`/tournaments/${id}`);
    return response.data.data;
  },

  getUpcomingTournaments: async (): Promise<Tournament[]> => {
    const response = await api.get<Tournament[]>('/tournaments/upcoming');
    return response.data.data;
  },

  createTournament: async (tournamentData: CreateTournamentRequest): Promise<Tournament> => {
    const response = await api.post<Tournament>('/tournaments', tournamentData);
    return response.data.data;
  },

  updateTournament: async (tournamentData: UpdateTournamentRequest): Promise<Tournament> => {
    const { id, ...data } = tournamentData;
    const response = await api.put<Tournament>(`/tournaments/${id}`, data);
    return response.data.data;
  },

  deleteTournament: async (id: string): Promise<void> => {
    await api.delete(`/tournaments/${id}`);
  },

  registerForTournament: async (registration: TournamentRegistration): Promise<void> => {
    await api.post(`/tournaments/${registration.tournamentId}/register`, registration);
  },

  getTournamentRegistrations: async (id: string): Promise<TournamentRegistration[]> => {
    const response = await api.get<TournamentRegistration[]>(`/tournaments/${id}/registrations`);
    return response.data.data;
  },

  updateTournamentStatus: async (id: string, status: Tournament['status']): Promise<Tournament> => {
    const response = await api.patch<Tournament>(`/tournaments/${id}/status`, { status });
    return response.data.data;
  },

  getTournamentResults: async (id: string): Promise<any[]> => {
    const response = await api.get<any[]>(`/tournaments/${id}/results`);
    return response.data.data;
  },

  addTournamentResult: async (id: string, result: any): Promise<any> => {
    const response = await api.post<any>(`/tournaments/${id}/results`, result);
    return response.data.data;
  },

  generateTournamentBracket: async (id: string): Promise<any> => {
    const response = await api.post<any>(`/tournaments/${id}/bracket`);
    return response.data.data;
  },

  getTournamentBracket: async (id: string): Promise<any> => {
    const response = await api.get<any>(`/tournaments/${id}/bracket`);
    return response.data.data;
  },

  searchTournaments: async (query: string): Promise<Tournament[]> => {
    const response = await api.get<Tournament[]>('/tournaments/search', { params: { q: query } });
    return response.data.data;
  },
};