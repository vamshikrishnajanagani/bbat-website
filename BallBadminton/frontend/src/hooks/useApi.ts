import { useState, useCallback } from 'react';
import { useQuery, useMutation, useQueryClient } from 'react-query';
import { useAppDispatch } from '../store';
import { addNotification } from '../store/slices/uiSlice';

interface UseApiOptions {
  onSuccess?: (data: any) => void;
  onError?: (error: any) => void;
  showSuccessMessage?: boolean;
  showErrorMessage?: boolean;
  successMessage?: string;
  errorMessage?: string;
}

export const useApi = () => {
  const dispatch = useAppDispatch();
  const queryClient = useQueryClient();

  const showNotification = useCallback((type: 'success' | 'error', message: string) => {
    dispatch(addNotification({ type, message }));
  }, [dispatch]);

  const useApiQuery = (
    key: string | string[],
    queryFn: () => Promise<any>,
    options?: any
  ) => {
    return useQuery(key, queryFn, {
      onError: (error: any) => {
        const message = error.response?.data?.message || 'An error occurred';
        showNotification('error', message);
      },
      ...options,
    });
  };

  const useApiMutation = (
    mutationFn: (variables: any) => Promise<any>,
    options: UseApiOptions = {}
  ) => {
    const {
      onSuccess,
      onError,
      showSuccessMessage = true,
      showErrorMessage = true,
      successMessage = 'Operation completed successfully',
      errorMessage,
    } = options;

    return useMutation(mutationFn, {
      onSuccess: (data, variables) => {
        if (showSuccessMessage) {
          showNotification('success', successMessage);
        }
        onSuccess?.(data);
      },
      onError: (error: any, variables) => {
        const message = errorMessage || error.response?.data?.message || 'An error occurred';
        if (showErrorMessage) {
          showNotification('error', message);
        }
        onError?.(error);
      },
    });
  };

  const invalidateQueries = useCallback((queryKey: string | string[]) => {
    queryClient.invalidateQueries(queryKey);
  }, [queryClient]);

  const setQueryData = useCallback((queryKey: string | string[], data: any) => {
    queryClient.setQueryData(queryKey, data);
  }, [queryClient]);

  return {
    useApiQuery,
    useApiMutation,
    invalidateQueries,
    setQueryData,
    showNotification,
  };
};