import React from 'react';
import ReactDOM from 'react-dom/client';
import { BrowserRouter } from 'react-router-dom';
import { Provider } from 'react-redux';
import { QueryClient, QueryClientProvider } from 'react-query';
import { HelmetProvider } from 'react-helmet-async';
import { ThemeProvider } from 'styled-components';

import App from './App';
import { store } from './store';
import { theme } from './styles/theme';
import { GlobalStyles } from './styles/GlobalStyles';
import './i18n';

const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      retry: 3,
      staleTime: 5 * 60 * 1000, // 5 minutes
      cacheTime: 10 * 60 * 1000, // 10 minutes
    },
  },
});

const root = ReactDOM.createRoot(
  document.getElementById('root') as HTMLElement
);

root.render(
  <React.StrictMode>
    <HelmetProvider>
      <Provider store={store}>
        <QueryClientProvider client={queryClient}>
          <BrowserRouter>
            <ThemeProvider theme={theme}>
              <GlobalStyles />
              <App />
            </ThemeProvider>
          </BrowserRouter>
        </QueryClientProvider>
      </Provider>
    </HelmetProvider>
  </React.StrictMode>
);