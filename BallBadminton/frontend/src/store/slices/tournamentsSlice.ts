import { createSlice, PayloadAction } from '@reduxjs/toolkit';

export interface TournamentCategory {
  id: string;
  name: string;
  ageGroup: string;
  gender: 'Men' | 'Women' | 'Mixed';
  maxParticipants: number;
}

export interface Tournament {
  id: string;
  name: string;
  description: string;
  startDate: string;
  endDate: string;
  venue: string;
  district: string;
  categories: TournamentCategory[];
  registrationDeadline: string;
  maxParticipants: number;
  entryFee: number;
  status: 'Upcoming' | 'Ongoing' | 'Completed' | 'Cancelled';
  results?: any[];
  createdAt: string;
  updatedAt: string;
}

interface TournamentsState {
  tournaments: Tournament[];
  upcomingTournaments: Tournament[];
  selectedTournament: Tournament | null;
  isLoading: boolean;
  error: string | null;
  filters: {
    status: string;
    district: string;
    dateRange: {
      start: string;
      end: string;
    };
  };
}

const initialState: TournamentsState = {
  tournaments: [],
  upcomingTournaments: [],
  selectedTournament: null,
  isLoading: false,
  error: null,
  filters: {
    status: '',
    district: '',
    dateRange: {
      start: '',
      end: '',
    },
  },
};

const tournamentsSlice = createSlice({
  name: 'tournaments',
  initialState,
  reducers: {
    setLoading: (state, action: PayloadAction<boolean>) => {
      state.isLoading = action.payload;
    },
    setTournaments: (state, action: PayloadAction<Tournament[]>) => {
      state.tournaments = action.payload;
      state.upcomingTournaments = action.payload.filter(t => t.status === 'Upcoming');
      state.error = null;
    },
    setSelectedTournament: (state, action: PayloadAction<Tournament | null>) => {
      state.selectedTournament = action.payload;
    },
    addTournament: (state, action: PayloadAction<Tournament>) => {
      state.tournaments.push(action.payload);
      if (action.payload.status === 'Upcoming') {
        state.upcomingTournaments.push(action.payload);
      }
    },
    updateTournament: (state, action: PayloadAction<Tournament>) => {
      const index = state.tournaments.findIndex(t => t.id === action.payload.id);
      if (index !== -1) {
        state.tournaments[index] = action.payload;
      }
      
      const upcomingIndex = state.upcomingTournaments.findIndex(t => t.id === action.payload.id);
      if (action.payload.status === 'Upcoming' && upcomingIndex === -1) {
        state.upcomingTournaments.push(action.payload);
      } else if (action.payload.status !== 'Upcoming' && upcomingIndex !== -1) {
        state.upcomingTournaments.splice(upcomingIndex, 1);
      } else if (upcomingIndex !== -1) {
        state.upcomingTournaments[upcomingIndex] = action.payload;
      }
    },
    removeTournament: (state, action: PayloadAction<string>) => {
      state.tournaments = state.tournaments.filter(t => t.id !== action.payload);
      state.upcomingTournaments = state.upcomingTournaments.filter(t => t.id !== action.payload);
    },
    setFilters: (state, action: PayloadAction<Partial<TournamentsState['filters']>>) => {
      state.filters = { ...state.filters, ...action.payload };
    },
    clearFilters: (state) => {
      state.filters = {
        status: '',
        district: '',
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
  setTournaments,
  setSelectedTournament,
  addTournament,
  updateTournament,
  removeTournament,
  setFilters,
  clearFilters,
  setError,
  clearError,
} = tournamentsSlice.actions;

export default tournamentsSlice.reducer;