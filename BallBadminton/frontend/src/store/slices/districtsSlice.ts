import { createSlice, PayloadAction } from '@reduxjs/toolkit';

export interface District {
  id: string;
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
  playerCount: number;
  activeClubs: number;
  createdAt: string;
  updatedAt: string;
}

interface DistrictsState {
  districts: District[];
  selectedDistrict: District | null;
  isLoading: boolean;
  error: string | null;
}

const initialState: DistrictsState = {
  districts: [],
  selectedDistrict: null,
  isLoading: false,
  error: null,
};

const districtsSlice = createSlice({
  name: 'districts',
  initialState,
  reducers: {
    setLoading: (state, action: PayloadAction<boolean>) => {
      state.isLoading = action.payload;
    },
    setDistricts: (state, action: PayloadAction<District[]>) => {
      state.districts = action.payload;
      state.error = null;
    },
    setSelectedDistrict: (state, action: PayloadAction<District | null>) => {
      state.selectedDistrict = action.payload;
    },
    addDistrict: (state, action: PayloadAction<District>) => {
      state.districts.push(action.payload);
    },
    updateDistrict: (state, action: PayloadAction<District>) => {
      const index = state.districts.findIndex(d => d.id === action.payload.id);
      if (index !== -1) {
        state.districts[index] = action.payload;
      }
    },
    removeDistrict: (state, action: PayloadAction<string>) => {
      state.districts = state.districts.filter(d => d.id !== action.payload);
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
  setDistricts,
  setSelectedDistrict,
  addDistrict,
  updateDistrict,
  removeDistrict,
  setError,
  clearError,
} = districtsSlice.actions;

export default districtsSlice.reducer;