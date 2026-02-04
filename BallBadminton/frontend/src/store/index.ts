import { configureStore } from '@reduxjs/toolkit';
import { TypedUseSelectorHook, useDispatch, useSelector } from 'react-redux';

import authSlice from './slices/authSlice';
import uiSlice from './slices/uiSlice';
import membersSlice from './slices/membersSlice';
import playersSlice from './slices/playersSlice';
import tournamentsSlice from './slices/tournamentsSlice';
import districtsSlice from './slices/districtsSlice';
import newsSlice from './slices/newsSlice';

export const store = configureStore({
  reducer: {
    auth: authSlice,
    ui: uiSlice,
    members: membersSlice,
    players: playersSlice,
    tournaments: tournamentsSlice,
    districts: districtsSlice,
    news: newsSlice,
  },
  middleware: (getDefaultMiddleware) =>
    getDefaultMiddleware({
      serializableCheck: {
        ignoredActions: ['persist/PERSIST'],
      },
    }),
});

export type RootState = ReturnType<typeof store.getState>;
export type AppDispatch = typeof store.dispatch;

export const useAppDispatch = () => useDispatch<AppDispatch>();
export const useAppSelector: TypedUseSelectorHook<RootState> = useSelector;