-- ============================================
-- LoveDev API - Database Schema (Simplified)
-- Version: 1.0.0
-- No complex functions to avoid Flyway issues
-- ============================================

-- ============================================
-- 1. ROLES TABLE
-- ============================================
CREATE TABLE roles (
                       id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                       name VARCHAR(50) NOT NULL UNIQUE,
                       description TEXT,
                       is_system_role BOOLEAN NOT NULL DEFAULT FALSE,
                       created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_roles_name ON roles(name);

-- ============================================
-- 2. PERMISSIONS TABLE
-- ============================================
CREATE TABLE permissions (
                             id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                             name VARCHAR(100) NOT NULL UNIQUE,
                             description TEXT,
                             resource VARCHAR(50) NOT NULL,
                             action VARCHAR(50) NOT NULL,
                             created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                             UNIQUE(resource, action)
);

CREATE INDEX idx_permissions_name ON permissions(name);
CREATE INDEX idx_permissions_resource ON permissions(resource);
CREATE INDEX idx_permissions_action ON permissions(action);

-- ============================================
-- 3. ROLE_PERMISSIONS JUNCTION TABLE
-- ============================================
CREATE TABLE role_permissions (
                                  role_id UUID NOT NULL,
                                  permission_id UUID NOT NULL,
                                  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                  PRIMARY KEY (role_id, permission_id),
                                  CONSTRAINT fk_role_permissions_role FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE,
                                  CONSTRAINT fk_role_permissions_permission FOREIGN KEY (permission_id) REFERENCES permissions(id) ON DELETE CASCADE
);

CREATE INDEX idx_role_permissions_role_id ON role_permissions(role_id);
CREATE INDEX idx_role_permissions_permission_id ON role_permissions(permission_id);

-- ============================================
-- 4. USERS TABLE
-- ============================================
CREATE TABLE users (
                       id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                       email VARCHAR(100) NOT NULL UNIQUE,
                       password VARCHAR(255) NOT NULL,
                       first_name VARCHAR(50) NOT NULL,
                       last_name VARCHAR(50) NOT NULL,
                       phone_number VARCHAR(20),
                       address VARCHAR(255),
                       date_of_birth DATE,
                       profile_picture_url VARCHAR(500),
                       bio TEXT,
                       status VARCHAR(20) NOT NULL DEFAULT 'INACTIVE',
                       email_verified BOOLEAN NOT NULL DEFAULT FALSE,
                       email_verification_token VARCHAR(255),
                       email_verification_expires_at TIMESTAMP,
                       password_reset_token VARCHAR(255),
                       password_reset_expires_at TIMESTAMP,
                       last_login_at TIMESTAMP,
                       created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       deleted_at TIMESTAMP
);

ALTER TABLE users ADD CONSTRAINT chk_status CHECK (status IN ('ACTIVE', 'INACTIVE', 'BANNED'));

CREATE INDEX idx_user_email ON users(email);
CREATE INDEX idx_user_status ON users(status);
CREATE INDEX idx_user_deleted ON users(deleted_at);
CREATE INDEX idx_user_email_verification ON users(email_verification_token);
CREATE INDEX idx_user_password_reset ON users(password_reset_token);

-- ============================================
-- 5. USER_ROLES JUNCTION TABLE
-- ============================================
CREATE TABLE user_roles (
                            user_id UUID NOT NULL,
                            role_id UUID NOT NULL,
                            created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                            PRIMARY KEY (user_id, role_id),
                            CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                            CONSTRAINT fk_user_roles_role FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);

CREATE INDEX idx_user_roles_user_id ON user_roles(user_id);
CREATE INDEX idx_user_roles_role_id ON user_roles(role_id);

-- ============================================
-- 6. REFRESH_TOKENS TABLE
-- ============================================
CREATE TABLE refresh_tokens (
                                id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                token VARCHAR(500) NOT NULL UNIQUE,
                                user_id UUID NOT NULL,
                                expires_at TIMESTAMP NOT NULL,
                                created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                revoked BOOLEAN NOT NULL DEFAULT FALSE,
                                revoked_at TIMESTAMP,
                                CONSTRAINT fk_refresh_token_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE UNIQUE INDEX idx_refresh_token ON refresh_tokens(token);
CREATE INDEX idx_refresh_token_user ON refresh_tokens(user_id);
CREATE INDEX idx_refresh_token_expires ON refresh_tokens(expires_at);

-- ============================================
-- 7. AUDIT_LOGS TABLE
-- ============================================
CREATE TABLE audit_logs (
                            id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                            user_id UUID,
                            action VARCHAR(50) NOT NULL,
                            entity_type VARCHAR(50),
                            entity_id VARCHAR(255),
                            old_value JSONB,
                            new_value JSONB,
                            ip_address VARCHAR(45),
                            user_agent VARCHAR(500),
                            description TEXT,
                            created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                            CONSTRAINT fk_audit_log_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
);

CREATE INDEX idx_audit_user ON audit_logs(user_id);
CREATE INDEX idx_audit_action ON audit_logs(action);
CREATE INDEX idx_audit_entity ON audit_logs(entity_type, entity_id);
CREATE INDEX idx_audit_created ON audit_logs(created_at);

-- ============================================
-- 8. SEED DEFAULT ROLES
-- ============================================
INSERT INTO roles (name, description, is_system_role) VALUES
                                                          ('ROLE_SUPER_ADMIN', 'System-wide administrator with full access', true),
                                                          ('ROLE_ORG_ADMIN', 'Organization administrator with org-level permissions', true),
                                                          ('ROLE_MANAGER', 'Manager with team management and evaluation permissions', true),
                                                          ('ROLE_EVALUATOR', 'Evaluator who can conduct assigned evaluations', true),
                                                          ('ROLE_USER', 'Standard user (Employee/Student) with basic permissions', true),
                                                          ('ROLE_GUEST', 'Guest with read-only access to public content', true);

-- ============================================
-- 9. SEED PERMISSIONS
-- ============================================

-- ==========================================
-- Authentication & Session (All Users)
-- ==========================================
INSERT INTO permissions (name, description, resource, action) VALUES
                                                                  ('auth:login', 'Login to the system', 'auth', 'login'),
                                                                  ('auth:logout', 'Logout from the system', 'auth', 'logout'),
                                                                  ('auth:refresh', 'Refresh authentication token', 'auth', 'refresh');

-- ==========================================
-- System Management (SUPER_ADMIN)
-- ==========================================
INSERT INTO permissions (name, description, resource, action) VALUES
                                                                  ('system:config:read', 'Read system configuration', 'system_config', 'read'),
                                                                  ('system:config:write', 'Write system configuration', 'system_config', 'write'),
                                                                  ('system:monitor', 'Monitor system health and performance', 'system', 'monitor'),
                                                                  ('system:logs:read', 'Read system logs', 'system_logs', 'read');

-- ==========================================
-- Organization Management
-- ==========================================
INSERT INTO permissions (name, description, resource, action) VALUES
                                                                  -- SUPER_ADMIN
                                                                  ('organization:create', 'Create new organization', 'organization', 'create'),
                                                                  ('organization:read:all', 'Read all organizations', 'organization_all', 'read'),
                                                                  ('organization:update:all', 'Update any organization', 'organization_all', 'update'),
                                                                  ('organization:delete:all', 'Delete any organization', 'organization_all', 'delete'),
                                                                  ('organization:list', 'List all organizations', 'organization', 'list'),

                                                                  -- ORG_ADMIN
                                                                  ('organization:read:own', 'Read own organization', 'organization_own', 'read'),
                                                                  ('organization:update:own', 'Update own organization', 'organization_own', 'update'),
                                                                  ('organization:settings:manage', 'Manage organization settings', 'organization_settings', 'manage'),

                                                                  -- GUEST
                                                                  ('organization:read:public', 'Read public organization info', 'organization_public', 'read');

-- ==========================================
-- User Management
-- ==========================================
INSERT INTO permissions (name, description, resource, action) VALUES
                                                                  -- SUPER_ADMIN
                                                                  ('user:create:all', 'Create any user', 'user_all', 'create'),
                                                                  ('user:read:all', 'Read all users', 'user_all', 'read'),
                                                                  ('user:update:all', 'Update any user', 'user_all', 'update'),
                                                                  ('user:delete:all', 'Delete any user', 'user_all', 'delete'),
                                                                  ('user:list:all', 'List all users', 'user_all', 'list'),

                                                                  -- ORG_ADMIN
                                                                  ('user:create:org', 'Create user in organization', 'user_org', 'create'),
                                                                  ('user:read:org', 'Read organization users', 'user_org', 'read'),
                                                                  ('user:update:org', 'Update organization users', 'user_org', 'update'),
                                                                  ('user:delete:org', 'Delete organization users', 'user_org', 'delete'),
                                                                  ('user:list:org', 'List organization users', 'user_org', 'list'),
                                                                  ('user:invite', 'Invite users to organization', 'user', 'invite'),
                                                                  ('user:role:assign', 'Assign roles to users', 'user_role', 'assign'),

                                                                  -- MANAGER
                                                                  ('user:read:team', 'Read team users', 'user_team', 'read'),
                                                                  ('user:list:team', 'List team users', 'user_team', 'list'),

                                                                  -- USER
                                                                  ('user:read:basic', 'Read basic user info', 'user_basic', 'read'),
                                                                  ('user:search:public', 'Search public users', 'user_public', 'search'),
                                                                  ('user:follow', 'Follow other users', 'user', 'follow'),
                                                                  ('user:unfollow', 'Unfollow users', 'user', 'unfollow'),
                                                                  ('user:search:public:limited', 'Limited search of public users', 'user_public_limited', 'search');

-- ==========================================
-- Profile Management
-- ==========================================
INSERT INTO permissions (name, description, resource, action) VALUES
                                                                  ('profile:read:own', 'Read own profile', 'profile_own', 'read'),
                                                                  ('profile:update:own', 'Update own profile', 'profile_own', 'update'),
                                                                  ('profile:read:public', 'Read public profiles', 'profile_public', 'read');

-- ==========================================
-- Evaluation Template Management
-- ==========================================
INSERT INTO permissions (name, description, resource, action) VALUES
                                                                  -- ORG_ADMIN
                                                                  ('template:create', 'Create evaluation template', 'template', 'create'),
                                                                  ('template:read:all', 'Read all templates', 'template', 'read'),
                                                                  ('template:update:all', 'Update any template', 'template', 'update'),
                                                                  ('template:delete:all', 'Delete any template', 'template', 'delete'),
                                                                  ('template:list', 'List all templates', 'template', 'list');

-- ==========================================
-- Group Management
-- ==========================================
INSERT INTO permissions (name, description, resource, action) VALUES
                                                                  -- ORG_ADMIN
                                                                  ('group:create', 'Create group', 'group', 'create'),
                                                                  ('group:read:all', 'Read all groups', 'group_all', 'read'),
                                                                  ('group:update:all', 'Update any group', 'group_all', 'update'),
                                                                  ('group:delete:all', 'Delete any group', 'group_all', 'delete'),
                                                                  ('group:member:manage', 'Manage group members', 'group_member', 'manage'),

                                                                  -- MANAGER
                                                                  ('group:read:own', 'Read own groups', 'group_own', 'read'),
                                                                  ('group:update:own', 'Update own groups', 'group_own', 'update'),
                                                                  ('group:delete:own', 'Delete own groups', 'group_own', 'delete'),
                                                                  ('group:member:add', 'Add group members', 'group_member', 'add'),
                                                                  ('group:member:remove', 'Remove group members', 'group_member', 'remove');

-- ==========================================
-- Evaluation Management
-- ==========================================
INSERT INTO permissions (name, description, resource, action) VALUES
                                                                  -- ORG_ADMIN
                                                                  ('evaluation:create', 'Create evaluation', 'evaluation', 'create'),
                                                                  ('evaluation:read:all', 'Read all evaluations', 'evaluation_all', 'read'),
                                                                  ('evaluation:assign:all', 'Assign evaluations to anyone', 'evaluation_all', 'assign'),
                                                                  ('evaluation:delete:all', 'Delete any evaluation', 'evaluation_all', 'delete'),

                                                                  -- MANAGER
                                                                  ('evaluation:read:team', 'Read team evaluations', 'evaluation_team', 'read'),
                                                                  ('evaluation:assign:team', 'Assign evaluations to team', 'evaluation_team', 'assign'),
                                                                  ('evaluation:conduct', 'Conduct evaluations', 'evaluation', 'conduct'),
                                                                  ('evaluation:review:team', 'Review team evaluations', 'evaluation_team', 'review'),
                                                                  ('evaluation:update:team', 'Update team evaluations', 'evaluation_team', 'update'),

                                                                  -- EVALUATOR
                                                                  ('evaluation:read:assigned', 'Read assigned evaluations', 'evaluation_assigned', 'read'),
                                                                  ('evaluation:submit', 'Submit evaluation', 'evaluation', 'submit'),
                                                                  ('evaluation:update:own', 'Update own evaluation', 'evaluation_own', 'update'),
                                                                  ('evaluation:history:read:own', 'Read own evaluation history', 'evaluation_history_own', 'read'),

                                                                  -- USER
                                                                  ('evaluation:read:own', 'Read own evaluations', 'evaluation_own', 'read'),
                                                                  ('evaluation:respond:assigned', 'Respond to assigned evaluation', 'evaluation_assigned', 'respond'),
                                                                  ('evaluation:submit:own', 'Submit own evaluation', 'evaluation_own', 'submit'),
                                                                  ('evaluation:peer:conduct', 'Conduct peer evaluation', 'evaluation_peer', 'conduct');

-- ==========================================
-- Feedback Management
-- ==========================================
INSERT INTO permissions (name, description, resource, action) VALUES
                                                                  ('feedback:provide', 'Provide feedback', 'feedback', 'provide'),
                                                                  ('feedback:read:own', 'Read own feedback', 'feedback_own', 'read');

-- ==========================================
-- Post Management (Social Features)
-- ==========================================
INSERT INTO permissions (name, description, resource, action) VALUES
                                                                  -- USER
                                                                  ('post:create', 'Create post', 'post', 'create'),
                                                                  ('post:read:public', 'Read public posts', 'post_public', 'read'),
                                                                  ('post:update:own', 'Update own post', 'post_own', 'update'),
                                                                  ('post:delete:own', 'Delete own post', 'post_own', 'delete'),
                                                                  ('post:comment', 'Comment on posts', 'post', 'comment'),
                                                                  ('post:react', 'React to posts', 'post', 'react'),
                                                                  ('post:search:public', 'Search public posts', 'post_public', 'search');

-- ==========================================
-- Chat/Messaging
-- ==========================================
INSERT INTO permissions (name, description, resource, action) VALUES
                                                                  -- MANAGER
                                                                  ('chat:create:team', 'Create team chat', 'chat_team', 'create'),
                                                                  ('chat:read:team', 'Read team chats', 'chat_team', 'read'),
                                                                  ('chat:write:team', 'Write in team chats', 'chat_team', 'write'),

                                                                  -- USER
                                                                  ('chat:read:own', 'Read own chats', 'chat_own', 'read'),
                                                                  ('chat:write:own', 'Write in own chats', 'chat_own', 'write'),
                                                                  ('chat:create:personal', 'Create personal chat', 'chat_personal', 'create');

-- ==========================================
-- Notification Management
-- ==========================================
INSERT INTO permissions (name, description, resource, action) VALUES
                                                                  ('notification:read:own', 'Read own notifications', 'notification_own', 'read'),
                                                                  ('notification:send:team', 'Send notification to team', 'notification_team', 'send'),
                                                                  ('notification:send', 'Send notifications', 'notification', 'send'),
                                                                  ('notification:broadcast', 'Broadcast notification', 'notification', 'broadcast');

-- ==========================================
-- FCM Token Management
-- ==========================================
INSERT INTO permissions (name, description, resource, action) VALUES
                                                                  ('fcm:token:register', 'Register FCM token', 'fcm_token', 'register'),
                                                                  ('fcm:token:remove', 'Remove FCM token', 'fcm_token', 'remove');

-- ==========================================
-- Analytics & Reporting
-- ==========================================
INSERT INTO permissions (name, description, resource, action) VALUES
                                                                  -- SUPER_ADMIN
                                                                  ('analytics:read:all', 'Read all analytics', 'analytics_all', 'read'),
                                                                  ('analytics:export:all', 'Export all analytics', 'analytics_all', 'export'),

                                                                  -- ORG_ADMIN
                                                                  ('analytics:read:org', 'Read organization analytics', 'analytics_org', 'read'),
                                                                  ('analytics:export:org', 'Export organization analytics', 'analytics_org', 'export'),
                                                                  ('report:generate:org', 'Generate organization reports', 'report_org', 'generate'),

                                                                  -- MANAGER
                                                                  ('analytics:read:team', 'Read team analytics', 'analytics_team', 'read'),
                                                                  ('analytics:export:team', 'Export team analytics', 'analytics_team', 'export'),
                                                                  ('report:generate:team', 'Generate team reports', 'report_team', 'generate');

-- ==========================================
-- Data Management
-- ==========================================
INSERT INTO permissions (name, description, resource, action) VALUES
                                                                  -- SUPER_ADMIN
                                                                  ('data:access:all', 'Access all data', 'data_all', 'access'),
                                                                  ('data:export:all', 'Export all data', 'data_all', 'export'),
                                                                  ('data:delete:all', 'Delete any data', 'data_all', 'delete'),

                                                                  -- ORG_ADMIN
                                                                  ('data:read:org', 'Read organization data', 'data_org', 'read'),
                                                                  ('data:export:org', 'Export organization data', 'data_org', 'export');

-- ============================================
-- 10. ASSIGN PERMISSIONS TO ROLES
-- ============================================

-- ==========================================
-- ROLE_SUPER_ADMIN - Full System Access
-- ==========================================
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r, permissions p
WHERE r.name = 'ROLE_SUPER_ADMIN'
  AND p.name IN (
                 'auth:login', 'auth:logout', 'auth:refresh',
                 'system:config:read', 'system:config:write', 'system:monitor', 'system:logs:read',
                 'organization:create', 'organization:read:all', 'organization:update:all', 'organization:delete:all', 'organization:list',
                 'user:create:all', 'user:read:all', 'user:update:all', 'user:delete:all', 'user:list:all',
                 'data:access:all', 'data:export:all', 'data:delete:all',
                 'analytics:read:all', 'analytics:export:all',
                 'notification:send', 'notification:broadcast',
                 'fcm:token:register', 'fcm:token:remove'
    );

-- ==========================================
-- ROLE_ORG_ADMIN - Organization Level
-- ==========================================
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r, permissions p
WHERE r.name = 'ROLE_ORG_ADMIN'
  AND p.name IN (
                 'auth:login', 'auth:logout', 'auth:refresh',
                 'organization:read:own', 'organization:update:own', 'organization:settings:manage',
                 'template:create', 'template:read:all', 'template:update:all', 'template:delete:all', 'template:list',
                 'user:create:org', 'user:read:org', 'user:update:org', 'user:delete:org', 'user:list:org',
                 'user:invite', 'user:role:assign',
                 'group:create', 'group:read:all', 'group:update:all', 'group:delete:all', 'group:member:manage',
                 'evaluation:create', 'evaluation:read:all', 'evaluation:assign:all', 'evaluation:delete:all',
                 'analytics:read:org', 'analytics:export:org', 'report:generate:org',
                 'data:read:org', 'data:export:org',
                 'notification:read:own', 'notification:send',
                 'fcm:token:register', 'fcm:token:remove'
    );

-- ==========================================
-- ROLE_MANAGER - Team Management
-- ==========================================
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r, permissions p
WHERE r.name = 'ROLE_MANAGER'
  AND p.name IN (
                 'auth:login', 'auth:logout', 'auth:refresh',
                 'group:create', 'group:read:own', 'group:update:own', 'group:delete:own',
                 'group:member:add', 'group:member:remove',
                 'evaluation:create', 'evaluation:read:team', 'evaluation:assign:team',
                 'evaluation:conduct', 'evaluation:review:team', 'evaluation:update:team',
                 'analytics:read:team', 'analytics:export:team', 'report:generate:team',
                 'user:read:team', 'user:list:team', 'user:read:basic',
                 'chat:create:team', 'chat:read:team', 'chat:write:team',
                 'notification:read:own', 'notification:send:team',
                 'profile:read:own', 'profile:update:own', 'profile:read:public',
                 'fcm:token:register', 'fcm:token:remove'
    );

-- ==========================================
-- ROLE_EVALUATOR - Conduct Evaluations
-- ==========================================
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r, permissions p
WHERE r.name = 'ROLE_EVALUATOR'
  AND p.name IN (
                 'auth:login', 'auth:logout', 'auth:refresh',
                 'evaluation:read:assigned', 'evaluation:conduct', 'evaluation:submit',
                 'evaluation:update:own', 'evaluation:history:read:own',
                 'feedback:provide', 'feedback:read:own',
                 'user:read:basic',
                 'notification:read:own',
                 'profile:read:own', 'profile:update:own', 'profile:read:public',
                 'fcm:token:register', 'fcm:token:remove'
    );

-- ==========================================
-- ROLE_USER - Standard User
-- ==========================================
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r, permissions p
WHERE r.name = 'ROLE_USER'
  AND p.name IN (
                 'auth:login', 'auth:logout', 'auth:refresh',
                 'profile:read:own', 'profile:update:own', 'profile:read:public',
                 'evaluation:read:own', 'evaluation:respond:assigned', 'evaluation:submit:own',
                 'evaluation:history:read:own', 'evaluation:peer:conduct',
                 'post:create', 'post:read:public', 'post:update:own', 'post:delete:own',
                 'post:comment', 'post:react', 'post:search:public',
                 'chat:read:own', 'chat:write:own', 'chat:create:personal',
                 'notification:read:own',
                 'user:follow', 'user:unfollow', 'user:search:public',
                 'fcm:token:register', 'fcm:token:remove'
    );

-- ==========================================
-- ROLE_GUEST - Read-Only Public Access
-- ==========================================
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r, permissions p
WHERE r.name = 'ROLE_GUEST'
  AND p.name IN (
                 'post:read:public',
                 'profile:read:public',
                 'organization:read:public',
                 'post:search:public',
                 'user:search:public:limited'
    );

-- ============================================
-- 11. INSERT DEFAULT ADMIN USER
-- ============================================
INSERT INTO users (
    email,
    password,
    first_name,
    last_name,
    status,
    email_verified
) VALUES (
             'admin@lovedev.me',
             '$2a$10$O6Aipmy8tD2H94nc7PPfV.2lJyxWWZvvSig3HdzuvJ51SXkgjZSWm',
             'Admin',
             'User',
             'ACTIVE',
             true
         );

-- Assign ADMIN role to admin user
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u, roles r
WHERE u.email = 'admin@lovedev.me'
  AND r.name = 'ROLE_SUPER_ADMIN';

-- ============================================
-- MIGRATION COMPLETE
-- ============================================