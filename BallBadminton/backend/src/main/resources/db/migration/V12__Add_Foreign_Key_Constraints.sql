-- Migration V12: Add Foreign Key Constraints and Additional Indexes
-- Adds foreign key constraints that reference tables created later in the schema
-- Author: Telangana Ball Badminton Association
-- Version: 1.0.0

-- Add foreign key constraint for achievements table to reference tournaments
-- This was deferred because tournaments table is created after achievements table
ALTER TABLE achievements 
ADD CONSTRAINT fk_achievement_tournament 
FOREIGN KEY (tournament_id) REFERENCES tournaments(id);

-- Add additional performance indexes
CREATE INDEX IF NOT EXISTS idx_achievements_tournament_verified ON achievements(tournament_id, is_verified);
CREATE INDEX IF NOT EXISTS idx_player_statistics_updated ON player_statistics(last_updated DESC);
CREATE INDEX IF NOT EXISTS idx_tournaments_registration_dates ON tournaments(registration_start_date, registration_end_date);
CREATE INDEX IF NOT EXISTS idx_news_articles_scheduled ON news_articles(scheduled_publication_date) WHERE scheduled_publication_date IS NOT NULL;

-- Add composite indexes for common query patterns
CREATE INDEX IF NOT EXISTS idx_players_district_category ON players(district_id, category) WHERE is_active = true;
CREATE INDEX IF NOT EXISTS idx_tournaments_district_status ON tournaments(district_id, status);
CREATE INDEX IF NOT EXISTS idx_news_articles_category_published ON news_articles(category_id, is_published, published_at DESC);

-- Add partial indexes for better performance on filtered queries
CREATE INDEX IF NOT EXISTS idx_members_active_prominent ON members(hierarchy_level) WHERE is_active = true AND is_prominent = true;
CREATE INDEX IF NOT EXISTS idx_players_prominent_active ON players(name) WHERE is_prominent = true AND is_active = true;
CREATE INDEX IF NOT EXISTS idx_tournaments_featured_upcoming ON tournaments(start_date) WHERE is_featured = true AND status IN ('UPCOMING', 'REGISTRATION_OPEN');

-- Add comments for documentation
COMMENT ON INDEX idx_achievements_tournament_verified IS 'Index for querying verified achievements by tournament';
COMMENT ON INDEX idx_player_statistics_updated IS 'Index for finding recently updated player statistics';
COMMENT ON INDEX idx_tournaments_registration_dates IS 'Index for tournament registration period queries';
COMMENT ON INDEX idx_news_articles_scheduled IS 'Index for scheduled publication queries';
COMMENT ON INDEX idx_players_district_category IS 'Composite index for player filtering by district and category';
COMMENT ON INDEX idx_tournaments_district_status IS 'Composite index for tournament queries by district and status';
COMMENT ON INDEX idx_news_articles_category_published IS 'Composite index for published news by category';
COMMENT ON INDEX idx_members_active_prominent IS 'Partial index for active prominent members';
COMMENT ON INDEX idx_players_prominent_active IS 'Partial index for prominent active players';
COMMENT ON INDEX idx_tournaments_featured_upcoming IS 'Partial index for featured upcoming tournaments';