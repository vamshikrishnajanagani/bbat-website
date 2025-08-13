import { useState, useEffect } from 'react'
import './FamousPlayers.css'

const FamousPlayers = () => {
  const [players, setPlayers] = useState([])
  const [filteredPlayers, setFilteredPlayers] = useState([])
  const [searchTerm, setSearchTerm] = useState('')
  const [selectedCategory, setSelectedCategory] = useState('all')
  const [selectedDistrict, setSelectedDistrict] = useState('all')
  const [selectedAchievement, setSelectedAchievement] = useState('all')
  const [isLoading, setIsLoading] = useState(true)
  const [selectedPlayer, setSelectedPlayer] = useState(null)

  // Mock data - in real app this would come from API
  useEffect(() => {
    const mockPlayers = [
      {
        id: 1,
        name: 'Rahul Kumar',
        district: 'Hyderabad',
        category: 'Men Singles',
        age: 28,
        experience: '12 years',
        achievements: [
          'National Champion 2023',
          'Telangana State Champion 2024',
          'South Zone Winner 2022',
          'International Tournament Runner-up 2021'
        ],
        stats: {
          matchesPlayed: 156,
          matchesWon: 142,
          winPercentage: 91,
          tournamentsWon: 18,
          currentRanking: 1
        },
        bio: 'Rahul Kumar is one of the most accomplished ball badminton players from Telangana. Known for his aggressive playing style and exceptional court coverage, he has dominated the state circuit for over a decade.',
        strengths: ['Powerful smashes', 'Excellent footwork', 'Strategic thinking'],
        photo: '🏸',
        isActive: true
      },
      {
        id: 2,
        name: 'Priya Sharma',
        district: 'Hyderabad',
        category: 'Women Singles',
        age: 25,
        experience: '8 years',
        achievements: [
          'National Women\'s Champion 2023',
          'Telangana State Champion 2023',
          'South Zone Champion 2022',
          'District League Winner 2021'
        ],
        stats: {
          matchesPlayed: 98,
          matchesWon: 89,
          winPercentage: 91,
          tournamentsWon: 12,
          currentRanking: 2
        },
        bio: 'Priya Sharma has emerged as a rising star in women\'s ball badminton. Her technical skills and mental toughness make her a formidable opponent on the court.',
        strengths: ['Technical precision', 'Mental strength', 'Consistent performance'],
        photo: '🏸',
        isActive: true
      },
      {
        id: 3,
        name: 'Vikram Singh',
        district: 'Warangal',
        category: 'Men Singles',
        age: 30,
        experience: '15 years',
        achievements: [
          'National Runner-up 2023',
          'Warangal District Champion 2024',
          'State Championship Winner 2022',
          'Multiple District Titles'
        ],
        stats: {
          matchesPlayed: 203,
          matchesWon: 178,
          winPercentage: 88,
          tournamentsWon: 22,
          currentRanking: 3
        },
        bio: 'Vikram Singh is a veteran player known for his experience and tactical gameplay. He has been a consistent performer in both district and state level tournaments.',
        strengths: ['Experience', 'Tactical play', 'Consistency'],
        photo: '🏸',
        isActive: true
      },
      {
        id: 4,
        name: 'Anjali Patel',
        district: 'Karimnagar',
        category: 'Women Doubles',
        age: 23,
        experience: '6 years',
        achievements: [
          'National Doubles Champion 2023',
          'State Doubles Winner 2023',
          'District Champion 2022',
          'Young Player of the Year 2021'
        ],
        stats: {
          matchesPlayed: 67,
          matchesWon: 58,
          winPercentage: 87,
          tournamentsWon: 8,
          currentRanking: 4
        },
        bio: 'Anjali Patel specializes in doubles play and has formed formidable partnerships. Her understanding of doubles strategy and coordination skills are exceptional.',
        strengths: ['Doubles strategy', 'Coordination', 'Quick reflexes'],
        photo: '🏸',
        isActive: true
      },
      {
        id: 5,
        name: 'Arjun Reddy',
        district: 'Nizamabad',
        category: 'Mixed Doubles',
        age: 26,
        experience: '9 years',
        achievements: [
          'National Mixed Doubles Champion 2023',
          'State Mixed Doubles Winner 2023',
          'District Champion 2022',
          'Tournament MVP 2021'
        ],
        stats: {
          matchesPlayed: 89,
          matchesWon: 76,
          winPercentage: 85,
          tournamentsWon: 11,
          currentRanking: 5
        },
        bio: 'Arjun Reddy excels in mixed doubles with his adaptability and excellent court positioning. He has won multiple titles with different partners.',
        strengths: ['Adaptability', 'Court positioning', 'Versatility'],
        photo: '🏸',
        isActive: true
      },
      {
        id: 6,
        name: 'Lakshmi Devi',
        district: 'Khammam',
        category: 'Women Singles',
        age: 27,
        experience: '10 years',
        achievements: [
          'State Championship Runner-up 2023',
          'District Champion 2023',
          'South Zone Winner 2022',
          'Multiple District Titles'
        ],
        stats: {
          matchesPlayed: 112,
          matchesWon: 94,
          winPercentage: 84,
          tournamentsWon: 14,
          currentRanking: 6
        },
        bio: 'Lakshmi Devi is known for her defensive skills and ability to turn defense into attack. Her patient approach often frustrates aggressive opponents.',
        strengths: ['Defensive skills', 'Patience', 'Counter-attacking'],
        photo: '🏸',
        isActive: true
      },
      {
        id: 7,
        name: 'Suresh Kumar',
        district: 'Adilabad',
        category: 'Men Doubles',
        age: 29,
        experience: '11 years',
        achievements: [
          'State Doubles Champion 2023',
          'District Champion 2023',
          'Tournament Winner 2022',
          'Best Doubles Player 2021'
        ],
        stats: {
          matchesPlayed: 134,
          matchesWon: 108,
          winPercentage: 81,
          tournamentsWon: 16,
          currentRanking: 7
        },
        bio: 'Suresh Kumar is a doubles specialist with excellent net play and communication skills. He has won multiple titles with his regular partner.',
        strengths: ['Net play', 'Communication', 'Team coordination'],
        photo: '🏸',
        isActive: true
      },
      {
        id: 8,
        name: 'Meera Reddy',
        district: 'Nalgonda',
        category: 'Women Singles',
        age: 24,
        experience: '7 years',
        achievements: [
          'District Champion 2023',
          'State Championship Semi-finalist 2023',
          'Young Achiever Award 2022',
          'Rising Star 2021'
        ],
        stats: {
          matchesPlayed: 78,
          matchesWon: 62,
          winPercentage: 79,
          tournamentsWon: 9,
          currentRanking: 8
        },
        bio: 'Meera Reddy is a young talent with a bright future. Her aggressive playing style and determination make her a player to watch out for.',
        strengths: ['Aggressive play', 'Determination', 'Youth energy'],
        photo: '🏸',
        isActive: true
      }
    ]

    setPlayers(mockPlayers)
    setFilteredPlayers(mockPlayers)
    setIsLoading(false)
  }, [])

  // Filter players based on search and filters
  useEffect(() => {
    let filtered = players

    if (searchTerm) {
      filtered = filtered.filter(player => 
        player.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
        player.district.toLowerCase().includes(searchTerm.toLowerCase()) ||
        player.achievements.some(achievement => 
          achievement.toLowerCase().includes(searchTerm.toLowerCase())
        )
      )
    }

    if (selectedCategory !== 'all') {
      filtered = filtered.filter(player => player.category === selectedCategory)
    }

    if (selectedDistrict !== 'all') {
      filtered = filtered.filter(player => player.district === selectedDistrict)
    }

    if (selectedAchievement !== 'all') {
      if (selectedAchievement === 'national') {
        filtered = filtered.filter(player => 
          player.achievements.some(achievement => 
            achievement.toLowerCase().includes('national')
          )
        )
      } else if (selectedAchievement === 'state') {
        filtered = filtered.filter(player => 
          player.achievements.some(achievement => 
            achievement.toLowerCase().includes('state')
          )
        )
      }
    }

    setFilteredPlayers(filtered)
  }, [searchTerm, selectedCategory, selectedDistrict, selectedAchievement, players])

  const categories = [...new Set(players.map(player => player.category))]
  const districts = [...new Set(players.map(player => player.district))]

  const openPlayerModal = (player) => {
    setSelectedPlayer(player)
  }

  const closePlayerModal = () => {
    setSelectedPlayer(null)
  }

  if (isLoading) {
    return (
      <div className="loading">
        <div className="loading-spinner"></div>
        <p>Loading famous players...</p>
      </div>
    )
  }

  return (
    <div className="famous-players">
      <div className="container">
        <div className="players-header">
          <h1>Famous Players</h1>
          <p>Meet the legends and rising stars of ball badminton in Telangana</p>
        </div>

        {/* Filters and Search */}
        <div className="filters-section">
          <div className="search-box">
            <input
              type="text"
              placeholder="Search players, achievements, or districts..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              className="search-input"
            />
          </div>
          
          <div className="filter-controls">
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

            <select
              value={selectedAchievement}
              onChange={(e) => setSelectedAchievement(e.target.value)}
              className="filter-select"
            >
              <option value="all">All Achievements</option>
              <option value="national">National Champions</option>
              <option value="state">State Champions</option>
            </select>
          </div>
        </div>

        {/* Results Count */}
        <div className="results-count">
          <p>Showing {filteredPlayers.length} of {players.length} players</p>
        </div>

        {/* Players Grid */}
        <div className="players-grid">
          {filteredPlayers.length === 0 ? (
            <div className="no-results">
              <p>No players found. Try adjusting your search or filters.</p>
            </div>
          ) : (
            filteredPlayers.map(player => (
              <div key={player.id} className="player-card" onClick={() => openPlayerModal(player)}>
                <div className="player-photo">
                  {player.photo}
                </div>
                <div className="player-info">
                  <h3 className="player-name">{player.name}</h3>
                  <p className="player-category">{player.category}</p>
                  <p className="player-district">📍 {player.district}</p>
                  <div className="player-stats">
                    <span className="stat">Rank: #{player.stats.currentRanking}</span>
                    <span className="stat">Win: {player.stats.winPercentage}%</span>
                  </div>
                  <div className="player-achievements">
                    <h4>Top Achievements:</h4>
                    <ul>
                      {player.achievements.slice(0, 2).map((achievement, index) => (
                        <li key={index}>{achievement}</li>
                      ))}
                    </ul>
                  </div>
                  <div className="player-status">
                    {player.isActive ? (
                      <span className="status active">Active Player</span>
                    ) : (
                      <span className="status retired">Retired</span>
                    )}
                  </div>
                </div>
              </div>
            ))
          )}
        </div>

        {/* Player Modal */}
        {selectedPlayer && (
          <div className="player-modal-overlay" onClick={closePlayerModal}>
            <div className="player-modal" onClick={(e) => e.stopPropagation()}>
              <button className="modal-close" onClick={closePlayerModal}>×</button>
              
              <div className="modal-header">
                <div className="modal-photo">{selectedPlayer.photo}</div>
                <div className="modal-info">
                  <h2>{selectedPlayer.name}</h2>
                  <p className="modal-category">{selectedPlayer.category}</p>
                  <p className="modal-district">📍 {selectedPlayer.district}</p>
                  <div className="modal-status">
                    {selectedPlayer.isActive ? (
                      <span className="status active">Active Player</span>
                    ) : (
                      <span className="status retired">Retired</span>
                    )}
                  </div>
                </div>
              </div>

              <div className="modal-content">
                <div className="modal-section">
                  <h3>Player Bio</h3>
                  <p>{selectedPlayer.bio}</p>
                </div>

                <div className="modal-section">
                  <h3>Statistics</h3>
                  <div className="stats-grid">
                    <div className="stat-item">
                      <div className="stat-number">{selectedPlayer.stats.matchesPlayed}</div>
                      <div className="stat-label">Matches Played</div>
                    </div>
                    <div className="stat-item">
                      <div className="stat-number">{selectedPlayer.stats.matchesWon}</div>
                      <div className="stat-label">Matches Won</div>
                    </div>
                    <div className="stat-item">
                      <div className="stat-number">{selectedPlayer.stats.winPercentage}%</div>
                      <div className="stat-label">Win Percentage</div>
                    </div>
                    <div className="stat-item">
                      <div className="stat-number">{selectedPlayer.stats.tournamentsWon}</div>
                      <div className="stat-label">Tournaments Won</div>
                    </div>
                    <div className="stat-item">
                      <div className="stat-number">#{selectedPlayer.stats.currentRanking}</div>
                      <div className="stat-label">Current Ranking</div>
                    </div>
                    <div className="stat-item">
                      <div className="stat-number">{selectedPlayer.experience}</div>
                      <div className="stat-label">Experience</div>
                    </div>
                  </div>
                </div>

                <div className="modal-section">
                  <h3>Key Strengths</h3>
                  <div className="strengths-list">
                    {selectedPlayer.strengths.map((strength, index) => (
                      <span key={index} className="strength-tag">{strength}</span>
                    ))}
                  </div>
                </div>

                <div className="modal-section">
                  <h3>Major Achievements</h3>
                  <ul className="achievements-list">
                    {selectedPlayer.achievements.map((achievement, index) => (
                      <li key={index}>🏆 {achievement}</li>
                    ))}
                  </ul>
                </div>
              </div>
            </div>
          </div>
        )}
      </div>
    </div>
  )
}

export default FamousPlayers
