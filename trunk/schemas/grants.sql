-- Grant select like privs on all tables to pahma
-- Grant insert, delete and update on specified tables.
-- This will have to be adjusted to reflect the deployment

-- You must set the password for your installation!

GRANT SELECT ON pahma_dev.* TO 'pahma'@'yourhost' IDENTIFIED BY 'password';

GRANT INSERT,UPDATE,DELETE ON pahma_dev.user TO 'pahma'@'yourhost'
GRANT INSERT,UPDATE,DELETE ON pahma_dev.permission TO 'pahma'@'yourhost' 
GRANT INSERT,UPDATE,DELETE ON pahma_dev.role TO 'pahma'@'yourhost' 
GRANT INSERT,UPDATE,DELETE ON pahma_dev.user_roles TO 'pahma'@'yourhost' 
GRANT INSERT,UPDATE,DELETE ON pahma_dev.role_perms TO 'pahma'@'yourhost' 
GRANT INSERT,UPDATE,DELETE ON pahma_dev.sets TO 'pahma'@'yourhost' 
GRANT INSERT,UPDATE,DELETE ON pahma_dev.set_objs TO 'pahma'@'yourhost' 
GRANT INSERT,UPDATE,DELETE ON pahma_dev.tags TO 'pahma'@'yourhost' 
GRANT INSERT,UPDATE,DELETE ON pahma_dev.tag_user_object TO 'pahma'@'yourhost' 

