# Telangana Ball Badminton Association - Frontend

This is the frontend application for the Telangana Ball Badminton Association website, built with React, TypeScript, and modern web technologies.

## Features

- **Modern React Application**: Built with React 18 and TypeScript for type safety
- **State Management**: Redux Toolkit for efficient state management
- **Routing**: React Router for client-side navigation
- **Styling**: Styled Components with a comprehensive design system
- **Internationalization**: Support for English and Telugu languages
- **API Integration**: Axios-based API client with interceptors
- **Responsive Design**: Mobile-first responsive design
- **Accessibility**: WCAG 2.1 compliant components
- **Performance**: Optimized for fast loading and smooth user experience

## Tech Stack

- **React 18** - Modern React with hooks and concurrent features
- **TypeScript** - Type-safe JavaScript development
- **Redux Toolkit** - Efficient Redux state management
- **React Router** - Client-side routing
- **Styled Components** - CSS-in-JS styling solution
- **React Query** - Server state management and caching
- **React Hook Form** - Performant form handling
- **i18next** - Internationalization framework
- **Axios** - HTTP client for API requests
- **React Helmet Async** - Document head management

## Getting Started

### Prerequisites

- Node.js 16+ 
- npm or yarn package manager

### Installation

1. Clone the repository:
   ```bash
   git clone <repository-url>
   cd frontend
   ```

2. Install dependencies:
   ```bash
   npm install
   ```

3. Copy environment configuration:
   ```bash
   cp .env.example .env.development
   ```

4. Update environment variables in `.env.development` as needed.

### Development

Start the development server:
```bash
npm start
```

The application will be available at `http://localhost:3000`.

### Building for Production

Create a production build:
```bash
npm run build
```

The build artifacts will be stored in the `build/` directory.

### Testing

Run the test suite:
```bash
npm test
```

Run tests with coverage:
```bash
npm test -- --coverage
```

### Linting

Run ESLint to check code quality:
```bash
npm run lint
```

Fix linting issues automatically:
```bash
npm run lint:fix
```

### Type Checking

Run TypeScript type checking:
```bash
npm run type-check
```

## Project Structure

```
src/
├── components/          # Reusable UI components
│   ├── Layout/         # Layout components (Header, Footer, Sidebar)
│   └── UI/             # Basic UI components (Button, Input, etc.)
├── pages/              # Page components
├── store/              # Redux store and slices
├── services/           # API services
├── hooks/              # Custom React hooks
├── styles/             # Global styles and theme
├── i18n/               # Internationalization files
├── types/              # TypeScript type definitions
├── utils/              # Utility functions
└── constants/          # Application constants
```

## API Integration

The frontend communicates with the Spring Boot backend through RESTful APIs. The API client is configured with:

- **Base URL**: Configurable via environment variables
- **Authentication**: JWT token-based authentication
- **Interceptors**: Request/response interceptors for error handling
- **Caching**: React Query for intelligent caching and synchronization

## State Management

The application uses Redux Toolkit for state management with the following slices:

- **auth**: Authentication state and user information
- **ui**: UI state (sidebar, notifications, theme, language)
- **members**: Association members data
- **players**: Player profiles and statistics
- **tournaments**: Tournament information and registration
- **districts**: Geographic and district information
- **news**: News articles and announcements

## Styling and Theming

The application uses a comprehensive design system with:

- **Color Palette**: Primary, secondary, and semantic colors
- **Typography**: Font families, sizes, and weights
- **Spacing**: Consistent spacing scale
- **Breakpoints**: Responsive design breakpoints
- **Components**: Styled components with theme integration

## Internationalization

The application supports multiple languages:

- **English** (default)
- **Telugu** (తెలుగు)

Language files are located in `src/i18n/locales/` and can be extended for additional languages.

## Performance Optimization

- **Code Splitting**: Automatic code splitting with React.lazy()
- **Image Optimization**: Lazy loading and responsive images
- **Caching**: Intelligent API response caching
- **Bundle Analysis**: Webpack bundle analyzer for optimization

## Accessibility

The application follows WCAG 2.1 guidelines:

- **Semantic HTML**: Proper HTML structure and landmarks
- **ARIA Labels**: Screen reader support
- **Keyboard Navigation**: Full keyboard accessibility
- **Color Contrast**: Sufficient color contrast ratios
- **Focus Management**: Proper focus indicators and management

## Browser Support

- Chrome 90+
- Firefox 88+
- Safari 14+
- Edge 90+

## Contributing

1. Follow the existing code style and conventions
2. Write tests for new features
3. Update documentation as needed
4. Ensure accessibility compliance
5. Test across different browsers and devices

## Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `REACT_APP_API_BASE_URL` | Backend API base URL | `http://localhost:8080/api/v1` |
| `REACT_APP_ENVIRONMENT` | Environment name | `development` |
| `REACT_APP_ENABLE_DEBUG` | Enable debug mode | `true` |

## Deployment

The application can be deployed to various platforms:

- **Netlify**: Automatic deployments from Git
- **Vercel**: Zero-configuration deployments
- **AWS S3 + CloudFront**: Static hosting with CDN
- **Docker**: Containerized deployment

## License

This project is licensed under the MIT License - see the LICENSE file for details.