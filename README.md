# BBAT Website - Ball Badminton Association of Telangana

A modern, responsive React website for the Ball Badminton Association of Telangana (BBAT), featuring player registration, match scores, history, and player profiles.

## 🏸 Features

### Core Functionality
- **Player Registration System** - Comprehensive registration form for new players
- **Recent Scores & Results** - Live and recent match results with filtering
- **Match History & Archives** - Historical match data with timeline and list views
- **Famous Players Showcase** - Player profiles with achievements and statistics
- **Responsive Design** - Mobile-first design that works on all devices

### Technical Features
- **Modern React 19** - Built with the latest React features
- **React Router DOM** - Client-side routing for seamless navigation
- **Responsive CSS Grid & Flexbox** - Modern CSS layouts
- **Interactive Components** - Hover effects, animations, and smooth transitions
- **Search & Filtering** - Advanced search and filter capabilities
- **Modal Windows** - Detailed player information in popup modals

## 🚀 Getting Started

### Prerequisites
- Node.js (version 16 or higher)
- npm or yarn package manager

### Installation

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd bbat-website
   ```

2. **Install dependencies**
   ```bash
   npm install
   ```

3. **Start development server**
   ```bash
   npm run dev
   ```

4. **Open in browser**
   Navigate to `http://localhost:5173` to view the website

### Available Scripts

- `npm run dev` - Start development server
- `npm run build` - Build for production
- `npm run preview` - Preview production build
- `npm run lint` - Run ESLint

## 🏗️ Project Structure

```
src/
├── components/          # React components
│   ├── Navbar.jsx      # Navigation bar
│   ├── Home.jsx        # Homepage
│   ├── PlayerRegistration.jsx  # Player registration form
│   ├── RecentScores.jsx        # Recent scores display
│   ├── MatchHistory.jsx        # Match history with filters
│   ├── FamousPlayers.jsx       # Player profiles
│   ├── Footer.jsx      # Footer component
│   └── *.css           # Component-specific styles
├── App.jsx             # Main application component
├── main.jsx            # Application entry point
├── App.css             # Global styles
└── index.css           # Base styles
```

## 🎨 Design Features

### Color Scheme
- **Primary**: Blue gradient (#1e3c72 to #2a5298)
- **Accent**: Gold (#ffd700)
- **Secondary**: Purple gradient (#667eea to #764ba2)
- **Neutral**: Grays (#f8f9fa, #e9ecef, #666)

### Typography
- Modern, readable font stack
- Responsive font sizes
- Clear hierarchy with proper contrast

### Layout
- **Grid-based design** for consistent spacing
- **Card-based components** for content organization
- **Responsive breakpoints** for mobile, tablet, and desktop

## 📱 Responsive Design

The website is fully responsive with breakpoints at:
- **Mobile**: < 480px
- **Tablet**: 480px - 768px
- **Desktop**: > 768px

## 🔧 Customization

### Adding New Players
Edit the mock data in `src/components/FamousPlayers.jsx` to add new players:

```javascript
{
  id: 9,
  name: 'New Player Name',
  district: 'District Name',
  category: 'Category',
  // ... other properties
}
```

### Adding New Matches
Edit the mock data in `src/components/RecentScores.jsx` and `src/components/MatchHistory.jsx`.

### Styling Changes
- Component-specific styles are in individual `.css` files
- Global styles are in `src/App.css`
- Color variables can be updated in individual CSS files

## 🌐 Deployment

### Build for Production
```bash
npm run build
```

The build output will be in the `dist/` folder, ready for deployment to any static hosting service.

### Recommended Hosting
- **Netlify** - Easy deployment with Git integration
- **Vercel** - Great for React applications
- **GitHub Pages** - Free hosting for open source projects
- **AWS S3 + CloudFront** - Enterprise-grade hosting

## 📊 Data Management

Currently, the application uses mock data for demonstration. To integrate with a real backend:

1. **Replace mock data** with API calls
2. **Add state management** (Redux, Zustand, or Context API)
3. **Implement authentication** for admin features
4. **Add database integration** for persistent data

## 🚧 Future Enhancements

### Planned Features
- **Admin Panel** - Tournament management and player administration
- **Live Scoring** - Real-time match updates
- **Tournament Brackets** - Interactive tournament visualization
- **Player Rankings** - Dynamic ranking system
- **News & Updates** - Association news and announcements
- **Photo Gallery** - Match and event photos
- **Contact Forms** - Inquiry and feedback forms

### Technical Improvements
- **PWA Support** - Progressive Web App capabilities
- **Offline Support** - Service worker for offline functionality
- **Performance Optimization** - Code splitting and lazy loading
- **SEO Optimization** - Meta tags and structured data
- **Analytics Integration** - User behavior tracking

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 📞 Contact

**Ball Badminton Association of Telangana**
- **Address**: Gachibowli Indoor Stadium, Hyderabad, Telangana 500032
- **Phone**: +91 98765 43210, +91 98765 43211
- **Email**: info@bbat.org.in, secretary@bbat.org.in
- **Office Hours**: Monday-Friday 9:00 AM - 6:00 PM, Saturday 9:00 AM - 2:00 PM

## 🙏 Acknowledgments

- **BBFI** - Ball Badminton Federation of India
- **Government of India** - Ministry of Youth Affairs & Sports
- **Telangana State Government** - Sports Department
- **React Team** - For the amazing framework
- **Vite Team** - For the fast build tool

---

**Built with ❤️ for the Ball Badminton community of Telangana**
