-- Enhanced Database Constraints and Indexes
-- Version: 3.0.0
-- Description: Adds enhanced foreign key constraints, additional indexes, and validation constraints

-- Add foreign key constraints with proper naming
ALTER TABLE achievements 
DROP CONSTRAINT IF EXISTS achievements_player_id_fkey,
ADD CONSTRAINT fk_achievement_player 
    FOREIGN KEY (player_id) REFERENCES players(id) ON DELETE CASCADE;

-- Add tournament foreign key to achievements (if tournament_id exists)
-- Note: This will be handled when tournaments are properly linked

-- Add foreign key constraint for player statistics
ALTER TABLE player_statistics 
DROP CONSTRAINT IF EXISTS player_statistics_player_id_fkey,
ADD CONSTRAINT fk_player_statistics_player 
    FOREIGN KEY (player_id) REFERENCES players(id) ON DELETE CASCADE;

-- Ensure unique constraint on player_statistics.player_id
ALTER TABLE player_statistics 
DROP CONSTRAINT IF EXISTS uk_player_statistics_player,
ADD CONSTRAINT uk_player_statistics_player UNIQUE (player_id);

-- Add foreign key constraints for tournament registrations
ALTER TABLE tournament_registrations 
DROP CONSTRAINT IF EXISTS tournament_registrations_tournament_id_fkey,
ADD CONSTRAINT fk_tournament_registration_tournament 
    FOREIGN KEY (tournament_id) REFERENCES tournaments(id) ON DELETE CASCADE;

ALTER TABLE tournament_registrations 
DROP CONSTRAINT IF EXISTS tournament_registrations_player_id_fkey,
ADD CONSTRAINT fk_tournament_registration_player 
    FOREIGN KEY (player_id) REFERENCES players(id) ON DELETE CASCADE;

-- Add foreign key constraint for tournaments to districts
ALTER TABLE tournaments 
DROP CONSTRAINT IF EXISTS tournaments_district_id_fkey,
ADD CONSTRAINT fk_tournament_district 
    FOREIGN KEY (district_id) REFERENCES districts(id);

-- Add foreign key constraint for players to districts
ALTER TABLE players 
DROP CONSTRAINT IF EXISTS players_district_id_fkey,
ADD CONSTRAINT fk_player_district 
    FOREIGN KEY (district_id) REFERENCES districts(id);

-- Add foreign key constraint for news articles to categories
ALTER TABLE news_articles 
DROP CONSTRAINT IF EXISTS news_articles_category_id_fkey,
ADD CONSTRAINT fk_news_article_category 
    FOREIGN KEY (category_id) REFERENCES news_categories(id);

-- Add foreign key constraint for media items to galleries
ALTER TABLE media_items 
DROP CONSTRAINT IF EXISTS media_items_gallery_id_fkey,
ADD CONSTRAINT fk_media_item_gallery 
    FOREIGN KEY (gallery_id) REFERENCES media_galleries(id) ON DELETE CASCADE;

-- Add additional indexes for achievements
CREATE INDEX IF NOT EXISTS idx_achievements_player ON achievements(player_id);
CREATE INDEX IF NOT EXISTS idx_achievements_tournament ON achievements(tournament_id);
CREATE INDEX IF NOT EXISTS idx_achievements_level ON achievements(level);
CREATE INDEX IF NOT EXISTS idx_achievements_date ON achievements(achievement_date);
CREATE INDEX IF NOT EXISTS idx_achievements_verified ON achievements(is_verified);

-- Add additional indexes for player statistics
CREATE UNIQUE INDEX IF NOT EXISTS idx_player_statistics_player ON player_statistics(player_id);
CREATE INDEX IF NOT EXISTS idx_player_statistics_ranking ON player_statistics(current_ranking);
CREATE INDEX IF NOT EXISTS idx_player_statistics_points ON player_statistics(total_points);

-- Add additional indexes for tournament registrations
CREATE INDEX IF NOT EXISTS idx_tournament_registrations_tournament ON tournament_registrations(tournament_id);
CREATE INDEX IF NOT EXISTS idx_tournament_registrations_player ON tournament_registrations(player_id);
CREATE INDEX IF NOT EXISTS idx_tournament_registrations_status ON tournament_registrations(status);
CREATE INDEX IF NOT EXISTS idx_tournament_registrations_payment ON tournament_registrations(payment_status);
CREATE INDEX IF NOT EXISTS idx_tournament_registrations_date ON tournament_registrations(registration_date);

-- Add additional indexes for media items
CREATE INDEX IF NOT EXISTS idx_media_items_gallery ON media_items(gallery_id);
CREATE INDEX IF NOT EXISTS idx_media_items_type ON media_items(media_type);
CREATE INDEX IF NOT EXISTS idx_media_items_active ON media_items(is_active);
CREATE INDEX IF NOT EXISTS idx_media_items_sort ON media_items(sort_order);

-- Add check constraints for data validation
ALTER TABLE tournaments 
ADD CONSTRAINT chk_tournament_dates 
    CHECK (start_date <= end_date);

ALTER TABLE tournaments 
ADD CONSTRAINT chk_tournament_registration_dates 
    CHECK (registration_start_date IS NULL OR registration_end_date IS NULL OR registration_start_date <= registration_end_date);

ALTER TABLE tournaments 
ADD CONSTRAINT chk_tournament_max_participants 
    CHECK (max_participants IS NULL OR max_participants > 0);

ALTER TABLE tournaments 
ADD CONSTRAINT chk_tournament_entry_fee 
    CHECK (entry_fee >= 0);

ALTER TABLE tournaments 
ADD CONSTRAINT chk_tournament_prize_money 
    CHECK (prize_money >= 0);

-- Add check constraints for player statistics
ALTER TABLE player_statistics 
ADD CONSTRAINT chk_player_stats_matches 
    CHECK (matches_played >= 0 AND matches_won >= 0 AND matches_won <= matches_played);

ALTER TABLE player_statistics 
ADD CONSTRAINT chk_player_stats_tournaments 
    CHECK (tournaments_participated >= 0 AND tournaments_won >= 0 AND tournaments_won <= tournaments_participated);

ALTER TABLE player_statistics 
ADD CONSTRAINT chk_player_stats_win_percentage 
    CHECK (win_percentage >= 0 AND win_percentage <= 100);

ALTER TABLE player_statistics 
ADD CONSTRAINT chk_player_stats_rankings 
    CHECK (current_ranking IS NULL OR current_ranking > 0);

ALTER TABLE player_statistics 
ADD CONSTRAINT chk_player_stats_best_ranking 
    CHECK (best_ranking IS NULL OR best_ranking > 0);

ALTER TABLE player_statistics 
ADD CONSTRAINT chk_player_stats_points 
    CHECK (total_points >= 0);

-- Add check constraints for achievements
ALTER TABLE achievements 
ADD CONSTRAINT chk_achievement_position 
    CHECK (position IS NULL OR position > 0);

-- Add check constraints for media items
ALTER TABLE media_items 
ADD CONSTRAINT chk_media_item_file_size 
    CHECK (file_size IS NULL OR file_size >= 0);

ALTER TABLE media_items 
ADD CONSTRAINT chk_media_item_sort_order 
    CHECK (sort_order >= 0);

-- Add check constraints for downloads
ALTER TABLE downloads 
ADD CONSTRAINT chk_download_file_size 
    CHECK (file_size IS NULL OR file_size >= 0);

ALTER TABLE downloads 
ADD CONSTRAINT chk_download_count 
    CHECK (download_count >= 0);

-- Add check constraints for news articles
ALTER TABLE news_articles 
ADD CONSTRAINT chk_news_view_count 
    CHECK (view_count >= 0);

-- Update the trigger function to handle audit fields properly
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Add comments to tables for documentation
COMMENT ON TABLE achievements IS 'Player achievements and accomplishments with tournament references';
COMMENT ON TABLE player_statistics IS 'Player performance metrics and rankings';
COMMENT ON TABLE tournament_registrations IS 'Tournament registration records with payment tracking';
COMMENT ON TABLE tournaments IS 'Tournament information with validation constraints';
COMMENT ON TABLE players IS 'Player profiles with district associations';
COMMENT ON TABLE districts IS 'Telangana state districts with geographic information';
COMMENT ON TABLE members IS 'Association body members with hierarchy';
COMMENT ON TABLE news_articles IS 'News articles with categorization and multilingual support';
COMMENT ON TABLE news_categories IS 'News categorization system';
COMMENT ON TABLE media_galleries IS 'Media gallery containers';
COMMENT ON TABLE media_items IS 'Individual media files within galleries';
COMMENT ON TABLE downloads IS 'Downloadable resources and documents';

COMMIT;