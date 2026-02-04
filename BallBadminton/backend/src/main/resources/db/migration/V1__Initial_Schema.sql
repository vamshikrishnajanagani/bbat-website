-- Initial Database Schema for Telangana Ball Badminton Association Website
-- Version: 1.0.0
-- Description: Creates the foundational database structure for the application

-- Enable UUID extension for PostgreSQL
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Create audit columns function for tracking changes
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Users table for authentication matching JPA entity structure
CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    phone VARCHAR(15),
    active BOOLEAN NOT NULL DEFAULT true,
    account_non_expired BOOLEAN NOT NULL DEFAULT true,
    account_non_locked BOOLEAN NOT NULL DEFAULT true,
    credentials_non_expired BOOLEAN NOT NULL DEFAULT true,
    email_verified BOOLEAN NOT NULL DEFAULT false,
    last_login TIMESTAMP,
    password_changed_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(50),
    updated_by VARCHAR(50)
);

-- User roles table (using enum-based approach as per JPA entity)
CREATE TABLE user_roles (
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    role VARCHAR(50) NOT NULL CHECK (role IN ('SUPER_ADMIN', 'ADMIN', 'EDITOR', 'MODERATOR', 'USER')),
    PRIMARY KEY (user_id, role)
);

-- Districts table for geographic information
CREATE TABLE districts (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(100) NOT NULL,
    code VARCHAR(10) UNIQUE NOT NULL,
    headquarters VARCHAR(100),
    area_sq_km DECIMAL(10,2),
    population BIGINT,
    latitude DECIMAL(10,8),
    longitude DECIMAL(11,8),
    contact_person VARCHAR(100),
    contact_email VARCHAR(100),
    contact_phone VARCHAR(20),
    description TEXT,
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(50) DEFAULT 'system',
    updated_by VARCHAR(50) DEFAULT 'system'
);

-- Members table for association body management
CREATE TABLE members (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(100) NOT NULL,
    position VARCHAR(100) NOT NULL,
    email VARCHAR(100),
    phone VARCHAR(20),
    biography TEXT,
    photo_url VARCHAR(500),
    hierarchy_level INTEGER DEFAULT 0,
    tenure_start_date DATE,
    tenure_end_date DATE,
    is_active BOOLEAN DEFAULT true,
    is_prominent BOOLEAN DEFAULT false,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(50) DEFAULT 'system',
    updated_by VARCHAR(50) DEFAULT 'system'
);

-- Players table for player profile management
CREATE TABLE players (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(100) NOT NULL,
    date_of_birth DATE,
    gender VARCHAR(10) CHECK (gender IN ('MALE', 'FEMALE', 'OTHER')),
    district_id UUID REFERENCES districts(id),
    category VARCHAR(20) CHECK (category IN ('MEN', 'WOMEN', 'JUNIOR', 'SENIOR', 'VETERANS')),
    profile_photo_url VARCHAR(500),
    contact_email VARCHAR(100),
    contact_phone VARCHAR(20),
    address TEXT,
    is_prominent BOOLEAN DEFAULT false,
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(50) DEFAULT 'system',
    updated_by VARCHAR(50) DEFAULT 'system'
);

-- Player statistics table
CREATE TABLE player_statistics (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    player_id UUID REFERENCES players(id) ON DELETE CASCADE,
    matches_played INTEGER DEFAULT 0,
    matches_won INTEGER DEFAULT 0,
    tournaments_participated INTEGER DEFAULT 0,
    tournaments_won INTEGER DEFAULT 0,
    win_percentage DECIMAL(5,2) DEFAULT 0.00,
    current_ranking INTEGER,
    best_ranking INTEGER,
    total_points INTEGER DEFAULT 0,
    last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(50) DEFAULT 'system',
    updated_by VARCHAR(50) DEFAULT 'system'
);

-- Achievements table for player accomplishments
CREATE TABLE achievements (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    player_id UUID NOT NULL REFERENCES players(id) ON DELETE CASCADE,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    achievement_date DATE,
    tournament_id UUID REFERENCES tournaments(id),
    category VARCHAR(50),
    level VARCHAR(50) CHECK (level IN ('DISTRICT', 'STATE', 'NATIONAL', 'INTERNATIONAL')),
    position INTEGER CHECK (position >= 1), -- 1st, 2nd, 3rd, etc.
    is_verified BOOLEAN DEFAULT false,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(50) DEFAULT 'system',
    updated_by VARCHAR(50) DEFAULT 'system'
);

-- Tournaments table for tournament management
CREATE TABLE tournaments (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(200) NOT NULL,
    description TEXT,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    venue VARCHAR(200),
    district_id UUID REFERENCES districts(id),
    registration_start_date DATE,
    registration_end_date DATE,
    max_participants INTEGER CHECK (max_participants >= 1),
    entry_fee DECIMAL(10,2) DEFAULT 0.00 CHECK (entry_fee >= 0),
    prize_money DECIMAL(12,2) DEFAULT 0.00 CHECK (prize_money >= 0),
    status VARCHAR(20) DEFAULT 'UPCOMING' CHECK (status IN ('UPCOMING', 'REGISTRATION_OPEN', 'REGISTRATION_CLOSED', 'ONGOING', 'COMPLETED', 'CANCELLED')),
    tournament_type VARCHAR(50) CHECK (tournament_type IN ('SINGLES', 'DOUBLES', 'MIXED', 'TEAM')),
    age_category VARCHAR(50),
    gender_category VARCHAR(20),
    is_featured BOOLEAN DEFAULT false,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(50) DEFAULT 'system',
    updated_by VARCHAR(50) DEFAULT 'system',
    CONSTRAINT chk_tournament_dates CHECK (start_date <= end_date),
    CONSTRAINT chk_registration_dates CHECK (registration_start_date IS NULL OR registration_end_date IS NULL OR registration_start_date <= registration_end_date)
);

-- Tournament registrations table
CREATE TABLE tournament_registrations (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tournament_id UUID NOT NULL REFERENCES tournaments(id) ON DELETE CASCADE,
    player_id UUID NOT NULL REFERENCES players(id) ON DELETE CASCADE,
    registration_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    payment_status VARCHAR(20) DEFAULT 'PENDING' CHECK (payment_status IN ('PENDING', 'PAID', 'REFUNDED', 'CANCELLED')),
    payment_amount DECIMAL(10,2),
    payment_reference VARCHAR(100),
    status VARCHAR(20) DEFAULT 'REGISTERED' CHECK (status IN ('REGISTERED', 'CONFIRMED', 'WITHDRAWN', 'DISQUALIFIED')),
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(50) DEFAULT 'system',
    updated_by VARCHAR(50) DEFAULT 'system',
    CONSTRAINT uk_tournament_player UNIQUE(tournament_id, player_id)
);

-- News categories table
CREATE TABLE news_categories (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(100) NOT NULL,
    description TEXT,
    slug VARCHAR(100) UNIQUE NOT NULL,
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(50) DEFAULT 'system',
    updated_by VARCHAR(50) DEFAULT 'system'
);

-- News articles table with scheduled publication support
CREATE TABLE news_articles (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    title VARCHAR(300) NOT NULL,
    slug VARCHAR(300) UNIQUE NOT NULL,
    summary TEXT,
    content TEXT NOT NULL,
    featured_image_url VARCHAR(500),
    category_id UUID REFERENCES news_categories(id),
    author VARCHAR(100),
    published_at TIMESTAMP,
    scheduled_publication_date TIMESTAMP,
    is_published BOOLEAN DEFAULT false,
    is_featured BOOLEAN DEFAULT false,
    view_count INTEGER DEFAULT 0,
    language VARCHAR(5) DEFAULT 'ENGLISH' CHECK (language IN ('ENGLISH', 'TELUGU')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(50) DEFAULT 'system',
    updated_by VARCHAR(50) DEFAULT 'system'
);

-- Media galleries table
CREATE TABLE media_galleries (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    title VARCHAR(200) NOT NULL,
    description TEXT,
    gallery_type VARCHAR(20) CHECK (gallery_type IN ('PHOTO', 'VIDEO', 'MIXED')),
    cover_image_url VARCHAR(500),
    is_featured BOOLEAN DEFAULT false,
    is_public BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(50) DEFAULT 'system',
    updated_by VARCHAR(50) DEFAULT 'system'
);

-- Media items table
CREATE TABLE media_items (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    gallery_id UUID NOT NULL REFERENCES media_galleries(id) ON DELETE CASCADE,
    title VARCHAR(200),
    description TEXT,
    file_url VARCHAR(500) NOT NULL,
    thumbnail_url VARCHAR(500),
    media_type VARCHAR(20) CHECK (media_type IN ('IMAGE', 'VIDEO', 'AUDIO')),
    file_size BIGINT CHECK (file_size >= 0),
    mime_type VARCHAR(100),
    sort_order INTEGER DEFAULT 0 CHECK (sort_order >= 0),
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(50) DEFAULT 'system',
    updated_by VARCHAR(50) DEFAULT 'system'
);

-- Downloads/Resources table
CREATE TABLE downloads (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    title VARCHAR(200) NOT NULL,
    description TEXT,
    file_url VARCHAR(500) NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    file_size BIGINT CHECK (file_size >= 0),
    mime_type VARCHAR(100),
    category VARCHAR(100),
    download_count INTEGER DEFAULT 0 CHECK (download_count >= 0),
    is_public BOOLEAN DEFAULT true,
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(50) DEFAULT 'system',
    updated_by VARCHAR(50) DEFAULT 'system'
);

-- Privacy consent table for GDPR compliance
CREATE TABLE privacy_consents (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    consent_type VARCHAR(50) NOT NULL,
    consent_given BOOLEAN NOT NULL,
    consent_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ip_address VARCHAR(45),
    user_agent VARCHAR(500),
    privacy_policy_version VARCHAR(20),
    revoked BOOLEAN NOT NULL DEFAULT false,
    revoked_date TIMESTAMP,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(50) DEFAULT 'system',
    updated_by VARCHAR(50) DEFAULT 'system'
);

-- Data deletion requests table for GDPR compliance
CREATE TABLE data_deletion_requests (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    request_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING_VERIFICATION' CHECK (status IN ('PENDING_VERIFICATION', 'VERIFIED', 'SCHEDULED', 'PROCESSING', 'COMPLETED', 'CANCELLED', 'FAILED')),
    deletion_type VARCHAR(20) NOT NULL CHECK (deletion_type IN ('FULL_ACCOUNT', 'PERSONAL_DATA_ONLY', 'SPECIFIC_DATA')),
    reason TEXT,
    scheduled_date TIMESTAMP,
    completed_date TIMESTAMP,
    ip_address VARCHAR(45),
    verification_code VARCHAR(100),
    verified BOOLEAN NOT NULL DEFAULT false,
    verified_date TIMESTAMP,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(50) DEFAULT 'system',
    updated_by VARCHAR(50) DEFAULT 'system'
);

-- Data export requests table for GDPR compliance
CREATE TABLE data_export_requests (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    request_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'PROCESSING', 'COMPLETED', 'FAILED', 'EXPIRED')),
    export_format VARCHAR(10) DEFAULT 'JSON',
    file_path VARCHAR(500),
    completed_date TIMESTAMP,
    expiry_date TIMESTAMP,
    ip_address VARCHAR(45),
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(50) DEFAULT 'system',
    updated_by VARCHAR(50) DEFAULT 'system'
);

-- Create indexes for better performance
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_active ON users(active);

CREATE INDEX idx_districts_code ON districts(code);
CREATE INDEX idx_districts_active ON districts(is_active);

CREATE INDEX idx_members_active ON members(is_active);
CREATE INDEX idx_members_prominent ON members(is_prominent);
CREATE INDEX idx_members_hierarchy ON members(hierarchy_level);

CREATE INDEX idx_players_district ON players(district_id);
CREATE INDEX idx_players_category ON players(category);
CREATE INDEX idx_players_prominent ON players(is_prominent);
CREATE INDEX idx_players_active ON players(is_active);

CREATE INDEX idx_player_statistics_player ON player_statistics(player_id);
CREATE INDEX idx_player_statistics_ranking ON player_statistics(current_ranking);
CREATE INDEX idx_player_statistics_points ON player_statistics(total_points);

CREATE INDEX idx_achievements_player ON achievements(player_id);
CREATE INDEX idx_achievements_tournament ON achievements(tournament_id);
CREATE INDEX idx_achievements_level ON achievements(level);
CREATE INDEX idx_achievements_date ON achievements(achievement_date);
CREATE INDEX idx_achievements_verified ON achievements(is_verified);

CREATE INDEX idx_tournaments_status ON tournaments(status);
CREATE INDEX idx_tournaments_dates ON tournaments(start_date, end_date);
CREATE INDEX idx_tournaments_district ON tournaments(district_id);
CREATE INDEX idx_tournaments_featured ON tournaments(is_featured);

CREATE INDEX idx_tournament_registrations_tournament ON tournament_registrations(tournament_id);
CREATE INDEX idx_tournament_registrations_player ON tournament_registrations(player_id);
CREATE INDEX idx_tournament_registrations_status ON tournament_registrations(status);
CREATE INDEX idx_tournament_registrations_payment ON tournament_registrations(payment_status);
CREATE INDEX idx_tournament_registrations_date ON tournament_registrations(registration_date);

CREATE INDEX idx_news_categories_slug ON news_categories(slug);
CREATE INDEX idx_news_categories_active ON news_categories(is_active);

CREATE INDEX idx_news_published ON news_articles(is_published);
CREATE INDEX idx_news_featured ON news_articles(is_featured);
CREATE INDEX idx_news_category ON news_articles(category_id);
CREATE INDEX idx_news_published_at ON news_articles(published_at);
CREATE INDEX idx_news_slug ON news_articles(slug);

CREATE INDEX idx_media_galleries_featured ON media_galleries(is_featured);
CREATE INDEX idx_media_galleries_public ON media_galleries(is_public);

CREATE INDEX idx_media_items_gallery ON media_items(gallery_id);
CREATE INDEX idx_media_items_type ON media_items(media_type);
CREATE INDEX idx_media_items_active ON media_items(is_active);
CREATE INDEX idx_media_items_sort ON media_items(sort_order);

CREATE INDEX idx_downloads_category ON downloads(category);
CREATE INDEX idx_downloads_public ON downloads(is_public);
CREATE INDEX idx_downloads_active ON downloads(is_active);

CREATE INDEX idx_privacy_consents_user ON privacy_consents(user_id);
CREATE INDEX idx_privacy_consents_type ON privacy_consents(consent_type);
CREATE INDEX idx_privacy_consents_date ON privacy_consents(consent_date);

CREATE INDEX idx_data_deletion_requests_user ON data_deletion_requests(user_id);
CREATE INDEX idx_data_deletion_requests_status ON data_deletion_requests(status);
CREATE INDEX idx_data_deletion_requests_date ON data_deletion_requests(request_date);

CREATE INDEX idx_data_export_requests_user ON data_export_requests(user_id);
CREATE INDEX idx_data_export_requests_status ON data_export_requests(status);
CREATE INDEX idx_data_export_requests_date ON data_export_requests(request_date);

-- Create triggers for updated_at columns
CREATE TRIGGER update_users_updated_at BEFORE UPDATE ON users FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_districts_updated_at BEFORE UPDATE ON districts FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_members_updated_at BEFORE UPDATE ON members FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_players_updated_at BEFORE UPDATE ON players FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_player_statistics_updated_at BEFORE UPDATE ON player_statistics FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_achievements_updated_at BEFORE UPDATE ON achievements FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_tournaments_updated_at BEFORE UPDATE ON tournaments FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_tournament_registrations_updated_at BEFORE UPDATE ON tournament_registrations FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_news_categories_updated_at BEFORE UPDATE ON news_categories FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_news_articles_updated_at BEFORE UPDATE ON news_articles FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_media_galleries_updated_at BEFORE UPDATE ON media_galleries FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_media_items_updated_at BEFORE UPDATE ON media_items FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_downloads_updated_at BEFORE UPDATE ON downloads FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_privacy_consents_updated_at BEFORE UPDATE ON privacy_consents FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_data_deletion_requests_updated_at BEFORE UPDATE ON data_deletion_requests FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_data_export_requests_updated_at BEFORE UPDATE ON data_export_requests FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- Insert default news categories
INSERT INTO news_categories (name, description, slug) VALUES
('General', 'General news and announcements', 'general'),
('Tournaments', 'Tournament-related news and updates', 'tournaments'),
('Players', 'Player achievements and profiles', 'players'),
('Association', 'Association news and updates', 'association'),
('Events', 'Upcoming events and activities', 'events');

-- Insert Telangana districts
INSERT INTO districts (name, code, headquarters, area_sq_km, population, latitude, longitude) VALUES
('Adilabad', 'ADB', 'Adilabad', 16128.00, 2741239, 19.6669, 78.5316),
('Bhadradri Kothagudem', 'BKG', 'Kothagudem', 7483.00, 1082916, 17.5501, 80.6193),
('Hyderabad', 'HYD', 'Hyderabad', 650.00, 6809970, 17.3850, 78.4867),
('Jagtial', 'JGT', 'Jagtial', 2951.00, 984781, 18.7894, 78.9113),
('Jangaon', 'JGN', 'Jangaon', 2175.00, 585506, 17.7243, 79.1514),
('Jayashankar Bhupalpally', 'JBP', 'Bhupalpally', 8811.00, 1224596, 18.4386, 79.6489),
('Jogulamba Gadwal', 'JGW', 'Gadwal', 3079.00, 811588, 16.2333, 77.8000),
('Kamareddy', 'KMD', 'Kamareddy', 3570.00, 1175516, 18.3219, 78.3419),
('Karimnagar', 'KMN', 'Karimnagar', 2128.00, 1001953, 18.4386, 79.1288),
('Khammam', 'KHM', 'Khammam', 4379.00, 1613344, 17.2473, 80.1514),
('Komaram Bheem Asifabad', 'KBA', 'Asifabad', 4147.00, 508944, 19.3667, 79.2833),
('Mahabubabad', 'MBD', 'Mahabubabad', 2315.00, 755626, 17.5982, 79.9953),
('Mahabubnagar', 'MBN', 'Mahabubnagar', 18432.00, 4108140, 16.7394, 77.9993),
('Mancherial', 'MCL', 'Mancherial', 4009.00, 708372, 18.8733, 79.4567),
('Medak', 'MDK', 'Medak', 9699.00, 3033288, 18.0553, 78.2747),
('Medchal-Malkajgiri', 'MCM', 'Medchal', 2796.00, 4122852, 17.6243, 78.4816),
('Mulugu', 'MLG', 'Mulugu', 2672.00, 294671, 18.1924, 79.9270),
('Nagarkurnool', 'NKL', 'Nagarkurnool', 6545.00, 1618416, 16.4833, 78.3167),
('Nalgonda', 'NLG', 'Nalgonda', 14240.00, 3452974, 17.0553, 79.2663),
('Narayanpet', 'NPT', 'Narayanpet', 2649.00, 640246, 16.7453, 77.4953),
('Nirmal', 'NML', 'Nirmal', 3545.00, 708652, 19.0969, 78.3428),
('Nizamabad', 'NZB', 'Nizamabad', 7956.00, 2551335, 18.6725, 78.0941),
('Peddapalli', 'PPL', 'Peddapalli', 1204.00, 791230, 18.6133, 79.3733),
('Rajanna Sircilla', 'RSL', 'Sircilla', 2432.00, 635701, 18.3953, 78.8153),
('Rangareddy', 'RRD', 'Rangareddy', 7493.00, 5296741, 17.4065, 78.3428),
('Sangareddy', 'SGD', 'Sangareddy', 9655.00, 2811738, 17.6186, 78.0833),
('Siddipet', 'SPT', 'Siddipet', 4196.00, 1355545, 18.1019, 78.8553),
('Suryapet', 'SPY', 'Suryapet', 3511.00, 1129013, 17.1400, 79.6200),
('Vikarabad', 'VKB', 'Vikarabad', 3386.00, 928123, 17.3386, 77.9053),
('Wanaparthy', 'WPT', 'Wanaparthy', 2488.00, 579563, 16.3667, 78.0667),
('Warangal Rural', 'WGR', 'Warangal', 2175.00, 1001318, 17.9689, 79.5941),
('Warangal Urban', 'WGU', 'Warangal', 812.00, 1135707, 17.9689, 79.5941),
('Yadadri Bhuvanagiri', 'YBG', 'Bhongir', 3045.00, 1058415, 17.5186, 78.8953);

COMMIT;