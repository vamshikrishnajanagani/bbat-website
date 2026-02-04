import { createSlice, PayloadAction } from '@reduxjs/toolkit';

export interface Member {
  id: string;
  name: string;
  position: string;
  email: string;
  phone: string;
  biography: string;
  photoUrl: string;
  hierarchyLevel: number;
  tenureStartDate: string;
  tenureEndDate?: string;
  isActive: boolean;
  isProminent: boolean;
  isCurrentlyServing?: boolean;
  hasTenureExpired?: boolean;
  createdAt: string;
  updatedAt: string;
}

interface MembersState {
  members: Member[];
  selectedMember: Member | null;
  isLoading: boolean;
  error: string | null;
}

const initialState: MembersState = {
  members: [],
  selectedMember: null,
  isLoading: false,
  error: null,
};

const membersSlice = createSlice({
  name: 'members',
  initialState,
  reducers: {
    setLoading: (state, action: PayloadAction<boolean>) => {
      state.isLoading = action.payload;
    },
    setMembers: (state, action: PayloadAction<Member[]>) => {
      state.members = action.payload;
      state.error = null;
    },
    setSelectedMember: (state, action: PayloadAction<Member | null>) => {
      state.selectedMember = action.payload;
    },
    addMember: (state, action: PayloadAction<Member>) => {
      state.members.push(action.payload);
    },
    updateMember: (state, action: PayloadAction<Member>) => {
      const index = state.members.findIndex(m => m.id === action.payload.id);
      if (index !== -1) {
        state.members[index] = action.payload;
      }
    },
    removeMember: (state, action: PayloadAction<string>) => {
      state.members = state.members.filter(m => m.id !== action.payload);
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
  setMembers,
  setSelectedMember,
  addMember,
  updateMember,
  removeMember,
  setError,
  clearError,
} = membersSlice.actions;

export default membersSlice.reducer;