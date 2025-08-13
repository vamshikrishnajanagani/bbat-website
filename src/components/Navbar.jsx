import { Link, useLocation } from 'react-router-dom'
import './Navbar.css'

const Navbar = () => {
  const location = useLocation()

  const isActive = (path) => location.pathname === path

  return (
    <nav className="navbar">
      <div className="navbar-container">
        <div className="navbar-brand">
          <Link to="/" className="brand-logo">
            <span className="brand-text">BBAT</span>
            <span className="brand-subtitle">Ball Badminton Association of Telangana</span>
          </Link>
        </div>
        
        <div className="navbar-menu">
          <Link 
            to="/" 
            className={`nav-link ${isActive('/') ? 'active' : ''}`}
          >
            Home
          </Link>
          <Link 
            to="/registration" 
            className={`nav-link ${isActive('/registration') ? 'active' : ''}`}
          >
            Player Registration
          </Link>
          <Link 
            to="/scores" 
            className={`nav-link ${isActive('/scores') ? 'active' : ''}`}
          >
            Recent Scores
          </Link>
          <Link 
            to="/history" 
            className={`nav-link ${isActive('/history') ? 'active' : ''}`}
          >
            Match History
          </Link>
          <Link 
            to="/players" 
            className={`nav-link ${isActive('/players') ? 'active' : ''}`}
          >
            Famous Players
          </Link>
        </div>
      </div>
    </nav>
  )
}

export default Navbar
