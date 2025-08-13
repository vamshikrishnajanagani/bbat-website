import { useState, useEffect } from 'react'
import './RecentScores.css'

const RecentScores = () => {
  const [scores, setScores] = useState([])
  const [filteredScores, setFilteredScores] = useState([])
  const [searchTerm, setSearchTerm] = useState('')
  const [selectedTournament, setSelectedTournament] = useState('all')
  const [selectedCategory, setSelectedCategory] = useState('all')
  const [isLoading, setIsLoading] = useState(true)

  // Mock data - in real app this would come from API
  useEffect(() => {
    const mockScores = [
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
        status: 'completed',
        venue: 'Gachibowli Indoor Stadium'
      },
      {
        id: 2,
        tournament: 'Hyderabad District League',
        category: 'Women Doubles',
        player1: 'Priya Sharma & Anjali Patel',
        player2: 'Meera Reddy & Kavya Rao',
        score1: 21,
        score2: 18,
        winner: 'Priya Sharma & Anjali Patel',
        date: '2024-01-14',
        time: '16:00',
        status: 'completed',
        venue: 'LB Stadium'
      },
      {
        id: 3,
        tournament: 'Warangal Open Tournament',
        category: 'Mixed Doubles',
        player1: 'Arjun & Sita',
        player2: 'Krishna & Radha',
        score1: 19,
        score2: 21,
        winner: 'Krishna & Radha',
        date: '2024-01-13',
        time: '15:30',
        status: 'completed',
        venue: 'Warangal Sports Complex'
      },
      {
        id: 4,
        tournament: 'Karimnagar Championship',
        category: 'Men Doubles',
        player1: 'Suresh & Ramesh',
        player2: 'Mohan & Gopal',
        score1: 21,
        score2: 23,
        winner: 'Mohan & Gopal',
        date: '2024-01-12',
        time: '17:00',
        status: 'completed',
        venue: 'Karimnagar Indoor Stadium'
      },
      {
        id: 5,
        tournament: 'Nizamabad District Cup',
        category: 'Women Singles',
        player1: 'Lakshmi Devi',
        player2: 'Saraswati Bai',
        score1: 21,
        score2: 17,
        winner: 'Lakshmi Devi',
        date: '2024-01-11',
        time: '14:00',
        status: 'completed',
        venue: 'Nizamabad Sports Ground'
      },
      {
        id: 6,
        tournament: 'Telangana State Championship 2024',
        category: 'Men Singles',
        player1: 'Amit Patel',
        player2: 'Rajesh Kumar',
        score1: 18,
        score2: 21,
        winner: 'Rajesh Kumar',
        date: '2024-01-10',
        time: '16:30',
        status: 'completed',
        venue: 'Gachibowli Indoor Stadium'
      }
    ]

    setScores(mockScores)
    setFilteredScores(mockScores)
    setIsLoading(false)
  }, [])

  // Filter scores based on search and filters
  useEffect(() => {
    let filtered = scores

    if (searchTerm) {
      filtered = filtered.filter(score => 
        score.player1.toLowerCase().includes(searchTerm.toLowerCase()) ||
        score.player2.toLowerCase().includes(searchTerm.toLowerCase()) ||
        score.tournament.toLowerCase().includes(searchTerm.toLowerCase()) ||
        score.venue.toLowerCase().includes(searchTerm.toLowerCase())
      )
    }

    if (selectedTournament !== 'all') {
      filtered = filtered.filter(score => score.tournament === selectedTournament)
    }

    if (selectedCategory !== 'all') {
      filtered = filtered.filter(score => score.category === selectedCategory)
    }

    setFilteredScores(filtered)
  }, [searchTerm, selectedTournament, selectedCategory, scores])

  const tournaments = [...new Set(scores.map(score => score.tournament))]
  const categories = [...new Set(scores.map(score => score.category))]

  const formatDate = (dateString) => {
    const date = new Date(dateString)
    return date.toLocaleDateString('en-IN', {
      day: 'numeric',
      month: 'short',
      year: 'numeric'
    })
  }

  if (isLoading) {
    return (
      <div className="loading">
        <div className="loading-spinner"></div>
        <p>Loading recent scores...</p>
      </div>
    )
  }

  return (
    <div className="recent-scores">
      <div className="container">
        <div className="scores-header">
          <h1>Recent Scores & Results</h1>
          <p>Stay updated with the latest match results and tournament scores</p>
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
          </div>
        </div>

        {/* Results Count */}
        <div className="results-count">
          <p>Showing {filteredScores.length} of {scores.length} results</p>
        </div>

        {/* Scores List */}
        <div className="scores-list">
          {filteredScores.length === 0 ? (
            <div className="no-results">
              <p>No results found. Try adjusting your search or filters.</p>
            </div>
          ) : (
            filteredScores.map(score => (
              <div key={score.id} className="score-card">
                <div className="score-header">
                  <div className="tournament-info">
                    <h3>{score.tournament}</h3>
                    <span className="category-badge">{score.category}</span>
                  </div>
                  <div className="match-status completed">
                    {score.status === 'completed' ? 'Completed' : 'Live'}
                  </div>
                </div>

                <div className="score-content">
                  <div className="player player1">
                    <div className="player-name">
                      {score.player1}
                      {score.winner === score.player1 && <span className="winner-indicator">🏆</span>}
                    </div>
                    <div className="player-score">{score.score1}</div>
                  </div>

                  <div className="vs-divider">
                    <span>VS</span>
                  </div>

                  <div className="player player2">
                    <div className="player-name">
                      {score.player2}
                      {score.winner === score.player2 && <span className="winner-indicator">🏆</span>}
                    </div>
                    <div className="player-score">{score.score2}</div>
                  </div>
                </div>

                <div className="score-footer">
                  <div className="match-details">
                    <span className="match-date">📅 {formatDate(score.date)}</span>
                    <span className="match-time">🕒 {score.time}</span>
                    <span className="match-venue">📍 {score.venue}</span>
                  </div>
                  <div className="winner-info">
                    <strong>Winner: {score.winner}</strong>
                  </div>
                </div>
              </div>
            ))
          )}
        </div>

        {/* Quick Stats */}
        <div className="quick-stats">
          <div className="stat-card">
            <div className="stat-number">{scores.length}</div>
            <div className="stat-label">Total Matches</div>
          </div>
          <div className="stat-card">
            <div className="stat-number">{tournaments.length}</div>
            <div className="stat-label">Tournaments</div>
          </div>
          <div className="stat-card">
            <div className="stat-number">{categories.length}</div>
            <div className="stat-label">Categories</div>
          </div>
        </div>
      </div>
    </div>
  )
}

export default RecentScores
