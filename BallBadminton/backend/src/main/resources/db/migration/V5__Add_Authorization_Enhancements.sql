-- Migration V5: Add Authorization Enhancements
-- Adds additional authorization-related data and constraints
-- Author: Telangana Ball Badminton Association
-- Version: 1.0.0

-- Add additional indexes for better performance on authorization queries
CREATE INDEX IF NOT EXISTS idx_user_roles_composite ON user_roles(user_id, role);
CREATE INDEX IF NOT EXISTS idx_users_active_roles ON users(active) WHERE active = true;

-- Create a view for user permissions (for easier querying)
CREATE OR REPLACE VIEW user_permissions AS
SELECT 
    u.id as user_id,
    u.username,
    u.email,
    ur.role,
    CASE ur.role
        WHEN 'SUPER_ADMIN' THEN ARRAY[
            'USER_CREATE', 'USER_READ', 'USER_UPDATE', 'USER_DELETE', 'USER_MANAGE_ROLES',
            'MEMBER_CREATE', 'MEMBER_READ', 'MEMBER_UPDATE', 'MEMBER_DELETE', 'MEMBER_MANAGE_HIERARCHY',
            'PLAYER_CREATE', 'PLAYER_READ', 'PLAYER_UPDATE', 'PLAYER_DELETE', 'PLAYER_MANAGE_STATISTICS', 'PLAYER_MANAGE_ACHIEVEMENTS',
            'TOURNAMENT_CREATE', 'TOURNAMENT_READ', 'TOURNAMENT_UPDATE', 'TOURNAMENT_DELETE', 'TOURNAMENT_MANAGE_REGISTRATION', 'TOURNAMENT_MANAGE_RESULTS',
            'NEWS_CREATE', 'NEWS_READ', 'NEWS_UPDATE', 'NEWS_DELETE', 'NEWS_PUBLISH', 'NEWS_MODERATE',
            'MEDIA_CREATE', 'MEDIA_READ', 'MEDIA_UPDATE', 'MEDIA_DELETE', 'MEDIA_MANAGE_GALLERIES',
            'DISTRICT_CREATE', 'DISTRICT_READ', 'DISTRICT_UPDATE', 'DISTRICT_DELETE', 'DISTRICT_MANAGE_STATISTICS',
            'SYSTEM_ADMIN', 'SYSTEM_BACKUP', 'SYSTEM_RESTORE', 'SYSTEM_MONITOR', 'SYSTEM_AUDIT',
            'CONTENT_MODERATE', 'CONTENT_APPROVE', 'CONTENT_REJECT',
            'FILE_UPLOAD', 'FILE_DOWNLOAD', 'FILE_DELETE', 'FILE_MANAGE'
        ]
        WHEN 'ADMIN' THEN ARRAY[
            'USER_CREATE', 'USER_READ', 'USER_UPDATE', 'USER_DELETE', 'USER_MANAGE_ROLES',
            'MEMBER_CREATE', 'MEMBER_READ', 'MEMBER_UPDATE', 'MEMBER_DELETE', 'MEMBER_MANAGE_HIERARCHY',
            'PLAYER_CREATE', 'PLAYER_READ', 'PLAYER_UPDATE', 'PLAYER_DELETE', 'PLAYER_MANAGE_STATISTICS', 'PLAYER_MANAGE_ACHIEVEMENTS',
            'TOURNAMENT_CREATE', 'TOURNAMENT_READ', 'TOURNAMENT_UPDATE', 'TOURNAMENT_DELETE', 'TOURNAMENT_MANAGE_REGISTRATION', 'TOURNAMENT_MANAGE_RESULTS',
            'NEWS_CREATE', 'NEWS_READ', 'NEWS_UPDATE', 'NEWS_DELETE', 'NEWS_PUBLISH', 'NEWS_MODERATE',
            'MEDIA_CREATE', 'MEDIA_READ', 'MEDIA_UPDATE', 'MEDIA_DELETE', 'MEDIA_MANAGE_GALLERIES',
            'DISTRICT_CREATE', 'DISTRICT_READ', 'DISTRICT_UPDATE', 'DISTRICT_DELETE', 'DISTRICT_MANAGE_STATISTICS',
            'SYSTEM_MONITOR', 'SYSTEM_AUDIT',
            'CONTENT_MODERATE', 'CONTENT_APPROVE', 'CONTENT_REJECT',
            'FILE_UPLOAD', 'FILE_DOWNLOAD', 'FILE_DELETE', 'FILE_MANAGE'
        ]
        WHEN 'EDITOR' THEN ARRAY[
            'MEMBER_READ', 'MEMBER_UPDATE',
            'PLAYER_READ', 'PLAYER_UPDATE', 'PLAYER_MANAGE_STATISTICS', 'PLAYER_MANAGE_ACHIEVEMENTS',
            'TOURNAMENT_READ', 'TOURNAMENT_UPDATE', 'TOURNAMENT_MANAGE_REGISTRATION',
            'NEWS_CREATE', 'NEWS_READ', 'NEWS_UPDATE', 'NEWS_DELETE', 'NEWS_PUBLISH', 'NEWS_MODERATE',
            'MEDIA_CREATE', 'MEDIA_READ', 'MEDIA_UPDATE', 'MEDIA_DELETE', 'MEDIA_MANAGE_GALLERIES',
            'DISTRICT_READ', 'DISTRICT_UPDATE',
            'FILE_UPLOAD', 'FILE_DOWNLOAD', 'FILE_MANAGE'
        ]
        WHEN 'MODERATOR' THEN ARRAY[
            'MEMBER_READ',
            'PLAYER_READ',
            'TOURNAMENT_READ',
            'NEWS_READ', 'NEWS_MODERATE',
            'MEDIA_READ',
            'DISTRICT_READ',
            'CONTENT_MODERATE', 'CONTENT_APPROVE', 'CONTENT_REJECT',
            'FILE_DOWNLOAD'
        ]
        WHEN 'USER' THEN ARRAY[
            'MEMBER_READ',
            'PLAYER_READ',
            'TOURNAMENT_READ',
            'NEWS_READ',
            'MEDIA_READ',
            'DISTRICT_READ',
            'FILE_DOWNLOAD'
        ]
        ELSE ARRAY[]::VARCHAR[]
    END as permissions
FROM users u
JOIN user_roles ur ON u.id = ur.user_id
WHERE u.active = true;

-- Create function to check if user has permission
CREATE OR REPLACE FUNCTION user_has_permission(user_uuid UUID, permission_name VARCHAR)
RETURNS BOOLEAN AS $$
BEGIN
    RETURN EXISTS (
        SELECT 1 
        FROM user_permissions up 
        WHERE up.user_id = user_uuid 
        AND permission_name = ANY(up.permissions)
    );
END;
$$ LANGUAGE plpgsql;

-- Create function to get user role hierarchy level
CREATE OR REPLACE FUNCTION get_role_hierarchy_level(role_name VARCHAR)
RETURNS INTEGER AS $$
BEGIN
    RETURN CASE role_name
        WHEN 'SUPER_ADMIN' THEN 5
        WHEN 'ADMIN' THEN 4
        WHEN 'EDITOR' THEN 3
        WHEN 'MODERATOR' THEN 2
        WHEN 'USER' THEN 1
        ELSE 0
    END;
END;
$$ LANGUAGE plpgsql;

-- Create function to check if user can manage another user
CREATE OR REPLACE FUNCTION user_can_manage_user(manager_uuid UUID, target_uuid UUID)
RETURNS BOOLEAN AS $$
DECLARE
    manager_max_level INTEGER;
    target_max_level INTEGER;
BEGIN
    -- Get the highest role level for the manager
    SELECT COALESCE(MAX(get_role_hierarchy_level(ur.role)), 0)
    INTO manager_max_level
    FROM user_roles ur
    WHERE ur.user_id = manager_uuid;
    
    -- Get the highest role level for the target user
    SELECT COALESCE(MAX(get_role_hierarchy_level(ur.role)), 0)
    INTO target_max_level
    FROM user_roles ur
    WHERE ur.user_id = target_uuid;
    
    -- Manager can manage target if they have higher or equal hierarchy level
    RETURN manager_max_level >= target_max_level;
END;
$$ LANGUAGE plpgsql;

-- Add some sample users for testing (in addition to the admin user)
-- Note: In production, these should be created through the application

-- Create an editor user
INSERT INTO users (
    id,
    username,
    email,
    password,
    first_name,
    last_name,
    active,
    email_verified,
    password_changed_at,
    created_by
) VALUES (
    gen_random_uuid(),
    'editor',
    'editor@telanganaballbadminton.org',
    '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewdBPj6hsW5qfyFS', -- admin123!
    'Content',
    'Editor',
    true,
    true,
    CURRENT_TIMESTAMP,
    'SYSTEM'
) ON CONFLICT (username) DO NOTHING;

-- Assign EDITOR role to editor user
INSERT INTO user_roles (user_id, role)
SELECT id, 'EDITOR'
FROM users
WHERE username = 'editor'
ON CONFLICT (user_id, role) DO NOTHING;

-- Create a moderator user
INSERT INTO users (
    id,
    username,
    email,
    password,
    first_name,
    last_name,
    active,
    email_verified,
    password_changed_at,
    created_by
) VALUES (
    gen_random_uuid(),
    'moderator',
    'moderator@telanganaballbadminton.org',
    '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewdBPj6hsW5qfyFS', -- admin123!
    'Content',
    'Moderator',
    true,
    true,
    CURRENT_TIMESTAMP,
    'SYSTEM'
) ON CONFLICT (username) DO NOTHING;

-- Assign MODERATOR role to moderator user
INSERT INTO user_roles (user_id, role)
SELECT id, 'MODERATOR'
FROM users
WHERE username = 'moderator'
ON CONFLICT (user_id, role) DO NOTHING;

-- Add comments for documentation
COMMENT ON VIEW user_permissions IS 'View that shows all permissions for each user based on their roles';
COMMENT ON FUNCTION user_has_permission(UUID, VARCHAR) IS 'Function to check if a user has a specific permission';
COMMENT ON FUNCTION get_role_hierarchy_level(VARCHAR) IS 'Function to get the hierarchy level of a role';
COMMENT ON FUNCTION user_can_manage_user(UUID, UUID) IS 'Function to check if one user can manage another based on role hierarchy';

-- Create audit table for authorization events (optional, for security monitoring)
CREATE TABLE IF NOT EXISTS authorization_audit (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID,
    action VARCHAR(100) NOT NULL,
    resource_type VARCHAR(50),
    resource_id VARCHAR(100),
    permission_checked VARCHAR(100),
    access_granted BOOLEAN NOT NULL,
    ip_address INET,
    user_agent TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
);

-- Create indexes for audit table
CREATE INDEX IF NOT EXISTS idx_authorization_audit_user_id ON authorization_audit(user_id);
CREATE INDEX IF NOT EXISTS idx_authorization_audit_created_at ON authorization_audit(created_at);
CREATE INDEX IF NOT EXISTS idx_authorization_audit_action ON authorization_audit(action);
CREATE INDEX IF NOT EXISTS idx_authorization_audit_access_granted ON authorization_audit(access_granted);

COMMENT ON TABLE authorization_audit IS 'Audit log for authorization events and access attempts';