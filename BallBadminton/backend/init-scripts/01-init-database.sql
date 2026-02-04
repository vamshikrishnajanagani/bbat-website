-- Database initialization script for development environment
-- Telangana Ball Badminton Association Website

-- Create additional databases for different environments if needed
CREATE DATABASE telangana_ball_badminton_test;
CREATE DATABASE telangana_ball_badminton_staging;

-- Grant permissions to the application user
GRANT ALL PRIVILEGES ON DATABASE telangana_ball_badminton_dev TO tbba_user;
GRANT ALL PRIVILEGES ON DATABASE telangana_ball_badminton_test TO tbba_user;
GRANT ALL PRIVILEGES ON DATABASE telangana_ball_badminton_staging TO tbba_user;

-- Create extensions that might be needed
\c telangana_ball_badminton_dev;
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pg_trgm";
CREATE EXTENSION IF NOT EXISTS "unaccent";

\c telangana_ball_badminton_test;
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pg_trgm";
CREATE EXTENSION IF NOT EXISTS "unaccent";

\c telangana_ball_badminton_staging;
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pg_trgm";
CREATE EXTENSION IF NOT EXISTS "unaccent";