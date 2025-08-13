import { useState, useEffect } from 'react'
import './MatchHistory.css'

const MatchHistory = () => {
  const [matches, setMatches] = useState([])
  const [filteredMatches, setFilteredMatches] = useState([])
  const [searchTerm, setSearchTerm] = useState('')
  const [selectedYear, setSelectedYear] = useState('all')
  const [selectedTournament, setSelectedTournament] = useState('all')
  const [selectedCategory, setSelectedCategory] = useState('all')
  const [selectedDistrict, setSelectedDistrict] = useState('all')
  const [isLoading, setIsLoading] = useState(true)
  const [viewMode, setViewMode] = useState('list') // 'list' or 'timeline'

  // Mock data - in real app this would come from API
  useEffect(() => {
    const mockMatches = [
      {
        id: 1,
        tournament: 'Telangana State Championship 2024',
        category: 'Men Singles',
        player1: 'Rahul Kumar',
        player2: 'Vikram Singh',
        score1: 21,
        score2: 19,
        winner: 'Rahul Kumar',
        date: '2024-01-15',
        time: '14:30',
        venue: 'Gachibowli Indoor Stadium',
        district: 'Hyderabad',
        round: 'Final',
        duration: '45 minutes',
        highlights: 'Close match with excellent rallies',
        spectators: 150
      },
      {
        id: 2,
        tournament: 'Hyderabad District League 2023',
        category: 'Women Doubles',
        player1: 'Priya Sharma & Anjali Patel',
        player2: 'Meera Reddy & Kavya Rao',
        score1: 21,
        score2: 18,
        winner: 'Priya Sharma & Anjali Patel',
        date: '2023-12-20',
        time: '16:00',
        venue: 'LB Stadium',
        district: 'Hyderabad',
        round: 'Semi-Final',
        duration: '38 minutes',
        highlights: 'Strategic doubles play',
        spectators: 120
      },
      {
        id: 3,
        tournament: 'Warangal Open Tournament 2023',
        category: 'Mixed Doubles',
        player1: 'Arjun & Sita',
        player2: 'Krishna & Radha',
        score1: 19,
        score2: 21,
        winner: 'Krishna & Radha',
        date: '2023-11-15',
        time: '15:30',
        venue: 'Warangal Sports Complex',
        district: 'Warangal',
        round: 'Quarter-Final',
        duration: '42 minutes',
        highlights: 'Exciting mixed doubles action',
        spectators: 80
      },
      {
        id: 4,
        tournament: 'Karimnagar Championship 2023',
        category: 'Men Doubles',
        player1: 'Suresh & Ramesh',
        player2: 'Mohan & Gopal',
        score1: 21,
        score2: 23,
        winner: 'Mohan & Gopal',
        date: '2023-10-28',
        time: '17:00',
        venue: 'Karimnagar Indoor Stadium',
        district: 'Karimnagar',
        round: 'Final',
        duration: '55 minutes',
        highlights: 'Longest match of the tournament',
        spectators: 200
      },
      {
        id: 5,
        tournament: 'Nizamabad District Cup 2023',
        category: 'Women Singles',
        player1: 'Lakshmi Devi',
        player2: 'Saraswati Bai',
        score1: 21,
        score2: 17,
        winner: 'Lakshmi Devi',
        date: '2023-09-12',
        time: '14:00',
        venue: 'Nizamabad Sports Ground',
        district: 'Nizamabad',
        round: 'Semi-Final',
        duration: '35 minutes',
        highlights: 'Dominant performance',
        spectators: 95
      },
      {
        id: 6,
        tournament: 'Adilabad Open 2023',
        category: 'Men Singles',
        player1: 'Amit Patel',
        player2: 'Rajesh Kumar',
        score1: 18,
        score2: 21,
        winner: 'Rajesh Kumar',
        date: '2023-08-25',
        time: '16:30',
        venue: 'Adilabad Sports Complex',
        district: 'Adilabad',
        round: 'Final',
        duration: '40 minutes',
        highlights: 'Comeback victory',
        spectators: 110
      },
      {
        id: 7,
        tournament: 'Khammam Championship 2023',
        category: 'Women Doubles',
        player1: 'Sunita & Geeta',
        player2: 'Radha & Sita',
        score1: 21,
        score2: 19,
        winner: 'Sunita & Geeta',
        date: '2023-07-18',
        time: '15:00',
        venue: 'Khammam Indoor Stadium',
        district: 'Khammam',
        round: 'Quarter-Final',
        duration: '36 minutes',
        highlights: 'Close doubles match',
        spectators: 75
      },
      {
        id: 8,
        tournament: 'Nalgonda District League 2023',
        category: 'Mixed Doubles',
        player1: 'Kumar & Priya',
        player2: 'Ravi & Anjali',
        score1: 23,
        score2: 21,
        winner: 'Kumar & Priya',
        date: '2023-06-30',
        time: '17:30',
        venue: 'Nalgonda Sports Ground',
        district: 'Nalgonda',
        round: 'Semi-Final',
        duration: '48 minutes',
        highlights: 'Thrilling finish',
        spectators: 130
      }
    ]

    setMatches(mockMatches)
    setFilteredMatches(mockMatches)
    setIsLoading(false)
  }, [])

  // Filter matches based on search and filters
  useEffect(() => {
    let filtered = matches

    if (searchTerm) {
      filtered = filtered.filter(match => 
        match.player1.toLowerCase().includes(searchTerm.toLowerCase()) ||
        match.player2.toLowerCase().includes(searchTerm.toLowerCase()) ||
        match.tournament.toLowerCase().includes(searchTerm.toLowerCase()) ||
        match.venue.toLowerCase().includes(searchTerm.toLowerCase())
      )
    }

    if (selectedYear !== 'all') {
      filtered = filtered.filter(match => match.date.startsWith(selectedYear))
    }

    if (selectedTournament !== 'all') {
      filtered = filtered.filter(match => match.tournament === selectedTournament)
    }

    if (selectedCategory !== 'all') {
      filtered = filtered.filter(match => match.category === selectedCategory)
    }

    if (selectedDistrict !== 'all') {
      filtered = filtered.filter(match => match.district === selectedDistrict)
    }

    setFilteredMatches(filtered)
  }, [searchTerm, selectedYear, selectedTournament, selectedCategory, selectedDistrict, matches])

  const years = [...new Set(matches.map(match => match.date.substring(0, 4)))]
  const tournaments = [...new Set(matches.map(match => match.tournament))]
  const categories = [...new Set(matches.map(match => match.category))]
  const districts = [...new Set(matches.map(match => match.district))]

  const formatDate = (dateString) => {
    const date = new Date(dateString)
    return date.toLocaleDateString('en-IN', {
      day: 'numeric',
      month: 'short',
      year: 'numeric'
    })
  }

  const getMatchStats = () => {
    const totalMatches = matches.length
    const totalTournaments = tournaments.length
    const totalDistricts = districts.length
    const avgSpectators = Math.round(matches.reduce((sum, match) => sum + match.spectators, 0) / totalMatches)
    
    return { totalMatches, totalTournaments, totalDistricts, avgSpectators }
  }

  if (isLoading) {
    return (
      <div className="loading">
        <div className="loading-spinner"></div>
        <p>Loading match history...</p>
      </div>
    )
  }

  const stats = getMatchStats()

  return (
    <div className="match-history">
      <div className="container">
        <div className="history-header">
          <h1>Match History & Archives</h1>
          <p>Explore comprehensive records of past matches, tournaments, and player performances</p>
        </div>

        {/* Stats Overview */}
        <div className="stats-overview">
          <div className="stat-item">
            <div className="stat-number">{stats.totalMatches}</div>
            <div className="stat-label">Total Matches</div>
          </div>
          <div className="stat-item">
            <div className="stat-number">{stats.totalTournaments}</div>
            <div className="stat-label">Tournaments</div>
          </div>
          <div className="stat-item">
            <div className="stat-number">{stats.totalDistricts}</div>
            <div className="stat-label">Districts</div>
          </div>
          <div className="stat-item">
            <div className="stat-number">{stats.avgSpectators}</div>
            <div className="stat-label">Avg. Spectators</div>
          </div>
        </div>

        {/* Filters and Search */}
        <div className="filters-section">
          <div className="search-box">
            <input
              type="text"
              placeholder="Search players, tournaments, or venues..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              className="search-input"
            />
          </div>
          
          <div className="filter-controls">
            <select
              value={selectedYear}
              onChange={(e) => setSelectedYear(e.target.value)}
              className="filter-select"
            >
              <option value="all">All Years</option>
              {years.map(year => (
                <option key={year} value={year}>{year}</option>
              ))}
            </select>

            <select
              value={selectedTournament}
              onChange={(e) => setSelectedTournament(e.target.value)}
              className="filter-select"
            >
              <option value="all">All Tournaments</option>
              {tournaments.map(tournament => (
                <option key={tournament} value={tournament}>{tournament}</option>
              ))}
            </select>

            <select
              value={selectedCategory}
              onChange={(e) => setSelectedCategory(e.target.value)}
              className="filter-select"
            >
              <option value="all">All Categories</option>
              {categories.map(category => (
                <option key={category} value={category}>{category}</option>
              ))}
            </select>

            <select
              value={selectedDistrict}
              onChange={(e) => setSelectedDistrict(e.target.value)}
              className="filter-select"
            >
              <option value="all">All Districts</option>
              {districts.map(district => (
                <option key={district} value={district}>{district}</option>
              ))}
            </select>
          </div>

          <div className="view-toggle">
            <button
              className={`toggle-btn ${viewMode === 'list' ? 'active' : ''}`}
              onClick={() => setViewMode('list')}
            >
              📋 List View
            </button>
            <button
              className={`toggle-btn ${viewMode === 'timeline' ? 'active' : ''}`}
              onClick={() => setViewMode('timeline')}
            >
              📅 Timeline View
            </button>
          </div>
        </div>

        {/* Results Count */}
        <div className="results-count">
          <p>Showing {filteredMatches.length} of {matches.length} matches</p>
        </div>

        {/* Matches Display */}
        {viewMode === 'list' ? (
          <div className="matches-list">
            {filteredMatches.length === 0 ? (
              <div className="no-results">
                <p>No matches found. Try adjusting your search or filters.</p>
              </div>
            ) : (
              filteredMatches.map(match => (
                <div key={match.id} className="match-card">
                  <div className="match-header">
                    <div className="match-info">
                      <h3>{match.tournament}</h3>
                      <div className="match-meta">
                        <span className="category-badge">{match.category}</span>
                        <span className="round-badge">{match.round}</span>
                        <span className="district-badge">{match.district}</span>
                      </div>
                    </div>
                    <div className="match-date">
                      <div className="date">{formatDate(match.date)}</div>
                      <div className="time">{match.time}</div>
                    </div>
                  </div>

                  <div className="match-content">
                    <div className="player player1">
                      <div className="player-name">
                        {match.player1}
                        {match.winner === match.player1 && <span className="winner-indicator">🏆</span>}
                      </div>
                      <div className="player-score">{match.score1}</div>
                    </div>

                    <div className="vs-divider">
                      <span>VS</span>
                    </div>

                    <div className="player player2">
                      <div className="player-name">
                        {match.player2}
                        {match.winner === match.player2 && <span className="winner-indicator">🏆</span>}
                      </div>
                      <div className="player-score">{match.score2}</div>
                    </div>
                  </div>

                  <div className="match-details">
                    <div className="detail-row">
                      <span className="detail-label">🏆 Winner:</span>
                      <span className="detail-value">{match.winner}</span>
                    </div>
                    <div className="detail-row">
                      <span className="detail-label">📍 Venue:</span>
                      <span className="detail-value">{match.venue}</span>
                    </div>
                    <div className="detail-row">
                      <span className="detail-label">⏱️ Duration:</span>
                      <span className="detail-value">{match.duration}</span>
                    </div>
                    <div className="detail-row">
                      <span className="detail-label">👥 Spectators:</span>
                      <span className="detail-value">{match.spectators}</span>
                    </div>
                    <div className="detail-row">
                      <span className="detail-label">💡 Highlights:</span>
                      <span className="detail-value">{match.highlights}</span>
                    </div>
                  </div>
                </div>
              ))
            )}
          </div>
        ) : (
          <div className="timeline-view">
            {filteredMatches.length === 0 ? (
              <div className="no-results">
                <p>No matches found. Try adjusting your search or filters.</p>
              </div>
            ) : (
              <div className="timeline">
                {filteredMatches.map((match, index) => (
                  <div key={match.id} className="timeline-item">
                    <div className="timeline-marker"></div>
                    <div className="timeline-content">
                      <div className="timeline-date">{formatDate(match.date)}</div>
                      <div className="timeline-match">
                        <h4>{match.tournament}</h4>
                        <p className="timeline-category">{match.category} • {match.round}</p>
                        <div className="timeline-players">
                          <span className={match.winner === match.player1 ? 'winner' : ''}>
                            {match.player1} ({match.score1})
                          </span>
                          <span className="vs">vs</span>
                          <span className={match.winner === match.player2 ? 'winner' : ''}>
                            {match.player2} ({match.score2})
                          </span>
                        </div>
                        <p className="timeline-venue">📍 {match.venue}, {match.district}</p>
                      </div>
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>
        )}
      </div>
    </div>
  )
}

export default MatchHistory
