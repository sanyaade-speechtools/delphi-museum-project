-- Grant select like privs on all tables to pahma
-- Grant insert, delete and update on specified tables.
-- This will have to be adjusted to reflect the deployment

GRANT SELECT ON pahma_dev.* TO 'pahma'@'pahma-dev.berkeley.edu' IDENTIFIED BY 'delphi4Evah';

GRANT INSERT,UPDATE,DELETE ON pahma_dev.user
TO 'pahma'@'pahma-dev.berkeley.edu' IDENTIFIED BY 'delphi4Evah';
GRANT INSERT,UPDATE,DELETE ON pahma_dev.permission 
TO 'pahma'@'pahma-dev.berkeley.edu' IDENTIFIED BY 'delphi4Evah';
GRANT INSERT,UPDATE,DELETE ON pahma_dev.role 
TO 'pahma'@'pahma-dev.berkeley.edu' IDENTIFIED BY 'delphi4Evah';
GRANT INSERT,UPDATE,DELETE ON pahma_dev.user_roles
TO 'pahma'@'pahma-dev.berkeley.edu' IDENTIFIED BY 'delphi4Evah';
GRANT INSERT,UPDATE,DELETE ON pahma_dev.role_perms 
TO 'pahma'@'pahma-dev.berkeley.edu' IDENTIFIED BY 'delphi4Evah';
GRANT INSERT,UPDATE,DELETE ON pahma_dev.sets 
TO 'pahma'@'pahma-dev.berkeley.edu' IDENTIFIED BY 'delphi4Evah';
GRANT INSERT,UPDATE,DELETE ON pahma_dev.set_objs 
TO 'pahma'@'pahma-dev.berkeley.edu' IDENTIFIED BY 'delphi4Evah';
GRANT INSERT,UPDATE,DELETE ON pahma_dev.tags 
TO 'pahma'@'pahma-dev.berkeley.edu' IDENTIFIED BY 'delphi4Evah';
GRANT INSERT,UPDATE,DELETE ON pahma_dev.tag_user_object
TO 'pahma'@'pahma-dev.berkeley.edu' IDENTIFIED BY 'delphi4Evah';

