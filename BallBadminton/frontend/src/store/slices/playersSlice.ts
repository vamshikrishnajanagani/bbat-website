import { createSlice, PayloadAction } from '@reduxjs/toolkit';

export interface Achievement {
  id: string;
  title: string;
  description: string;
  achievementDate: string;
  category: string;
  level: 'INTERNATIONAL' | 'NATIONAL' | 'STATE' | 'DISTRICT' | 'LOCAL';
  position?: number;
  positionText?: string;
  isVerified: boolean;
  isRecentAchievement?: boolean;
  isMajorAchievement?: boolean;
  tournamentId?: string;
  tournamentName?: string;
  createdAt: string;
  updatedAt: string;
}

export interface PlayerStatistics {
  matchesPlayed: number;
  matchesWon: number;
  tournamentsParticipated: number;
  tournamentsWon: number;
  winPercentage: number;
  currentRanking?: number;
  bestRanking?: number;
  totalPoints: number;
}

export interface Player {
  id: string;
  name: string;
  dateOfBirth?: string;
  age?: number;
  gender?: 'MALE' | 'FEMALE';
  category: 'MEN' | 'WOMEN' | 'JUNIOR' | 'SENIOR';
  profilePhotoUrl?: string;
  contactEmail?: string;
  contactPhone?: string;
  address?: string;
  districtId?: string;
  districtName?: string;
  recentAchievements?: Achievement[];
  totalAchievements: number;
  statistics?: PlayerStatistics;
  isProminent: boolean;
  isActive: boolean;
  createdAt: string;
  updatedAt: string;
}

interface PlayersState {
  players: Player[];
  prominentPlayers: Player[];
  selectedPlayer: Player | null;
  isLoading: boolean;
  error: string | null;
  filters: {
    district: string;
    category: string;
    searchTerm: string;
  };
}

const initialState: PlayersState = {
  players: [],
  prominentPlayers: [],
  selectedPlayer: null,
  isLoading: false,
  error: null,
  filters: {
    district: '',
    category: '',
    searchTerm: '',
  },
};

const playersSlice = createSlice({
  name: 'players',
  initialState,
  reducers: {
    setLoading: (state, action: PayloadAction<boolean>) => {
      state.isLoading = action.payload;
    },
    setPlayers: (state, action: PayloadAction<Player[]>) => {
      state.players = action.payload;
      state.prominentPlayers = action.payload.filter(p => p.isProminent);
      state.error = null;
    },
    setSelectedPlayer: (state, action: PayloadAction<Player | null>) => {
      state.selectedPlayer = action.payload;
    },
    addPlayer: (state, action: PayloadAction<Player>) => {
      state.players.push(action.payload);
      if (action.payload.isProminent) {
        state.prominentPlayers.push(action.payload);
      }
    },
    updatePlayer: (state, action: PayloadAction<Player>) => {
      const index = state.players.findIndex(p => p.id === action.payload.id);
      if (index !== -1) {
        state.players[index] = action.payload;
      }
      
      const prominentIndex = state.prominentPlayers.findIndex(p => p.id === action.payload.id);
      if (action.payload.isProminent && prominentIndex === -1) {
        state.prominentPlayers.push(action.payload);
      } else if (!action.payload.isProminent && prominentIndex !== -1) {
        state.prominentPlayers.splice(prominentIndex, 1);
      } else if (prominentIndex !== -1) {
        state.prominentPlayers[prominentIndex] = action.payload;
      }
    },
    removePlayer: (state, action: PayloadAction<string>) => {
      state.players = state.players.filter(p => p.id !== action.payload);
      state.prominentPlayers = state.prominentPlayers.filter(p => p.id !== action.payload);
    },
    setFilters: (state, action: PayloadAction<Partial<PlayersState['filters']>>) => {
      state.filters = { ...state.filters, ...action.payload };
    },
    clearFilters: (state) => {
      state.filters = {
        district: '',
        category: '',
        searchTerm: '',
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
  setPlayers,
  setSelectedPlayer,
  addPlayer,
  updatePlayer,
  removePlayer,
  setFilters,
  clearFilters,
  setError,
  clearError,
} = playersSlice.actions;

export default playersSlice.reducer;