import { Routes, Route } from 'react-router-dom'
import './App.css'
import Navbar from './components/Navbar'
import Home from './components/Home'
import PlayerRegistration from './components/PlayerRegistration'
import RecentScores from './components/RecentScores'
import MatchHistory from './components/MatchHistory'
import FamousPlayers from './components/FamousPlayers'
import Footer from './components/Footer'

function App() {
  return (
    <div className="App">
      <Navbar />
      <main className="main-content">
        <Routes>
          <Route path="/" element={<Home />} />
          <Route path="/registration" element={<PlayerRegistration />} />
          <Route path="/scores" element={<RecentScores />} />
          <Route path="/history" element={<MatchHistory />} />
          <Route path="/players" element={<FamousPlayers />} />
        </Routes>
      </main>
      <Footer />
    </div>
  )
}

export default App
