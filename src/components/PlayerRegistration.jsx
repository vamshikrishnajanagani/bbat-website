import { useState } from 'react'
import './PlayerRegistration.css'

const PlayerRegistration = () => {
  const [formData, setFormData] = useState({
    firstName: '',
    lastName: '',
    dateOfBirth: '',
    gender: '',
    email: '',
    phone: '',
    address: '',
    city: '',
    district: '',
    pincode: '',
    emergencyContact: '',
    emergencyPhone: '',
    playingLevel: '',
    experience: '',
    achievements: '',
    preferredPosition: '',
    medicalConditions: '',
    photo: null
  })

  const [isSubmitting, setIsSubmitting] = useState(false)
  const [submitSuccess, setSubmitSuccess] = useState(false)

  const handleInputChange = (e) => {
    const { name, value } = e.target
    setFormData(prev => ({
      ...prev,
      [name]: value
    }))
  }

  const handleFileChange = (e) => {
    const file = e.target.files[0]
    setFormData(prev => ({
      ...prev,
      photo: file
    }))
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    setIsSubmitting(true)
    
    // Simulate API call
    await new Promise(resolve => setTimeout(resolve, 2000))
    
    setIsSubmitting(false)
    setSubmitSuccess(true)
    
    // Reset form after 3 seconds
    setTimeout(() => {
      setSubmitSuccess(false)
      setFormData({
        firstName: '',
        lastName: '',
        dateOfBirth: '',
        gender: '',
        email: '',
        phone: '',
        address: '',
        city: '',
        district: '',
        pincode: '',
        emergencyContact: '',
        emergencyPhone: '',
        playingLevel: '',
        experience: '',
        achievements: '',
        preferredPosition: '',
        medicalConditions: '',
        photo: null
      })
    }, 3000)
  }

  if (submitSuccess) {
    return (
      <div className="registration-success">
        <div className="success-content">
          <div className="success-icon">✅</div>
          <h2>Registration Successful!</h2>
          <p>Thank you for registering with BBAT. We'll review your application and contact you soon.</p>
          <p>Your registration ID: BBAT-{Date.now().toString().slice(-6)}</p>
        </div>
      </div>
    )
  }

  return (
    <div className="player-registration">
      <div className="container">
        <div className="registration-header">
          <h1>Player Registration</h1>
          <p>Join the Ball Badminton Association of Telangana and become part of our growing community</p>
        </div>

        <form onSubmit={handleSubmit} className="registration-form">
          <div className="form-section">
            <h3>Personal Information</h3>
            <div className="form-grid">
              <div className="form-group">
                <label htmlFor="firstName">First Name *</label>
                <input
                  type="text"
                  id="firstName"
                  name="firstName"
                  value={formData.firstName}
                  onChange={handleInputChange}
                  required
                  placeholder="Enter your first name"
                />
              </div>

              <div className="form-group">
                <label htmlFor="lastName">Last Name *</label>
                <input
                  type="text"
                  id="lastName"
                  name="lastName"
                  value={formData.lastName}
                  onChange={handleInputChange}
                  required
                  placeholder="Enter your last name"
                />
              </div>

              <div className="form-group">
                <label htmlFor="dateOfBirth">Date of Birth *</label>
                <input
                  type="date"
                  id="dateOfBirth"
                  name="dateOfBirth"
                  value={formData.dateOfBirth}
                  onChange={handleInputChange}
                  required
                />
              </div>

              <div className="form-group">
                <label htmlFor="gender">Gender *</label>
                <select
                  id="gender"
                  name="gender"
                  value={formData.gender}
                  onChange={handleInputChange}
                  required
                >
                  <option value="">Select Gender</option>
                  <option value="male">Male</option>
                  <option value="female">Female</option>
                  <option value="other">Other</option>
                </select>
              </div>
            </div>
          </div>

          <div className="form-section">
            <h3>Contact Information</h3>
            <div className="form-grid">
              <div className="form-group">
                <label htmlFor="email">Email Address *</label>
                <input
                  type="email"
                  id="email"
                  name="email"
                  value={formData.email}
                  onChange={handleInputChange}
                  required
                  placeholder="Enter your email address"
                />
              </div>

              <div className="form-group">
                <label htmlFor="phone">Phone Number *</label>
                <input
                  type="tel"
                  id="phone"
                  name="phone"
                  value={formData.phone}
                  onChange={handleInputChange}
                  required
                  placeholder="Enter your phone number"
                />
              </div>

              <div className="form-group full-width">
                <label htmlFor="address">Address *</label>
                <textarea
                  id="address"
                  name="address"
                  value={formData.address}
                  onChange={handleInputChange}
                  required
                  placeholder="Enter your complete address"
                  rows="3"
                />
              </div>

              <div className="form-group">
                <label htmlFor="city">City *</label>
                <input
                  type="text"
                  id="city"
                  name="city"
                  value={formData.city}
                  onChange={handleInputChange}
                  required
                  placeholder="Enter your city"
                />
              </div>

              <div className="form-group">
                <label htmlFor="district">District *</label>
                <select
                  id="district"
                  name="district"
                  value={formData.district}
                  onChange={handleInputChange}
                  required
                >
                  <option value="">Select District</option>
                  <option value="hyderabad">Hyderabad</option>
                  <option value="warangal">Warangal</option>
                  <option value="karimnagar">Karimnagar</option>
                  <option value="nizamabad">Nizamabad</option>
                  <option value="adilabad">Adilabad</option>
                  <option value="khammam">Khammam</option>
                  <option value="nalgonda">Nalgonda</option>
                  <option value="medak">Medak</option>
                  <option value="rangareddy">Rangareddy</option>
                  <option value="mahabubnagar">Mahabubnagar</option>
                </select>
              </div>

              <div className="form-group">
                <label htmlFor="pincode">Pincode *</label>
                <input
                  type="text"
                  id="pincode"
                  name="pincode"
                  value={formData.pincode}
                  onChange={handleInputChange}
                  required
                  placeholder="Enter pincode"
                  pattern="[0-9]{6}"
                />
              </div>
            </div>
          </div>

          <div className="form-section">
            <h3>Emergency Contact</h3>
            <div className="form-grid">
              <div className="form-group">
                <label htmlFor="emergencyContact">Emergency Contact Name *</label>
                <input
                  type="text"
                  id="emergencyContact"
                  name="emergencyContact"
                  value={formData.emergencyContact}
                  onChange={handleInputChange}
                  required
                  placeholder="Enter emergency contact name"
                />
              </div>

              <div className="form-group">
                <label htmlFor="emergencyPhone">Emergency Contact Phone *</label>
                <input
                  type="tel"
                  id="emergencyPhone"
                  name="emergencyPhone"
                  value={formData.emergencyPhone}
                  onChange={handleInputChange}
                  required
                  placeholder="Enter emergency contact phone"
                />
              </div>
            </div>
          </div>

          <div className="form-section">
            <h3>Playing Information</h3>
            <div className="form-grid">
              <div className="form-group">
                <label htmlFor="playingLevel">Playing Level *</label>
                <select
                  id="playingLevel"
                  name="playingLevel"
                  value={formData.playingLevel}
                  onChange={handleInputChange}
                  required
                >
                  <option value="">Select Playing Level</option>
                  <option value="beginner">Beginner</option>
                  <option value="intermediate">Intermediate</option>
                  <option value="advanced">Advanced</option>
                  <option value="professional">Professional</option>
                </select>
              </div>

              <div className="form-group">
                <label htmlFor="experience">Years of Experience</label>
                <input
                  type="number"
                  id="experience"
                  name="experience"
                  value={formData.experience}
                  onChange={handleInputChange}
                  min="0"
                  max="50"
                  placeholder="Enter years of experience"
                />
              </div>

              <div className="form-group">
                <label htmlFor="preferredPosition">Preferred Position</label>
                <select
                  id="preferredPosition"
                  name="preferredPosition"
                  value={formData.preferredPosition}
                  onChange={handleInputChange}
                >
                  <option value="">Select Position</option>
                  <option value="singles">Singles</option>
                  <option value="doubles">Doubles</option>
                  <option value="mixed-doubles">Mixed Doubles</option>
                  <option value="all">All Positions</option>
                </select>
              </div>

              <div className="form-group full-width">
                <label htmlFor="achievements">Previous Achievements</label>
                <textarea
                  id="achievements"
                  name="achievements"
                  value={formData.achievements}
                  onChange={handleInputChange}
                  placeholder="List your previous achievements, tournaments won, etc."
                  rows="3"
                />
              </div>
            </div>
          </div>

          <div className="form-section">
            <h3>Additional Information</h3>
            <div className="form-grid">
              <div className="form-group full-width">
                <label htmlFor="medicalConditions">Medical Conditions (if any)</label>
                <textarea
                  id="medicalConditions"
                  name="medicalConditions"
                  value={formData.medicalConditions}
                  onChange={handleInputChange}
                  placeholder="Please mention any medical conditions or allergies"
                  rows="3"
                />
              </div>

              <div className="form-group full-width">
                <label htmlFor="photo">Profile Photo</label>
                <input
                  type="file"
                  id="photo"
                  name="photo"
                  onChange={handleFileChange}
                  accept="image/*"
                />
                <small>Upload a recent passport size photo (JPG, PNG, max 2MB)</small>
              </div>
            </div>
          </div>

          <div className="form-actions">
            <button
              type="submit"
              className="btn-submit"
              disabled={isSubmitting}
            >
              {isSubmitting ? 'Submitting...' : 'Submit Registration'}
            </button>
          </div>
        </form>
      </div>
    </div>
  )
}

export default PlayerRegistration
