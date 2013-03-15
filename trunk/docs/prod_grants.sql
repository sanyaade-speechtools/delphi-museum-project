-- Grant select like privs on all tables to pahma
-- Grant insert, delete and update on specified tables.
-- This will have to be adjusted to reflect the deployment

-- You must set the G0Delphi!! for your installation!

GRANT SELECT ON pahma_prod.* TO 'pahma'@'webfarm%.berkeley.edu' IDENTIFIED BY 'G0Delphi!!';

GRANT INSERT,UPDATE,DELETE ON pahma_prod.user
TO 'pahma'@'webfarm%.berkeley.edu' IDENTIFIED BY 'G0Delphi!!';
GRANT INSERT,UPDATE,DELETE ON pahma_prod.permission 
TO 'pahma'@'webfarm%.berkeley.edu' IDENTIFIED BY 'G0Delphi!!';
GRANT INSERT,UPDATE,DELETE ON pahma_prod.role 
TO 'pahma'@'webfarm%.berkeley.edu' IDENTIFIED BY 'G0Delphi!!';
GRANT INSERT,UPDATE,DELETE ON pahma_prod.user_roles
TO 'pahma'@'webfarm%.berkeley.edu' IDENTIFIED BY 'G0Delphi!!';
GRANT INSERT,UPDATE,DELETE ON pahma_prod.role_perms 
TO 'pahma'@'webfarm%.berkeley.edu' IDENTIFIED BY 'G0Delphi!!';
GRANT INSERT,UPDATE,DELETE ON pahma_prod.sets 
TO 'pahma'@'webfarm%.berkeley.edu' IDENTIFIED BY 'G0Delphi!!';
GRANT INSERT,UPDATE,DELETE ON pahma_prod.set_objs 
TO 'pahma'@'webfarm%.berkeley.edu' IDENTIFIED BY 'G0Delphi!!';
GRANT INSERT,UPDATE,DELETE ON pahma_prod.tags 
TO 'pahma'@'webfarm%.berkeley.edu' IDENTIFIED BY 'G0Delphi!!';
GRANT INSERT,UPDATE,DELETE ON pahma_prod.tag_user_object
TO 'pahma'@'webfarm%.berkeley.edu' IDENTIFIED BY 'G0Delphi!!';

