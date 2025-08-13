import './Footer.css'

const Footer = () => {
  const currentYear = new Date().getFullYear()

  return (
    <footer className="footer">
      <div className="footer-content">
        <div className="footer-main">
          <div className="footer-section">
            <h3>BBAT</h3>
            <p className="footer-description">
              Ball Badminton Association of Telangana is the official governing body 
              for ball badminton in Telangana state, promoting excellence and 
              development of the sport.
            </p>
            <div className="social-links">
              <a href="#" className="social-link" aria-label="Facebook">
                📘
              </a>
              <a href="#" className="social-link" aria-label="Twitter">
                🐦
              </a>
              <a href="#" className="social-link" aria-label="Instagram">
                📷
              </a>
              <a href="#" className="social-link" aria-label="YouTube">
                📺
              </a>
            </div>
          </div>

          <div className="footer-section">
            <h4>Quick Links</h4>
            <ul className="footer-links">
              <li><a href="/">Home</a></li>
              <li><a href="/registration">Player Registration</a></li>
              <li><a href="/scores">Recent Scores</a></li>
              <li><a href="/history">Match History</a></li>
              <li><a href="/players">Famous Players</a></li>
            </ul>
          </div>

          <div className="footer-section">
            <h4>Contact Information</h4>
            <div className="contact-info">
              <div className="contact-item">
                <span className="contact-icon">📍</span>
                <div>
                  <p>BBAT Office</p>
                  <p>Gachibowli Indoor Stadium</p>
                  <p>Hyderabad, Telangana 500032</p>
                </div>
              </div>
              <div className="contact-item">
                <span className="contact-icon">📞</span>
                <div>
                  <p>+91 98765 43210</p>
                  <p>+91 98765 43211</p>
                </div>
              </div>
              <div className="contact-item">
                <span className="contact-icon">✉️</span>
                <div>
                  <p>info@bbat.org.in</p>
                  <p>secretary@bbat.org.in</p>
                </div>
              </div>
            </div>
          </div>

          <div className="footer-section">
            <h4>Office Hours</h4>
            <div className="office-hours">
              <div className="hours-item">
                <span>Monday - Friday:</span>
                <span>9:00 AM - 6:00 PM</span>
              </div>
              <div className="hours-item">
                <span>Saturday:</span>
                <span>9:00 AM - 2:00 PM</span>
              </div>
              <div className="hours-item">
                <span>Sunday:</span>
                <span>Closed</span>
              </div>
            </div>
          </div>
        </div>

        <div className="footer-bottom">
          <div className="footer-bottom-content">
            <div className="copyright">
              <p>&copy; {currentYear} Ball Badminton Association of Telangana. All rights reserved.</p>
            </div>
            <div className="footer-bottom-links">
              <a href="#">Privacy Policy</a>
              <a href="#">Terms of Service</a>
              <a href="#">Disclaimer</a>
            </div>
          </div>
        </div>
      </div>

      <div className="footer-affiliation">
        <div className="affiliation-content">
          <p>
            <strong>Affiliated with:</strong> Ball Badminton Federation of India (BBFI)
          </p>
          <p>
            <strong>Recognized by:</strong> Government of India, Ministry of Youth Affairs & Sports
          </p>
        </div>
      </div>
    </footer>
  )
}

export default Footer
