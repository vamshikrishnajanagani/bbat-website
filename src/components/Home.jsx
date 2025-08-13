import { Link } from 'react-router-dom'
import './Home.css'

const Home = () => {
  return (
    <div className="home">
      {/* Hero Section */}
      <section className="hero">
        <div className="hero-content">
          <h1 className="hero-title">
            Welcome to Ball Badminton Association of Telangana
          </h1>
          <p className="hero-subtitle">
            Promoting excellence in ball badminton across Telangana state
          </p>
          <div className="hero-buttons">
            <Link to="/registration" className="btn btn-primary">
              Register as Player
            </Link>
            <Link to="/scores" className="btn btn-secondary">
              View Recent Scores
            </Link>
          </div>
        </div>
        <div className="hero-image">
          <div className="badminton-court">
            <div className="court-lines"></div>
            <div className="net"></div>
          </div>
        </div>
      </section>

      {/* Features Section */}
      <section className="features">
        <div className="container">
          <h2 className="section-title">What We Offer</h2>
          <div className="features-grid">
            <div className="feature-card">
              <div className="feature-icon">🏸</div>
              <h3>Player Registration</h3>
              <p>Join our association and become part of the ball badminton community</p>
              <Link to="/registration" className="feature-link">Learn More →</Link>
            </div>
            
            <div className="feature-card">
              <div className="feature-icon">📊</div>
              <h3>Live Scores</h3>
              <p>Stay updated with real-time match scores and tournament results</p>
              <Link to="/scores" className="feature-link">View Scores →</Link>
            </div>
            
            <div className="feature-card">
              <div className="feature-icon">📚</div>
              <h3>Match History</h3>
              <p>Explore comprehensive records of past matches and tournaments</p>
              <Link to="/history" className="feature-link">Browse History →</Link>
            </div>
            
            <div className="feature-card">
              <div className="feature-icon">⭐</div>
              <h3>Famous Players</h3>
              <p>Discover the legends and rising stars of ball badminton</p>
              <Link to="/players" className="feature-link">Meet Players →</Link>
            </div>
          </div>
        </div>
      </section>

      {/* About Section */}
      <section className="about">
        <div className="container">
          <div className="about-content">
            <div className="about-text">
              <h2>About BBAT</h2>
              <p>
                The Ball Badminton Association of Telangana (BBAT) is the official governing body 
                for ball badminton in Telangana state. We are affiliated with the Ball Badminton 
                Federation of India (BBFI), which is recognized by the Government of India.
              </p>
              <p>
                Our mission is to promote, develop, and popularize ball badminton across Telangana, 
                organizing tournaments, training programs, and identifying talented players for 
                national and international competitions.
              </p>
            </div>
            <div className="about-stats">
              <div className="stat-item">
                <div className="stat-number">500+</div>
                <div className="stat-label">Registered Players</div>
              </div>
              <div className="stat-item">
                <div className="stat-number">50+</div>
                <div className="stat-label">Tournaments</div>
              </div>
              <div className="stat-item">
                <div className="stat-number">25+</div>
                <div className="stat-label">Districts</div>
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* Quick Actions */}
      <section className="quick-actions">
        <div className="container">
          <h2>Quick Actions</h2>
          <div className="actions-grid">
            <Link to="/registration" className="action-card">
              <h3>New Player Registration</h3>
              <p>Join our association today</p>
            </Link>
            <Link to="/scores" className="action-card">
              <h3>Latest Match Results</h3>
              <p>Check recent scores</p>
            </Link>
            <Link to="/history" className="action-card">
              <h3>Tournament Archives</h3>
              <p>Browse past events</p>
            </Link>
          </div>
        </div>
      </section>
    </div>
  )
}

export default Home
